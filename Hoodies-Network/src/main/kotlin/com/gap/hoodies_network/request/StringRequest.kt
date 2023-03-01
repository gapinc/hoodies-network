package com.gap.hoodies_network.request

import android.util.Log
import androidx.annotation.GuardedBy
import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.core.*
import com.gap.hoodies_network.header.HttpHeaderParser
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.NULL_POINTER_ERROR_CODE
import com.gap.hoodies_network.core.UNSUPPORTED_ENCODING_ERROR_CODE
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.CookieManager


/**
 * StringRequest class handles the string requests
 *
 * @param url
 * @param method
 * @param requestBody
 * @param responseListener
 * @param errorListener
 *
 */
open class StringRequest(
    url: String, method: String, requestBody: String,
    @GuardedBy("mLock") private val responseListener: Response.ResponseListener?,
    errorListener: Response.ErrorListener?,
    encryptedCache: EncryptedCache,
    cookieManager: CookieManager?
) : Request<String?>(url, method, requestBody, errorListener, encryptedCache, cookieManager) {
    private val mLock = Any()

    constructor(
        url: String,
        method: String,
        jsonRequest: JSONObject? = null,
        encryptedCache: EncryptedCache,
        cookieManager: CookieManager?
    ) : this(
        url,
        method,
        jsonRequest,
        null,
        null,
        encryptedCache,
        cookieManager
    )

    constructor(
        url: String,
        method: String,
        responseListener: Response.ResponseListener?,
        errorListener: Response.ErrorListener?,
        encryptedCache: EncryptedCache,
        cookieManager: CookieManager?
    ) : this(
        url,
        method,
        null as JSONObject?,
        responseListener,
        errorListener,
        encryptedCache,
        cookieManager
    )

    constructor(
        url: String,
        method: String,
        requestBody: JSONObject?,
        responseListener: Response.ResponseListener?,
        errorListener: Response.ErrorListener?,
        encryptedCache: EncryptedCache,
        cookieManager: CookieManager?
    ) : this(
        url,
        method,
        requestBody.toString(),
        responseListener,
        errorListener,
        encryptedCache,
        cookieManager
    )

    @Throws(HoodiesNetworkError::class)
    override fun parseNetworkResponse(response: Response<Any>?): Response<Any>? {
        val parsedResponse: String
        return try {
            parsedResponse = response?.getData()?.let {
                Response.toHeaderMap(response.getAllHeaders())?.let { it1 ->
                    HttpHeaderParser.parseCharset(
                        it1
                    )

                }?.let { it2 ->
                    String(
                        it,
                        it2
                    )
                }

            }.toString()
            response?.setResultResponse(parsedResponse)
            response
        } catch (e: UnsupportedEncodingException) {
            Log.e("parseNetworkResponse", e.toString())
            throw HoodiesNetworkError(e.message, UNSUPPORTED_ENCODING_ERROR_CODE)
        } catch (e: NullPointerException) {
            Log.e("parseNetworkResponse", e.toString())
            throw HoodiesNetworkError(e.message, NULL_POINTER_ERROR_CODE)
        }
    }

    override fun deliverResponse(response: Response<Any>?) {
        var listener: Response.ResponseListener?
        synchronized(mLock) { listener = responseListener }
        listener?.onResponse(response)
    }
}
