package com.gap.hoodies_network.request.query

import android.util.Log
import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.core.Response
import com.gap.hoodies_network.request.StringRequest
import com.gap.hoodies_network.utils.NetworkHelper
import java.io.UnsupportedEncodingException
import java.net.CookieManager
import java.net.URL
import java.net.URLDecoder

/**
 * UrlQueryParamRequest class appends query params to url of request
 *
 * @param host
 * @param method
 * @param scheme
 * @param queryParams
 * @param responseListener
 * @param errorListener
 *
 */
class UrlQueryParamRequest(
    host: String,
    method: String,
    scheme: String,
    queryParams: Map<String, String>?,
    responseListener: Response.ResponseListener?,
    errorListener: Response.ErrorListener?,
    encryptedCache: EncryptedCache,
    cookieManager: CookieManager?
) : StringRequest(
    convertToQueryParamURL(scheme, host, queryParams),
    method,
    convertToQueryParamURL(scheme, host, queryParams),
    responseListener,
    errorListener,
    encryptedCache,
    cookieManager
) {
    companion object {

        @Throws(UnsupportedEncodingException::class)
        private fun convertToQueryParamURL(
            scheme: String,
            host: String,
            queryParams: Map<String, String>?
        ): String {
            val networkHelper = NetworkHelper()
            val finalURL = URL(
                URLDecoder.decode(
                    networkHelper.getFinalURL(
                        queryParams, host,
                        scheme
                    ), "UTF-8"
                )
            ).toString()
            Log.d("QueryParamURL-", finalURL + "")
            return finalURL

        }

    }
}
