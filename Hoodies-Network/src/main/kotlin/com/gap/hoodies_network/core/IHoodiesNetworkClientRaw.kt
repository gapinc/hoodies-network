package com.gap.hoodies_network.core

import android.graphics.Bitmap
import android.widget.ImageView
import com.gap.hoodies_network.cache.configuration.CacheConfiguration
import com.gap.hoodies_network.cache.configuration.CacheDisabled
import java.util.*

interface IHoodiesNetworkClientRaw {

    /**
     * GET method to send String as a Request body
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun getRaw(
        api: String,
        additionalHeaders: HashMap<String, String>? = null
    ): Result<String, HoodiesNetworkError>

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
     *  @return [Success] in case of a positive response, otherwise [Failure]
     * */
    suspend fun getRawUrlQueryParam(
        queryParams: HashMap<String, String>,
        api: String,
        additionalHeaders: HashMap<String, String>? = null
    ): Result<String, HoodiesNetworkError>

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
    suspend fun getRawUrlQueryParamEncoded(
        queryParams: HashMap<String, String>,
        api: String,
        additionalHeaders: HashMap<String, String>? = null
    ): Result<String, HoodiesNetworkError>

    /**
     * POST method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun postRaw(
        api: String,
        additionalHeaders: HashMap<String, String>? = null
    ): Result<String, HoodiesNetworkError>

    /**
     * POST method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun postRaw(
        api: String,
        requestBody: String,
        additionalHeaders: HashMap<String, String>? = null
    ): Result<String, HoodiesNetworkError>

    /**
     * POST method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun postRaw(
        api: String,
        requestBody: HashMap<String, String>,
        additionalHeaders: HashMap<String, String>
    ): Result<String, HoodiesNetworkError>

    /**
     * PATCH method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun patchRaw(
        api: String,
        additionalHeaders: HashMap<String, String>? = null
    ): Result<String, HoodiesNetworkError>

    /**
     * PATCH method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun patchRaw(
        api: String,
        requestBody: String,
        additionalHeaders: HashMap<String, String>? = null
    ): Result<String, HoodiesNetworkError>

    /**
     * PATCH method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun patchRaw(
        api: String,
        requestBody: HashMap<String, String>,
        additionalHeaders: HashMap<String, String>
    ): Result<String, HoodiesNetworkError>

    /**
     * PUT method
     *
     * @param api
     * @param requestBody
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun putRaw(
        api: String,
        requestBody: String,
        additionalHeaders: HashMap<String, String>? = null
    ): Result<String, HoodiesNetworkError>

    /**
     * DELETE method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun deleteRaw(
        api: String,
        additionalHeaders: HashMap<String, String>? = null
    ): Result<String, HoodiesNetworkError>

    /**
     * GET method
     *
     * @param api the path to the endpoint to be called.
     * @param additionalHeaders additional headers needed for the specific requests besides the [defaultHeaders].
     * @param maxWidth the max width of the image.
     * @param maxHeight the max height of the image.
     * @param scaleType the ImageView.ScaleType for setting the scaleType
     * @param config the Bitmap.Config
     *
     * @return [Success] in case of a positive response, otherwise [Failure]
     */
    suspend fun getImage(
        api: String,
        additionalHeaders: HashMap<String, String>? = null,
        maxWidth: Int,
        maxHeight: Int,
        scaleType: ImageView.ScaleType,
        config: Bitmap.Config,
        cacheConfiguration: CacheConfiguration = CacheDisabled()
    ): Result<Bitmap?, HoodiesNetworkError>
}
