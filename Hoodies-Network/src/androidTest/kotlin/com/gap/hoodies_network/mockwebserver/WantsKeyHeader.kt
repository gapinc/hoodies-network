package com.gap.hoodies_network.mockwebserver


class WantsKeyHeader : WebServerHandler() {
    override fun handleRequest(call: HttpCall) {
        get {
            val key = call.getHeaders().getFirst("Key")

            if (key != "20") {
                call.respond(403, "Unauthorized")
            } else {
                call.respond(200, "Success!")
            }
        }
    }

}