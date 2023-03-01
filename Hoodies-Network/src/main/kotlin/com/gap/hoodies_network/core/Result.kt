package com.gap.hoodies_network.core

import com.gap.hoodies_network.header.Header

/**
 * Result class to handle Success and Failure of requests
 */
sealed class Result<out T, out E>

data class Success<out T>(val value: T, val headers: List<Header>? = null, val url: String? = null) : Result<T, Nothing>()
data class Failure<out E>(val reason: E) : Result<Nothing, E>()

/**
 * Call a function and wrap the result in a Result, catching any Exception and returning it as Err value.
 */
fun <T> resultFrom(block: () -> T): Result<T, Exception> =
    try {
        Success(block())
    } catch (x: Exception) {
        Failure(x)
    }

/**
 * Map a function over the _value_ of a successful Result.
 */
fun <T, T2, E> Result<T, E>.map(f: (T) -> T2): Result<T2, E> =
    flatMap { value -> Success(f(value)) }

/**
 * Flat-map a function over the _value_ of a successful Result.
 */
fun <T, T2, E> Result<T, E>.flatMap(f: (T) -> Result<T2, E>): Result<T2, E> = when (this) {
    is Success<T> -> f(value)
    is Failure<E> -> this
}

/**
 * Flat-map a function over the _reason_ of a unsuccessful Result.
 */
fun <T, E, E2> Result<T, E>.flatMapFailure(f: (E) -> Result<T, E2>): Result<T, E2> =
    when (this) {
        is Success<T> -> this
        is Failure<E> -> f(reason)
    }

/**
 * Map a function over the _reason_ of an unsuccessful Result.
 */
fun <T, E, E2> Result<T, E>.mapFailure(f: (E) -> E2): Result<T, E2> =
    flatMapFailure { reason -> Failure(f(reason)) }

/**
 * Unwrap a Result in which both the success and failure values have the same type, returning a plain value.
 */
fun <T> Result<T, T>.get() = when (this) {
    is Success<T> -> value
    is Failure<T> -> reason
}

/**
 * Unwrap a Result, by returning the success value or calling _block_ on failure to abort from the current function.
 */
fun <T, E> Result<T, E>.onFailure(block: (Failure<E>) -> T): T = when (this) {
    is Success<T> -> value
    is Failure<E> -> block(this)
}

/**
 * Unwrap a Result by returning the success value or calling _failureToValue_ to mapping the failure reason to a plain value.
 */
fun <S, T : S, U : S, E> Result<T, E>.recover(errorToValue: (E) -> U): S =
    mapFailure(errorToValue).get()

/**
 * Perform a side effect with the success value.
 */
fun <T, E> Result<T, E>.peek(f: (T) -> Unit) = apply { if (this is Success<T>) f(value) }

/**
 * Perform a side effect with the failure reason.
 */
fun <T, E> Result<T, E>.peekFailure(f: (E) -> Unit) =
    apply { if (this is Failure<E>) f(reason) }
