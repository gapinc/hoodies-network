package com.gap.hoodies_network.core

/**
 * Exception style class encapsulating Gap Network errors
 */
data class HoodiesNetworkError(
    override val message: String?,
    var code : Int,
    override var cause: Throwable? = null
    ) : Exception(message)
