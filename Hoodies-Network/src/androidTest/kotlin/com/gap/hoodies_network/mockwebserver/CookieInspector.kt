package com.gap.hoodies_network.mockwebserver

import org.json.JSONObject

class CookieInspector : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        post {
            val response = JSONObject()

            call.getHeaders()["Cookie"]?.forEach { cookieLine ->
                cookieLine.split("; ").forEach { singleCookie ->
                    val cookieParts = singleCookie.split("=")
                    response.put(cookieParts[0], cookieParts[1])
                }
            }

            call.respond(200, response.toString())
        }
    }

}