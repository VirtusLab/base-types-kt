package com.virtuslab.basetypes.rop.reactor

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.handleErrorWith
import arrow.fx.reactor.k
import reactor.core.publisher.Flux

typealias FluxEither<E, V> = FluxK<Either<E, V>>

fun <S, E, S2> FluxEither<E, S>.mapRight(mapper: (S) -> S2): FluxEither<E, S2> =
    this.map {
        it.map(mapper)
    }

fun <S, E, E2> FluxEither<E, S>.mapLeft(mapper: (E) -> E2): FluxEither<E2, S> =
    this.map {
        it.mapLeft(mapper)
    }

// TODO refine name
fun <S, E, S2> FluxEither<E, S>.flatMapEither(mapper: (S) -> Either<E, S2>): FluxEither<E, S2> =
    this.map {
        it.flatMap(mapper)
    }

fun <S, E, S2> FluxEither<E, S>.flatMapRight(mapper: (S) -> FluxEither<E, S2>): FluxEither<E, S2> =
    this.flatMap { either ->
        when (either) {
            is Either.Right -> mapper(either.b)
            is Either.Left -> Either.left(either.a).toFluxK()
        }
    }

// TODO refine name
fun <S, E, S2, E2> FluxEither<E, S>.flatMapEither(
    mapper: (S) -> Either<E2, S2>,
    leftMapper: (E2) -> E
): FluxEither<E, S2> = this.map {
    when (it) {
        is Either.Right -> mapper(it.b).mapLeft(leftMapper)
        is Either.Left -> it.a.left()
    }
}

fun <S, E, S2, E2> FluxEither<E, S>.flatMapRight(
    mapper: (S) -> FluxEither<E2, S2>,
    leftMapper: (E2) -> E
): FluxEither<E, S2> = this.flatMap {
    when (it) {
        is Either.Right -> mapper(it.b).mapLeft(leftMapper)
        is Either.Left -> it.a.left().toFluxK()
    }
}

fun <S, E> FluxK<S>.liftEither(): FluxEither<E, S> = this.map { Either.right(it) as Either<E, S> }

fun <S, E> Flux<S>.liftEither(): FluxEither<E, S> = k().liftEither()

fun <S, E> FluxK<S>.liftEither(errorMapper: (Throwable) -> E): FluxEither<E, S> =
    this.map { Either.right(it) as Either<E, S> }
        .handleErrorWith { Either.left(errorMapper(it)).toFluxK() }

fun <S, E> Flux<S>.liftEither(errorMapper: (Throwable) -> E): FluxEither<E, S> = k().liftEither(errorMapper)

fun <V, E> FluxEither<E, V>.any(predicate: (V) -> Boolean): FluxK<Boolean> = this.map {
    when (it) {
        is Either.Right -> predicate(it.b)
        else -> false
    }
}

//flux
fun <T> T.toFluxK(): FluxK<T> = Flux.just(this).k()