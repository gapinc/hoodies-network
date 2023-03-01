package com.gap.hoodies_network.mockwebserver


class EchoDelay : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        get {
            val delayLength = call.httpExchange.requestURI.toString().split("/").last()

            Thread.sleep(delayLength.toLong() * 1000L)

            call.respond(200, "{\"delay\":\"$delayLength\"}")
        }
    }

}