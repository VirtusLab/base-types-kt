package com.virtuslab.basetypes.refined.numbers

import arrow.core.Option
import arrow.core.toOption

enum class Digit(val digit: Int) {

    Zero(0),
    One(1),
    Two(2),
    Three(3),
    Four(4),
    Five(5),
    Six(6),
    Seven(7),
    Eight(8),
    Nine(9);

    fun of(int: Int): Option<Digit> =
        values()
            .find { it.digit == int }
            .toOption()

    fun of(int: String): Option<Digit> =
        int.toIntOrNull()
            .let { Option.fromNullable(it) }
            .flatMap(::of)
}
