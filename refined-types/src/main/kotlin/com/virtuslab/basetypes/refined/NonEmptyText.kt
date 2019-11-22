package com.virtuslab.basetypes.refined

import arrow.core.Option
import arrow.core.maybe

data class NonEmptyText internal constructor(val text: String) : CharSequence by text {

    companion object {
        fun of(string: String): Option<NonEmptyText> =
            (string.isNotEmpty()).maybe { NonEmptyText(string) }
    }

    override fun toString() = text
}