package com.gap.hoodies_network.request

import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.core.Response
import java.io.UnsupportedEncodingException
import java.net.CookieManager
import java.net.URLEncoder


/**
 * FormUrlEncodedRequest class converts request body to a parameterized string
 *
 *  @param url not null
 * @param method not null
 * @param requestBody can be null
 * @param responseListener can be null
 * @param errorListener can be null
 *
 */
class FormUrlEncodedRequest(
    url: String,
    method: String,
    requestBody: Map<String, String>?,
    responseListener: Response.ResponseListener?,
    errorListener: Response.ErrorListener?,
    encryptedCache: EncryptedCache,
    cookieManager: CookieManager?
) : StringRequest(
    url,
    method,
    convertToParameterizedString(requestBody),
    responseListener,
    errorListener,
    encryptedCache,
    cookieManager
) {
    companion object {
        @Throws(UnsupportedEncodingException::class)
        private fun convertToParameterizedString(requestBody: Map<String, String>?): String {
            return if (requestBody != null) {
                val result = StringBuilder()
                var first = true
                for ((key, value) in requestBody) {
                    if (first) {
                        first = false
                    } else {
                        result.append("&")
                    }
                    result.append(URLEncoder.encode(key, "UTF-8"))
                    result.append("=")
                    result.append(URLEncoder.encode(value, "UTF-8"))
                }
                result.toString()
            } else {
                return "" // @MSP replaced null with empty string
            }
        }
    }

}
