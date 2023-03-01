package com.gap.hoodies_network.delivery

import com.gap.hoodies_network.core.Response
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.request.Request

interface ResponseDelivery {
    fun postResponse(request: Request<Any>, response: Response<Any>)

    fun postError(request: Request<Any>, error: HoodiesNetworkError)
}
