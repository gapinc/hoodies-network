package com.gap.hoodies_network.mockwebserver

import org.json.JSONObject
import java.lang.StringBuilder

class HttpBinClone {
    fun handleRequest(call: HttpCall) : String {
        val response = JSONObject()
        val headers = JSONObject()

        val files = JSONObject()

        if (call.getHeaders()["Content-type"]?.firstOrNull()?.contains("multipart/form-data;") == true) {
            val boundary = call.getHeaders()["Content-type"]?.firstOrNull()?.split("boundary=")?.last()!!

            val bodyParts = call.getBodyString().split("--$boundary")

            for (part in bodyParts){
                val lines = part.split("\n")
                if (lines.size > 3) {
                    val fileName = lines[1].split(" name=").last().split(";").first()
                    val fileContents = StringBuilder()
                    println(lines.size)
                    lines.subList(3, lines.size).forEach { if (it.isNotBlank()) { fileContents.append(it.replace("\r","")) } }

                    files.put(fileName, fileContents)
                }
            }
        }

        response.put("files", files)


        for (item in call.getHeaders()) {
            headers.put(item.key, (item.value.firstOrNull()) ?: "")
        }

        val query: String = call.httpExchange.requestURI.query ?: ""

        response.put("headers", headers)
        response.put("url", "http://localhost:6969${call.httpExchange.requestURI}$query")
        response.put("origin", call.httpExchange.remoteAddress.toString())

        if (call.getHeaders()["Content-Type"]?.firstOrNull() == "application/x-www-form-urlencoded") {
            response.put("data", call.getFormUrlEncodedParameters())
        } else {
            response.put("data", call.getBodyString())
        }

        try {
            val args = JSONObject()
            for (item in call.getFormUrlEncodedParameters()) {
                args.put(item.key, item.value)
            }
            response.put("args", args)
        } catch (e: Exception) {
            //Unsupported
        }

        return response.toString()
    }
}