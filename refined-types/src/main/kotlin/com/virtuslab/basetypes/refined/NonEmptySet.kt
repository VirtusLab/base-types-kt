package com.virtuslab.basetypes.refined


class NonEmptySet<T> private constructor(private val elements: Set<T>) : Set<T> by elements {
    companion object {
        fun <T> create(element: T): NonEmptySet<T> = NonEmptySet(setOf(element))

        fun <T> create(first: T, vararg rest: T): NonEmptySet<T> = NonEmptySet(setOf(first) + rest)

        fun <T> create(set: Set<T>): NonEmptySet<T>? = NonEmptySet(set)
    }
}