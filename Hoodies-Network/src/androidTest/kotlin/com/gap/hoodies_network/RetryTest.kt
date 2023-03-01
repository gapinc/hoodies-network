@file:Suppress("UNCHECKED_CAST")

package com.gap.hoodies_network

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.cache.configuration.CacheDisabled
import com.gap.hoodies_network.config.HttpClientConfig
import com.gap.hoodies_network.core.Failure
import com.gap.hoodies_network.core.HoodiesNetworkClient
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.Result
import com.gap.hoodies_network.core.Success
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.request.Request
import com.gap.hoodies_network.request.RetryableCancellableMutableRequest
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration


@RunWith(AndroidJUnit4::class)
class RetryTest {
    val mContext = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun startMockWebServer() {
        ServerManager.setup(mContext)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    class testInterceptor(context: Context) : com.gap.hoodies_network.interceptor.Interceptor(context)  {
        var runs = 0

        override fun interceptError(
            error: HoodiesNetworkError,
            retryableCancellableMutableRequest: RetryableCancellableMutableRequest,
            autoRetryAttempts: Int
        ) {
            runs = autoRetryAttempts
        }
    }

    val interceptor = testInterceptor(InstrumentationRegistry.getInstrumentation().targetContext)

    @Test
    fun retryRequest() {
        runBlocking {

            val client = HoodiesNetworkClient.Builder()
                .baseUrl("http://localhost:6969/")
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true, HoodiesNetworkClient.RetryCount.RETRY_TWICE)
                .build().nonInlinedClient

            HttpClientConfig.setConnectTimeOut(Duration.ofSeconds(2))
            HttpClientConfig.setReadTimeOut(Duration.ofSeconds(2))

            val result = suspendCancellableCoroutine { continuation: CancellableContinuation<Result<*, HoodiesNetworkError>> ->
                val request = client.getRequestUrlQueryParamEncoded(
                    "echo/10","id",  HoodiesNetworkClient.HttpMethod.GET, hashMapOf(),
                    continuation = continuation, resultType = String::class.java
                )
                client.sendRequest("id", request as Request<Any>, continuation, CacheDisabled())
            }

            when (result) {
                is Success -> {
                    throw Exception("This request should've timed out")
                }
                is Failure -> {
                    Assert.assertEquals(interceptor.runs, 3)
                }
            }
        }
    }
}