package com.gap.hoodies_network.request

import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.Response
import java.io.File
import java.net.CookieManager
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.Collections.emptyMap

/**
 * Network request base class
 *
 * @param <T> the type of parsed response which request expects.
</T> */
abstract class Request<T> : Comparable<Request<T>?> {

    private val method: String
    private val url: String
    var requestBody: String
    var cache: EncryptedCache = EncryptedCache()
    var cookieManager: CookieManager? = null
    var files: List<File>? = null
    var multipartBoundary: String = ""
    private var headers: Map<String, String?> = emptyMap()
    var errorListener: Response.ErrorListener? = null
    internal var requestIsCancelled = false
    internal var retryingRequest = false
    internal var cancellationResult: com.gap.hoodies_network.core.Result<*, HoodiesNetworkError>? = null

    @Throws(HoodiesNetworkError::class)
    abstract fun parseNetworkResponse(response: Response<Any>?): Response<Any>?
    abstract fun deliverResponse(response: Response<Any>?)

    fun deliverError(hoodiesNetworkError: HoodiesNetworkError) =
        errorListener?.onErrorResponse(hoodiesNetworkError)

    /** Supported request methods.  */
    interface Method {
        companion object {
            const val GET = "GET"
            const val POST = "POST"
            const val PUT = "PUT"
            const val DELETE = "DELETE"
            const val HEAD = "HEAD"
            const val OPTIONS = "OPTIONS"
            const val TRACE = "TRACE"
            const val PATCH = "PATCH"
        }
    }

    constructor(
        url: String,
        method: String,
        encryptedCache: EncryptedCache,
        cookieManager: CookieManager?
    ) {
        this.url = url
        this.method = method
        this.requestBody = "" // @MSP replaced null with empty string
        this.cache = encryptedCache
        this.cookieManager = cookieManager
    }

    constructor(
        url: String,
        method: String,
        requestBody: String,
        errorListener: Response.ErrorListener?,
        encryptedCache: EncryptedCache,
        cookieManager: CookieManager?
    ) {
        this.url = url
        this.method = method
        this.errorListener = errorListener
        this.requestBody = requestBody
        this.cache = encryptedCache
        this.cookieManager = cookieManager
    }

    constructor(
        url: String,
        method: String,
        files: List<File>,
        multipartBoundary: String,
        errorListener: Response.ErrorListener?,
        cookieManager: CookieManager?
    ) {
        this.url = url
        this.method = method
        this.requestBody = ""
        this.multipartBoundary = multipartBoundary
        this.files = files
        this.errorListener = errorListener
        this.cookieManager = cookieManager
    }

    /** Return the method for this request. Can be one of the values in [Method].  */
    open fun getMethod(): String {
        return this.method
    }

    /**  return the url of request  */
    open fun getUrl(): String {
        return this.url
    }

    fun getBody(): String {
        return this.requestBody
    }

    fun getFile(): ArrayList<ByteArray> {
        return postMultipartFormData(files)
    }

    /** return the headers of request */
    open fun getHeaders(): Map<String, String?> {
        return this.headers
    }

    fun setRequestHeaders(headers: Map<String, String?>) {
        this.headers = headers
    }

    override fun compareTo(other: Request<T>?): Int {
        return 0
    }

    fun postMultipartFormData(data: List<File>?): ArrayList<ByteArray> {
        val byteArrays = ArrayList<ByteArray>()
        val separator =
            "--$multipartBoundary\r\nContent-Disposition: multipart/form-data; name=".toByteArray(
                StandardCharsets.UTF_8
            )

        //If data null, return empty ByteArrays
        data ?: return arrayListOf(ByteArray(0))

        for (file in data) {
            byteArrays.add(separator)

            val path = Paths.get(file.toURI())
            val mimeType = Files.probeContentType(path)
            byteArrays.add(
                "file${data.indexOf(file)}; filename=\"${path.fileName}\"\r\nContent-Type: $mimeType\r\n\r\n".toByteArray(
                    StandardCharsets.UTF_8
                )
            )
            byteArrays.add(Files.readAllBytes(path))
            byteArrays.add("\r\n".toByteArray(StandardCharsets.UTF_8))
        }
        byteArrays.add("--$multipartBoundary--".toByteArray(StandardCharsets.UTF_8))

        return byteArrays
    }

}
