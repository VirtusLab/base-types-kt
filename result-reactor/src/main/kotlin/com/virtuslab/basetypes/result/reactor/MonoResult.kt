package com.virtuslab.basetypes.result.reactor

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.Result.Failure
import com.github.kittinunf.result.Result.Success
import com.github.kittinunf.result.flatMap
import com.github.kittinunf.result.map
import com.github.kittinunf.result.mapError
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

typealias MonoResult<S, E> = Mono<Result<S, E>>

typealias Completable<E> = Mono<Result<Unit, E>>

fun <S : Any, E : Exception, S2 : Any> MonoResult<S, E>.mapSuccess(mapper: (S) -> S2): MonoResult<S2, E> =
    this.map {
        it.map(mapper)
    }

fun <S : Any, E : Exception, E2 : java.lang.Exception> MonoResult<S, E>.mapFailure(mapper: (E) -> E2): MonoResult<S, E2> =
    this.map {
        it.mapError(mapper)
    }

// TODO test handling errors
fun <S : Any, E : Exception, S2 : Any> MonoResult<S, E>.flatMapResult(mapper: (S) -> Result<S2, E>): MonoResult<S2, E> =
    this.map {
        it.flatMap(mapper)
    }

// TODO handle errors
fun <S : Any, E : Exception, S2 : Any> MonoResult<S, E>.flatMapSuccess(mapper: (S) -> MonoResult<S2, E>): MonoResult<S2, E> =
    this.flatMap { result1 ->
        when (result1) {
            is Success -> mapper(result1.value)
            is Failure -> Failure(result1.error).toMono()
        }
    }

fun <S : Any, E : Exception, S2 : Any, E2 : Exception> MonoResult<S, E>.flatMap(mapper: (S) -> MonoResult<S2, E2>, errorMapper: (E) -> E2): MonoResult<S2, E2> = TODO()

fun <S : Any, E : Exception, S2 : Any, E2 : Exception> MonoResult<S, E>.flatMapResult(mapper: (S) -> Result<S2, E2>, errorMapper: (E) -> E2): MonoResult<S2, E2> = TODO()


fun <S : Any, E : Exception, S2 : Any> MonoResult<S, E>.flatMapSuccess(mapper: (S) -> Mono<S2>, errorMapper: (Throwable) -> E): MonoResult<S2, E> =
    this.flatMap { result1 ->
        when (result1) {
            is Success -> mapper(result1.value).liftResult(errorMapper)
            is Failure -> Failure(result1.error).toMono()
        }
    }

fun <S : Any, E : Exception, S2 : Any> Result<S, E>.liftMap(mapper: (S) -> MonoResult<S2, E>): MonoResult<S2, E> =
    when (this) {
        is Success -> mapper(value)
        is Failure -> Failure(error).toMono()
    }

fun <S : Any, E : Exception> S.justMonoResult(): MonoResult<S, E> =
    Result.of<S, E> { this }
        .let { Mono.just(it) }

fun <S : Any, E : Exception> E.failedMonoResult(): MonoResult<S, E> =
    Result.error(this)
        .let { Mono.just(it) }

fun <S : Any, E : Exception> Result<S, E>.liftMono(): MonoResult<S, E> =
    this.toMono()

fun <S : Any, E : Exception> Mono<S>.liftResult(errorMapper: (Throwable) -> E): MonoResult<S, E> =
    this.map { Result.success(it) as Result<S, E> }
        .onErrorResume { Result.error(errorMapper(it)).toMono() }

fun <V : Any, E : Exception> MonoResult<V, E>.any(predicate: (V) -> Boolean): Boolean = TODO()