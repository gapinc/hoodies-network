package com.gap.hoodies_network.request.json

import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.core.*
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.utils.NetworkHelper
import org.json.JSONObject
import java.net.CookieManager

/**
 * JsonObjectRequest class parses response to jsonObject
 */
class JsonObjectRequest @JvmOverloads constructor(
    url: String?, method: String?, jsonRequest: JSONObject?,
    responseListener: Response.ResponseListener? = null,
    errorListener: Response.ErrorListener? = null,
    encryptedCache: EncryptedCache,
    cookieManager: CookieManager?
) :
    JsonRequest<JSONObject?>(
        url, method, jsonRequest.toString(), responseListener,
        errorListener, encryptedCache, cookieManager
    ) {
    @JvmOverloads
    constructor(
        url: String?, method: String?,
        responseListener: Response.ResponseListener? = null,
        errorListener: Response.ErrorListener? = null,
        encryptedCache: EncryptedCache,
        cookieManager: CookieManager?
    ) : this(url, method, null, responseListener, errorListener, encryptedCache, cookieManager)

    @Throws(HoodiesNetworkError::class)
    override fun parseNetworkResponse(response: Response<Any>?): Response<Any>? {
        val networkHelper = NetworkHelper()
        return networkHelper.getParseNetworkResponse(response)
    }


}
