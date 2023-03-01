@file:Suppress("UNCHECKED_CAST")

package com.gap.hoodies_network

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.request.json.JsonArrayRequest
import com.gap.hoodies_network.request.json.JsonObjectRequest
import com.gap.hoodies_network.request.Request
import com.gap.hoodies_network.connection.queue.RequestQueue
import com.gap.hoodies_network.mockwebserver.ServerManager
import com.gap.hoodies_network.request.StringRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URL
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class MultiRequestTest {
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
    fun multipleRequestTest() {
        val cache = EncryptedCache(null)
        val testUrl = "http://localhost:6969/echo/1"
        val queue = RequestQueue.getInstance(URL(testUrl).host, null)
        assertNull(queue?.dequeue())
        val requestOne = StringRequest(
            "http://localhost:6969/echo/1",
            Request.Method.GET,
            { response ->
                println(
                    "requestOne result - " + response?.result
                )
                assertNotNull(response)
            },
            { error -> println("error requestOne - " + error.message) }, cache, null
        )
        val requestTwo = StringRequest(
            "http://localhost:6969/echo/2",
            Request.Method.GET,
            { response ->
                println(
                    "requestTwo result - " + response?.result
                )
                println("timeMS - " + response?.networkTimeMs)
                assertNotNull(response)
            },
            { error -> println("error requestTwo - " + error.message) }, cache, null
        )
        val requestThree = StringRequest(
            "http://localhost:6969/todos/30",
            Request.Method.GET,
            encryptedCache = cache,
            cookieManager = null
        )
        val body = JSONObject()
        try {
            body.put("key", "value")
        } catch (e: JSONException) {
            Log.e("error", e.toString())
        }
        val bodyArr = JSONArray()
        try {
            bodyArr.put(0, body)
        } catch (e: JSONException) {
            Log.e("error", e.toString())
        }
        val requestFour = StringRequest(
            "http://localhost:6969/echo/1",
            Request.Method.GET, body, encryptedCache = cache,
            cookieManager = null
        )
        val requestFive = JsonObjectRequest(
            "http://localhost:6969/echo/1",
            Request.Method.GET, body, encryptedCache = cache,
            cookieManager = null
        )

        val requestSix = JsonArrayRequest(
            "http://localhost:6969/todos/",
            Request.Method.GET, bodyArr, encryptedCache = cache,
            cookieManager = null
        )

        val requestSeven = StringRequest(
            "http://localhost:6969/echo/3",
            Request.Method.GET,
            { response ->
                println(
                    "requestSeven result - " + response?.result
                )
                assertNotNull(response)
            },
            { error -> println("error requestSeven - " + error.message) }, cache, null
        )
        val requestEight = StringRequest(
            "http://localhost:6969/echo/4",
            Request.Method.GET,
            { response ->
                println(
                    "requestEight result - " + response?.result
                )
                println("timeMS - " + response?.networkTimeMs)
                assertNotNull(response)
            },
            { error -> println("error requestTwo - " + error.message) }, cache, null
        )
        assertNotSame(requestOne.hashCode(), requestTwo.hashCode())
        assertNotEquals(requestOne, requestFour)
        queue?.enqueue(requestOne as Request<Any>)
        queue?.enqueue(requestTwo as Request<Any>)
        queue?.enqueue(requestThree as Request<Any>)
        queue?.enqueue(requestFour as Request<Any>)
        queue?.enqueue(requestFive as Request<Any>)
        queue?.enqueue(requestSix as Request<Any>)
        queue?.enqueue(requestSeven as Request<Any>)
        queue?.enqueue(requestEight as Request<Any>)
        if (queue != null) {
            assertTrue(queue.hasItems())
        }

        try {
            TimeUnit.SECONDS.sleep(15)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

}
