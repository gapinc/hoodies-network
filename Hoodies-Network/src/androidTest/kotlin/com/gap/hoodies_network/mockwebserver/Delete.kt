package com.gap.hoodies_network.mockwebserver


class Delete : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        delete {
            call.respond(200, HttpBinClone().handleRequest(call))
        }
    }

}