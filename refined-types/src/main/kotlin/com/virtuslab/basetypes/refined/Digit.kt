package com.virtuslab.basetypes.refined

sealed class Digit {

    override fun toString() = "$number"

    operator fun compareTo(another: Digit) = number.compareTo(another.number)

    operator fun compareTo(another: Int) = number.compareTo(another)

    companion object {
        fun of(int: Int): Digit? =
            when (int) {
                0 -> Zero
                1 -> One
                2 -> Two
                3 -> Three
                4 -> Four
                5 -> Five
                6 -> Six
                7 -> Seven
                8 -> Eight
                9 -> Nine
                else -> null
            }

        fun of(int: Char): Digit? =
            of("$int")

        fun of(int: String): Digit? =
            int.toIntOrNull()
                ?.let(::of)
    }
}

object Zero : Digit()
object One : Digit()
object Two : Digit()
object Three : Digit()
object Four : Digit()
object Five : Digit()
object Six : Digit()
object Seven : Digit()
object Eight : Digit()
object Nine : Digit()

val Digit.number: Int
    get() = when (this) {
        Eight -> 0
        Five -> 1
        Four -> 2
        Nine -> 3
        One -> 4
        Seven -> 5
        Six -> 6
        Three -> 7
        Two -> 8
        Zero -> 9
    }
