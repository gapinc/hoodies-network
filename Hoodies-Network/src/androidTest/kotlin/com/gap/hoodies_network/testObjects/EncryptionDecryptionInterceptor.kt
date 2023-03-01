package com.gap.hoodies_network.testObjects

import android.content.Context
import com.gap.hoodies_network.interceptor.EncryptionDecryptionInterceptor
import com.gap.hoodies_network.keystore.CacheKeyManager
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

class EncryptionDecryptionInterceptor(override val context: Context) : EncryptionDecryptionInterceptor {
    val iv = byteArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)

    override fun encryptRequest(requestBodyOrUrlQueryParamKeyValue: ByteArray): ByteArray {
        val ciphertext = runAES(requestBodyOrUrlQueryParamKeyValue, iv, Cipher.ENCRYPT_MODE)
        return Base64.getEncoder().encode(ciphertext)
    }

    override fun encryptAdditionalHeaders(additionalHeaderValue: ByteArray): ByteArray {
        val ciphertext = runAES(additionalHeaderValue, iv, Cipher.ENCRYPT_MODE)
        return Base64.getEncoder().encode(ciphertext)
    }

    override fun decryptResponse(response: ByteArray): ByteArray {
        return response
    }

    fun runAES(input: ByteArray, iv: ByteArray, cipherMode: Int): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(cipherMode, CacheKeyManager.getKey(), GCMParameterSpec(128, iv))
        return cipher.doFinal(input)
    }
}