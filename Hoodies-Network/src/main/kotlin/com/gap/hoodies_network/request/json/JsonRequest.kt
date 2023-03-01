package com.gap.hoodies_network.request.json

import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.Response
import com.gap.hoodies_network.request.Request
import java.net.CookieManager

/**
 * JsonRequest<T> class parses response to json
 */
abstract class JsonRequest<T>(
    url: String?, method: String?, requestBody: String,
    private val responseListener: Response.ResponseListener?,
    errorListener: Response.ErrorListener?,
    encryptedCache: EncryptedCache,
    cookieManager: CookieManager?
) :
    Request<T>(url!!, method!!, requestBody, errorListener, encryptedCache, cookieManager) {
    private val mLock = Any()

    @Throws(HoodiesNetworkError::class)
    abstract override fun parseNetworkResponse(response: Response<Any>?): Response<Any>?
    override fun deliverResponse(response: Response<Any>?) {
        var listener: Response.ResponseListener?
        synchronized(mLock) { listener = responseListener }
        listener?.onResponse(response)
    }
}
