package com.virtuslab.basetypes.result.reactor

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.fx.IO
import arrow.fx.IOResult
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.k
import arrow.fx.reactor.value
import com.virtuslab.basetypes.Either.reactor.MonoEither
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


fun <E, A> IO<E, A>.toMono(): MonoEither<E, A> =
    Mono.create<Either<E, A>> { sink ->
        val dispose = unsafeRunAsyncCancellableEither { result ->
            result.fold(
                { sink.error(it) },
                { sink.success(Either.left(it)) },
                { sink.success(Either.right(it)) }
            )
        }
        sink.onCancel { dispose.invoke() }
    }.let { MonoK(it) }

fun <A> MonoK<A>.toIO(): IO<Nothing, A> =
    IO.cancellable { cb ->
        val dispose = this.value().subscribe({ a -> cb(IOResult.Success(a)) }, { e -> cb(IOResult.Exception(e)) })
        IO { dispose.dispose() }
    }

fun <A> Mono<A>.toIO(): IO<Nothing, A> = k().toIO()

fun <T : Any> T.toMonoK(): MonoK<T> = Mono.just(this).k()

fun <T : Any> T.toMonoRight(): MonoEither<Nothing, T> = this.right().toMonoK()

fun <T : Any> T.toMonoLeft(): MonoEither<T, Nothing> = this.left().toMonoK()

fun <T> Throwable.toMonoK(): MonoK<T> = Mono.error<T>(this).k()

//flux
fun <T : Any> T.toFluxK(): FluxK<T> = Flux.just(this).k()

fun <E, A> IO<E, A>.toFlux(): FluxEither<E, A> =
    Flux.create<Either<E, A>> { sink ->
        val dispose = unsafeRunAsyncCancellableEither { result ->
            result.fold(
                { sink.error(it) },
                { sink.next(Either.left(it)) },
                { sink.next(Either.right(it)) }
            )
        }
        sink.onCancel { dispose.invoke() }
    }.let { FluxK(it) }


fun <A> FluxK<A>.toIO(): IO<Nothing, A> =
    IO.cancellable { cb ->
        val dispose = this.value().subscribe({ a -> cb(IOResult.Success(a)) }, { e -> cb(IOResult.Exception(e)) })
        IO { dispose.dispose() }
    }

fun <A> Flux<A>.toIO(): IO<Nothing, A> = k().toIO()
