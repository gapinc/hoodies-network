package com.gap.hoodies_network.mockwebserver


class Put : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        put {
            call.respond(200, HttpBinClone().handleRequest(call))
        }
    }

}