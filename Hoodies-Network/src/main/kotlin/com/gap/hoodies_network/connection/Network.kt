package com.gap.hoodies_network.connection

import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.Response
import com.gap.hoodies_network.request.Request

interface Network {
    @Throws(HoodiesNetworkError::class)
    fun executeRequest(request: Request<Any>): Response<Any>
}
