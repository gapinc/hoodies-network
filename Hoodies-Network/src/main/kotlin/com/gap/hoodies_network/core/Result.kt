package com.gap.hoodies_network.core

import com.gap.hoodies_network.header.Header

/**
 * Result class to handle Success and Failure of requests
 */
sealed class Result<out T, out E>

data class Success<out T>(val value: T, val headers: List<Header>? = null, val url: String? = null) : Result<T, Nothing>()
data class Failure<out E>(val reason: E) : Result<Nothing, E>()