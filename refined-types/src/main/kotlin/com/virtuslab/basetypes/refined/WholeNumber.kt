package com.virtuslab.basetypes.refined

data class WholeNumber internal constructor(val number: Int) {
    operator fun minus(number: WholeNumber): WholeNumber? = of(this.number - number.number)
    operator fun inc(): WholeNumber = this.copy(number = this.number + 1)
    fun dec(): WholeNumber? = of(this.number - 1)
    operator fun compareTo(number: Int): Int = this.number.compareTo(number)

    companion object {
        val ZERO = WholeNumber(0)
        val ONE = WholeNumber(1)
        val TWO = WholeNumber(2)
        val THREE = WholeNumber(3)
        val FOUR = WholeNumber(4)
        val FIVE = WholeNumber(5)
        val SIX = WholeNumber(6)
        val SEVEN = WholeNumber(7)
        val EIGHT = WholeNumber(8)
        val NINE = WholeNumber(9)

        fun of(int: Int): WholeNumber? =
            int.takeIf { it >= 0 }
                ?.let(::WholeNumber)
    }
}
