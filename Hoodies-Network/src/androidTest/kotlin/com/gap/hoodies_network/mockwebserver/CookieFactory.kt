package com.gap.hoodies_network.mockwebserver

import com.sun.net.httpserver.Headers
import org.json.JSONObject


class CookieFactory : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        post {
            val body = JSONObject(call.getBodyString())

            val respHeaders = Headers()
            for (key in body.keys()) {
                respHeaders.add("Set-Cookie", "$key=${body.get(key)}; SameSite=Strict; HttpOnly")
            }

            call.setResponseHeaders(respHeaders)

            call.respond(200, "{}")
        }
    }

}