package com.gap.hoodies_network.request

import com.gap.hoodies_network.core.HoodiesNetworkError

open class CancellableMutableRequest(
    val request: Request<Any>
) {
    fun cancelRequest(result: com.gap.hoodies_network.core.Result<*, HoodiesNetworkError>) {
        request.requestIsCancelled = true
        request.cancellationResult = result
    }
}