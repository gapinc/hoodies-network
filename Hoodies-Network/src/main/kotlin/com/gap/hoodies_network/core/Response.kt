@file:Suppress("UNCHECKED_CAST")

package com.gap.hoodies_network.core

import android.graphics.Bitmap
import com.gap.hoodies_network.header.Header
import java.net.HttpURLConnection
import java.util.*

/**
 * Response class handles response data
 */

class Response<T> {
    private var bitmap: Bitmap? = null
    var statusCode = 0
    private var data: ByteArray? = null
    private var allHeaders: List<Header>? = null
    var networkTimeMs: Long = 0
    var result: T? = null
    var url: String? = null

    internal constructor(
        statusCode: Int,
        data: ByteArray?,
        networkTimeMs: Long,
        allHeaders: List<Header?>?
    ) : this(statusCode, data, allHeaders, networkTimeMs)

    internal constructor(
        statusCode: Int,
        data: ByteArray?,
        networkTimeMs: Long,
        allHeaders: List<Header?>?,
        url: String
    ) : this(statusCode, data, allHeaders, networkTimeMs) {
        this.url = url
    }

    internal constructor(data: ByteArray?) : this(
        HttpURLConnection.HTTP_OK,
        data,  /* networkTimeMs= */
        0,
        emptyList<Header>()
    )

    internal constructor(bitmap: Bitmap?) {
        this.bitmap = bitmap
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }

    fun setBitmap(bitmap: Bitmap?) {
        this.bitmap = bitmap
    }

    fun getData(): ByteArray? {
        return data
    }

    fun setData(data: ByteArray?) {
        this.data = data
    }

    internal constructor(
        statusCode: Int,
        data: ByteArray?,
        allHeaders: List<Header?>?,
        networkTimeMs: Long
    ) {
        this.statusCode = statusCode
        this.data = data
        if (allHeaders == null) {
            this.allHeaders = null
        } else {
            this.allHeaders = Collections.unmodifiableList(allHeaders) as List<Header>?
        }
        this.networkTimeMs = networkTimeMs
    }

    constructor(statusCode: Int) {
        this.statusCode = statusCode
    }

    fun setResultResponse(result: T) {
        this.result = result
    }

    fun getAllHeaders(): List<Header>? {
        return allHeaders
    }

    /** gapnetworkandroid calls response listeners */
    fun interface ResponseListener {
        fun onResponse(response: Response<Any>?)
    }
    /** gapnetworkandroid calls error listeners */
    fun interface ErrorListener {
        fun onErrorResponse(error: HoodiesNetworkError)
    }

    /** bitmap calls listeners */
    interface BitmapResponseListener {
        fun onResponse(bitmap: Bitmap?)
        fun onError(response: HoodiesNetworkError)
    }

    companion object {
        fun toHeaderMap(allHeaders: List<Header>?): Map<String, String>? {

            if (allHeaders == null) {
                return null
            }
            if (allHeaders.isEmpty()) {
                return emptyMap()
            }
            val headers: MutableMap<String, String> =
                TreeMap(java.lang.String.CASE_INSENSITIVE_ORDER)
            /** Later elements in the list take precedence.*/
            for (header in allHeaders) {
                headers[header.getName()] = header.getValue()
            }
            return headers
        }
    }
}
