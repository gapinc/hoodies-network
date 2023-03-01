package com.gap.hoodies_network.connection

import android.os.SystemClock
import android.util.Log
import com.gap.hoodies_network.config.HttpClientConfig
import com.gap.hoodies_network.config.UrlResolver
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.Response
import com.gap.hoodies_network.core.UNKNOWN_ERROR_CODE
import com.gap.hoodies_network.header.Header
import com.gap.hoodies_network.interceptor.EncryptionDecryptionInterceptor
import com.gap.hoodies_network.request.FileUploadRequest
import com.gap.hoodies_network.request.Request
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.*
import java.nio.charset.StandardCharsets
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

/**
 * BaseNetwork class do init the HttpURLConnection and execute the Http Requests
 *
 * @param mSslHost pass null value if mSslHost is not available/required
 * @param mSslSocketFactory pass null value if mSslSocketFactory is not available/required
 * @throws HoodiesNetworkError on IOException,SocketTimeoutException,MalformedURLException
 *
</T> */
class BaseNetwork(private val mSslHost: String?, private val mSslSocketFactory: SSLSocketFactory?) :
    Network {
    @Throws(HoodiesNetworkError::class)

    /**
     * This fun will execute the Network Request sent by the Network Handler
     *
     * @param request as Request<Any>
     * @return Response as Response<Any>
     * @throws IOException
     * @throws SocketTimeoutException
     * @throws MalformedURLException
     * */
    override fun executeRequest(request: Request<Any>): Response<Any> {
        val requestStartTimeMs = SystemClock.elapsedRealtime()
        var responseContents: ByteArray? = null
        var connection: HttpURLConnection? = null
        var responseCode: Int = UNKNOWN_ERROR_CODE
        var keepConnectionOpen = true
        return try {
            val url: String = request.getUrl()
            Log.d("NWLib url", url)
            val requestedUrl = URL(url)
            connection = openConnection(requestedUrl)
            keepConnectionOpen = false

            //Add stored cookies
            addStoredCookiesToRequest(request, url)

            /** pass request headers, if having any */
            applyRequestHeaders(request, connection)

            /** pass request method */
            setRequestMethod(request, connection)

            /** get network status code */
            responseCode = connection.responseCode
            if (responseCode == -1) {
                /**
                 *  if the response code could not be retrieved -1 will be returned by getResponseCode().
                 * Signal to the caller that something was wrong with the connection.
                 * */

                throw IOException("Could not retrieve response code from HttpUrlConnection.")
            }


            /** Some responses such as 204s do not have content.  We must check. */
            val inputStream: InputStream? = if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                connection.inputStream
            } else {
                // Error
                connection.errorStream
            }
            /** Add 0 byte response as a way of honestly representing a no-content request*/
            responseContents = inputStream?.let { inputStreamToBytes(it) } ?: ByteArray(0)
            if (responseCode < STATUS_OK || responseCode > STATUS_NOT_SUPPORTED) {
                throw IOException()
            }
            keepConnectionOpen = true

            //If encryptionDecryptionInterceptor is present, decrypt response ByteArray
            if (request.cache.encryptionDecryptionInterceptor != null)
                responseContents =
                    request.cache.encryptionDecryptionInterceptor!!.decryptResponse(responseContents)

            //Try caching response
            request.cache.cacheRequestResult(responseContents, request)

            //Store cookies
            request.cookieManager?.put(URI(url), connection.headerFields)

            Response(
                responseCode,
                responseContents,
                SystemClock.elapsedRealtime() - requestStartTimeMs,
                convertHeaders(connection.headerFields),
                url
            )
        } catch (e: SocketTimeoutException) {
            throw HoodiesNetworkError(e.message, responseCode, e)
        } catch (e: MalformedURLException) {
            throw HoodiesNetworkError("Bad URL " + request.getUrl(), responseCode, e)
        } catch (e: IOException) {
            if (null == responseContents) {
                throw HoodiesNetworkError(e.message, responseCode, e)
            } else {
                throw HoodiesNetworkError(String(responseContents), responseCode, e)
            }
        } finally {
            if (!keepConnectionOpen) {
                connection!!.disconnect()
            }
        }
    }

    /**
     * Adds cookies from the CookieManager to the request
     */
    private fun addStoredCookiesToRequest(request: Request<*>, url: String) {
        val headers = request.getHeaders().toMutableMap()
        val cookies = request.cookieManager?.get(URI(url), mapOf())
        val sb = StringBuilder()
        if (cookies != null && cookies["Cookie"] != null) {
            for (line in cookies["Cookie"]!!)
                sb.append(line)

            headers["Cookie"] = sb.toString()
            request.setRequestHeaders(headers)
        }
    }

    /**
     * Opens an [HttpURLConnection] with parameters.
     *
     * @param url
     * @return an open connection
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun openConnection(url: URL): HttpURLConnection {
        val connection = initConnection(url)
        connection.useCaches = false
        connection.doInput = true
        connection.connectTimeout = HttpClientConfig.getConnectTimeOutDuration().toMillis().toInt()
        connection.readTimeout = HttpClientConfig.getReadTimeoutDuration().toMillis().toInt()

        // use caller-provided custom SslSocketFactory, if any, for HTTPS
        if (UrlResolver.PROTOCOL_HTTPS == url.protocol && mSslSocketFactory != null && mSslHost != null && mSslHost == url.host) {
            (connection as HttpsURLConnection).sslSocketFactory = mSslSocketFactory
        }
        UrlResolver.resolveHttpProtocol(url)
        return connection
    }

    /**
     * Create an [HttpURLConnection] for the specified `url`.
     */
    @Throws(IOException::class)
    private fun initConnection(url: URL): HttpURLConnection {
        val connection = url.openConnection() as HttpURLConnection

        // Workaround for the M release HttpURLConnection not observing the
        // HttpURLConnection.setFollowRedirects() property.
        // https://code.google.com/p/android/issues/detail?id=194495
        connection.instanceFollowRedirects = HttpURLConnection.getFollowRedirects()
        return connection
    }


    /**
     * Make the Request configurable using the request method types
     * @param request pass the Request instance to set the Request Type
     * @param connection pass the HttpURLConnection instance to make changes Request in Request Type
     */
    @Throws(IOException::class)
    private fun setRequestMethod(request: Request<*>, connection: HttpURLConnection?) {
        when (request.getMethod()) {
            Request.Method.GET -> connection!!.requestMethod = "GET"
            Request.Method.POST -> {
                connection!!.requestMethod = "POST"
                // pass request body, if any having
                applyBodyData(request, connection)
            }
            Request.Method.PUT -> {
                connection!!.requestMethod = "PUT"
                // pass request body, if any having
                applyBodyData(request, connection)
            }
            Request.Method.DELETE -> connection!!.requestMethod = "DELETE"
            Request.Method.HEAD -> connection!!.requestMethod = "HEAD"
            Request.Method.OPTIONS -> connection!!.requestMethod = "OPTIONS"
            Request.Method.TRACE -> connection!!.requestMethod = "TRACE"
            Request.Method.PATCH -> {
                connection!!.requestMethod = "PATCH"
                // pass request body, if any having
                applyBodyData(request, connection)
            }
            else -> throw IllegalStateException("request method type unknown")
        }
    }

    /**
     * InputStream to bytes
     *
     * @param in input stream
     * @return byte array
     * @throws IOException throw IO  exception
     */
    @Throws(IOException::class)
    private fun inputStreamToBytes(`in`: InputStream?): ByteArray {
        ByteArrayOutputStream().use { bytes ->
            val buffer: ByteArray?
            if (`in` == null) {
                throw IOException()
            }
            buffer = ByteArray(MAX_BUFFER_SIZE)
            var count: Int
            while (`in`.read(buffer).also { count = it } != -1) {
                bytes.write(buffer, 0, count)
            }
            return bytes.toByteArray()
        }
    }

    /**
     * InputStream to bytes
     *
     * @param con HttpURLConnection
     * @param data pass data to write to the DataOutputStream connection
     * @throws IOException throw IO  exception
     */
    @Throws(IOException::class)
    private fun sendData(
        con: HttpURLConnection?,
        data: String,
        encryptionDecryptionInterceptor: EncryptionDecryptionInterceptor?
    ) {
        DataOutputStream(con!!.outputStream).use { wr ->

            //Perform encryption of body data using encryptionDecryptionInterceptor if applicable
            var dataByteArray = data.toByteArray(StandardCharsets.UTF_8)
            if (encryptionDecryptionInterceptor != null)
                dataByteArray = encryptionDecryptionInterceptor.encryptRequest(dataByteArray)

            //Write data to server
            wr.write(dataByteArray)
            wr.flush()
        }
    }


    @Throws(IOException::class)
    private fun sendByteArrays(con: HttpURLConnection?, data: ArrayList<ByteArray>) {
        DataOutputStream(con!!.outputStream).use { wr ->

            for (item in data)
                wr.write(item)

            wr.flush()
        }
    }

    /**
     * Make the Request configurable using the request method types
     * @param request pass the Request instance to set the Request Properties
     * @param connection pass the HttpURLConnection instance
     */
    @Throws(IOException::class)
    private fun applyBodyData(request: Request<*>, connection: HttpURLConnection) {
        if (!request.getHeaders().containsKey("Content-Type")) {
            connection.setRequestProperty("Content-Type", "application/json")
        }
        connection.doOutput = true

        if (request is FileUploadRequest) {
            sendByteArrays(connection, request.getFile())
        } else {
            sendData(connection, request.getBody(), request.cache.encryptionDecryptionInterceptor)
        }
    }

    private fun applyRequestHeaders(request: Request<*>, connection: HttpURLConnection) {
        if (request.getHeaders().isNotEmpty()) {
            for (headerKey in request.getHeaders().keys) {
                connection.setRequestProperty(
                    headerKey,
                    java.lang.String.valueOf(request.getHeaders()[headerKey])
                )
            }
        }
    }


    companion object {
        private const val STATUS_OK = 200
        private const val STATUS_NOT_SUPPORTED = 209
        private const val MAX_BUFFER_SIZE = 1024

        private fun convertHeaders(responseHeaders: Map<String?, List<String>>): List<Header?> {
            val headerList: MutableList<Header?> = ArrayList(responseHeaders.size)
            for (entry in responseHeaders.entries) {
                addToList(entry, headerList)
            }
            return headerList
        }

        private fun addToList(
            entry: Map.Entry<String?, List<String>>,
            headerList: MutableList<Header?>
        ) {
            if (entry.key != null) {
                for (value in entry.value) {
                    headerList.add(Header(entry.key!!, value))
                }
            }
        }
    }


}
