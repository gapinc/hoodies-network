package com.gap.hoodies_network.connection.queue

import com.gap.hoodies_network.connection.Network
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.core.Response
import com.gap.hoodies_network.delivery.ResponseDelivery
import com.gap.hoodies_network.request.Request

/**
 * NetworkHandler class does execute the request
 *
 * @param mDelivery
 *
 */
class NetworkHandler(private val mDelivery: ResponseDelivery) {
    fun executeRequest(request: Request<Any>, network: Network) {
        try {
            // execute network request
            val networkResponse: Response<Any> = network.executeRequest(request)
            request.parseNetworkResponse(networkResponse)?.let {
                mDelivery.postResponse(
                    request,
                    it
                )
            }
        } catch (hoodiesNetworkError: HoodiesNetworkError) {
            mDelivery.postError(request, hoodiesNetworkError)
        }
    }
}
