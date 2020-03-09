package com.virtuslab.basetypes.refined

import arrow.core.Option
import arrow.core.maybe

class NonEmptySet<T> private constructor(private val elements: Set<T>) : Set<T> by elements { // TODO do not implement Set, methods should return NonEmptySet
    companion object {
        fun <T> create(element: T): NonEmptySet<T> = NonEmptySet(setOf(element))

        fun <T> create(first: T, vararg rest: T): NonEmptySet<T> = NonEmptySet(setOf(first) + rest)

        fun <T> create(set: Set<T>): Option<NonEmptySet<T>> = set.isNotEmpty().maybe { NonEmptySet(set) }
    }
}