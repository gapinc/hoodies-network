package com.gap.hoodies_network.mockwebserver

import android.content.Context

class ServerManager {
    companion object {
        var server: MockWebServerManager? = null

        fun setup(context:Context?) {

            val builder = MockWebServerManager.Builder()

            //HttpBin replica
            builder.addContext("/post", Post())
            builder.addContext("/get", Get())
            builder.addContext("/options", Options())
            builder.addContext("/put", Put())
            builder.addContext("/delete", Delete())
            builder.addContext("/patch", Patch())
            builder.addContext("/html", Html())
            builder.addContext("/image", ImageReturn(context!!))

            //Postman echo replica
            builder.addContext("/echo", EchoDelay())

            //OpenWeatherMap sample replica
            builder.addContext("/weather", Weather())

            //JsonTodos replica
            builder.addContext("/todos", JsonTodos())

            //Cookie testing setup
            builder.addContext("/cookie_factory", CookieFactory())
            builder.addContext("/cookie_inspector", CookieInspector())

            //Interceptor testing setup
            builder.addContext("/wants_key", WantsKeyHeader())
            //Sometimes the tests get run in parallel and fail because the port is already in use
            //For those cases, we will wait here until the server can start

            var started = false

            while (!started) {
                try {
                    server = builder.start()
                    started = true
                } catch (e: Exception) {
                    Thread.sleep(100)
                }
            }
        }

        fun stop() {
            server?.stop()
        }
    }
}