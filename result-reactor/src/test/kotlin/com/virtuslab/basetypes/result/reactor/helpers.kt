package com.virtuslab.basetypes.result.reactor

import arrow.core.left
import arrow.core.right
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.MonoK
import reactor.test.test

fun <V> MonoK<V>.test() = mono.map { it as V }.test()

fun <V> FluxK<V>.test() = flux.map { it as V }.test()

fun <T> T.toFluxRight(): FluxEither<Nothing, T> = this.right().toFluxK()

fun <T> T.toFluxLeft(): FluxEither<T, Nothing> = this.left().toFluxK()