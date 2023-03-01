package com.gap.hoodies_network.mockwebserver


class Options : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        options {
            call.respond(200, HttpBinClone().handleRequest(call))
        }
    }

}