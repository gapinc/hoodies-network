@file:Suppress("UNCHECKED_CAST", "NAME_SHADOWING")

package com.gap.hoodies_network

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.core.*
import com.gap.hoodies_network.delivery.ResponseDelivery
import com.gap.hoodies_network.header.Header
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.request.json.JsonArrayRequest
import com.gap.hoodies_network.request.json.JsonObjectRequest
import com.gap.hoodies_network.request.Request
import com.gap.hoodies_network.connection.queue.RequestQueue
import com.gap.hoodies_network.mockwebserver.MockWebServerManager
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.mockwebserver.helper.MockServerMaker
import com.gap.hoodies_network.request.json.JsonRequest
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URL
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class JsonRequestTest {
    val mContext = InstrumentationRegistry.getInstrumentation().context

    val cache = EncryptedCache(null)
    private var queue: RequestQueue? = null
    private var testUrl = "http://localhost:6969/echo/1"

    private var mDelivery: ResponseDelivery? = null

    var mRequest: Request<Any> =
        object : JsonRequest<Any>("url", "method", "body", null, null, cache, null) {
            override fun parseNetworkResponse(response: Response<Any>?): Response<Any> {
                return Response(null as Bitmap?)
            }
        }

    var mResponse: Response<Any> = Response(null as Bitmap?)

    var mError: HoodiesNetworkError? = null


    @Before
    @Throws(Exception::class)
    fun setUp() {
        mDelivery = ResponseDeliveryInstant()
        queue = RequestQueue.getInstance(URL(testUrl).host, null)
        ServerManager.setup(mContext)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    @Test
    fun deliverResponseTest() {
        mDelivery?.postResponse(mRequest, mResponse)
        assertNotNull(mDelivery)
    }

    @Test
    fun deliverErrorTest() {
        mError?.let { mDelivery?.postError(mRequest, it) }
        assertNotNull(mDelivery)
    }


    @Test
    fun jsonObjectRequestTest() {
        val mResult = arrayOfNulls<Any>(1)
        val request = JsonObjectRequest(testUrl,
            Request.Method.GET,
            { response ->
                println("response Code - " + response?.statusCode)
                assertNotNull(response)
                mResult[0] = response
            }, { error -> println("Error - $error") }, cache, null
        )
        assertEquals("GET", request.getMethod())
        assertNull(request.getBody().toIntOrNull())
        assertEquals(testUrl, request.getUrl())
        queue?.enqueue(request as Request<Any>)
        try {
            TimeUnit.SECONDS.sleep(6)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        assertEquals((mResult[0] as Response<JSONObject>).result.toString(), "{\"delay\":\"1\"}")
    }

    @Test
    fun deliverResponseInstantTest() {
        val request = JsonObjectRequest(testUrl,
            Request.Method.GET,
            { response ->
                println("response Code - " + response?.statusCode)
                assertNotNull(response)
            },
            { error -> println("Error - $error") }, cache, null
        )

        queue?.enqueue(request as Request<Any>)
        request.deliverResponse(Response(200, ByteArray(1024), 100, ArrayList<Header>()))
        val requestArr = JsonArrayRequest(testUrl,
            Request.Method.GET,
            { response -> assertNotNull(response) },
            { error -> println("Error - $error") },
            cache,
            null
        )
        queue?.enqueue(requestArr as Request<Any>)
        requestArr.deliverResponse(Response(200, ByteArray(1024), 100, ArrayList<Header>()))
        try {
            TimeUnit.SECONDS.sleep(10)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun jsonArrayRequestTest() {
        val mResult = arrayOfNulls<Any>(1)
        val request = JsonArrayRequest(testUrl,
            Request.Method.GET,
            { response ->
                assertNotNull(response)
                mResult[0] = response
            }, { error -> mResult[0] = error }, cache, null
        )
        assertEquals("GET", request.getMethod())
        assertNull(request.getBody().toIntOrNull())
        assertEquals(testUrl, request.getUrl())
        queue?.enqueue(request as Request<Any>)
        try {
            TimeUnit.SECONDS.sleep(8)
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

        //Make request headers
        val reqHeaders: MutableMap<String, String> = HashMap()
        reqHeaders["key"] = "value"

        //Mock response
        val response =
            "{\"status\":\"success\",\"data\":{\"name\":\"test_1\",\"salary\":\"1234\",\"age\":\"123\",\"id\":9221},\"message\":\"Successfully! Record has been added.\"}"

        //Set up MockWebServer builder with port
        val serverBuilder = MockWebServerManager.Builder().usePort(5000)

        //Set up handler on MockWebServer to accept the request body and headers from above
        MockServerMaker.Builder()
            .acceptMethod(HoodiesNetworkClient.HttpMethod.POST)
            .expect(body)
            .expectHeaders(reqHeaders)
            .returnThisJsonIfInputMatches(JSONObject(response))
            .applyToMockWebServerBuilder("/test", serverBuilder)

        //Start MockWebServer
        val server = serverBuilder.start()


        val testUrl = "http://localhost:5000/test"
        val request = JsonObjectRequest(testUrl,
            Request.Method.POST,
            body,
            { response ->
                println(
                    "postDataTest json res - " +
                            response?.result
                )
                assertNotNull(response)
                mResult[0] = response?.result
            }, { error ->
                println("postDat error - " + error.message)
                mResult[0] = error.toString()
            }, cache, null
        )

        request.setRequestHeaders(reqHeaders)
        assertEquals("POST", request.getMethod())
        assertNull(request.getBody().toIntOrNull())
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
    fun jsonArrayRequestWithoutCallback() {
        val mResult = arrayOfNulls<Any>(1)
        testUrl = "http://localhost:6969/todos/"
        val request = JsonArrayRequest(
            testUrl,
            Request.Method.GET,
            encryptedCache = cache,
            responseListener = { response ->
                assertNotNull(response)
                mResult[0] = response
            },
            cookieManager = null
        )
        assertEquals("GET", request.getMethod())
        assertNull(request.getBody().toIntOrNull())
        assertEquals(testUrl, request.getUrl())
        queue?.enqueue(request as Request<Any>)
        try {
            TimeUnit.SECONDS.sleep(10)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        //Make sure we can make a JsonArray out of the response of this test
        JSONArray((mResult[0] as Response<String>).result)
    }

    @Test
    fun jsonObjectRequestWithoutCallback() {
        testUrl = "http://localhost:6969/todos/11"
        val request = JsonObjectRequest(
            testUrl,
            Request.Method.GET,
            encryptedCache = cache,
            cookieManager = null
        )
        assertEquals("GET", request.getMethod())
        assertNull(request.getBody().toIntOrNull())
        assertEquals(testUrl, request.getUrl())
        queue?.enqueue(request as Request<Any>)

    }
}
