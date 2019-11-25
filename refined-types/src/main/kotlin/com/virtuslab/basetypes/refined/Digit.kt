package com.virtuslab.basetypes.refined

import arrow.core.Option
import arrow.core.maybe

data class Digit internal constructor(val digit: Int) {

    operator fun compareTo(other: Digit) = digit.compareTo(other = other.digit)

    companion object {
        val ZERO = Digit(0)
        val ONE = Digit(1)
        val TWO = Digit(2)
        val THREE = Digit(3)
        val FOUR = Digit(4)
        val FIVE = Digit(5)
        val SIX = Digit(6)
        val SEVEN = Digit(7)
        val EIGHT = Digit(8)
        val NINE = Digit(9)

        fun of(int: Int): Option<Digit> =
            (int >= 0).and(int <= 9)
                .maybe { Digit(int) }

        fun of(int: String): Option<Digit> =
            int.toIntOrNull()
                .let { Option.fromNullable(it) }
                .flatMap(::of)
    }

    override fun toString() = "$digit"
}