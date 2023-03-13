package com.gap.hoodies_network.mockwebserver

import com.gap.hoodies_network.utils.Generated
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

/**
 * This class manages the MockWebServer
 */
@Generated
class MockWebServerManager(builder: Builder) {

    private val httpServer: HttpServer = HttpServer.create(InetSocketAddress(builder.port), 0)

    init {
        for (item in builder.context)
            httpServer.createContext(item.key, item.value.internalHandler)
    }

    /**
     * Starts the MockWebServer
     */
    fun start() = apply {
        httpServer.start()
    }

    /**
     * Stops the MockWebServer
     */
    fun stop() {
        httpServer.stop(0)
    }

    /**
     * Builder for the MockWebServer
     */
    class Builder {
        internal val context: HashMap<String, WebServerHandler> = HashMap()
        internal var port: Int = 6969

        /**
         * Called to add API endpoints to be served by the MockWebServer
         * For more details, see the WebServerHandler documentation
         */
        fun addContext(key: String, value: WebServerHandler) = apply {
            context[key] = value
        }

        /**
         * Specifies the port the MockWebServer should use. Default is 6969
         */
        fun usePort(port: Int) = apply {
            this.port = port
        }

        /**
         * Starts the MockWebServer and returns a MockWebServerManager object
         * To stop the MockWebServer, call the MockWebServerManager's stop() method
         */
        fun start() : MockWebServerManager {
            return MockWebServerManager(this).start()
        }
    }

}
