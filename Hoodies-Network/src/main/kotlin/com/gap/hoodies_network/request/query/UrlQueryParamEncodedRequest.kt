package com.gap.hoodies_network.request.query

import android.util.Log
import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.core.Response
import com.gap.hoodies_network.request.StringRequest
import com.gap.hoodies_network.utils.NetworkHelper
import java.io.UnsupportedEncodingException
import java.net.CookieManager
import java.net.URL

/**
 * UrlQueryParamEncodedRequest class appends query params to url of encoded request
 *
 * @param host
 * @param method
 * @param scheme
 * @param queryParams
 * @param responseListener
 * @param errorListener
 *
 */
class UrlQueryParamEncodedRequest(
    host: String,
    method: String,
    scheme: String,
    queryParams: Map<String, String>?,
    responseListener: Response.ResponseListener?,
    errorListener: Response.ErrorListener?,
    encryptedCache: EncryptedCache,
    cookieManager: CookieManager?
) : StringRequest(
    convertToQueryParamEncodedURL(scheme, host, queryParams),
    method,
    convertToQueryParamEncodedURL(scheme, host, queryParams),
    responseListener,
    errorListener,
    encryptedCache,
    cookieManager
) {
    companion object {
        @Throws(UnsupportedEncodingException::class)
        private fun convertToQueryParamEncodedURL(
            scheme: String,
            host: String,
            queryParams: Map<String, String>?
        ): String {
            val networkHelper = NetworkHelper()
            val finalURL = URL(
                networkHelper.getFinalURL(
                    queryParams, host,
                    scheme
                )
            ).toString()
            Log.d("QueryParam Encoded URL-", finalURL + "")
            return finalURL
        }
    }
}
