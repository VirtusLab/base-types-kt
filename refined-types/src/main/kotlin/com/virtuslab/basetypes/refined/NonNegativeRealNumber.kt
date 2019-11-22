package com.virtuslab.basetypes.refined

import arrow.core.Option
import arrow.core.maybe

data class NonNegativeRealNumber internal constructor(val number: Double) {
    operator fun minus(another: NonNegativeRealNumber): Option<NonNegativeRealNumber> =
        (this.number - another.number)
            .let(::of)

    operator fun minus(another: Double): Option<NonNegativeRealNumber> =
        (this.number - another)
            .let(::of)

    operator fun minus(another: Int): Option<NonNegativeRealNumber> =
        (this.number - another)
            .let(::of)

    operator fun plus(another: NonNegativeRealNumber): NonNegativeRealNumber =
        NonNegativeRealNumber(this.number + another.number)

    operator fun plus(another: Double): NonNegativeRealNumber =
        NonNegativeRealNumber(this.number + another)

    operator fun plus(another: Int): NonNegativeRealNumber =
        NonNegativeRealNumber(this.number + another)

    operator fun compareTo(another: NonNegativeRealNumber) = this.number.compareTo(another.number)

    operator fun compareTo(another: Double) = this.number.compareTo(another)

    companion object {
        val ZERO: NonNegativeRealNumber = NonNegativeRealNumber(0.0)

        fun of(double: Double): Option<NonNegativeRealNumber> =
            (double >= 0).maybe { NonNegativeRealNumber(double) }
    }

    override fun toString() = "$number"
}