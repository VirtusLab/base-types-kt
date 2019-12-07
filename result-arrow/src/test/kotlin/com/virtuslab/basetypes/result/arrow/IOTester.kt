package com.virtuslab.basetypes.result.arrow

import arrow.core.getOrElse
import arrow.fx.IO
import arrow.fx.typeclasses.seconds
import io.kotlintest.shouldBe

fun <T> IO<T>.test(): IOTester<T> = IOTester(this)
class IOTester<T>(private val io: IO<T>) {

    fun assertResult(t: T) {
        io.unsafeRunTimed(5.seconds)
            .getOrElse { null } shouldBe t
    }

    fun assertError(ex: Throwable) {
        io.attempt().unsafeRunTimed(5.seconds)
            .getOrElse { null }
            ?.swap()
            ?.toOption()
            ?.getOrElse { null } shouldBe ex
    }
}