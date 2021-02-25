package com.virtuslab.basetypes.refined

data class NonNegativeRealNumber internal constructor(val number: Double) {
    operator fun minus(another: NonNegativeRealNumber): NonNegativeRealNumber? =
        (this.number - another.number)
            .let(::of)

    operator fun minus(another: Double): NonNegativeRealNumber? =
        (this.number - another)
            .let(::of)

    operator fun minus(another: Int): NonNegativeRealNumber? =
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

        fun of(double: Double): NonNegativeRealNumber? =
            double.takeIf { it >= 0 }
                ?.let(::NonNegativeRealNumber)
    }

    override fun toString() = "$number"
}
