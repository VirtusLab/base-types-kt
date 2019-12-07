package com.virtuslab.basetypes.result.arrow

import arrow.fx.IO
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.Result.Failure
import com.virtuslab.basetypes.result.Result.Success
import com.virtuslab.basetypes.result.flatMap

typealias AsyncResult<T, E> = IO<Result<T, E>>

fun <T : Any, E : Exception> Result<T, E>.liftAsync(): AsyncResult<T, E> =
    IO.just(this)


infix fun <T1 : Any, T2 : Any, E : Exception> Result<T1, E>.then(f: (T1) -> Result<T2, E>): Result<T2, E> =
    when (this) {
        is Success -> f(this.value)
        is Failure -> Failure(this.error)
    }

infix fun <T1 : Any, T2 : Any, E : Exception> AsyncResult<T1, E>.thenSync(f: (T1) -> Result<T2, E>): AsyncResult<T2, E> =
    map { it.flatMap(f) }

infix fun <T1 : Any, T2 : Any, E : Exception> AsyncResult<T1, E>.then(f: (T1) -> AsyncResult<T2, E>): AsyncResult<T2, E> =
    this.flatMap { result1 ->
        when (result1) {
            is Success -> f(result1.value)
            is Failure -> Failure(result1.error).liftAsync()
        }
    }

infix fun <T1 : Any, T2 : Any, E : Exception> Result<T1, E>.thenAsync(f: (T1) -> AsyncResult<T2, E>): AsyncResult<T2, E> =
    when (this) {
        is Success -> f(this.value)
        is Failure -> Failure(this.error).liftAsync()
    }

infix fun <T1 : Any, T2 : Any, E : Exception> T1.to(f: (T1) -> Result<T2, E>): Result<T2, E> = Success(this) then f

infix fun <T1 : Any, T2 : Any, E : Exception> T1.toAsyncResult(f: (T1) -> AsyncResult<T2, E>): AsyncResult<T2, E> = Success(this) thenAsync f
