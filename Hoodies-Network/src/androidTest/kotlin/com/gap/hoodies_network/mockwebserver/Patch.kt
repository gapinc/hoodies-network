package com.gap.hoodies_network.mockwebserver


class Patch : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        patch {
            call.respond(200, HttpBinClone().handleRequest(call))
        }
    }

}