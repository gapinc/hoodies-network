@file:Suppress("UNCHECKED_CAST")

package com.gap.hoodies_network.mockwebserver.helper

import com.gap.hoodies_network.core.HoodiesNetworkClient
import com.gap.hoodies_network.mockwebserver.HttpCall
import com.gap.hoodies_network.mockwebserver.MockWebServerManager
import com.gap.hoodies_network.mockwebserver.WebServerHandler
import com.gap.hoodies_network.utils.Generated
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.function.Consumer

/**
 * Provides a friendly DSL for mocking APIs
 */
@Generated
class MockServerMaker {
    @Generated
    class Builder {
        private var method = HoodiesNetworkClient.HttpMethod.POST
        private var expectedInput: Any = JSONObject()
        private var expectedHeaders: Map<String, String> = hashMapOf()
        private var successOutput = ""
        private var errorCode = 500
        private var errorMessage = "Expected input parameters were not received"

        /**
         * Used to specify the accepted HTTP method (default is POST)
         * Requests with any other method will throw error 405
         */
        fun acceptMethod(method: HoodiesNetworkClient.HttpMethod) = apply {
            this.method = method
        }

        /**
         * Used to specify the expected UrlEncodedParams for the page
         * If the parameters received by this page match the expected parameters, the success output will be returned
         * Otherwise, the error output will be returned
         */
        fun expect(urlParameters: Map<String, String>) = apply {
            expectedInput = urlParameters
        }

        /**
         * Used to specify the expected JSONObject in the request body for this page
         * If the object received by this page match the expected object, the success output will be returned
         * Otherwise, the error output will be returned
         */
        fun expect(jsonObject: JSONObject) = apply {
            expectedInput = jsonObject
        }

        /**
         * Used to specify the headers expected for this page
         * If the headers received by this page match the expected headers, the success output will be returned
         * Otherwise, the error output will be returned
         */
        fun expectHeaders(headers: Map<String, String>) = apply {
            expectedHeaders = headers
        }

        /**
         * This is the success output
         * This JSONObject will be returned if the received input and headers match the expected input and headers
         */
        fun returnThisJsonIfInputMatches(jsonObject: JSONObject) = apply {
            successOutput = jsonObject.toString()
        }

        /**
         * This is the success output
         * This JSONArray will be returned if the received input and headers match the expected input and headers
         */
        fun returnThisJsonIfInputMatches(jsonArray: JSONArray) = apply {
            successOutput = jsonArray.toString()
        }

        /**
         * This is the error output
         * This HTTP code and message will be returned if the received input and headers do not match the expected input and headers
         */
        fun returnErrorIfInputDoesNotMatch(httpCode: Int, message: String) = apply {
            errorCode = httpCode
            errorMessage = message
        }

        /**
         * This method applies builds the mock and applied it to a MockWebServerManager Builder
         * @param path - the API endpoint to use. For example, if path = "/test", the page will be served at http://localhost:port/test
         * @param builder - The MockWebServerManager Builder that the mock will be served from
         */
        fun applyToMockWebServerBuilder(path: String, builder: MockWebServerManager.Builder) {
            val callConsumer: Consumer<HttpCall> = Consumer { call ->
                var paramsMatched = false

                when (expectedInput) {
                    is JSONObject -> paramsMatched = (expectedInput as JSONObject).similar(JSONObject(call.getBodyString()))
                    is Map<*, *> -> {
                        val input = call.getFormUrlEncodedParameters()

                        for (item in expectedInput as HashMap<String, String>) {
                            paramsMatched = paramsMatched && item.value == input[item.key]
                        }
                    }
                }

                for (item in expectedHeaders) {
                    paramsMatched = paramsMatched && item.value == call.getHeaders()[item.key]?.firstOrNull()
                }

                if (paramsMatched)
                    call.respond(200, successOutput)
                else
                    call.respond(errorCode, errorMessage)
            }

            val handler = object : WebServerHandler() {
                override fun handleRequest(call: HttpCall) {
                    when (method) {
                        HoodiesNetworkClient.HttpMethod.GET -> get{ callConsumer.accept(call) }
                        HoodiesNetworkClient.HttpMethod.POST -> post{ callConsumer.accept(call) }
                        HoodiesNetworkClient.HttpMethod.PUT -> put{ callConsumer.accept(call) }
                        HoodiesNetworkClient.HttpMethod.DELETE -> delete{ callConsumer.accept(call) }
                        HoodiesNetworkClient.HttpMethod.PATCH -> patch{ callConsumer.accept(call) }
                    }
                }
            }

            builder.addContext(path, handler)
        }
    }
}

/**
 * This function determines if two JSONObjects are the same
 */
@Generated
fun JSONObject.similar(compareTo: JSONObject) : Boolean {
    var result = true

    return try {
        this.keys().forEach {
            result = when (this.get(it)) {
                is JSONObject -> result && (this.get(it) as JSONObject).similar(compareTo.get(it) as JSONObject)
                is JSONArray -> result && (this.get(it) as JSONArray).similar(compareTo.get(it) as JSONArray)
                else -> result && this.get(it) == compareTo.get(it)
            }
        }

        result
    } catch (e: Exception) {
        false
    }
}

/**
 * This function determines if two JSONArrays are the same
 */
@Generated
fun JSONArray.similar(compareTo: JSONArray): Boolean {
    var result = true

    return try {
        for (i in 0 until this.length()) {
            result = when (this.get(i)) {
                is JSONObject -> result && (this.get(i) as JSONObject).similar(compareTo.get(i) as JSONObject)
                is JSONArray -> result && (this.get(i) as JSONArray).similar(compareTo.get(i) as JSONArray)
                else -> result && this.get(i) == compareTo.get(i)
            }
        }
        result
    } catch (e: Exception) {
        false
    }
}
