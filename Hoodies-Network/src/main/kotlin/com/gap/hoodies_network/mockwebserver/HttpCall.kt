package com.gap.hoodies_network.mockwebserver

import android.net.Uri
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange

/**
 * Provides a friendly interface for interacting with HTTP requests and sending responses
 */
class HttpCall(val httpExchange: HttpExchange) {

    /**
     * Returns the request's body as a ByteArray
     */
    fun getBodyByteArray() : ByteArray {
        return httpExchange.requestBody.readBytes()
    }

    /**
     * Returns the request's body as a String
     */
    fun getBodyString() : String {
        return getBodyByteArray().decodeToString()
    }

    /**
     * Gets FormUrlEncoded parameters from the GET request's URL or non-GET request body
     */
    fun getFormUrlEncodedParameters() : Map<String, String> {
        val body = if (httpExchange.requestMethod == "GET") {
            httpExchange.requestURI.query
        } else {
            getBodyString()
        }

        val map = hashMapOf<String, String>()

        //take the String with POST params and turn it into a HashMap
        var last = 0
        var next: Int
        val l = body.length
        while (last < l) {
            next = body.indexOf('&', last)
            if (next == -1) next = l
            if (next > last) {
                val eqPos: Int = body.indexOf('=', last)
                    if (eqPos < 0 || eqPos > next) {
                        map[Uri.decode((body.substring(last, next)))] = ""
                    } else {
                        map[Uri.decode(body.substring(last, eqPos))] = Uri.decode(body.substring(eqPos + 1, next))
                    }
            }
            last = next + 1
        }

        return map
    }

    /**
     * Returns the request headers
     */
    fun getHeaders() : Headers {
        return httpExchange.requestHeaders
    }

    /**
     * Used to set the response headers
     */
    fun setResponseHeaders(headers: Headers) {
        for (header in headers)
            httpExchange.responseHeaders[header.key] = header.value
    }

    /**
     * Used to respond with an HTTP status code and response String
     */
    fun respond(code: Int, response: String) {
        respond(code, response.toByteArray())
    }

    /**
     * Used to respond with an HTTP status code and response ByteArray
     */
    fun respond(code: Int, response: ByteArray) {
        httpExchange.sendResponseHeaders(code, response.size.toLong())
        httpExchange.responseBody.write(response)
        httpExchange.responseBody.flush()
        httpExchange.responseBody.close()
    }
}