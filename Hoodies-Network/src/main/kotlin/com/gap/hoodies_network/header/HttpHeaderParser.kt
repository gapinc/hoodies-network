package com.gap.hoodies_network.header

import java.nio.charset.Charset

/**
 * HttpHeaderParser class does the parsing of header based on charset
 */
class HttpHeaderParser {

    companion object {
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val DEFAULT_CONTENT_CHARSET = "UTF-8"

        private const val PAIR_LENGTH = 2
        fun parseCharset(headers: Map<String, String>): Charset {
            return parseCharset(headers, charset(DEFAULT_CONTENT_CHARSET))
        }

        fun parseCharset(headers: Map<String, String>, defaultCharset: Charset): Charset {
            val contentType = headers[HEADER_CONTENT_TYPE] ?: return defaultCharset
            val params = contentType.split(";").dropLastWhile { it.isEmpty() }
                .toTypedArray()
            for (i in 1 until params.size) {
                val pair = params[i].trim { it <= ' ' }.split("=").dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                if (pair.size == PAIR_LENGTH && "charset" == pair[0]) {
                    return charset(pair[1])
                }
            }
            return defaultCharset
        }
    }
}
