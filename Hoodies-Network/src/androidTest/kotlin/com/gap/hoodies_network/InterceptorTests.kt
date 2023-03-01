package com.gap.hoodies_network

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.core.Failure
import com.gap.hoodies_network.core.HoodiesNetworkClient
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.Success
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.request.CancellableMutableRequest
import com.gap.hoodies_network.request.RetryableCancellableMutableRequest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InterceptorTests {
    @Before
    fun startMockWebServer() {
        ServerManager.setup(InstrumentationRegistry.getInstrumentation().context)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    @Test
    fun cancelRequestInInterceptNetworkTest() {
        runBlocking {
            val interceptor = object: com.gap.hoodies_network.interceptor.Interceptor(InstrumentationRegistry.getInstrumentation().context) {
                override fun interceptNetwork(isOnline: Boolean, cancellableMutableRequest: CancellableMutableRequest) {
                    cancellableMutableRequest.cancelRequest(Success("Cancelled!"))
                }

                override fun interceptRequest(identifier: String, cancellableMutableRequest: CancellableMutableRequest) {
                    throw Exception("Request wasn't cancelled!")
                }
            }

            val client = HoodiesNetworkClient.Builder()
                .baseUrl("http://localhost:6969/")
                .addInterceptor(interceptor)
                .build()

            when (val result = client.getRaw("echo/10")) {
                is Success -> {
                    Assert.assertEquals(result.value, "Cancelled!")
                }
                is Failure -> {
                    throw Exception("Request wasn't cancelled!")
                }
            }
        }
    }

    @Test
    fun cancelRequestInInterceptRequestTest() {
        runBlocking {
            val interceptor = object: com.gap.hoodies_network.interceptor.Interceptor(InstrumentationRegistry.getInstrumentation().context) {
                override fun interceptRequest(identifier: String, cancellableMutableRequest: CancellableMutableRequest) {
                    cancellableMutableRequest.cancelRequest(Success("Cancelled!"))
                }
            }

            val client = HoodiesNetworkClient.Builder()
                .baseUrl("http://localhost:6969/")
                .addInterceptor(interceptor)
                .build()

            when (val result = client.getRaw("echo/10")) {
                is Success -> {
                    Assert.assertEquals(result.value, "Cancelled!")
                }
                is Failure -> {
                    throw Exception("Request wasn't cancelled!")
                }
            }
        }
    }

    @Test
    fun retryRequestTest() {
        runBlocking {
            var counter = 0
            val interceptor = object: com.gap.hoodies_network.interceptor.Interceptor(InstrumentationRegistry.getInstrumentation().context) {
                override fun interceptError(
                    error: HoodiesNetworkError,
                    retryableCancellableMutableRequest: RetryableCancellableMutableRequest,
                    autoRetryAttempts: Int
                ) {
                    if (error.code == 403) {
                        val headers = retryableCancellableMutableRequest.request.getHeaders().toMutableMap()
                        headers["key"] = counter++.toString()
                        retryableCancellableMutableRequest.request.setRequestHeaders(headers)

                        retryableCancellableMutableRequest.retryRequest()
                    }
                }
            }

            val client = HoodiesNetworkClient.Builder()
                .baseUrl("http://localhost:6969/")
                .addInterceptor(interceptor)
                .build()

            when (val result = client.getRaw("wants_key")) {
                is Success -> {
                    Assert.assertEquals(result.value, "Success!")
                }
                is Failure -> {
                    throw Exception(result.reason)
                }
            }
        }
    }
}