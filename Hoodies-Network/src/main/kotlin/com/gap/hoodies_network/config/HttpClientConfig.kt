package com.gap.hoodies_network.config

import java.time.Duration

/**
 * HttpClientConfig class handles connect timeout and read timeout configs
 */
class HttpClientConfig {

    companion object {
        private const val SOCKET_CONNECT_TIMEOUT = 60*1000
        private const val SOCKET_READ_TIMEOUT = 60*1000
        private var socketConnectTimeout: Int = SOCKET_CONNECT_TIMEOUT
        private var socketReadTimeout: Int = SOCKET_READ_TIMEOUT

        /**
         * It returns the connect time out in Duration Type seconds
         */
        fun getConnectTimeOutDuration(): Duration {
            return Duration.ofMillis(socketConnectTimeout.toLong())
        }

        /**
         * It returns the read time out in Duration Type seconds
         */
        fun getReadTimeoutDuration(): Duration {
            return Duration.ofMillis(socketReadTimeout.toLong())
        }

        /**
         * Connect Timeout values are in Duration Type seconds.
         * Set Time Out Values to zero means no Socket Time Out at all.
         * Socket will wait for the connection to complete/connect, if value set to 0.
         * If Time out is set to more than 0, Sockets will close/timeout connect operations after the given time.
         * */
        fun setConnectTimeOut(connectTimeOutInSeconds: Duration) {
            socketConnectTimeout = connectTimeOutInSeconds.toMillis().toInt()
        }

        /**
         * Read Timeout values are in Duration Type seconds.
         * Set Time Out Values to zero means no Socket Time Out at all.
         * Socket will wait for the read operation to complete, if value set to 0.
         * If Time out is set to more than 0, Sockets will close/timeout read operations after the given time.
         * */
        fun setReadTimeOut(readTimeOutInSeconds: Duration) {
            socketReadTimeout = readTimeOutInSeconds.toMillis().toInt()
        }

        /**
         * This function will set the http client configuration to factory defaults
         */

        fun setFactoryDefaultConfiguration() {
            socketConnectTimeout = SOCKET_CONNECT_TIMEOUT
            socketReadTimeout = SOCKET_READ_TIMEOUT
        }
    }

}