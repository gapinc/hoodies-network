package com.gap.hoodies_network

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gap.hoodies_network.config.HttpClientConfig
import com.gap.hoodies_network.connection.queue.RequestQueue
import com.gap.hoodies_network.mockwebserver.ServerManager
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import org.mockito.Mockito
import java.io.IOException
import java.lang.Exception
import java.net.URLConnection
import java.time.Duration


@RunWith(AndroidJUnit4::class)
class SocketTimeOutTest {
    val mContext = InstrumentationRegistry.getInstrumentation().context

    private var queue: RequestQueue? = null
    private val testUrl = "http://localhost:6969/echo/2"

    companion object{
        private var CONNECT_TIMEOUT=1
        private var READ_TIMEOUT=1
    }

    @Before
    fun setUp() {
        queue = RequestQueue.getInstance(URL(testUrl).host, null)
        ServerManager.setup(mContext)
    }

    @After
    fun stopServer() {
        ServerManager.stop()
    }

    open class UrlWrapper(spec: String?) {
        var url: URL = URL(spec)

        @Throws(IOException::class)
        open fun openConnection(): URLConnection {
            return url.openConnection()
        }
    }

    @Test
    @Throws(Exception::class)
    fun socketConnectTimeOutTest() {
        val url = Mockito.mock(UrlWrapper::class.java)
        val mockConnection = Mockito.mock(HttpURLConnection::class.java)

        Mockito.`when`(url.openConnection()).thenReturn(mockConnection)
        assertTrue(url.openConnection() is HttpURLConnection)

        /** This is expected exception to throw to simulate a Socket Connect Timeout */
        val expectedException = SocketTimeoutException()
        /** mockConnection should throw the timeout exception instead of returning a response code */
        Mockito.`when`(mockConnection.responseCode).thenThrow(expectedException)
        /** Now its ready to call the client code */
        val baseNet = BaseNet()
        /** It should catch the Socket Connect Timeout and return false */
        assertFalse(baseNet.testConnectTimeOut())
    }


    @Test
    fun socketReadTimeOutTest() {
        val url = Mockito.mock(UrlWrapper::class.java)
        val mockConnection = Mockito.mock(HttpURLConnection::class.java)
        Mockito.`when`(url.openConnection()).thenReturn(mockConnection)
        assertTrue(url.openConnection() is HttpURLConnection)

        /** This is expected exception to throw to simulate a Socket Connect Timeout */
        val expectedException = SocketTimeoutException()
        /** mockConnection should throw the timeout exception instead of returning a response code */
        Mockito.`when`(mockConnection.responseCode).thenThrow(expectedException)
        /** Now its ready to call the client code */
        val baseNet = BaseNet()
        /** It should catch the Socket Read Timeout and return false */
        assertFalse(baseNet.testReadTimeOut())

    }

    open class BaseNet {
        private val url = "http://localhost:6969/echo/2"
        open fun testConnectTimeOut(): Boolean {
            return try {
                CONNECT_TIMEOUT = 1
                READ_TIMEOUT = 5000
                HttpURLConnection.setFollowRedirects(false)
                val con = URL(url).openConnection() as HttpURLConnection
                con.requestMethod = "HEAD"
                con.connectTimeout = CONNECT_TIMEOUT /**set connect timeout in MilliSeconds */
                con.readTimeout = READ_TIMEOUT /** set read timeout in MilliSeconds */
                con.responseCode == HttpURLConnection.HTTP_OK
            } catch (e: SocketTimeoutException) {
                println(
                    "SocketTimeoutException - " + e.localizedMessage
                )
                false
            } catch (e: IOException) {
                println(
                    "IOException - " + e.localizedMessage
                )
                false
            }
            finally {
               // con.disconnect()

            }
        }

        open fun testReadTimeOut(): Boolean {
            return try {
                CONNECT_TIMEOUT = 5000
                READ_TIMEOUT = 1
                HttpURLConnection.setFollowRedirects(false)
                val con = URL(url).openConnection() as HttpURLConnection
                con.requestMethod = "HEAD"
                con.connectTimeout = CONNECT_TIMEOUT /**set connect timeout in MilliSeconds */
                con.readTimeout = READ_TIMEOUT /** set read timeout in MilliSeconds */
                con.responseCode == HttpURLConnection.HTTP_OK
            } catch (e: SocketTimeoutException) {
                println(
                    "SocketTimeoutException - " + e.localizedMessage
                )
                false
            } catch (e: IOException) {
                println(
                    "IOException - " + e.localizedMessage
                )
                false
            }
            finally {
               // con.disconnect()
            }
        }
    }

    @Test
    fun configSetTimeoutsDurationMilliSeconds() {
        HttpClientConfig.setConnectTimeOut(Duration.ofMillis(5))
        HttpClientConfig.setReadTimeOut(Duration.ofMillis(4))

        assertEquals(HttpClientConfig.getConnectTimeOutDuration(), Duration.ofMillis(5))
        assertEquals(HttpClientConfig.getReadTimeoutDuration(), Duration.ofMillis(4))

        HttpClientConfig.setFactoryDefaultConfiguration()

        assertEquals(HttpClientConfig.getConnectTimeOutDuration(), Duration.ofMillis(60000))
        assertEquals(HttpClientConfig.getReadTimeoutDuration(), Duration.ofMillis(60000))
    }

    @Test
    fun configSetTimeoutsDurationSeconds() {
        HttpClientConfig.setConnectTimeOut(Duration.ofSeconds(5))
        HttpClientConfig.setReadTimeOut(Duration.ofSeconds(4))

        assertEquals(HttpClientConfig.getConnectTimeOutDuration(), Duration.ofSeconds(5))
        assertEquals(HttpClientConfig.getReadTimeoutDuration(), Duration.ofSeconds(4))

        HttpClientConfig.setFactoryDefaultConfiguration()

        assertEquals(HttpClientConfig.getConnectTimeOutDuration(), Duration.ofSeconds(60))
        assertEquals(HttpClientConfig.getReadTimeoutDuration(), Duration.ofSeconds(60))
    }

}
