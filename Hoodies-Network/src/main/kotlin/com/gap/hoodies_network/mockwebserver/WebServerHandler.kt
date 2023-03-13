package com.gap.hoodies_network.mockwebserver

import com.gap.hoodies_network.utils.Generated
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

/**
 * Base class for handling requests in the MockWebServer
 * For every endpoint being served by the MockWebServer, a class inheriting from WebServerHandler must be made
 * Then, the class needs to override handleRequest(call: HttpCall) in order to handle the request using a ktor-like syntax
 */
@Generated
open class WebServerHandler {
    var postRunnable: Runnable? = null
    var getRunnable: Runnable? = null
    var putRunnable: Runnable? = null
    var deleteRunnable: Runnable? = null
    var optionsRunnable: Runnable? = null
    var patchRunnable: Runnable? = null

    val internalHandler = HttpHandler { exchange ->
        handleRequest(HttpCall(exchange))

        when(exchange.requestMethod) {
            "POST" -> postRunnable?.run() ?: return405(exchange)
            "GET" -> getRunnable?.run() ?: return405(exchange)
            "PUT" -> putRunnable?.run() ?: return405(exchange)
            "DELETE" -> deleteRunnable?.run() ?: return405(exchange)
            "OPTIONS" -> optionsRunnable?.run() ?: return405(exchange)
            "PATCH" -> patchRunnable?.run() ?: return405(exchange)
        }
    }

    private fun return405(exchange: HttpExchange) {
        val error = "Method not allowed"
        exchange.sendResponseHeaders(405, error.length.toLong())
        exchange.responseBody.write(error.encodeToByteArray())
        exchange.responseBody.flush()
        exchange.responseBody.close()
    }

    /**
     * This class needs to be overridden with your logic
     */
    open fun handleRequest(call: HttpCall) {
        //Left open for overriding
    }

    fun post(runnable: Runnable) {
        postRunnable = runnable
    }

    fun get(runnable: Runnable) {
        getRunnable = runnable
    }

    fun put(runnable: Runnable) {
        putRunnable = runnable
    }

    fun delete(runnable: Runnable) {
        deleteRunnable = runnable
    }

    fun options(runnable: Runnable) {
        optionsRunnable = runnable
    }

    fun patch(runnable: Runnable) {
        patchRunnable = runnable
    }
}