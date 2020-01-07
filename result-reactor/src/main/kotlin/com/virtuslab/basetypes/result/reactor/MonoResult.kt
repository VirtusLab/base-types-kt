package com.virtuslab.basetypes.result.reactor

import com.virtuslab.basetypes.result.Result
import com.virtuslab.basetypes.result.Result.Failure
import com.virtuslab.basetypes.result.Result.Success
import com.virtuslab.basetypes.result.flatMap
import com.virtuslab.basetypes.result.map
import com.virtuslab.basetypes.result.mapError
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

typealias MonoResult<S, E> = Mono<Result<S, E>>

typealias Completable<E> = Mono<Result<Unit, E>>

fun <S : Any, E : Any, S2 : Any> MonoResult<S, E>.mapSuccess(mapper: (S) -> S2): MonoResult<S2, E> =
    this.map {
        it.map(mapper)
    }

fun <S : Any, E : Any, E2 : Any> MonoResult<S, E>.mapFailure(mapper: (E) -> E2): MonoResult<S, E2> =
    this.map {
        it.mapError(mapper)
    }

fun <S : Any, E : Any, S2 : Any> MonoResult<S, E>.flatMapResult(mapper: (S) -> Result<S2, E>): MonoResult<S2, E> =
    this.map {
        it.flatMap(mapper)
    }

fun <S : Any, E : Any, S2 : Any> MonoResult<S, E>.flatMapSuccess(mapper: (S) -> MonoResult<S2, E>): MonoResult<S2, E> =
    this.flatMap { result1 ->
        when (result1) {
            is Success -> mapper(result1.value)
            is Failure -> Failure(result1.error).toMono()
        }
    }

//fun <S : Any, E : Any, S2 : Any, E2 : Any> MonoResult<S, E>.flatMap(mapper: (S) -> MonoResult<S2, E2>, errorMapper: (E) -> E2): MonoResult<S2, E2> = TODO()
//
//fun <S : Any, E : Any, S2 : Any, E2 : Any> MonoResult<S, E>.flatMapResult(mapper: (S) -> Result<S2, E2>, errorMapper: (E) -> E2): MonoResult<S2, E2> = TODO()


fun <S : Any, E : Any, S2 : Any> MonoResult<S, E>.flatMapSuccess(mapper: (S) -> Mono<S2>, errorMapper: (Throwable) -> E): MonoResult<S2, E> =
    this.flatMap { result1 ->
        when (result1) {
            is Success -> mapper(result1.value).liftResult(errorMapper)
            is Failure -> Failure(result1.error).toMono()
        }
    }

fun <S : Any, E : Any, S2 : Any> Result<S, E>.liftMap(mapper: (S) -> MonoResult<S2, E>): MonoResult<S2, E> =
    when (this) {
        is Success -> mapper(value)
        is Failure -> Failure(error).toMono()
    }

fun <S : Any, E : Any> S.justMonoResult(): MonoResult<S, E> =
    Result.of<S, E> { this }
        .let { Mono.just(it) }

fun <S : Any, E : Any> E.failedMonoResult(): MonoResult<S, E> =
    Result.error(this)
        .let { Mono.just(it) }

fun <S : Any, E : Any> Result<S, E>.liftMono(): MonoResult<S, E> =
    this.toMono()

fun <S : Any, E : Any> Mono<S>.liftResult(errorMapper: (Throwable) -> E): MonoResult<S, E> =
    this.map { Result.success(it) as Result<S, E> }
        .onErrorResume { Result.error(errorMapper(it)).toMono() }

//fun <V : Any, E : Any> MonoResult<V, E>.any(predicate: (V) -> Boolean): Boolean = TODO()