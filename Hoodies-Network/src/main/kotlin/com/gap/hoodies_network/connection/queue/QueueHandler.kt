package com.gap.hoodies_network.connection.queue

import android.os.Process
import com.gap.hoodies_network.connection.Network
import com.gap.hoodies_network.request.Request
import java.util.concurrent.BlockingQueue

/**
 * QueueHandler class processes Requests in queue
 *
 * @param queue
 * @param networkHandler
 * @param network
 *
 */
class QueueHandler internal constructor(
    queue: BlockingQueue<Request<Any>>,
    networkHandler: NetworkHandler,
    network: Network
) : Thread() {
    // NO SONAR
    private val mQueue: BlockingQueue<Request<Any>> = queue
    private val mNetworkHandler: NetworkHandler = networkHandler
    private val mNetwork: Network = network

    @Volatile
    private var mQuit = false
    override fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
        while (true) {
            try {
                processRequest()
            } catch (e: InterruptedException) {
                if (mQuit) {
                    currentThread().interrupt()
                    return
                }
            }
        }
    }

    @Throws(InterruptedException::class)
    private fun processRequest() {
        val request: Request<Any> = mQueue.take()
        processRequest(request)
    }

    private fun processRequest(request: Request<Any>) {
        mNetworkHandler.executeRequest(request, mNetwork)
    }

    /**
     * dispatcher to quit immediately forcefully. If any requests are still in the queue, they are
     * not guaranteed to be processed.
     */
    fun quit() {
        mQuit = true
        interrupt()
    }
}
