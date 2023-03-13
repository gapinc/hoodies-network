@file:Suppress("UNCHECKED_CAST")

package com.gap.hoodies_network.core

import android.graphics.Bitmap
import android.widget.ImageView
import com.gap.hoodies_network.cache.configuration.CacheConfiguration
import com.gap.hoodies_network.cache.configuration.CacheDisabled
import com.gap.hoodies_network.config.*
import com.gap.hoodies_network.cookies.CookieJar
import com.gap.hoodies_network.interceptor.EncryptionDecryptionInterceptor
import com.gap.hoodies_network.interceptor.Interceptor
import com.gap.hoodies_network.request.*
import com.gap.hoodies_network.utils.Generated
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.CookieManager
import java.util.*

@Generated
class HoodiesNetworkClient(
    builder: Builder
) : IHoodiesNetworkClientRaw {

    /**
     * Implementation is in nonInlinedClient so that test coverage can be geenrated using JaCoCo
     */
    val nonInlinedClient = HoodiesNetworkClientNonInlined(builder)

    fun getInterceptors() : List<Interceptor> {
        return nonInlinedClient.interceptors
    }
    /**
     * GET method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified RESULT> get(
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.get(api, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }

    /**
     * GET method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun getHtml(
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.getHtml(api, additionalHeaders, cacheConfiguration)
    }

    /**
     * GET method
     *
     * @param api the path to the endpoint to be called.
     * @param maxWidth the max width of the image.
     * @param maxHeight the max height of the image.
     * @param scaleType the ImageView.ScaleType for setting the scaleType
     * @param config the Bitmap.Config
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    override suspend fun getImage(
        api: String,
        additionalHeaders: HashMap<String, String>?,
        maxWidth: Int,
        maxHeight: Int,
        scaleType: ImageView.ScaleType,
        config: Bitmap.Config,
        cacheConfiguration: CacheConfiguration
    ): Result<Bitmap?, HoodiesNetworkError> {
        return nonInlinedClient.getImage(api, additionalHeaders, maxWidth, maxHeight, scaleType, config, cacheConfiguration)
    }

    /**
     * POST Multipart files
     *
     * @param api the path to the endpoint to be called.
     * @param files the list of files to send
     * @param multipartBoundary you can optionally specify the multipart boundary. By default, it is randomly generated
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified RESULT> postMultipartFiles(
        api: String,
        files: List<File>,
        additionalHeaders: HashMap<String, String>?,
        multipartBoundary: String = UUID.randomUUID().toString()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.postMultipartFiles(api, files, additionalHeaders, multipartBoundary, RESULT::class.java)  as Result<RESULT, HoodiesNetworkError>
    }
    /**
     * HTTP GET method to send URL Query parameters.
     * When you query web resources, you can use either use the HTTP GET method or HTTP POST method.When you query web resources
     * by using HTTP GET, you specify the query parameters in the URI. If the URI is longer than 2000 characters, you must query
     * by using HTTP POST, instead of HTTP GET. You can also use HTTP POST if the URI is less than 2000 characters but still long,
     * or if you want to hide the query parameters so that they are not displayed in the URI.
     * When you query by using HTTP POST, you set the HTTP header Content-Type to application/x-www-form-urlencoded, send the URI
     * without parameters, and specify the query parameters in the HTTP request body( additional headers in GapHTTPClient
     *
     * @param queryParams the HashMap<String, String> with key value pairs of Query Parameters.
     * @param api the String with the endpoint.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     *  @return Response Type Any & [Success] in case of a positive response, otherwise [Failure]
     * */
    suspend inline fun <reified RESULT> getUrlQueryParam(
        queryParams: HashMap<String, String>,
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.getUrlQueryParam(queryParams, api, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }
    /**
     * HTTP GET method to send UTF-8 encoded URL Query parameters
     * When you query by using HTTP POST, you set the HTTP header Content-Type to application/x-www-form-urlencoded, send the URI
     * without URI parameters, and specify the query parameters in the HTTP request body( additional headers in GapHTTPClient)
     *
     * @UseCase Use this method if you want to hide the query parameters so that they are not displayed in the URI
     *
     * @param queryParams the HashMap<String, String> with key value pairs of Query Parameters.
     * @param api the String with the endpoint.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     * @return [Success] in case of a positive response, otherwise [Failure]
     * */
    suspend inline fun <reified RESULT> getUrlQueryParamEncoded(
        queryParams: HashMap<String, String>,
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.getUrlQueryParamEncoded(queryParams, api, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }
    /**
     * POST method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified RESULT> post(
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.post(api, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }
    /**
     * POST method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified BODY, reified RESULT> post(
        api: String,
        requestBody: BODY,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.post(api, requestBody as Any, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }

    /**
     * POST method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified RESULT> post(
        api: String,
        requestBody: JSONObject,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.post(api, requestBody, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }

    /**
     * POST method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified RESULT> post(
        api: String,
        requestBody: JSONArray,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.post(api, requestBody, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }

    /**
     * PATCH method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified RESULT> patch(
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.patch(api, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }
    /**
     * PATCH method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified BODY, reified RESULT> patch(
        api: String,
        requestBody: BODY,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.patch(api, requestBody as Any, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }

    /**
     * PATCH method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified RESULT> patch(
        api: String,
        requestBody: JSONObject,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.patch(api, requestBody, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }

    /**
     * PATCH method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified RESULT> patch(
        api: String,
        requestBody: JSONArray,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.patch(api, requestBody, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }

    /**
     * PUT method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified BODY, reified RESULT> put(
        api: String,
        requestBody: BODY,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.put(api, requestBody as Any, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }

    /**
     * DELETE method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend inline fun <reified RESULT> delete(
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<RESULT, HoodiesNetworkError> {
        return nonInlinedClient.delete(api, additionalHeaders, RESULT::class.java, cacheConfiguration) as Result<RESULT, HoodiesNetworkError>
    }

    @PublishedApi
    internal fun getRequestHeaders(): HashMap<String, String> {
        return nonInlinedClient.getRequestHeaders()
    }

    override suspend fun getRaw(
        api: String,
        additionalHeaders: HashMap<String, String>?
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.get(api, additionalHeaders, String::class.java, CacheDisabled()) as Result<String, HoodiesNetworkError>
    }

    override suspend fun getRawUrlQueryParam(
        queryParams: HashMap<String, String>,
        api: String,
        additionalHeaders: HashMap<String, String>?
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.getUrlQueryParam(queryParams, api, additionalHeaders, String::class.java, CacheDisabled()) as Result<String, HoodiesNetworkError>
    }

    override suspend fun getRawUrlQueryParamEncoded(
        queryParams: HashMap<String, String>,
        api: String,
        additionalHeaders: HashMap<String, String>?
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.getUrlQueryParamEncoded(queryParams, api, additionalHeaders, String::class.java, CacheDisabled()) as Result<String, HoodiesNetworkError>
    }

    override suspend fun postRaw(
        api: String,
        additionalHeaders: HashMap<String, String>?
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.post(api, additionalHeaders, String::class.java, CacheDisabled()) as Result<String, HoodiesNetworkError>
    }

    override suspend fun postRaw(
        api: String,
        requestBody: String,
        additionalHeaders: HashMap<String, String>?
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.post(api, requestBody as Any, additionalHeaders, String::class.java, CacheDisabled()) as Result<String, HoodiesNetworkError>
    }

    override suspend fun postRaw(
        api: String,
        requestBody: HashMap<String, String>,
        additionalHeaders: HashMap<String, String>
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.post(api, requestBody as Any, additionalHeaders, String::class.java, CacheDisabled()) as Result<String, HoodiesNetworkError>
    }

    override suspend fun patchRaw(
        api: String,
        additionalHeaders: HashMap<String, String>?
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.patch(api, additionalHeaders, String::class.java, CacheDisabled()) as Result<String, HoodiesNetworkError>
    }

    override suspend fun patchRaw(
        api: String,
        requestBody: String,
        additionalHeaders: HashMap<String, String>?
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.patch(api, requestBody as Any, additionalHeaders, String::class.java, CacheDisabled()) as Result<String, HoodiesNetworkError>
    }

    override suspend fun patchRaw(
        api: String,
        requestBody: HashMap<String, String>,
        additionalHeaders: HashMap<String, String>
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.patch(api, requestBody as Any, additionalHeaders, String::class.java, CacheDisabled()) as Result<String, HoodiesNetworkError>
    }

    override suspend fun putRaw(
        api: String,
        requestBody: String,
        additionalHeaders: HashMap<String, String>?
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.put(api, requestBody as Any, additionalHeaders, String::class.java, CacheDisabled()) as Result<String, HoodiesNetworkError>
    }

    override suspend fun deleteRaw(
        api: String,
        additionalHeaders: HashMap<String, String>?
    ): Result<String, HoodiesNetworkError> {
        return nonInlinedClient.delete(api, additionalHeaders, String::class.java, CacheDisabled()) as Result<String, HoodiesNetworkError>
    }



    enum class HttpMethod(val value: String) {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        PATCH("PATCH")
    }

    enum class RetryCount {
        RETRY_NEVER,
        RETRY_ONCE,
        RETRY_TWICE,
        RETRY_THRICE,
        RETRY_MAX;

        companion object {
            fun parseToInt(retryCount: RetryCount): Int {
                return when (retryCount) {
                    RETRY_NEVER -> 0
                    RETRY_ONCE -> 1
                    RETRY_TWICE -> 2
                    RETRY_THRICE -> 3
                    RETRY_MAX -> 5
                }
            }
        }

    }

    class Builder {
        internal var baseUrl: String = ""
        internal val defaultHeaders: HashMap<String, String> = HashMap()
        internal val interceptors: MutableList<Interceptor> = mutableListOf()
        internal var encryptionDecryptionInterceptor: EncryptionDecryptionInterceptor? = null
        internal var retryOnConnectionFailure: Boolean = false
        internal var maxRetryAttempts: RetryCount = RetryCount.RETRY_NEVER
        internal var cookieManager: CookieManager? = null

        fun baseUrl(baseUrl: String) = apply {
            this.baseUrl = baseUrl
        }

        fun addInterceptor(interceptor: Interceptor) = apply {
            interceptors += interceptor
        }

        fun addEncryptionDecryptionInterceptor(encryptionDecryptionInterceptor: EncryptionDecryptionInterceptor) = apply {
            this.encryptionDecryptionInterceptor = encryptionDecryptionInterceptor
        }

        fun addHeader(key: String, value: String) = apply {
            defaultHeaders[key] = value
        }

        fun addHeaders(headers: HashMap<String, String>) = apply {
            for (header in headers) {
                defaultHeaders[header.key] = header.value
            }
        }

        fun enableCookiesWithCookieJar(cookieJar: CookieJar) = apply {
            this.cookieManager = cookieJar
        }

        fun retryOnConnectionFailure(
            retryOnConnectionFailure: Boolean,
            maxRetryAttempts: RetryCount
        ) = apply {
            this.retryOnConnectionFailure = retryOnConnectionFailure
            this.maxRetryAttempts = maxRetryAttempts
        }

        fun build(): HoodiesNetworkClient = HoodiesNetworkClient(this)
    }


}
