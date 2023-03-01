package com.gap.hoodies_network.request

import com.gap.hoodies_network.connection.queue.RequestQueue

class RetryableCancellableMutableRequest(
    request: Request<Any>
) : CancellableMutableRequest(request) {
    fun retryRequest() {
        request.retryingRequest = true
        RequestQueue.instance?.enqueue(request)
    }
}