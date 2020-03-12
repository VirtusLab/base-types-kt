package com.virtuslab.basetypes.refined

import arrow.core.Option
import kotlin.random.Random


fun Random.nextDigit(): Digit =
    nextInt(0, 10)
        .let(Digit.Companion::of)
        .let(Option<Digit>::orNull)!!

fun Random.nextNaturalNumber(): NaturalNumber =
    nextInt(Int.MAX_VALUE).plus(1)
        .let(NaturalNumber.Companion::of)
        .let(Option<NaturalNumber>::orNull)!!

fun Random.nextWholeNumber(): WholeNumber =
    nextInt()
        .let(WholeNumber.Companion::of)
        .let(Option<WholeNumber>::orNull)!!
