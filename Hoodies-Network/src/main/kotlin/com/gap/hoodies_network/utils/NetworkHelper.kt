package com.gap.hoodies_network.utils

import android.net.Uri
import android.util.Log
import com.gap.hoodies_network.core.*
import com.gap.hoodies_network.header.HttpHeaderParser
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException

class NetworkHelper {


        fun getFinalURL(
            queryParams: Map<String, String>?,
            host: String,
            scheme: String
        ): String {
            var mUrl = Uri.Builder()

            mUrl = getAppendQueryParameter(queryParams, mUrl)
            return scheme+"://"+host+mUrl.build().toString()
        }

        private fun getAppendQueryParameter(queryParams: Map<String, String>?, uri: Uri.Builder) : Uri.Builder {
            uri.apply {
                if (queryParams != null) {
                    for ((key, value) in queryParams) {
                        appendQueryParameter(key, value)
                        Log.d("queryParams=", ("$key = $value").toString())
                    }
                }
            }

            return uri
        }

    fun getParseNetworkResponse(response: Response<Any>?, array: Boolean = false): Response<Any>?{
        try {
            val jsonString = response?.getData()?.let {
                Response.toHeaderMap(response.getAllHeaders())?.let { it1 ->
                    HttpHeaderParser.parseCharset(
                        it1,
                        charset(NetworkHelper.PROTOCOL_CHARSET)
                    )
                }?.let { it2 ->
                    String(
                        it,
                        it2
                    )
                }
            }

            if (array)
                response?.setResultResponse(jsonString!!)
            else
                response?.setResultResponse(JSONObject(jsonString!!))

            return response
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

