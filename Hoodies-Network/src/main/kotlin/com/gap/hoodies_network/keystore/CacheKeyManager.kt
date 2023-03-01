package com.gap.hoodies_network.keystore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class CacheKeyManager {
    companion object {
        //This is our key alias
        private const val keyAlias = "HoodiesNetworkCacheKey"

        //This is our key
        private var key: SecretKey? = null

        /**
         * If our key has been cached, returns it immediately
         * Otherwise, checks if a key has been generated and generates new one if necessary
         * Then, fetches key, caches, and returns it
         */
        @Synchronized
        fun getKey() : SecretKey {
            if (key != null)
                return key!!

            if (!doesKeyExist())
                generateNewKey()

            //Create keystore instance
            val keystore = KeyStore.getInstance("AndroidKeyStore")
            keystore.load(null)

            //Otherwise, let's fetch the existing key
            println("HoodiesNetworkCache fetching existing key from KeyStore")
            val secretKeyEntry = keystore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry
            key = secretKeyEntry.secretKey
            return key!!
        }

        /**
         * Create keystore instance and check if key exists
         */
        @Synchronized
        private fun doesKeyExist() : Boolean {
            println("HoodiesNetworkCache checking if encryption key exists")
            val keystore = KeyStore.getInstance("AndroidKeyStore")
            keystore.load(null)
            return keystore.containsAlias(keyAlias)
        }

        /**
         * Generate a new key and store in KeyStore
         */
        @Synchronized
        private fun generateNewKey() {
            println("HoodiesNetworkCache generating new encryption key and putting into KeyStore")
            val keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false) //We need this so we can provide our own IV
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }
}
