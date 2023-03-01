package com.gap.hoodies_network.interceptor

import android.content.Context
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.Result
import com.gap.hoodies_network.request.Request
import com.gap.hoodies_network.request.CancellableMutableRequest
import com.gap.hoodies_network.request.RetryableCancellableMutableRequest

//Context is required here for interceptNetwork

open class Interceptor(val context: Context?) {

    open fun interceptRequest(identifier: String, cancellableMutableRequest: CancellableMutableRequest) {
        //Stub
    }

    open fun interceptError(error: HoodiesNetworkError, retryableCancellableMutableRequest: RetryableCancellableMutableRequest, autoRetryAttempts: Int) {
        //Stub
    }

    open fun interceptNetwork(isOnline: Boolean, cancellableMutableRequest: CancellableMutableRequest) {
        //Stub
    }

    open fun interceptResponse(result: Result<*, HoodiesNetworkError>, request: Request<Any>?) {
        //Stub
    }

}