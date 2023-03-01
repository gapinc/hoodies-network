package com.gap.hoodies_network

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.config.*
import com.gap.hoodies_network.header.Header
import com.gap.hoodies_network.header.HttpHeaderParser
import com.gap.hoodies_network.mockwebserver.ServerManager
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeaderTest {
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
    fun headerTest() {
        val header = Header("key", "value")
        assertEquals("key", header.getName())
        assertEquals("value", header.getValue())
        val header2 = Header("key2", "value2")
        val b: Boolean = header == header2
        assertFalse(b)
        val b2: Boolean = header.equals(null)
        assertFalse(b2)
        val hashCode: Int = header.hashCode()
        val hashCode2: Int = header2.hashCode()
        assertNotEquals(hashCode.toLong(), hashCode2.toLong())
        val str: String = header.toString()
        assertNotNull(str)
    }

    @Test
    fun headerParserTest() {
        val defaultHeaders = hashMapOf(
            CONTENT_TYPE_KEY to APPLICATION_JSON, MULTIDB_ENABLED to MULTIDB_ENABLED_VALUE,
            CLIENT_ID to CLIENT_ID_VALUE,
            CLIENT_OS to CLIENT_OS_VALUE
        )

        val charset = HttpHeaderParser.parseCharset(defaultHeaders)
        Log.e("charset",charset.toString())
    }
}
