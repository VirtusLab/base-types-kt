package com.virtuslab.basetypes.result.reactor

import arrow.core.Either
import com.virtuslab.basetypes.Either.reactor.MonoEither
import reactor.test.test

fun <E, V> MonoEither<E, V>.test() = mono.map { it as Either }.test()