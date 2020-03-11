package com.virtuslab.basetypes.rop.reactor

import arrow.core.Either
import arrow.fx.IO
import arrow.fx.IOResult
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.k
import arrow.fx.reactor.value
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

fun <A> Mono<A>.toIO(): IO<Nothing, A> =
    IO.cancellable { cb ->
        val dispose = this.subscribe({ a -> cb(IOResult.Success(a)) }, { e -> cb(IOResult.Exception(e)) })
        IO { dispose.dispose() }
    }

fun <E, V> MonoEither<E, V>.toIO(): IO<E, V> =
    IO.cancellable { cb ->
        val dispose = this.value().subscribe(
            { either ->
                when (either) {
                    is Either.Left -> IOResult.Error(either.a)
                    is Either.Right -> IOResult.Success(either.b)
                }.let { cb(it) }
            },
            { e -> cb(IOResult.Exception(e)) }
        )
        IO { dispose.dispose() }
    }

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
