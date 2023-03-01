@file:Suppress("UNCHECKED_CAST")

package com.gap.hoodies_network.core

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.gap.hoodies_network.cache.EncryptedCache
import com.gap.hoodies_network.cache.configuration.CacheConfiguration
import com.gap.hoodies_network.cache.configuration.CacheDisabled
import com.gap.hoodies_network.cache.configuration.CacheEnabled
import com.gap.hoodies_network.config.*
import com.gap.hoodies_network.config.UrlResolver.Companion.resolveUrl
import com.gap.hoodies_network.config.UrlResolver.Companion.validateUrl
import com.gap.hoodies_network.connection.queue.RequestQueue
import com.gap.hoodies_network.interceptor.Interceptor
import com.gap.hoodies_network.request.*
import com.gap.hoodies_network.request.json.JsonArrayRequest
import com.gap.hoodies_network.request.json.JsonObjectRequest
import com.gap.hoodies_network.request.query.UrlQueryParamEncodedRequest
import com.gap.hoodies_network.request.query.UrlQueryParamRequest
import com.gap.hoodies_network.request.CancellableMutableRequest
import com.gap.hoodies_network.request.RetryableCancellableMutableRequest
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.net.SocketException
import java.net.SocketTimeoutException
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.coroutines.resume


class HoodiesNetworkClientNonInlined(
    builder: HoodiesNetworkClient.Builder
) {
    private val requestQueue = RequestQueue.instance
    private val retryRequests = LinkedHashMap<String, Request<Any>>()
    private val retryAttempts = LinkedHashMap<String, Int>()

    @PublishedApi
    internal val cookieManager = builder.cookieManager

    @PublishedApi
    internal val baseUrl = builder.baseUrl

    @PublishedApi
    internal val maxRetryLimit = builder.maxRetryAttempts

    @PublishedApi
    internal val defaultHeaders: HashMap<String, String> = builder.defaultHeaders

    @PublishedApi
    internal val interceptors: List<Interceptor> = builder.interceptors

    @PublishedApi
    internal val encryptionDecryptionInterceptor = builder.encryptionDecryptionInterceptor

    @PublishedApi
    internal val retryOnConnectionFailure: Boolean = builder.retryOnConnectionFailure

    @PublishedApi
    internal val gson = Gson()

    /**
     * GET method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun get(
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendRequest(
            api,
            HoodiesNetworkClient.HttpMethod.GET,
            additionalHeaders = handleAdditionalHeaderEncryption(additionalHeaders),
            resultType = resultType,
            cacheConfiguration = cacheConfiguration
        )

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
          return sendRequestHtml(
            api,
            HoodiesNetworkClient.HttpMethod.GET,
            additionalHeaders = handleAdditionalHeaderEncryption(additionalHeaders),
            cacheConfiguration = cacheConfiguration
        ) as Result<String, HoodiesNetworkError>
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
    suspend fun getImage(
        api: String,
        additionalHeaders: HashMap<String, String>?,
        maxWidth: Int,
        maxHeight: Int,
        scaleType: ImageView.ScaleType,
        config: Bitmap.Config,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<Bitmap?, HoodiesNetworkError> {
        return sendImageRequest(
            api,
            maxWidth,
            maxHeight,
            scaleType,
            config,
            handleAdditionalHeaderEncryption(additionalHeaders),
            cacheConfiguration = cacheConfiguration
        ) as Result<Bitmap?, HoodiesNetworkError>
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
    suspend fun postMultipartFiles(
        api: String,
        files: List<File>,
        additionalHeaders: HashMap<String, String>? = null,
        multipartBoundary: String,
        resultType: Type,
    ): Result<*, HoodiesNetworkError> {
        return sendFileUploadRequest(
            api,
            files,
            multipartBoundary,
            handleAdditionalHeaderEncryption(additionalHeaders),
            resultType
        ) as Result<File?, HoodiesNetworkError>
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
    suspend fun getUrlQueryParam(
        queryParams: HashMap<String, String>,
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendUrlQueryRequest(
            HoodiesNetworkClient.HttpMethod.GET,
            handleUrlQueryParamEncryption(queryParams),
            api,
            handleAdditionalHeaderEncryption(additionalHeaders),
            resultType,
            cacheConfiguration = cacheConfiguration
        )
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
    suspend fun getUrlQueryParamEncoded(
        queryParams: HashMap<String, String>,
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendUrlQueryParamEncodedRequest(
            HoodiesNetworkClient.HttpMethod.GET,
            handleUrlQueryParamEncryption(queryParams),
            api,
            handleAdditionalHeaderEncryption(additionalHeaders),
            resultType,
            cacheConfiguration = cacheConfiguration
        )
    }

    /**
     * POST method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun post(
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendRequest(
            api,
            HoodiesNetworkClient.HttpMethod.POST,
            additionalHeaders = handleAdditionalHeaderEncryption(additionalHeaders),
            resultType = resultType,
            cacheConfiguration = cacheConfiguration
        )
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
    suspend fun post(
        api: String,
        requestBody: Any,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendRequest(
            api,
            HoodiesNetworkClient.HttpMethod.POST,
            requestBody,
            handleAdditionalHeaderEncryption(additionalHeaders),
            resultType,
            cacheConfiguration = cacheConfiguration
        )
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
    suspend fun post(
        api: String,
        requestBody: JSONObject,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendJsonObjectRequest(
            api,
            HoodiesNetworkClient.HttpMethod.POST,
            requestBody,
            handleAdditionalHeaderEncryption(additionalHeaders),
            resultType,
            cacheConfiguration = cacheConfiguration
        )
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
    suspend fun post(
        api: String,
        requestBody: JSONArray,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendJsonArrayRequest(
            api,
            HoodiesNetworkClient.HttpMethod.POST,
            requestBody,
            handleAdditionalHeaderEncryption(additionalHeaders),
            resultType,
            cacheConfiguration = cacheConfiguration
        )
    }

    /**
     * PATCH method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun patch(
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendRequest(
            api,
            HoodiesNetworkClient.HttpMethod.PATCH,
            additionalHeaders = handleAdditionalHeaderEncryption(additionalHeaders),
            resultType = resultType,
            cacheConfiguration = cacheConfiguration
        )
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
    suspend fun patch(
        api: String,
        requestBody: Any,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendRequest(
            api,
            HoodiesNetworkClient.HttpMethod.PATCH,
            requestBody,
            handleAdditionalHeaderEncryption(additionalHeaders),
            resultType,
            cacheConfiguration = cacheConfiguration
        )
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
    suspend fun patch(
        api: String,
        requestBody: JSONObject,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
       return sendJsonObjectRequest(
            api,
            HoodiesNetworkClient.HttpMethod.PATCH,
            requestBody,
            handleAdditionalHeaderEncryption(additionalHeaders),
            resultType,
            cacheConfiguration = cacheConfiguration
        )
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
    suspend fun patch(
        api: String,
        requestBody: JSONArray,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendJsonArrayRequest(
            api,
            HoodiesNetworkClient.HttpMethod.PATCH,
            requestBody,
            handleAdditionalHeaderEncryption(additionalHeaders),
            resultType,
            cacheConfiguration = cacheConfiguration
        )
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
    suspend fun put(
        api: String,
        requestBody: Any,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendRequest(
            api,
            HoodiesNetworkClient.HttpMethod.PUT,
            requestBody,
            handleAdditionalHeaderEncryption(additionalHeaders),
            resultType,
            cacheConfiguration = cacheConfiguration
        )
    }

    /**
     * DELETE method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun delete(
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<*, HoodiesNetworkError> {
        return sendRequest(
            api,
            HoodiesNetworkClient.HttpMethod.DELETE,
            additionalHeaders = handleAdditionalHeaderEncryption(additionalHeaders),
            resultType = resultType,
            cacheConfiguration = cacheConfiguration
        )
    }

    /**
     * This function is for internal purposes.
     * Preferable to use functions below instead.
     * @see get
     * @see post
     * @see put
     * @see delete
     *
     * This function send a given request, handle response or error to then be stored in Success or Failure according to result type.
     * @param resultType, class type that matches up with Json response to be deserialized. Use {@link Unit} if you expect an empty response.
     *
     * @param api, endpoint path without slash.
     * @param method, a http method name according to {@link HttpMethod}.
     * @param requestBody, class instance BODY type to be serialized to Json.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */

    @PublishedApi
    internal suspend fun sendRequest(
        api: String,
        method: HoodiesNetworkClient.HttpMethod,
        requestBody: Any? = null,
        additionalHeaders: HashMap<String, String>?,
        resultType: Type,
        cacheConfiguration: CacheConfiguration
    ): Result<*, HoodiesNetworkError> = suspendCancellableCoroutine { continuation ->
        val identifier = System.currentTimeMillis().toString()

        val request =
            getRequest(
                identifier,
                api,
                method,
                requestBody,
                additionalHeaders,
                continuation,
                resultType
            )

        sendRequest(identifier, request as Request<Any>, continuation, cacheConfiguration)
    }

    @PublishedApi
    internal suspend fun sendRequestHtml(
        api: String,
        method: HoodiesNetworkClient.HttpMethod,
        requestBody: Any? = null,
        additionalHeaders: HashMap<String, String>?,
        cacheConfiguration: CacheConfiguration
    ): Result<*, HoodiesNetworkError> = suspendCancellableCoroutine { continuation ->
        val identifier = System.currentTimeMillis().toString()
        val request =
            getRequestHtml(identifier, api, method, requestBody, additionalHeaders, continuation)

        sendRequest(identifier, request as Request<Any>, continuation, cacheConfiguration)
    }

    @PublishedApi
    internal suspend fun sendImageRequest(
        api: String,
        maxWidth: Int,
        maxHeight: Int,
        scaleType: ImageView.ScaleType,
        config: Bitmap.Config,
        additionalHeaders: HashMap<String, String>? = null,
        cacheConfiguration: CacheConfiguration
    ): Result<*, HoodiesNetworkError> = suspendCancellableCoroutine { continuation ->
        val identifier = System.currentTimeMillis().toString()
        val request =
            getImageRequest(
                api,
                maxWidth,
                maxHeight,
                scaleType,
                config,
                additionalHeaders,
                continuation
            )

        sendRequest(identifier, request as Request<Any>, continuation, cacheConfiguration)
    }

    @PublishedApi
    internal suspend fun sendFileUploadRequest(
        api: String,
        files: List<File>,
        multipartBoundary: String,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
    ): Result<*, HoodiesNetworkError> = suspendCancellableCoroutine { continuation ->
        val identifier = System.currentTimeMillis().toString()
        val request =
            getFileUploadRequest(
                api,
                identifier,
                files,
                multipartBoundary,
                additionalHeaders,
                continuation,
                resultType
            )

        sendRequest(identifier, request as Request<Any>, continuation, CacheDisabled())
    }

    @PublishedApi
    internal suspend fun sendJsonObjectRequest(
        api: String,
        method: HoodiesNetworkClient.HttpMethod,
        requestBody: JSONObject? = null,
        additionalHeaders: HashMap<String, String>?,
        resultType: Type,
        cacheConfiguration: CacheConfiguration
    ): Result<*, HoodiesNetworkError> = suspendCancellableCoroutine { continuation ->
        val identifier = System.currentTimeMillis().toString()
        val request = getJsonObjectRequest(
            identifier,
            api,
            method,
            requestBody,
            additionalHeaders,
            continuation,
            resultType
        )

        sendRequest(identifier, request as Request<Any>, continuation, cacheConfiguration)
    }

    @PublishedApi
    internal suspend fun sendJsonArrayRequest(
        api: String,
        method: HoodiesNetworkClient.HttpMethod,
        requestBody: JSONArray? = null,
        additionalHeaders: HashMap<String, String>?,
        resultType: Type,
        cacheConfiguration: CacheConfiguration
    ): Result<*, HoodiesNetworkError> = suspendCancellableCoroutine { continuation ->
        val identifier = System.currentTimeMillis().toString()
        val request = getJsonArrayRequest(
            identifier,
            api,
            method,
            requestBody,
            additionalHeaders,
            continuation,
            resultType
        )

        sendRequest(identifier, request as Request<Any>, continuation, cacheConfiguration)
    }

    @PublishedApi
    internal suspend fun sendUrlQueryParamEncodedRequest(
        method: HoodiesNetworkClient.HttpMethod,
        queryParams: HashMap<String, String>?,
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration
    ): Result<*, HoodiesNetworkError> = suspendCancellableCoroutine { continuation ->
        val identifier = System.currentTimeMillis().toString()
        val request =
            getRequestUrlQueryParamEncoded(
                api,
                identifier,
                method,
                queryParams,
                additionalHeaders,
                continuation,
                resultType
            )

        sendRequest(identifier, request as Request<Any>, continuation, cacheConfiguration)
    }

    @PublishedApi
    internal suspend fun sendUrlQueryRequest(
        method: HoodiesNetworkClient.HttpMethod,
        queryParams: HashMap<String, String>?,
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        resultType: Type,
        cacheConfiguration: CacheConfiguration
    ): Result<*, HoodiesNetworkError> = suspendCancellableCoroutine { continuation ->
        val identifier = System.currentTimeMillis().toString()
        val request =
            getRequestUrlQueryParam(
                api,
                identifier,
                method,
                queryParams,
                additionalHeaders,
                continuation,
                resultType
            )

        Log.d("sendUrlQueryRequest", request.toString())
        sendRequest(identifier, request as Request<Any>, continuation, cacheConfiguration)
    }


    @PublishedApi
    internal fun sendRequest(
        identifier: String,
        request: Request<Any>,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>?,
        cacheConfiguration: CacheConfiguration
    ) {
        for (interceptor in interceptors) {
            //First, intercept network. User will have option to cancel the request
            interceptor.interceptNetwork(isOnline(interceptor.context), CancellableMutableRequest(request))

            //If the user didn't cancel the request in interceptNetwork, run interceptRequest
            if (!request.requestIsCancelled)
                interceptor.interceptRequest(identifier, CancellableMutableRequest(request))
        }

        //If the request wasn't cancelled inside the interceptors, continue
        if (request.requestIsCancelled && continuation != null) {
            continuation.resume(request.cancellationResult!!)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                //If cache is enabled, set up cache for this operation
                request.cache = EncryptedCache(
                    if (cacheConfiguration is CacheEnabled) {
                        cacheConfiguration
                    } else {
                        null
                    }, encryptionDecryptionInterceptor
                )

                //If data isn't stale and cache isn't empty, get cached data and return
                if (cacheConfiguration is CacheEnabled && !request.cache.isDataStale(request))
                 {
                    request.cache.getCachedData(request)
                    return@launch
                }

                //If we didn't get cached data and return, enqueue network request
                requestQueue?.enqueue(request)
                retryRequests[identifier] = request
            }
        }
    }

    @PublishedApi
    internal fun getRequest(
        identifier: String,
        api: String,
        method: HoodiesNetworkClient.HttpMethod,
        requestBody: Any? = null,
        additionalHeaders: HashMap<String, String>?,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        resultType: Type
    ): StringRequest {
        val request: StringRequest
        val headers = getRequestHeaders().toMutableMap()

        if (additionalHeaders != null && additionalHeaders.isNotEmpty()) {
            val contentType = additionalHeaders[CONTENT_TYPE_KEY]
            request = if (contentType == FORM_URL_ENCODED) {
                getRequestFormUrlEncoded(
                    identifier,
                    api,
                    method,
                    requestBody,
                    additionalHeaders,
                    continuation,
                    resultType
                )
            } else {
                getRequestRegular(
                    identifier,
                    api,
                    method,
                    requestBody,
                    additionalHeaders,
                    continuation,
                    resultType
                )
            }
            headers.putAll(additionalHeaders)

        } else {
            request = getRequestRegular(
                identifier,
                api,
                method,
                requestBody,
                additionalHeaders,
                continuation,
                resultType
            )
        }
        Log.d("headers", headers.toString())
        request.setRequestHeaders(headers)
        return request
    }

    @PublishedApi
    internal fun getRequestHtml(
        identifier: String,
        api: String,
        method: HoodiesNetworkClient.HttpMethod,
        requestBody: Any? = null,
        additionalHeaders: HashMap<String, String>?,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>
    ): StringRequest {
        val headers = getRequestHeaders().toMutableMap()
        if (additionalHeaders != null && additionalHeaders.isNotEmpty()) {
            headers.putAll(additionalHeaders)
        }
        val request: StringRequest = getRegularRequestHtml(
            api,
            identifier,
            method,
            requestBody,
            additionalHeaders,
            continuation
        )
        request.setRequestHeaders(headers)
        return request
    }

    @PublishedApi
    internal fun getRequestFormUrlEncoded(
        identifier: String,
        api: String,
        method: HoodiesNetworkClient.HttpMethod,
        requestBody: Any?,
        additionalHeaders: HashMap<String, String>? = null,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        resultType: Type,
    ): StringRequest {
        val body: Map<String, String>? =
            if (requestBody != null && (requestBody is HashMap<*, *>)) {
                requestBody as Map<String, String>
            } else {
                null
            }
        val headers = getRequestHeaders().toMutableMap()
        val request: StringRequest
        if (additionalHeaders != null && additionalHeaders.isNotEmpty()) {
            headers.putAll(additionalHeaders)
        }

        request = FormUrlEncodedRequest(
            validateUrl(baseUrl).plus(api),
            method.value,
            body,
            { successCallback(identifier, gson, continuation, resultType).invoke(it) },
            { failureCallback(identifier, continuation).invoke(it); },
            EncryptedCache(),
            cookieManager
        )
        request.setRequestHeaders(headers)
        return request
    }

    @PublishedApi
    internal fun getRequestUrlQueryParam(
        api: String,
        identifier: String,
        method: HoodiesNetworkClient.HttpMethod,
        queryParams: HashMap<String, String>?,
        additionalHeaders: HashMap<String, String>? = null,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        resultType: Type
    ): StringRequest {

        return getStringRequest(
            api,
            identifier,
            method,
            queryParams,
            additionalHeaders,
            continuation,
            resultType
        )
    }

    @PublishedApi
    internal fun getRequestUrlQueryParamEncoded(
        api: String,
        identifier: String,
        method: HoodiesNetworkClient.HttpMethod,
        queryParams: HashMap<String, String>?,
        additionalHeaders: HashMap<String, String>? = null,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        resultType: Type
    ): StringRequest {

        return getStringRequestEncoded(
            api,
            identifier,
            method,
            queryParams,
            additionalHeaders,
            continuation,
            resultType
        )
    }

    @PublishedApi
    internal fun getRequestRegular(
        identifier: String,
        api: String,
        method: HoodiesNetworkClient.HttpMethod,
        requestBody: Any? = null,
        additionalHeaders: HashMap<String, String>? = null,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        resultType: Type
    ): StringRequest {
        val body: String = if (requestBody != null) gson.toJson(requestBody) else ""
        Log.d("requestBody", body)
        Log.d("requestUrl", validateUrl(baseUrl).plus(api))

        val headers = getRequestHeaders().toMutableMap()

        if (additionalHeaders != null && additionalHeaders.isNotEmpty()) {
            headers.putAll(additionalHeaders)
        }

        val request = StringRequest(
            validateUrl(baseUrl).plus(api),
            method.value,
            body,
            { successCallback(identifier, gson, continuation, resultType).invoke(it) },
            { failureCallback(identifier, continuation).invoke(it); },
            EncryptedCache(),
            cookieManager
        )
        request.setRequestHeaders(headers)
        return request
    }

    @PublishedApi
    internal fun getRegularRequestHtml(
        api: String,
        identifier: String,
        method: HoodiesNetworkClient.HttpMethod,
        requestBody: Any? = null,
        additionalHeaders: HashMap<String, String>?,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
    ): StringRequest {
        val headers = getRequestHeaders().toMutableMap()
        val body: String = requestBody.toString()
        Log.d("requestBodyHtml", body)
        Log.d("rbody", validateUrl(baseUrl).plus(api))

        val request = StringRequest(
            validateUrl(baseUrl).plus(api),
            method.value,
            body,
            { successCallbackHtml(identifier, continuation).invoke(it) },
            { failureCallback(identifier, continuation).invoke(it); },
            EncryptedCache(),
            cookieManager
        )

        if (additionalHeaders != null && additionalHeaders.isNotEmpty()) {
            headers.putAll(additionalHeaders)
        }
        request.setRequestHeaders(headers)
        return request
    }

    @PublishedApi
    internal fun getImageRequest(
        api: String,
        maxWidth: Int,
        maxHeight: Int,
        scaleType: ImageView.ScaleType,
        config: Bitmap.Config,
        additionalHeaders: HashMap<String, String>? = null,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>
    ): ImageRequest {
        val headers = getRequestHeaders().toMutableMap()
        val request: ImageRequest
        val identifier = System.currentTimeMillis().toString()
        Log.d("requestUrl", validateUrl(baseUrl).plus(api))
        val listener = object : Response.BitmapResponseListener {
            override fun onResponse(bitmap: Bitmap?) {
                successCallbackImage(identifier, continuation).invoke(bitmap)
            }

            override fun onError(response: HoodiesNetworkError) {
                failureCallback(identifier, continuation).invoke(response)
            }
        }

        if (additionalHeaders != null && additionalHeaders.isNotEmpty()) {
            headers.putAll(additionalHeaders)
        }
        request = ImageRequest(
            validateUrl(baseUrl).plus(api),
            maxWidth,
            maxHeight,
            scaleType,
            config,
            listener,
            { failureCallback(identifier, continuation).invoke(it); },
            EncryptedCache(),
            cookieManager
        )
        request.setRequestHeaders(headers)
        return request
    }

    @PublishedApi
    internal fun getFileUploadRequest(
        api: String,
        identifier: String,
        files: List<File>,
        multipartBoundary: String,
        additionalHeaders: HashMap<String, String>? = null,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        resultType: Type,
    ): FileUploadRequest {

        val headers = getRequestHeaders().toMutableMap()

        //Add multipart header with boundary
        headers["Content-Type"] = "multipart/form-data; boundary=$multipartBoundary"

        Log.d("requestUrl", validateUrl(baseUrl).plus(api))

        if (additionalHeaders != null && additionalHeaders.isNotEmpty()) {
            headers.putAll(additionalHeaders)
        }
        val request = FileUploadRequest(
            validateUrl(baseUrl).plus(api),
            files,
            multipartBoundary,
            HoodiesNetworkClient.HttpMethod.POST,
            { successCallback(identifier, gson, continuation, resultType).invoke(it) },
            { failureCallback(identifier, continuation).invoke(it); },
            cookieManager
        )

        request.setRequestHeaders(headers)
        return request
    }

    @PublishedApi
    internal fun getJsonObjectRequest(
        identifier: String,
        api: String,
        method: HoodiesNetworkClient.HttpMethod,
        requestBody: JSONObject? = null,
        additionalHeaders: HashMap<String, String>?,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        resultType: Type
    ): JsonObjectRequest {
        val headers = getRequestHeaders().toMutableMap()
        Log.d("rbody", validateUrl(baseUrl).plus(api))
        val request = JsonObjectRequest(
            validateUrl(baseUrl).plus(api),
            method.value,
            requestBody,
            { successCallback(identifier, gson, continuation, resultType).invoke(it) },
            { failureCallback(identifier, continuation).invoke(it); },
            EncryptedCache(),
            cookieManager
        )
        if (additionalHeaders != null && additionalHeaders.isNotEmpty()) {
            headers.putAll(additionalHeaders)
        }
        request.setRequestHeaders(headers)
        return request
    }

    @PublishedApi
    internal fun getJsonArrayRequest(
        identifier: String,
        api: String,
        method: HoodiesNetworkClient.HttpMethod,
        requestBody: JSONArray? = null,
        additionalHeaders: HashMap<String, String>?,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        resultType: Type
    ): JsonArrayRequest {
        val headers = getRequestHeaders().toMutableMap()
        Log.d("rbody", validateUrl(baseUrl).plus(api))
        val request = JsonArrayRequest(
            validateUrl(baseUrl).plus(api),
            method.value,
            requestBody,
            { successCallback(identifier, gson, continuation, resultType).invoke(it) },
            { failureCallback(identifier, continuation).invoke(it); },
            EncryptedCache(),
            cookieManager
        )
        if (additionalHeaders != null && additionalHeaders.isNotEmpty()) {
            headers.putAll(additionalHeaders)
        }
        request.setRequestHeaders(headers)
        return request
    }

    @PublishedApi
    internal fun successCallback(
        identifier: String,
        gson: Gson,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        resultType: Type
    ): (Response<Any>?) -> Unit {
        return {
            try {
                Log.d("success response", it?.result.toString())
                if (resultType.typeName == "kotlin.Unit") {
                    deliverSuccess(identifier, Unit, continuation, it)
                } else if (resultType.typeName == "java.lang.Object" || resultType.typeName == "java.lang.String") {
                    deliverSuccess(identifier, it?.result.toString(), continuation, it)
                } else {
                    val objectType: Any = gson.fromJson(
                        it?.result.toString(),
                        resultType
                    )

                    deliverSuccess(identifier, objectType, continuation, it)
                }
            } catch (error: JsonSyntaxException) {
                Log.d("error", it.toString())
                safeResume(
                    identifier,
                    continuation,
                    Failure(HoodiesNetworkError(error.message, JSON_SYNTAX_ERROR_CODE))
                )
            } catch (error: Exception) {
                Log.d("error", it.toString())
                safeResume(
                    identifier,
                    continuation,
                    Failure(HoodiesNetworkError(error.message, EXCEPTION_ERROR_CODE))
                )
            }
        }
    }

    private fun deliverSuccess(
        identifier: String,
        obj: Any,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        response: Response<Any>?
    ) {
        val successObject = Success(
            obj,
            response?.getAllHeaders(),
            response?.url
        )

        for (i in interceptors.indices) {
            interceptors[i].interceptResponse(successObject, retryRequests[identifier])
        }

        safeResume(identifier, continuation, successObject)
    }

    @PublishedApi
    internal fun successCallbackImage(
        identifier: String,
        continuation: CancellableContinuation<Result<Bitmap?, HoodiesNetworkError>>
    ): (Bitmap?) -> Unit {
        return {
            try {
                for (i in interceptors.indices) {
                    interceptors[i].interceptResponse(
                        Success(it, null, null),
                        retryRequests[identifier])
                }

                safeResume(
                    identifier,
                    continuation as CancellableContinuation<Result<*, HoodiesNetworkError>>,
                    Success(it, null, null)
                )
            } catch (error: Exception) {
                safeResume(
                    identifier,
                    continuation as CancellableContinuation<Result<*, HoodiesNetworkError>>,
                    Failure(HoodiesNetworkError(error.message, EXCEPTION_ERROR_CODE))
                )
            }
        }
    }

    @PublishedApi
    internal fun successCallbackHtml(
        identifier: String,
        continuation: CancellableContinuation<Result<String, HoodiesNetworkError>>
    ): (Response<Any>?) -> Unit {
        return {
            try {
                Log.d("responseBody", it?.result.toString())
                for (i in interceptors.indices) {
                    interceptors[i].interceptResponse(
                        Success(
                            it?.result.toString(),
                            it?.getAllHeaders(),
                            it?.url
                        ),
                        retryRequests[identifier]
                    )
                }

                safeResume(
                    identifier,
                    continuation as CancellableContinuation<Result<*, HoodiesNetworkError>>,
                    Success(it?.result.toString(), it?.getAllHeaders(), it?.url)
                )
            } catch (error: Exception) {
                safeResume(
                    identifier,
                    continuation as CancellableContinuation<Result<*, HoodiesNetworkError>>,
                    Failure(HoodiesNetworkError(error.message, EXCEPTION_ERROR_CODE))
                )
            }
        }
    }


    @PublishedApi
    internal fun safeResume(
        identifier: String,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        result: Result<*, HoodiesNetworkError>
    ) {
        removeRequestFromQueue(identifier)

        if (continuation.isCancelled || continuation.isCompleted)
            return

        continuation.resume(result)
    }

    @PublishedApi
    internal fun failureCallback(
        identifier: String,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>
    ): (HoodiesNetworkError) -> Unit = {

            gapError ->
        getFailureCallback(identifier, continuation, gapError)
    }

    @PublishedApi
    internal fun isRetryAttempts(
        identifier: String, retryLimit: Int, error: HoodiesNetworkError,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>
    ) {
        val isTrue = retryAttempts[identifier]!! > retryLimit
        if (isTrue) {
            for (i in interceptors.indices) {
                interceptors[i].interceptError(error, RetryableCancellableMutableRequest(retryRequests[identifier]!!), retryAttempts[identifier]!!)
            }

            if (retryRequests[identifier]?.retryingRequest == true) {
                retryRequests[identifier]?.retryingRequest = false
            } else {
                removeRequestFromQueue(identifier)
                removeRequestFromRetry(identifier)

                if (continuation.isCancelled || continuation.isCompleted)
                    return
                continuation.resume(Failure(error))
            }
        }
    }

    @PublishedApi
    internal fun getStringRequest(
        api: String,
        identifier: String,
        method: HoodiesNetworkClient.HttpMethod,
        queryParams: HashMap<String, String>?,
        additionalHeaders: HashMap<String, String>? = null,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        resultType: Type
    ): StringRequest {
        val headers = getRequestHeaders().toMutableMap()
        val request: StringRequest = UrlQueryParamRequest(
            resolveUrl(baseUrl).plus(api),
            method.value,
            UrlResolver.getProtocol(baseUrl),
            queryParams,
            { successCallback(identifier, gson, continuation, resultType).invoke(it) },
            { failureCallback(identifier, continuation).invoke(it); },
            EncryptedCache(),
            cookieManager
        )

        if (additionalHeaders != null && additionalHeaders.isNotEmpty()) {
            headers.putAll(additionalHeaders)
        }
        request.setRequestHeaders(headers)
        return request
    }

    @PublishedApi
    internal fun getStringRequestEncoded(
        api: String,
        identifier: String,
        method: HoodiesNetworkClient.HttpMethod,
        queryParams: HashMap<String, String>?,
        additionalHeaders: HashMap<String, String>? = null,
        continuation: CancellableContinuation<Result<*, HoodiesNetworkError>>,
        resultType: Type
    ): StringRequest {
        val headers = getRequestHeaders().toMutableMap()
        val request: StringRequest = UrlQueryParamEncodedRequest(
            resolveUrl(baseUrl).plus(api),
            method.value,
            UrlResolver.getProtocol(baseUrl),
            queryParams,
            { successCallback(identifier, gson, continuation, resultType).invoke(it) },
            { failureCallback(identifier, continuation).invoke(it); },
            EncryptedCache(),
            cookieManager
        )

        if (additionalHeaders != null && additionalHeaders.isNotEmpty()) {
            headers.putAll(additionalHeaders)
        }
        request.setRequestHeaders(headers)
        return request
    }

    private fun validateRetryRequest(
        retryOnConnectionFailure: Boolean,
        error: HoodiesNetworkError
    ): Boolean {
        return (retryOnConnectionFailure &&
                (error.cause is SocketException || error.cause is SocketTimeoutException || error.cause is IOException))
    }

    private fun removeRequestFromQueue(identifier: String) =
        synchronized(retryRequests) { retryRequests.remove(identifier) }

    private fun removeRequestFromRetry(identifier: String) =
        synchronized(retryAttempts) { retryAttempts.remove(identifier) }

    @PublishedApi
    internal fun getRequestHeaders(): HashMap<String, String> {
        return defaultHeaders
    }

    @PublishedApi
    internal fun getFailureCallback(
        identifier: String,
        continuation:
        CancellableContinuation<Result<*, HoodiesNetworkError>>,
        error: HoodiesNetworkError
    ) {

        Log.e("fail response", error.message.toString())
        if (validateRetryRequest(retryOnConnectionFailure, error)) {

            val retryLimit = HoodiesNetworkClient.RetryCount.parseToInt(retryCount = maxRetryLimit)
            if (retryLimit > 0) {
                retryRequests[identifier]?.let {
                    requestQueue?.enqueue(it)
                }
            }
            if (!retryAttempts.containsKey(identifier))
                retryAttempts[identifier] = 0

            retryAttempts[identifier] = retryAttempts[identifier]!!.plus(1)
            isRetryAttempts(identifier, retryLimit, error, continuation)


        } else {
            for (i in interceptors.indices) {
                interceptors[i].interceptError(error, RetryableCancellableMutableRequest(retryRequests[identifier]!!), 0)
            }

            if (retryRequests[identifier]?.retryingRequest == true) {
                retryRequests[identifier]?.retryingRequest = false
            } else {
                removeRequestFromQueue(identifier)
                continuation.resume(Failure(error))
            }
        }
    }

    /**
     * Method to encrypt additional headers
     * We do not encrypt the header name, only the value
     * @param additionalHeaders - hashmap of additional headers
     */
    private fun handleAdditionalHeaderEncryption(additionalHeaders: HashMap<String, String>?): HashMap<String, String>? {
        return if (encryptionDecryptionInterceptor != null && !additionalHeaders.isNullOrEmpty()) {
            for (item in additionalHeaders) {
                additionalHeaders[item.key] =
                    encryptionDecryptionInterceptor.encryptAdditionalHeaders(item.value.toByteArray())
                        .decodeToString()
            }

            additionalHeaders
        } else {
            additionalHeaders
        }
    }

    /**
     * Method to encrypt  query params
     * We encrypt both the key and the value
     * @param queryParams- hashmap of query params
     */
    private fun handleUrlQueryParamEncryption(queryParams: HashMap<String, String>): HashMap<String, String> {
        return if (encryptionDecryptionInterceptor != null && queryParams.isNotEmpty()) {
            val outputMap = mutableMapOf<String, String>()
            for (item in queryParams) {
                outputMap[encryptionDecryptionInterceptor.encryptRequest(item.key.toByteArray())
                    .decodeToString()] =
                    encryptionDecryptionInterceptor.encryptRequest(item.value.toByteArray())
                        .decodeToString()
            }

            outputMap as HashMap<String, String>
        } else {
            queryParams
        }
    }
}
