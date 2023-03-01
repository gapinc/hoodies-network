package com.gap.hoodies_network

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.request.FormUrlEncodedRequest
import com.gap.hoodies_network.request.Request
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.UnsupportedEncodingException

@RunWith(AndroidJUnit4::class)
class FormUrlEncodedRequestTest {
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
    fun requestFormattingTest() {
        val cache = EncryptedCache(null)
        val requestMap: MutableMap<String, String> = HashMap()
        requestMap["key1"] = "value1"
        requestMap["key2"] = "value2"
        requestMap["key3"] = "value3"
        var request: FormUrlEncodedRequest? = null
        try {
            request =
                FormUrlEncodedRequest("", Request.Method.POST, requestMap, null, null, cache, null)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        assertEquals("key1=value1&key2=value2&key3=value3", request?.getBody())
    }
}
