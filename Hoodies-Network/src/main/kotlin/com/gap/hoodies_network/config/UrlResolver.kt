package com.gap.hoodies_network.config

import android.util.Log
import java.net.MalformedURLException
import java.net.URL


/**
 * UrlResolver class resolves different parts of url like protocol and hostname
 *
 */
class UrlResolver {

    companion object {
        internal const val PROTOCOL_HTTPS = "https"
        private const val PROTOCOL_HTTP = "http"
        private var mProtocol = PROTOCOL_HTTPS

        /**
         * It resolves the url protocol scheme from the specified `url`.
         * By default, it will use @param PROTOCOL_HTTPS, If no protocol scheme is provided in the url.
         */
        @PublishedApi
        internal fun resolveHttpProtocol(url: URL): String {
            val protocol = url.protocol
            if (protocol.equals(PROTOCOL_HTTPS) || protocol.equals(PROTOCOL_HTTP)) {
                setProtocol(protocol)
            } else {
                setProtocol(PROTOCOL_HTTPS)
            }
            return mProtocol
        }

        /**
         * This @fun  will return the host name of baseUrl
         * if no protocol is provided in the baseUrl , PROTOCOL_HTTPS will be used by default"
         */
        @PublishedApi
        @Throws(MalformedURLException::class)
        internal fun resolveUrl(baseUrl: String?): String? {
            val mUrl = validateUrl(baseUrl)
            if (mUrl.isNullOrBlank()) return mUrl
            val url = URL(mUrl)
            val domain: String = url.authority + url.path
            return if (domain.isNotBlank()) {
                Log.e("getHostName ", domain)
                if (domain.startsWith("www.")) domain.substring(4) else domain
            } else {
                baseUrl
            }
        }


        /**
         * This @fun validateUrl(baseUrl: String?) will validate the url by adding the missing protocol
         * scheme and will return a valid url.
         *
         * If @param baseUrl is already valid or null then it will return it, as it is.
         * If @param baseUrl start with "://"  then PROTOCOL_HTTPS will be concatenated as
         * a prefix to baseurl.
         * If @param baseUrl start with "//"  then PROTOCOL_HTTPS.plus("//") will be
         * concatenated as a prefix to baseurl.
         * If @param baseUrl DO NOT start with "http://" or "https://"  then PROTOCOL_HTTPS.plus("://")
         * will be concatenated as a prefix to baseurl.
         */

        @PublishedApi
        internal fun validateUrl(baseUrl: String?): String? {

            if (baseUrl.isNullOrBlank()) return baseUrl
            var mUrl = baseUrl.lowercase()
            if (mUrl.startsWith("https://") || mUrl.startsWith("http://")) {
                return mUrl
            }
            if (mUrl.startsWith("https:/")){
                return insertCharAtUrl(mUrl,7)
            }
            if(mUrl.startsWith("http:/")) {
                return insertCharAtUrl(mUrl,6)
            }
            if (mUrl.startsWith("://")) {
                mUrl = PROTOCOL_HTTPS.plus(baseUrl.lowercase())
            } else if (mUrl.startsWith("//")) {
                mUrl = PROTOCOL_HTTPS.plus(":").plus(baseUrl.lowercase())
            } else if (!mUrl.startsWith("https://") || !mUrl.startsWith("http://")) {
                mUrl = PROTOCOL_HTTPS.plus("://").plus(baseUrl.lowercase())
            }
            return mUrl
        }

        /**
         * This @fun will get the value of http client protocol scheme
         * By default, it will use @param PROTOCOL_HTTPS, If no protocol is provided in the baseurl.
         */
        @PublishedApi
        internal fun getProtocol(baseUrl: String?): String {
            return resolveHttpProtocol(URL(validateUrl(baseUrl)))
        }

        /**
         * This private @fun is for internal use only, that will set the value of http client protocol scheme
         */
        private fun setProtocol(scheme: String) {
            mProtocol = scheme
        }

        private fun insertCharAtUrl(url: String, index: Int): String{
            val sb = StringBuilder(url)
            sb.insert(index,'/')
            return sb.toString()
        }

    }
}