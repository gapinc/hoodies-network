package com.gap.hoodies_network

import com.gap.hoodies_network.delivery.ResponseDeliveryExecutor
import java.util.concurrent.Executor

class ResponseDeliveryInstant : ResponseDeliveryExecutor(
    Executor { obj: Runnable -> obj.run() }
)
