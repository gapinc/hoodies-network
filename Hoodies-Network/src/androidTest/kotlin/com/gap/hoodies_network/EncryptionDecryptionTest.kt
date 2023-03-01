package com.gap.hoodies_network

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.mockwebserver.ServerManager
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@RunWith(AndroidJUnit4::class)
class EncryptionDecryptionTest {
    val mContext = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun startMockWebServer() {
        ServerManager.setup(mContext)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    @Test
    fun encryptAndDecryptString() {
        val algorithm = "AES/CBC/PKCS5Padding"
        val key = SecretKeySpec("1234567890123456".toByteArray(), "AES")
        val iv = IvParameterSpec(ByteArray(16))
        val testString = "test12345"
        val encryptedString = encrypt(
            algorithm = algorithm,
            key = key,
            iv = iv,
            inputText = testString
        )
        val decryptedString = decrypt(
            algorithm = algorithm,
            key = key,
            iv = iv,
            cipherText = encryptedString
        )

        Assert.assertEquals(decryptedString, testString)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(algorithm: String, inputText: String, key: SecretKeySpec, iv: IvParameterSpec): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText = cipher.doFinal(inputText.toByteArray())
        return Base64.getEncoder().encodeToString(cipherText)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(algorithm: String, cipherText: String, key: SecretKeySpec, iv: IvParameterSpec): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText))
        return String(plainText)
    }
}