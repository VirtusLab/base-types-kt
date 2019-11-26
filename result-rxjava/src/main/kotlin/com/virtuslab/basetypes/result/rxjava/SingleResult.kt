package com.virtuslab.basetypes.result.rxjava

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.Result.Failure
import com.github.kittinunf.result.Result.Success
import com.github.kittinunf.result.flatMap
import com.github.kittinunf.result.map
import com.github.kittinunf.result.mapError
import io.reactivex.Single

typealias SingleResult<T, E> = Single<Result<T, E>>


fun <S : Any, E : Exception, S2 : Any> SingleResult<S, E>.mapSuccess(mapper: (S) -> S2): SingleResult<S2, E> =
    this.map {
        it.map(mapper)
    }

fun <S : Any, E : Exception, E2 : java.lang.Exception> SingleResult<S, E>.mapFailure(mapper: (E) -> E2): SingleResult<S, E2> =
    this.map {
        it.mapError(mapper)
    }

fun <S : Any, E : Exception, S2 : Any> SingleResult<S, E>.flatMapResult(mapper: (S) -> Result<S2, E>): SingleResult<S2, E> =
    this.map {
        it.flatMap(mapper)
    }

fun <S : Any, E : Exception, S2 : Any> SingleResult<S, E>.flatMapSuccess(mapper: (S) -> SingleResult<S2, E>): SingleResult<S2, E> =
    this.flatMap { result1 ->
        when (result1) {
            is Success -> try {
                mapper(result1.value)
            } catch (ex: Exception) {
                Failure(ex as E).toSingle()
            }
            is Failure -> Failure(result1.error).toSingle()
        }
    }

//fun <S : Any, E : Exception, S2 : Any, E2 : Exception> SingleResult<S, E>.flatMap(mapper: (S) -> SingleResult<S2, E2>, errorMapper: (E) -> E2): SingleResult<S2, E2> = TODO()
//
//fun <S : Any, E : Exception, S2 : Any, E2 : Exception> SingleResult<S, E>.flatMapResult(mapper: (S) -> Result<S2, E2>, errorMapper: (E) -> E2): SingleResult<S2, E2> = TODO()


fun <S : Any, E : Exception, S2 : Any> SingleResult<S, E>.flatMapSuccess(mapper: (S) -> Single<S2>, errorMapper: (Throwable) -> E): SingleResult<S2, E> =
    this.flatMap { result1 ->
        when (result1) {
            is Success -> mapper(result1.value).liftResult(errorMapper)
            is Failure -> Failure(result1.error).toSingle()
        }
    }

fun <S : Any, E : Exception, S2 : Any> Result<S, E>.liftMap(mapper: (S) -> SingleResult<S2, E>): SingleResult<S2, E> =
    when (this) {
        is Success -> mapper(value)
        is Failure -> Failure(error).toSingle()
    }

fun <S : Any, E : Exception> S.justSingleResult(): SingleResult<S, E> =
    Result.of<S, E> { this }
        .let { Single.just(it) }


fun <S : Any, E : Exception> E.failedSingleResult(): SingleResult<S, E> =
    Result.error(this)
        .let { Single.just(it) }

fun <S : Any, E : Exception> Result<S, E>.liftSingle(): SingleResult<S, E> =
    this.toSingle()

fun <S : Any, E : Exception> Single<S>.liftResult(errorMapper: (Throwable) -> E): SingleResult<S, E> =
    this.map { Result.success(it) as Result<S, E> }
        .onErrorReturn { Result.error(errorMapper(it)) }

//fun <V : Any, E : Exception> SingleResult<V, E>.any(predicate: (V) -> Boolean): Boolean = TODO()

fun <S> S.toSingle(): Single<S> = Single.just(this)

// TODO test
fun <S : Any, E : Exception> Result<S, E>.toFailure(): Option<E> = this.fold({ None }, { Some(it) })

// TODO test
fun <S : Any, E : Exception> SingleResult<S, E>.toFailure(): Single<Option<E>> = this.map { it.toFailure() }

// TODO test
fun <S : Any, E : Exception> Result<S, E>.toSuccess(): Option<S> = this.fold({ Some(it) }, { None })

// TODO test
fun <S : Any, E : Exception> SingleResult<S, E>.toSuccess(): Single<Option<S>> = this.map { it.toSuccess() }