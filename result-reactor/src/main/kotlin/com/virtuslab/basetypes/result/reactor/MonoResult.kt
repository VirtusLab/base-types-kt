package com.virtuslab.basetypes.result.reactor

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.Result.Failure
import com.github.kittinunf.result.Result.Success
import com.github.kittinunf.result.flatMap
import com.github.kittinunf.result.map
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

typealias MonoResult<S, E> = Mono<Result<S, E>>

typealias Completable<E> = Mono<Result<Unit, E>>

fun <S : Any, E : Exception, S2 : Any> MonoResult<S, E>.map(mapper: (S) -> S2): MonoResult<S2, E> =
    this.map {
        it.map(mapper)
    }

fun <S : Any, E : Exception, S2 : Any> MonoResult<S, E>.mapResult(mapper: (S) -> Result<S2, E>): MonoResult<S2, E> =
    this.map {
        it.flatMap(mapper)
    }

fun <S : Any, E : Exception, S2 : Any> MonoResult<S, E>.flatMap(mapper: (S) -> MonoResult<S2, E>): MonoResult<S2, E> =
    this.flatMap { result1 ->
        when (result1) {
            is Success -> mapper(result1.value)
            is Failure -> Failure(result1.error).toMono()
        }
    }

fun <S : Any, E : Exception, S2 : Any> Result<S, E>.liftMap(mapper: (S) -> MonoResult<S2, E>): MonoResult<S2, E> =
    when (this) {
        is Success -> mapper(value)
        is Failure -> Failure(error).toMono()
    }

fun <S : Any, E : Exception> S.toMonoResult(): MonoResult<S, E> =
    Result.of<S, E> { this }
        .let { Mono.just(it) }

fun <S : Any, E : Exception> S.toMonoSuccess(): MonoResult<S, E> =
    Result.success(this)
        .let { Mono.just(it) }

fun <S : Any, E : Exception> Result<S, E>.liftToMono(): MonoResult<S, E> =
    this.toMono()

fun <S : Any, E : Exception> Mono<S>.toResult(errorMapper: (Throwable) -> E): MonoResult<S, E> =
    this.map { Result.success(it) as Result<S, E> }
        .onErrorResume { errorMapper(it).toMono() }
