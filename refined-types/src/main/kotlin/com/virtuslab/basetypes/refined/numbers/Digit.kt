package com.virtuslab.basetypes.refined.numbers

import arrow.core.Option
import arrow.core.some

sealed class Digit(val digit: Int) {

    companion object {
        fun of(int: Int): Option<Digit> =
            when (int) {
                0 -> Zero.some()
                1 -> One.some()
                2 -> Two.some()
                3 -> Three.some()
                4 -> Four.some()
                5 -> Five.some()
                6 -> Six.some()
                7 -> Seven.some()
                8 -> Eight.some()
                9 -> Nine.some()
                else -> Option.empty()
            }

        fun of(int: String): Option<Digit> =
            int.toIntOrNull()
                .let { Option.fromNullable(it) }
                .flatMap(::of)
    }
}

object Zero : Digit(0)
object One : Digit(1)
object Two : Digit(2)
object Three : Digit(3)
object Four : Digit(4)
object Five : Digit(5)
object Six : Digit(6)
object Seven : Digit(7)
object Eight : Digit(8)
object Nine : Digit(9)