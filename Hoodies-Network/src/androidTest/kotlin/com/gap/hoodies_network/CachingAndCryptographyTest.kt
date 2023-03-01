@file:Suppress("UNCHECKED_CAST", "NAME_SHADOWING")

package com.gap.hoodies_network

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.cache.configuration.CacheEnabled
import com.gap.hoodies_network.cache.persistentstorage.CacheDatabase
import com.gap.hoodies_network.core.Failure
import com.gap.hoodies_network.core.HoodiesNetworkClient
import com.gap.hoodies_network.core.Success
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.testObjects.CallResponse
import com.gap.hoodies_network.testObjects.EmptyEncryptionDecryptionInterceptor
import com.gap.hoodies_network.testObjects.EncryptionDecryptionInterceptor
import com.gap.hoodies_network.testObjects.testInterceptor
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.util.*
import javax.crypto.Cipher

@RunWith(AndroidJUnit4::class)
class CachingAndCryptographyTest {
    val mContext = InstrumentationRegistry.getInstrumentation().targetContext
    val db = Room.databaseBuilder(mContext, CacheDatabase::class.java, "HoodiesNetworkCache").build().cacheDao()

    @Before
    fun startMockWebServer() {
        ServerManager.setup(mContext)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    @Test
    fun makeSureDataWasCached() {
        runBlocking {
            val testData = UUID.randomUUID().toString()

            val client =HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/")
                .addInterceptor(testInterceptor(InstrumentationRegistry.getInstrumentation().targetContext)).build()

            when (val result = client.patch<String, CallResponse>(
                "patch",
                testData,
                cacheConfiguration = CacheEnabled(applicationContext = InstrumentationRegistry.getInstrumentation().targetContext)
            )) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    Assert.assertEquals(
                        result.value.data,
                        "\"$testData\""
                    )

                    val url = "http://localhost:6969/patch"
                    //Wait for cache data to be written on another thread
                    while (db.get(url, "\"$testData\"".hashCode()) == null) {
                        delay(100)
                    }

                    //Get the cached data
                    val cachedData = db.get(url, "\"$testData\"".hashCode())!!
                    var data = Base64.getDecoder().decode(cachedData.data).decodeToString()

                    //Convert it to an object
                    val cachedObj = Gson().fromJson(data, CallResponse::class.java)

                    //Assert that the cached object equals the object in the original server response
                    Assert.assertEquals(result.value.data, cachedObj.data)

                    //Now, we replace the data in the cache with a modified version
                    data = data.replace(testData, "cacheModified")
                    cachedData.data = Base64.getEncoder().encodeToString(data.toByteArray())
                    db.delete(url, "\"$testData\"".hashCode())
                    db.insert(cachedData)

                    //Now, we make the same network call again but make sure the data is fetched from the cache
                    when (val resultFromCache = client.patch<String, CallResponse>(
                        "patch",
                        testData,
                        cacheConfiguration = CacheEnabled(applicationContext = InstrumentationRegistry.getInstrumentation().targetContext))) {
                        is Success -> {
                            //Assert that the result we got came from the cache
                            Assert.assertEquals(
                                resultFromCache.value.data,
                                "\"cacheModified\""
                            )

                            //Now, we wait 2 seconds
                            delay(2000)

                            //And finally, make another request, but this time make sure the data is stale and ensure the cached data is not fetched
                            when (val result = client.patch<String, CallResponse>(
                                "patch",
                                testData,
                                cacheConfiguration = CacheEnabled(staleDataThreshold = Duration.ofSeconds(1), applicationContext = InstrumentationRegistry.getInstrumentation().targetContext))) {
                                is Success -> {
                                    //Assert that we got the parameters we were expecting back
                                    Assert.assertEquals(
                                        result.value.data,
                                        "\"$testData\""
                                    )

                                    //Wait for cache data to be written on another thread
                                    delay(1000)

                                    //Get the cached data
                                    val cachedData = db.get(url, "\"$testData\"".hashCode())!!
                                    val data = Base64.getDecoder().decode(cachedData.data).decodeToString()

                                    //Convert it to an object
                                    val cachedObj = Gson().fromJson(data, CallResponse::class.java)

                                    //Assert that the cached object equals the object in the original server response
                                    Assert.assertEquals(result.value.data, cachedObj.data)
                                }
                                is Failure -> {
                                    throw result.reason
                                }
                            }

                        }
                        is Failure -> {
                            throw resultFromCache.reason
                        }
                    }

                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }
    @Test
    fun makeSureDataWasCachedEncryptedFlow() {
        runBlocking {
            val testData = UUID.randomUUID().toString()

            val client =HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").build()

            when (val result = client.patch<String, CallResponse>(
                "patch",
                testData,
                cacheConfiguration = CacheEnabled(applicationContext = InstrumentationRegistry.getInstrumentation().targetContext, encryptionEnabled = true))) {
                is Success -> {
                    //Assert that we got the parameters we were expecting back
                    Assert.assertEquals(
                        result.value.data,
                        "\"$testData\""
                    )

                    val url = "http://localhost:6969/patch"
                    //Wait for cache data to be written on another thread
                    while (db.get(url, "\"$testData\"".hashCode()) == null) {
                        delay(100)
                    }

                    //Get the cached data
                    val cachedData = db.get(url, "\"$testData\"".hashCode())!!
                    var data = EncryptedCache.runAES(Base64.getDecoder().decode(cachedData.data), Base64.getDecoder().decode(cachedData.iv), Cipher.DECRYPT_MODE).decodeToString()

                    //Convert it to an object
                    val cachedObj = Gson().fromJson(data, CallResponse::class.java)

                    //Assert that the cached object equals the object in the original server response
                    Assert.assertEquals(result.value.data, cachedObj.data)

                    //Now, we replace the data in the cache with a modified version
                    data = data.replace(testData, "cacheModified")
                    val iv = EncryptedCache.genIV()
                    cachedData.data = Base64.getEncoder().encodeToString(
                        EncryptedCache.runAES(
                            data.encodeToByteArray(),
                            iv,
                            Cipher.ENCRYPT_MODE
                        )
                    )
                    cachedData.iv = Base64.getEncoder().encodeToString(iv)
                    db.delete(url, "\"$testData\"".hashCode())
                    db.insert(cachedData)

                    //Now, we make the same network call again but make sure the data is fetched from the cache
                    when (val resultFromCache = client.patch<String, CallResponse>(
                        "patch",
                        testData,
                        cacheConfiguration = CacheEnabled(applicationContext = InstrumentationRegistry.getInstrumentation().targetContext, encryptionEnabled = true))) {
                        is Success -> {
                            //Assert that the result we got came from the cache
                            Assert.assertEquals(
                                resultFromCache.value.data,
                                "\"cacheModified\""
                            )

                            //Now, we wait 2 seconds
                            delay(2000)

                            //And finally, make another request, but this time make sure the data is stale and ensure the cached data is not fetched
                            when (val result = client.patch<String, CallResponse>(
                                "patch",
                                testData,
                                cacheConfiguration = CacheEnabled(Duration.ofSeconds(1), true, InstrumentationRegistry.getInstrumentation().targetContext))) {
                                is Success -> {
                                    //Assert that we got the parameters we were expecting back
                                    Assert.assertEquals(
                                        result.value.data,
                                        "\"$testData\""
                                    )

                                    //Wait for cache data to be written on another thread
                                    delay(1000)

                                    //Get the cached data
                                    val cachedData = db.get(url, "\"$testData\"".hashCode())!!
                                    val data = EncryptedCache.runAES(Base64.getDecoder().decode(cachedData.data), Base64.getDecoder().decode(cachedData.iv), Cipher.DECRYPT_MODE).decodeToString()

                                    //Convert it to an object
                                    val cachedObj = Gson().fromJson(data, CallResponse::class.java)

                                    //Assert that the cached object equals the object in the original server response
                                    Assert.assertEquals(result.value.data, cachedObj.data)
                                }
                                is Failure -> {
                                    throw result.reason
                                }
                            }

                        }
                        is Failure -> {
                            throw resultFromCache.reason
                        }
                    }

                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun requestWithBodyAndHeadersEmptyInterceptor() {
        runBlocking {
            val testData = UUID.randomUUID().toString()
            val client =HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/")
                .addEncryptionDecryptionInterceptor(EmptyEncryptionDecryptionInterceptor(InstrumentationRegistry.getInstrumentation().targetContext)).build()

            val headers: HashMap<String, String> = HashMap()
            headers["header1"] = testData

            when (val result = client.patch<String, String>("patch", testData, additionalHeaders = headers)) {
                is Success -> {
                    val obj = JSONObject(result.value)
                    val headers = obj.getJSONObject("headers")

                    //Assert that we got the parameters we were expecting back
                    Assert.assertEquals(obj.getString("data"), "\"$testData\"")
                    Assert.assertEquals(headers.getString("Header1"), testData)

                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun requestWithUrlEncodedParamsAndHeadersEmptyInterceptor() {
        runBlocking {
            val testData = UUID.randomUUID().toString()
            val client =HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/")
                .addEncryptionDecryptionInterceptor(EmptyEncryptionDecryptionInterceptor(InstrumentationRegistry.getInstrumentation().targetContext)).build()

            val queryParams: HashMap<String, String> = HashMap()
            queryParams["test1"] = "body1"
            queryParams["test2"] = testData

            val headers: HashMap<String, String> = HashMap()
            headers["header1"] = testData

            when (val result = client.getUrlQueryParamEncoded<String>(queryParams, "get", additionalHeaders = headers)) {
                is Success -> {
                    val obj = JSONObject(result.value)
                    val args = obj.getJSONObject("args")
                    val headers = obj.getJSONObject("headers")

                    //Assert that we got the parameters we were expecting back
                    Assert.assertEquals(args.getString("test1"), "body1")
                    Assert.assertEquals(args.getString("test2"), testData)
                    Assert.assertEquals(headers.getString("Header1"), testData)
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun requestWithBodyAndHeadersEncryptingInterceptor() {
        runBlocking {
            val testData = UUID.randomUUID().toString()
            val interceptor = EncryptionDecryptionInterceptor(InstrumentationRegistry.getInstrumentation().targetContext)
            val client =HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addEncryptionDecryptionInterceptor(interceptor).build()

            val headers: HashMap<String, String> = HashMap()
            headers["header1"] = testData

            when (val result = client.patch<String, String>("patch", testData, additionalHeaders = headers)) {
                is Success -> {
                    val obj = JSONObject(result.value)
                    val headers = obj.getJSONObject("headers")

                    //Decrypt response
                    val decryptedData = interceptor.runAES(Base64.getDecoder().decode(obj.getString("data")), interceptor.iv, Cipher.DECRYPT_MODE)
                    val decryptedHeader = interceptor.runAES(Base64.getDecoder().decode(headers.getString("Header1")), interceptor.iv, Cipher.DECRYPT_MODE)

                    //Assert that we got the parameters we were expecting back
                    Assert.assertEquals(decryptedData.decodeToString(), "\"$testData\"")
                    Assert.assertEquals(decryptedHeader.decodeToString(), testData)

                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun requestWithUrlEncodedParamsAndHeadersEncryptingInterceptor() {
        runBlocking {
            val testData = UUID.randomUUID().toString()
            val interceptor = EncryptionDecryptionInterceptor(InstrumentationRegistry.getInstrumentation().targetContext)
            val client =HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addEncryptionDecryptionInterceptor(interceptor).build()

            val queryParams: HashMap<String, String> = HashMap()
            queryParams["test1"] = "body1"
            queryParams["test2"] = testData

            val headers: HashMap<String, String> = HashMap()
            headers["header1"] = testData

            when (val result = client.getUrlQueryParamEncoded<String>(queryParams, "get", additionalHeaders = headers)) {
                is Success -> {
                    val obj = JSONObject(result.value)
                    val args = obj.getJSONObject("args")
                    val headers = obj.getJSONObject("headers")

                    val argsMap = mutableMapOf<String, String>()
                    args.keys().forEach {
                        argsMap[interceptor.runAES(Base64.getDecoder().decode(it), interceptor.iv, Cipher.DECRYPT_MODE).decodeToString()] =
                            interceptor.runAES(Base64.getDecoder().decode(args.getString(it)), interceptor.iv, Cipher.DECRYPT_MODE).decodeToString()
                    }

                    //Decrypt response
                    val decryptedHeader = interceptor.runAES(Base64.getDecoder().decode(headers.getString("Header1")), interceptor.iv, Cipher.DECRYPT_MODE)

                    //Assert that we got the parameters we were expecting back
                    Assert.assertEquals(argsMap["test1"], "body1")
                    Assert.assertEquals(argsMap["test2"], testData)
                    Assert.assertEquals(decryptedHeader.decodeToString(), testData)
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

}