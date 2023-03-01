package com.gap.hoodies_network.interceptor

import android.content.Context
import java.io.IOException

/**
 * Implement this interface to encrypt requests and responses.
 * If you do not want to perform any encryption for a particular case, just return the input ByteArray
 */
interface EncryptionDecryptionInterceptor {
    val context: Context
    @Throws(IOException::class)

            /**
             * @param requestBodyOrUrlQueryParamKeyValue -  Request body encryption works in 2 ways:
             * 1. GET params (in URL, urlQueryEncoded, etc): This function is called to encrypt every key and value
             * 2. Parameters in body (other requests): This function is called once to encrypt the entire body before the request is sent
             */
    fun encryptRequest(requestBodyOrUrlQueryParamKeyValue: ByteArray) : ByteArray

    /**
     * @param additionalHeaderValue - Called to encrypt every value for additional headers
     */
    fun encryptAdditionalHeaders(additionalHeaderValue: ByteArray) : ByteArray

    /**
     * @param response - The raw response from the server
     * This is called before any response parsing to decrypt any encrypted response
     */
    fun decryptResponse(response: ByteArray) : ByteArray
}