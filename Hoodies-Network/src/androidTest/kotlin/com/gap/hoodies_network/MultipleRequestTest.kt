package com.gap.hoodies_network

import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.core.*
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.testObjects.testInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Implemented tests sending multiple network requests
 */
class MultipleRequestTest {
    private val interceptor = testInterceptor(InstrumentationRegistry.getInstrumentation().context)

    data class DelayResponse(
        val delay: Int
    )

    @Before
    fun startMockWebServer() {
        ServerManager.setup(interceptor.context)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    @Test
    fun sendMultipleRequest() {
        /* You can customize this. */
        val count = 3
        val delayList = ArrayList<Result<DelayResponse, HoodiesNetworkError>>()

        runBlocking(Dispatchers.IO) {
            val client = HoodiesNetworkClient.Builder().baseUrl("http://localhost:6969/").addInterceptor(interceptor).build()

            for( i in 1 .. count ) {
                val result = client.get<DelayResponse>("echo/$i")
                delayList.add(result)
            }

            Assert.assertEquals(delayList.size, count)
            delayList.forEachIndexed { index, item ->
                when (item) {
                    is Success -> {
                        println("${index + 1} Request Successful -> Delay: {${item.value.delay}}")
                    }
                    is Failure -> {
                        println("${index + 1} Request Failure -> Error: {${item.reason}}")
                    }
                }
            }
        }
    }
}