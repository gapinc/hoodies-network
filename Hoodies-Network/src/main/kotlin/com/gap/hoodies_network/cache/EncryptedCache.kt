package com.gap.hoodies_network.cache

import androidx.room.Room
import com.gap.hoodies_network.cache.configuration.CacheEnabled
import com.gap.hoodies_network.cache.persistentstorage.CacheDao
import com.gap.hoodies_network.interceptor.EncryptionDecryptionInterceptor
import com.gap.hoodies_network.keystore.CacheKeyManager
import com.gap.hoodies_network.request.Request
import com.gap.hoodies_network.cache.persistentstorage.CacheDatabase
import com.gap.hoodies_network.cache.persistentstorage.CachedData
import com.gap.hoodies_network.core.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

class EncryptedCache(
    var cacheConfig: CacheEnabled? = null,
    var encryptionDecryptionInterceptor: EncryptionDecryptionInterceptor? = null
) {
    private var db: CacheDao? = null

    init {
        if (cacheConfig != null)
            db = Room.databaseBuilder(cacheConfig!!.applicationContext, CacheDatabase::class.java, "HoodiesNetworkCache").build().cacheDao()
    }

    fun cacheRequestResult(data: ByteArray, request: Request<*>) {
        //Abort if config is null
        cacheConfig ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val cachedData = if (cacheConfig!!.encryptionEnabled) {
                var iv = genIV()

                //Make sure the IV is unique
                while (db!!.getByIv(Base64.getEncoder().encodeToString(iv)).isNotEmpty())
                    iv = genIV()

                //Encrypt the data
                val encryptedData = runAES(data, iv, Cipher.ENCRYPT_MODE)

                CachedData(
                    0,
                    request.getUrl(),
                    request.getBody().hashCode(),
                    getCurrentSeconds(),
                    Base64.getEncoder().encodeToString(encryptedData),
                    Base64.getEncoder().encodeToString(iv)
                )
            } else {
                CachedData(
                    0,
                    request.getUrl(),
                    request.getBody().hashCode(),
                    getCurrentSeconds(),
                    Base64.getEncoder().encodeToString(data),
                    null
                )
            }

            db!!.delete(request.getUrl(), request.getBody().hashCode())
            db!!.insert(cachedData)
        }
    }

    fun getCachedData(request: Request<Any>) {
        val cachedData = db!!.get(request.getUrl(), request.getBody().hashCode())!!

        val data = if (cachedData.iv != null) {
            runAES(Base64.getDecoder().decode(cachedData.data), Base64.getDecoder().decode(cachedData.iv), Cipher.DECRYPT_MODE)
        } else {
            Base64.getDecoder().decode(cachedData.data)
        }

        val cachedResponse = Response<Any>(
            200,
            data,
            0,
            listOf()
        )

        request.parseNetworkResponse(cachedResponse)?.let {
           request.deliverResponse(it)
        }
    }

    /**
     * Method to get current date and time at utc in seconds since UNIX epoch
     * @return [String] of current date
     */
    private fun getCurrentSeconds(): Long {
        return OffsetDateTime.now(ZoneOffset.ofHours(0)).toEpochSecond()
    }

    /**
     * Method to check if data in datastore is stale
     * @param request - The network request
     * @return [Boolean]
     */
    fun isDataStale(request: Request<Any>): Boolean {
        val data = db!!.get(request.getUrl(), request.getBody().hashCode())

        return data == null || Duration.ofSeconds(getCurrentSeconds() - data.cachedAt) > cacheConfig!!.staleDataThreshold
    }

    companion object {
        /**
         * Method to encrypt/decrypt data
         * This is used for encryptedCache and persistent cookie storage
         *
         * @param input - the input data
         * @param iv - the IV
         * @param cipherMode - ENCRYPT_MODE or DECRYPT_MODE
         */
        fun runAES(input: ByteArray, iv: ByteArray, cipherMode: Int): ByteArray {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(cipherMode, CacheKeyManager.getKey(), GCMParameterSpec(128, iv))
            return cipher.doFinal(input)
        }

        /**
         * Generates SecureRandom 16-byte IV
         */
        fun genIV() : ByteArray {
            val r = SecureRandom()
            val ivBytes = ByteArray(12)
            r.nextBytes(ivBytes)

            return ivBytes
        }
    }
}