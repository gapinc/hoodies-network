package com.gap.hoodies_network.delivery

import android.os.Handler
import com.gap.hoodies_network.core.Response
import com.gap.hoodies_network.core.HoodiesNetworkError
import com.gap.hoodies_network.request.Request
import java.util.concurrent.Executor


/**
 * ResponseDeliveryExecutor class executes response and error for responseDelivery interface
 */
open class ResponseDeliveryExecutor : ResponseDelivery {
    private val mResponseExecutor: Executor

    /**
     * Creates a new response delivery interface.
     *
     * @param handler [Handler] to post responses on
     */
    constructor(handler: Handler) {
        // Make an Executor that just wraps the handler.
        mResponseExecutor = Executor { command ->
                handler.post(command)
        }
    }

    /**
     * Creates a new response delivery interface, mock able version for testing.
     *
     * @param executor For running delivery tasks
     */
    constructor(executor: Executor) {
        mResponseExecutor = executor
    }

    override fun postResponse(request: Request<Any>, response: Response<Any>) {
        mResponseExecutor.execute(ResponseDeliveryExecutorRunnable(request, response, null, true))
    }

    override fun postError(request: Request<Any>, error: HoodiesNetworkError) {
        mResponseExecutor.execute(ResponseDeliveryExecutorRunnable(request, null, error, false))
    }

    private class ResponseDeliveryExecutorRunnable(
        request: Request<Any>,
        response: Response<Any>?,
        error: HoodiesNetworkError?,
        success: Boolean
    ) :
        Runnable {
        private val mRequest: Request<Any> = request
        private val mResponse: Response<Any>? = response
        private val isSuccess: Boolean = success
        private val hoodiesNetworkError: HoodiesNetworkError? = error
        override fun run() {
            if (isSuccess) {
                mRequest.deliverResponse(mResponse)
            } else {
                hoodiesNetworkError?.let {
                    mRequest.deliverError(it)
                }
            }
        }
    }
}
