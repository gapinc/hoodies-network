package com.gap.hoodies_network.mockwebserver


class Get : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        get {
            call.respond(200, HttpBinClone().handleRequest(call))
        }
    }

}