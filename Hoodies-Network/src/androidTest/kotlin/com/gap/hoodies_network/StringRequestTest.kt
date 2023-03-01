@file:Suppress("UNCHECKED_CAST", "NAME_SHADOWING")

package com.gap.hoodies_network

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.core.*
import com.gap.hoodies_network.header.Header
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.UNKNOWN_ERROR_CODE
import com.gap.hoodies_network.request.Request
import com.gap.hoodies_network.connection.queue.RequestQueue
import com.gap.hoodies_network.mockwebserver.MockWebServerManager
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.mockwebserver.helper.MockServerMaker
import com.gap.hoodies_network.request.StringRequest
import org.json.JSONException
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory

@RunWith(AndroidJUnit4::class)
class StringRequestTest {
    val mContext = InstrumentationRegistry.getInstrumentation().context

    private var queue: RequestQueue? = null
    private var testUrl = "http://localhost:6969/echo/1"
    val cache = EncryptedCache(null)

    @Before
    fun setUp() {
        queue = RequestQueue.getInstance(
            URL(testUrl).host,
            SSLSocketFactory.getDefault() as SSLSocketFactory?
        )
        ServerManager.setup(mContext)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    @Test
    fun unSupportedExceptionTest() {
        val testUrl = "http://localhost:6969/echo/1"
        val request = StringRequest(
            testUrl,
            Request.Method.GET,
            encryptedCache = cache,
            cookieManager = null
        )
        val data = ByteArray(1000)
        val response = Response<Any>(data)
        try {
            request.parseNetworkResponse(null)
        } catch (hoodiesNetworkError: HoodiesNetworkError) {
            println("error unSupportedExceptionTest")
        }
        assertNull(response.result)
    }

    @Test
    fun errorDeliverTest() {
        testUrl = "http://localhost:6969/echo/1"
        val request = StringRequest(testUrl,
            Request.Method.GET,
            { response ->
                assertNotNull(response)
            },
            { error -> println("Error - $error") }, cache, null
        )
        queue?.enqueue(request as Request<Any>)
        request.deliverError(HoodiesNetworkError("test error", UNKNOWN_ERROR_CODE))
        try {
            TimeUnit.SECONDS.sleep(6)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun stringRequestTest() {
        val mResult = arrayOfNulls<Any>(1)
        testUrl = "http://localhost:6969/echo/3"
        val request = StringRequest(testUrl,
            Request.Method.GET,
            { response ->
                assertNotNull(response)
                mResult[0] = response
            },
            { error -> mResult[0] = error.toString() }, cache, null
        )
        assertEquals("GET", request.getMethod())
        assertNull(request.getBody().toIntOrNull())
        assertNotNull(testUrl, request.getUrl())
        queue?.enqueue(request as Request<Any>)
        request.deliverResponse(Response(200, ByteArray(1024), 100, ArrayList<Header>()))
        try {
            TimeUnit.SECONDS.sleep(8)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        assertEquals((mResult[0] as Response<String>).result, "{\"delay\":\"3\"}")
    }


    @Test
    fun stringRequestErrorTest() {
        val mResult = arrayOfNulls<Any>(1)
        val testUrl = "https://127.0.0.1/"
        val request = StringRequest(testUrl,
            Request.Method.GET,
            { response -> mResult[0] = response?.result },
            { error ->
                println("Error - $error")
                assertNotNull(error)
                mResult[0] = error
            }, cache, null
        )

        queue?.enqueue(request as Request<Any>)
        try {
            TimeUnit.SECONDS.sleep(6)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun postDataTest() {
        val mResult = arrayOfNulls<Any>(1)

        //Make request body
        val body = JSONObject()
        body.put("name", "test_1")
        body.put("salary", "1234")
        body.put("age", "123")

        //Mock response
        val response =
            "{\"status\":\"success\",\"data\":{\"name\":\"test_1\",\"salary\":\"1234\",\"age\":\"123\",\"id\":9221},\"message\":\"Successfully! Record has been added.\"}"

        //Set up MockWebServer builder with port
        val serverBuilder = MockWebServerManager.Builder().usePort(5000)

        //Set up handler on MockWebServer to accept the request body and headers from above
        MockServerMaker.Builder()
            .acceptMethod(HoodiesNetworkClient.HttpMethod.POST)
            .expect(body)
            .returnThisJsonIfInputMatches(JSONObject(response))
            .applyToMockWebServerBuilder("/test", serverBuilder)

        //Start MockWebServer
        val server = serverBuilder.start()


        val testUrl = "http://localhost:5000/test"
        val request = StringRequest(testUrl,
            Request.Method.POST,
            body,
            { response ->
                assertNotNull(response)
                mResult[0] = response?.result
            }, { error -> mResult[0] = error }, cache, null
        )
        assertEquals("POST", request.getMethod())
        assertNotNull(request.getBody())
        assertEquals(testUrl, request.getUrl())
        queue?.enqueue(request as Request<Any>)
        try {
            TimeUnit.SECONDS.sleep(8)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        server.stop()
    }

    @Test
    fun apiMethodTest() {
        val requestPUT = StringRequest(
            "http://localhost:6969/echo/1", Request.Method.PUT, encryptedCache = cache,
            cookieManager = null
        )
        queue?.enqueue(requestPUT as Request<Any>)
        val requestDELETE =
            StringRequest(
                "http://localhost:6969/echo/1", Request.Method.DELETE, encryptedCache = cache,
                cookieManager = null
            )
        queue?.enqueue(requestDELETE as Request<Any>)
        val requestPATCH = StringRequest(
            "http://localhost:6969/echo/1", Request.Method.PATCH, encryptedCache = cache,
            cookieManager = null
        )
        queue?.enqueue(requestPATCH as Request<Any>)
        val requestTRACE = StringRequest(
            "http://localhost:6969/echo/1", Request.Method.TRACE, encryptedCache = cache,
            cookieManager = null
        )
        queue?.enqueue(requestTRACE as Request<Any>)
        val requestHEAD = StringRequest(
            "http://localhost:6969/echo/1", Request.Method.HEAD, encryptedCache = cache,
            cookieManager = null
        )
        queue?.enqueue(requestHEAD as Request<Any>)
        val requestOPTIONS =
            StringRequest(
                "http://localhost:6969/echo/1", Request.Method.OPTIONS, encryptedCache = cache,
                cookieManager = null
            )
        queue?.enqueue(requestOPTIONS as Request<Any>)
        try {
            TimeUnit.SECONDS.sleep(8)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
