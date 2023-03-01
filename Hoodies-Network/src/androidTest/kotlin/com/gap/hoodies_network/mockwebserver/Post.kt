package com.gap.hoodies_network.mockwebserver


class Post : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        post {
            call.respond(200, HttpBinClone().handleRequest(call))
        }
    }

}