package com.gap.hoodies_network.request

import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.Response
import java.io.File
import java.net.CookieManager
import java.util.*

abstract class FileRequest<T>(
    url: String?, method: String?, files: List<File>, multipartBoundary: String,
    private val responseListener: Response.ResponseListener?,
    errorListener: Response.ErrorListener?, cookieManager: CookieManager?
) :
    Request<T>(url!!, method!!, files, multipartBoundary, errorListener, cookieManager) {
    private val mLock = Any()

    @Throws(HoodiesNetworkError::class)
    abstract override fun parseNetworkResponse(response: Response<Any>?): Response<Any>?
    override fun deliverResponse(response: Response<Any>?) {
        var listener: Response.ResponseListener?
        synchronized(mLock) { listener = responseListener }
        listener?.onResponse(response)
    }
}