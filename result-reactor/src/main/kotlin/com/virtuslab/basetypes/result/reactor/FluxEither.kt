package com.virtuslab.basetypes.result.reactor

import arrow.core.Either
import arrow.core.flatMap
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.handleErrorWith
import arrow.fx.reactor.k
import reactor.core.publisher.Flux

typealias FluxEither<E, V> = FluxK<Either<E, V>>

fun <S : Any, E : Any, S2 : Any> FluxEither<E, S>.mapRight(mapper: (S) -> S2): FluxEither<E, S2> =
    this.map {
        it.map(mapper)
    }

fun <S : Any, E : Any, E2 : Any> FluxEither<E, S>.mapLeft(mapper: (E) -> E2): FluxEither<E2, S> =
    this.map {
        it.mapLeft(mapper)
    }

fun <S : Any, E : Any, S2 : Any> FluxEither<E, S>.flatMapEither(mapper: (S) -> Either<E, S2>): FluxEither<E, S2> =
    this.map {
        it.flatMap(mapper)
    }

fun <S : Any, E : Any, S2 : Any> FluxEither<E, S>.flatMapRight(mapper: (S) -> FluxEither<E, S2>): FluxEither<E, S2> =
    this.flatMap { either ->
        when (either) {
            is Either.Right -> mapper(either.b)
            is Either.Left -> Either.left(either.a).toFluxK()
        }
    }

//fun <S : Any, E : Any, S2 : Any, E2 : Any> FluxEither<E, S>.flatMap(mapper: (S) -> FluxEither<S2, E2>, errorMapper: (E) -> E2): FluxEither<S2, E2> = TODO()
//
//fun <S : Any, E : Any, S2 : Any, E2 : Any> FluxEither<E, S>.flatMapEither(mapper: (S) -> Either<S2, E2>, errorMapper: (E) -> E2): FluxEither<S2, E2> = TODO()

fun <S : Any, E : Any> FluxK<S>.liftEither(): FluxEither<E, S> = this.map { Either.right(it) as Either<E, S> }

fun <S : Any, E : Any> Flux<S>.liftEither(): FluxEither<E, S> = k().liftEither()

fun <S : Any, E : Any> FluxK<S>.liftEither(errorMapper: (Throwable) -> E): FluxEither<E, S> =
    this.map { Either.right(it) as Either<E, S> }
        .handleErrorWith { Either.left(errorMapper(it)).toFluxK() }

fun <S : Any, E : Any> Flux<S>.liftEither(errorMapper: (Throwable) -> E): FluxEither<E, S> = k().liftEither(errorMapper)

fun <V : Any, E : Any> FluxEither<E, V>.any(predicate: (V) -> Boolean): FluxK<Boolean> = this.map {
    when (it) {
        is Either.Right -> predicate(it.b)
        else -> false
    }
}
