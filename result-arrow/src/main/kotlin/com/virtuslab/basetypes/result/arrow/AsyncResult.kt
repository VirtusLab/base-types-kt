package com.virtuslab.basetypes.result.arrow

import arrow.fx.IO
import arrow.fx.handleError
import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.Result.Failure
import com.virtuslab.basetypes.result.Result.Success
import com.virtuslab.basetypes.result.flatMap
import com.virtuslab.basetypes.result.map
import com.virtuslab.basetypes.result.mapError

typealias AsyncResult<T, E> = IO<Result<T, E>>

fun <S : Any, E : Any, S2 : Any> AsyncResult<S, E>.mapSuccess(mapper: (S) -> S2): AsyncResult<S2, E> =
    this.map {
        it.map(mapper)
    }

fun <S : Any, E : Any, E2 : Any> AsyncResult<S, E>.mapFailure(mapper: (E) -> E2): AsyncResult<S, E2> =
    this.map {
        it.mapError(mapper)
    }

fun <S : Any, E : Any, S2 : Any> AsyncResult<S, E>.flatMapResult(mapper: (S) -> Result<S2, E>): AsyncResult<S2, E> =
    this.map {
        it.flatMap(mapper)
    }

fun <S : Any, E : Any, S2 : Any> AsyncResult<S, E>.flatMapSuccess(mapper: (S) -> AsyncResult<S2, E>): AsyncResult<S2, E> =
    this.flatMap { result1 ->
        when (result1) {
            is Success -> mapper(result1.value)
            is Failure -> Failure(result1.error).toAsync()
        }
    }

fun <S : Any, E : Any, S2 : Any> AsyncResult<S, E>.flatMapSuccess(mapper: (S) -> IO<S2>, errorMapper: (Throwable) -> E): AsyncResult<S2, E> =
    this.flatMap { result1 ->
        when (result1) {
            is Success -> mapper(result1.value).liftResult(errorMapper)
            is Failure -> Failure(result1.error).toAsync()
        }
    }

fun <S : Any, E : Any, S2 : Any> Result<S, E>.liftMap(mapper: (S) -> AsyncResult<S2, E>): AsyncResult<S2, E> =
    when (this) {
        is Success -> mapper(value)
        is Failure -> Failure(error).toAsync()
    }

fun <S : Any, E : Any> S.justAsyncResult(): AsyncResult<S, E> =
    Result.of<S, E> { this }
        .let { IO.just(it) }

fun <S : Any, E : Any> E.failedAsyncResult(): AsyncResult<S, E> =
    Result.error(this)
        .let { IO.just(it) }

fun <S : Any, E : Any> IO<S>.liftResult(errorMapper: (Throwable) -> E): AsyncResult<S, E> =
    this.map { Result.success(it) as Result<S, E> }
        .handleError { Result.error(errorMapper(it)) }

fun <S> S.toAsync(): IO<S> = IO.just(this)

fun <T : Any, E : Any> Result<T, E>.liftAsync(): AsyncResult<T, E> = IO.just(this)