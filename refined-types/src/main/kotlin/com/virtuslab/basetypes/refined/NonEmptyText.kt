package com.virtuslab.basetypes.refined

data class NonEmptyText internal constructor(val text: String) : CharSequence by text {

    companion object {
        fun of(string: String): NonEmptyText? =
            string.takeIf(String::isNotEmpty)
                ?.let(::NonEmptyText)
    }

    override fun toString() = text
}
