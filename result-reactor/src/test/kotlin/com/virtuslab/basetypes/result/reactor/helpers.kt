package com.virtuslab.basetypes.result.reactor

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.virtuslab.basetypes.Either.reactor.MonoEither
import reactor.test.test

fun <E, V> MonoEither<E, V>.test() = mono.map { it as Either }.test()

fun <E, V> FluxEither<E, V>.test() = flux.map { it as Either }.test()

fun <T : Any> T.toFluxRight(): FluxEither<Nothing, T> = this.right().toFluxK()

fun <T : Any> T.toFluxLeft(): FluxEither<T, Nothing> = this.left().toFluxK()