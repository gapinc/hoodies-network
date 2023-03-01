package com.gap.hoodies_network.testObjects

import android.content.Context
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.Result
import com.gap.hoodies_network.interceptor.Interceptor
import com.gap.hoodies_network.request.Request
import com.gap.hoodies_network.request.CancellableMutableRequest
import com.gap.hoodies_network.request.RetryableCancellableMutableRequest

class testInterceptor(context: Context) : Interceptor( context) {
    override fun interceptRequest(identifier: String, cancellableMutableRequest: CancellableMutableRequest) {

    }

    override fun interceptError(error: HoodiesNetworkError, retryableCancellableMutableRequest: RetryableCancellableMutableRequest, autoRetryAttempts: Int) {

    }

    override fun interceptNetwork(isOnline: Boolean, cancellableMutableRequest: CancellableMutableRequest) {

    }

    override fun interceptResponse(result: Result<*, HoodiesNetworkError>, request: Request<Any>?) {

    }

}