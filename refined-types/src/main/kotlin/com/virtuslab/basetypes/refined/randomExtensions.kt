package com.virtuslab.basetypes.refined

import kotlin.random.Random

fun Random.nextDigit(): Digit =
    nextInt(0, 10)
        .let(Digit.Companion::of)!!

fun Random.nextNaturalNumber(): NaturalNumber =
    nextInt(Int.MAX_VALUE).plus(1)
        .let(::NaturalNumber)

fun Random.nextWholeNumber(): WholeNumber =
    nextInt()
        .let(::WholeNumber)
