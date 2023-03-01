package com.gap.hoodies_network

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gap.hoodies_network.header.Header
import com.gap.hoodies_network.core.Response
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class ResponseTest {
    @Test
    fun mapToList() {
        val headers: MutableList<Header> = ArrayList<Header>()
        headers.add(Header("key1", "value1"))
        headers.add(Header("key2", "value2"))
        val resp = Response<Any>(200, null, 10, headers)
        val expectedHeaders: MutableMap<String, String> = HashMap()
        expectedHeaders["key1"] = "value1"
        expectedHeaders["key2"] = "value2"
        val map: Map<String, String>? = Response.toHeaderMap(resp.getAllHeaders())
        assertEquals(expectedHeaders, map)
    }

    @Test
    fun nullValuesDontCrashAndStatusCode() {
        Response<Any>(null as Bitmap?)
        Response<Any>(null as ByteArray?)
        Response<Any>(0, null, 0, null)
        Response<Any>(0, null, null, 0)

        val resp = Response<Any>(-1)
        assertEquals(resp.statusCode, -1)
    }

    @Test
    fun toHeaderMap() {
        assertEquals(Response.toHeaderMap(null), null)
        assertEquals(Response.toHeaderMap(listOf()), emptyMap<String, String>())
    }
}
