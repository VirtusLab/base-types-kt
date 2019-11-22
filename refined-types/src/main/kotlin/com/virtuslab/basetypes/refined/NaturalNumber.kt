package com.virtuslab.basetypes.refined

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

data class NaturalNumber internal constructor(val number: Int) {
    companion object {
        val ONE = NaturalNumber(1)
        val TWO = NaturalNumber(2)
        val THREE = NaturalNumber(3)
        val FOUR = NaturalNumber(4)
        val FIVE = NaturalNumber(5)
        val SIX = NaturalNumber(6)
        val SEVEN = NaturalNumber(7)
        val EIGHT = NaturalNumber(8)
        val NINE = NaturalNumber(9)

        fun of(int: Int): Option<NaturalNumber> =
            if (int > 0) Some(NaturalNumber(int))
            else None
    }

    operator fun plus(that: NaturalNumber): NaturalNumber =
        NaturalNumber(this.number + that.number)

    override fun toString() = "$number"
}