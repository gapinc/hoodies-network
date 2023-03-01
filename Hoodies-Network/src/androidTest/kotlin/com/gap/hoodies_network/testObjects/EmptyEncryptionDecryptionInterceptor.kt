package com.gap.hoodies_network.testObjects

import android.content.Context
import com.gap.hoodies_network.interceptor.EncryptionDecryptionInterceptor

class EmptyEncryptionDecryptionInterceptor(override val context: Context) : EncryptionDecryptionInterceptor {
    override fun encryptRequest(requestBodyOrUrlQueryParamKeyValue: ByteArray): ByteArray {
        return requestBodyOrUrlQueryParamKeyValue
    }

    override fun encryptAdditionalHeaders(additionalHeaderValue: ByteArray): ByteArray {
        return additionalHeaderValue
    }

    override fun decryptResponse(response: ByteArray): ByteArray {
        return response
    }
}