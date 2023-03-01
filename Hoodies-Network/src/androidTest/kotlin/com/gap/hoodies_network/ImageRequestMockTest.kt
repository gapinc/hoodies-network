@file:Suppress("UNCHECKED_CAST")

package com.gap.hoodies_network

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.cache.configuration.CacheDisabled
import com.gap.hoodies_network.request.ImageRequest
import com.gap.hoodies_network.connection.queue.RequestQueue
import com.gap.hoodies_network.core.*
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.request.Request
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URL

@RunWith(AndroidJUnit4::class)
class ImageRequestMockTest {

    val mContext = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun startMockWebServer() {
        ServerManager.setup(mContext)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    val cache = EncryptedCache(null)
    private var queue: RequestQueue? = null
    private var testUrl = "http://localhost:6969/image" // it should be an imageUrl

    private var context: Context = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun setUp() {
        queue = RequestQueue.getInstance(URL(testUrl).host, null)
    }

    @Test
    fun imageRequestTest() {
        val errorListener = Response.ErrorListener { }
        val request = ImageRequest(
            testUrl, 200, 200, Bitmap.Config.ARGB_8888,
            object : Response.BitmapResponseListener {
                override fun onResponse(bitmap: Bitmap?) {}
                override fun onError(response: HoodiesNetworkError) {}
            },
            errorListener, cache, null)
        queue?.let { request.processRequest(it, request) }
    }

    // TO DO parseCharset UNIT TEST

    @Test
    fun getImageRequestTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").build()

            when (val result = client.getImage(
                "image",
                null,
                0,
                0,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.ALPHA_8
            )) {
                is Success -> {
                    //Assume request is successful, will be further covered in androidTest
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getImageRequestTest2() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").build().nonInlinedClient

            when (val result = client.sendImageRequest(
                "image",
                0,
                0,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.ALPHA_8,
                cacheConfiguration = CacheDisabled()
            )) {
                is Success -> {
                    //Assume request is successful, will be further covered in androidTest
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getImageRequestTest3() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").build()

            when (val result = client.getImage(
                "image",
                maxWidth = 0,
                maxHeight = 0,
                scaleType = ImageView.ScaleType.CENTER,
                config = Bitmap.Config.ALPHA_8
            )) {
                is Success -> {
                    //Assume request is successful, will be further covered in androidTest
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getImageRequestWithHeadersTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").build().nonInlinedClient

            val result =
                suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                    val request = client.getImageRequest(
                        "image",
                        0,
                        0,
                        ImageView.ScaleType.FIT_XY,
                        Bitmap.Config.ALPHA_8,
                        hashMapOf(),
                        continuation = continuation
                    )
                    client.sendRequest("id", request as Request<Any>, continuation, CacheDisabled())
                }

            when (result) {
                is Success -> {
                    //Assume request is successful, will be further covered in androidTest
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getImageRequestNoHeadersTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").build().nonInlinedClient

            val result =
                suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                    val request = client.getImageRequest(
                        "image",
                        0,
                        0,
                        ImageView.ScaleType.CENTER_CROP,
                        Bitmap.Config.ALPHA_8,
                        continuation = continuation
                    )
                    client.sendRequest("id", request as Request<Any>, continuation, CacheDisabled())
                }

            when (result) {
                is Success -> {
                    //Assume request is successful, will be further covered in androidTest
                }
                is Failure -> {
                    throw result.reason
                }
            }
        }
    }

    @Test
    fun getImageRequestFailureTest() {
        runBlocking {
            val client = HoodiesNetworkClient.Builder().baseUrl("https://127.0.0.1/").build().nonInlinedClient

            val result =
                suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                    val request = client.getImageRequest(
                        "image",
                        0,
                        0,
                        ImageView.ScaleType.CENTER,
                        Bitmap.Config.ALPHA_8,
                        continuation = continuation
                    )
                    client.sendRequest("id", request as Request<Any>, continuation, CacheDisabled())
                }

            when (result) {
                is Success -> {
                    throw Exception()
                }
                is Failure -> {
                    assertEquals(result.reason.code, -1)
                }
            }
        }
    }
}
