package com.virtuslab.basetypes.rop.reactor

import arrow.fx.reactor.FluxK
import arrow.fx.reactor.MonoK
import reactor.test.test

fun <V> MonoK<V>.test() = mono.map { it as V }.test()

fun <V> FluxK<V>.test() = flux.map { it as V }.test()
