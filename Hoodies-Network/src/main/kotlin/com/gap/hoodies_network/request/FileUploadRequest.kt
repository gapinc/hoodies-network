package com.gap.hoodies_network.request

import android.util.Log
import com.gap.hoodies_network.core.*
import com.gap.hoodies_network.header.HttpHeaderParser
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.CookieManager
import java.util.*

class FileUploadRequest(
    url: String?,
    files: List<File>,
    multipartBoundary: String,
    method: HoodiesNetworkClient.HttpMethod,
    responseListener: Response.ResponseListener? = null,
    errorListener: Response.ErrorListener?,
    cookieManager: CookieManager?
) : FileRequest<File?>(
    url,
    method.value,
    files,
    multipartBoundary,
    responseListener,
    errorListener,
    cookieManager
) {

    @Throws(HoodiesNetworkError::class)
    override fun parseNetworkResponse(response: Response<Any>?): Response<Any>? {
        return try {
            val jsonString = response?.getData()?.let {
                Response.toHeaderMap(response.getAllHeaders())?.let { it1 ->
                    HttpHeaderParser.parseCharset(
                        it1,
                        charset(PROTOCOL_CHARSET)
                    )
                }?.let { it2 ->
                    String(
                        it,
                        it2
                    )
                }
            }
            response?.setResultResponse(JSONObject(jsonString!!))
            response
        } catch (e: UnsupportedEncodingException) {
            Log.e("parseNetworkResponse", e.toString())
            throw HoodiesNetworkError(e.message, UNSUPPORTED_ENCODING_ERROR_CODE)
        } catch (e: JSONException) {
            Log.e("parseNetworkResponse", e.toString())
            throw HoodiesNetworkError(e.message, JSON_ERROR_CODE)
        } catch (e: NullPointerException) {
            Log.e("parseNetworkResponse", e.toString())
            throw HoodiesNetworkError(e.message, NULL_POINTER_ERROR_CODE)
        }
    }

    companion object {
        const val PROTOCOL_CHARSET = "utf-8"
    }

}