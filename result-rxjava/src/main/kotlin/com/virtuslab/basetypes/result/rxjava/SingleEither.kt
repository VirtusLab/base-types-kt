package com.virtuslab.basetypes.result.rxjava

import arrow.core.Either
import arrow.core.flatMap
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.handleErrorWith
import arrow.fx.rx2.k
import io.reactivex.Single


typealias SingleEither<E, T> = SingleK<Either<E, T>>


fun <S : Any, E : Any, S2 : Any> SingleEither<E, S>.mapRight(mapper: (S) -> S2): SingleEither<E, S2> =
    this.map {
        it.map(mapper)
    }

fun <S : Any, E : Any, E2 : Any> SingleEither<E, S>.mapLeft(mapper: (E) -> E2): SingleEither<E2, S> =
    this.map {
        it.mapLeft(mapper)
    }

fun <S : Any, E : Any, S2 : Any> SingleEither<E, S>.flatMapEither(mapper: (S) -> Either<E, S2>): SingleEither<E, S2> =
    this.map {
        it.flatMap(mapper)
    }

fun <S : Any, E : Any, S2 : Any> SingleEither<E, S>.flatMapRight(mapper: (S) -> SingleEither<E, S2>): SingleEither<E, S2> =
    this.flatMap { either ->
        when (either) {
            is Either.Right -> mapper(either.b)
            is Either.Left -> Either.left(either.a).toSingleK()
        }
    }

//fun <S : Any, E : Any, S2 : Any, E2 : Any> SingleEither<E, S>.flatMap(mapper: (S) -> SingleEither<S2, E2>, errorMapper: (E) -> E2): SingleEither<S2, E2> = TODO()
//
//fun <S : Any, E : Any, S2 : Any, E2 : Any> SingleEither<E, S>.flatMapEither(mapper: (S) -> Either<S2, E2>, errorMapper: (E) -> E2): SingleEither<S2, E2> = TODO()

fun <S : Any, E : Any, S2 : Any> Either<E, S>.liftMapRight(mapper: (S) -> SingleEither<E, S2>): SingleEither<E, S2> =
    when (this) {
        is Either.Right -> mapper(b)
        is Either.Left -> Either.left(a).toSingleK()
    }

fun <E, S> SingleK.Companion.justRight(s: S): SingleEither<E, S> = Either.right(s).toSingleK()

fun <E, S> SingleK.Companion.justLeft(e: E): SingleEither<E, S> = Either.left(e).toSingleK()

fun <S : Any, E : Any> Either<E, S>.liftSingle(): SingleEither<E, S> = this.toSingleK()

fun <S : Any, E : Any> SingleK<S>.liftEither(): SingleEither<E, S> = this.map { Either.right(it) as Either<E, S> }

fun <S : Any, E : Any> Single<S>.liftEither(): SingleEither<E, S> = k().liftEither()

fun <S : Any, E : Any> SingleK<S>.liftEither(errorMapper: (Throwable) -> E): SingleEither<E, S> =
    this.map { Either.right(it) as Either<E, S> }
        .handleErrorWith { Either.left(errorMapper(it)).toSingleK() }

fun <S : Any, E : Any> Single<S>.liftEither(errorMapper: (Throwable) -> E): SingleEither<E, S> = k().liftEither(errorMapper)

fun <V : Any, E : Any> SingleEither<E, V>.any(predicate: (V) -> Boolean): SingleK<Boolean> = this.map {
    when (it) {
        is Either.Right -> predicate(it.b)
        else -> false
    }
}