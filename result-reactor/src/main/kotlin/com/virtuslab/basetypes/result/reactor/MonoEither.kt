package com.virtuslab.basetypes.Either.reactor

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.handleErrorWith
import arrow.fx.reactor.k
import com.virtuslab.basetypes.result.reactor.toMonoK
import com.virtuslab.basetypes.result.reactor.toMonoLeft
import reactor.core.publisher.Mono

typealias MonoEither<E, V> = MonoK<Either<E, V>>

typealias CompletableEither<E> = MonoEither<E, Nothing>

fun <S, E, S2> MonoEither<E, S>.mapRight(mapper: (S) -> S2): MonoEither<E, S2> =
    this.map {
        it.map(mapper)
    }

fun <S, E, E2> MonoEither<E, S>.mapLeft(mapper: (E) -> E2): MonoEither<E2, S> =
    this.map {
        it.mapLeft(mapper)
    }

// TODO refine name
fun <S, E, S2> MonoEither<E, S>.flatMapEither(mapper: (S) -> Either<E, S2>): MonoEither<E, S2> =
    this.map {
        it.flatMap(mapper)
    }

fun <S, E, S2> MonoEither<E, S>.flatMapRight(mapper: (S) -> MonoEither<E, S2>): MonoEither<E, S2> =
    this.flatMap { either ->
        when (either) {
            is Either.Right -> mapper(either.b)
            is Either.Left -> Either.left(either.a).toMonoK()
        }
    }

// TODO refine name
fun <S, E, S2, E2> MonoEither<E, S>.flatMapEither(
    mapper: (S) -> Either<E2, S2>,
    leftMapper: (E2) -> E
): MonoEither<E, S2> = this.map {
    when (it) {
        is Either.Right -> mapper(it.b).mapLeft(leftMapper)
        is Either.Left -> it.a.left()
    }
}

fun <S, E, S2, E2> MonoEither<E, S>.flatMapRight(
    mapper: (S) -> MonoEither<E2, S2>,
    leftMapper: (E2) -> E
): MonoEither<E, S2> = this.flatMap {
    when (it) {
        is Either.Right -> mapper(it.b).mapLeft(leftMapper)
        is Either.Left -> it.a.toMonoLeft()
    }
}

fun <E, S> MonoK.Companion.justRight(s: S): MonoEither<E, S> = Either.right(s).toMonoK()

fun <E, S> MonoK.Companion.justLeft(e: E): MonoEither<E, S> = Either.left(e).toMonoK()

fun <S, E> Either<E, S>.toMono(): MonoEither<E, S> = this.toMonoK()

fun <S, E> MonoK<S>.liftEither(): MonoEither<E, S> = this.map { Either.right(it) as Either<E, S> }

fun <S, E> Mono<S>.liftEither(): MonoEither<E, S> = k().liftEither()

fun <S, E> MonoK<S>.liftEither(errorMapper: (Throwable) -> E): MonoEither<E, S> =
    this.map { Either.right(it) as Either<E, S> }
        .handleErrorWith { Either.left(errorMapper(it)).toMonoK() }

fun <S, E> Mono<S>.liftEither(errorMapper: (Throwable) -> E): MonoEither<E, S> = k().liftEither(errorMapper)

fun <V, E> MonoEither<E, V>.any(predicate: (V) -> Boolean): MonoK<Boolean> = this.map {
    when (it) {
        is Either.Right -> predicate(it.b)
        else -> false
    }
}
