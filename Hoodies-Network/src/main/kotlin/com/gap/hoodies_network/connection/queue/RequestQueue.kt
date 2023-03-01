package com.gap.hoodies_network.connection.queue

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.gap.hoodies_network.connection.BaseNetwork
import com.gap.hoodies_network.connection.Network
import com.gap.hoodies_network.delivery.ResponseDelivery
import com.gap.hoodies_network.delivery.ResponseDeliveryExecutor
import com.gap.hoodies_network.request.Request
import java.util.concurrent.PriorityBlockingQueue
import javax.net.ssl.SSLSocketFactory

/**
 * RequestQueue class handles enqueue and dequeue of requests' queue
 *
 * @param sslHost
 * @param sslSocketFactory
 *
 */
class RequestQueue constructor(sslHost: String?, sslSocketFactory: SSLSocketFactory?) {

    private val mNetworkQueue: PriorityBlockingQueue<Request<Any>> =
        PriorityBlockingQueue<Request<Any>>()

    /** The gapnetworkandroid dispatchers.  */
    private val mQueueDispatchers: Array<QueueHandler?> =
        arrayOfNulls(DEFAULT_NETWORK_THREAD_POOL_SIZE)
    private val mNetwork: Network
    private val mResponseDelivery: ResponseDelivery

    fun enqueue(request: Request<Any>) {
        try {
            mNetworkQueue.add(request)
        } catch (e: Exception) {
            Log.e("exception in enqueue", e.toString())
        }
    }

    fun dequeue(): Request<Any>? {
        return mNetworkQueue.poll()
    }

    fun hasItems(): Boolean {
        return !mNetworkQueue.isEmpty()
    }

    fun size(): Int {
        return mNetworkQueue.size
    }

    /**
     * Starts the dispatchers in this queue
     */
    private fun startDispatchers() {
        /** If any currently dispatchers are running, stop them */
        stopDispatchers()

        /**create n/w dispatchers up to the pool size */
        for (i in mQueueDispatchers.indices) {
            val apiManager = NetworkHandler(mResponseDelivery)
            val networkDispatcher = QueueHandler(mNetworkQueue, apiManager, mNetwork)
            mQueueDispatchers[i] = networkDispatcher
            networkDispatcher.start()
        }
    }

    private fun stopDispatchers() {
        for (mQueueDispatcher in mQueueDispatchers) {
            mQueueDispatcher?.quit()
        }
    }

    companion object {
        private var requestQueue: RequestQueue? = null
        private const val DEFAULT_NETWORK_THREAD_POOL_SIZE = 4
        val instance: RequestQueue?
            get() {
                synchronized(RequestQueue::class.java) {
                    if (requestQueue == null) {
                        requestQueue = RequestQueue(null, null)
                    }
                    return requestQueue
                }
            }

        fun getInstance(sslHost: String?, sslSocketFactory: SSLSocketFactory?): RequestQueue? {
            synchronized(RequestQueue::class.java) {
                if (requestQueue == null) {
                    requestQueue = RequestQueue(sslHost, sslSocketFactory)
                }
                return requestQueue
            }
        }
    }

    init {
        mNetwork = BaseNetwork(sslHost, sslSocketFactory)
        mResponseDelivery = ResponseDeliveryExecutor(Handler(Looper.getMainLooper()))
        startDispatchers()
    }
}
