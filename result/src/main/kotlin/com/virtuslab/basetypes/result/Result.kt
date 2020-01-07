package com.virtuslab.basetypes.result

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.some
import arrow.core.toOption

inline fun <reified X> Result<*, *>.getAs(): Option<X> = when (this) {
    is Result.Success -> value as? X
    is Result.Failure -> error as? X
}.toOption()

fun <V : Any> Result<V, *>.handleSuccess(f: (V) -> Unit) = fold(f, {})

fun <E : Any> Result<*, E>.handleError(f: (E) -> Unit) = fold({}, f)

infix fun <V : Any, E : Any> Result<V, E>.or(fallback: V) = when (this) {
    is Result.Success -> this
    else -> Result.Success(fallback)
}

infix fun <V : Any, E : Any> Result<V, E>.getOrElse(fallback: V) = when (this) {
    is Result.Success -> value
    else -> fallback
}

inline fun <V : Any, U : Any, E : Any> Result<V, E>.map(transform: (V) -> U): Result<U, E> =
    when (this) {
        is Result.Success -> Result.Success(transform(value))
        is Result.Failure -> this
    }

inline fun <V : Any, U : Any, E : Any> Result<V, E>.flatMap(transform: (V) -> Result<U, E>): Result<U, E> =
    when (this) {
        is Result.Success -> transform(value)
        is Result.Failure -> this
    }

fun <V : Any, E : Any, E2 : Any> Result<V, E>.mapError(transform: (E) -> E2) = when (this) {
    is Result.Success -> this
    is Result.Failure -> Result.Failure(transform(error))
}

fun <V : Any, E : Any, E2 : Any> Result<V, E>.flatMapError(transform: (E) -> Result<V, E2>) = when (this) {
    is Result.Success -> this
    is Result.Failure -> transform(error)
}

fun <V : Any, E : Any> Result<V, E>.any(predicate: (V) -> Boolean): Boolean =
    when (this) {
        is Result.Success -> predicate(value)
        is Result.Failure -> false
    }

fun <T : Any, E : Any, S : Any> Result<T, E>.pairWith(s: S): Result<Pair<T, S>, E> = map { it to s }

fun <V : Any, U : Any> Result<V, *>.zip(other: () -> Result<U, *>): Result<Pair<V, U>, *> =
    flatMap { outer -> other().map { outer to it } }

fun <V : Any, E : Any> List<Result<V, E>>.sequence(): Result<List<V>, E> = fold(Result.success(mutableListOf<V>()) as Result<MutableList<V>, E>) { acc, result ->
    acc.flatMap { combine ->
        result.map { combine.apply { add(it) } }
    }
}

fun <T : Any> T.toResult() = Result.success(this)

fun <E : Any> E.toResultFailure() = Result.error(this)

sealed class Result<out V : Any, out E : Any> {

    open operator fun component1(): V? = null
    open operator fun component2(): E? = null

    fun getSuccess(): Option<V> = when (this) {
        is Success -> this.value.some()
        is Failure -> None
    }

    fun getFailure(): Option<E> = when (this) {
        is Success -> None
        is Failure -> this.error.some()
    }

    fun toEither(): Either<E, V> = when (this) {
        is Success -> Either.right(this.value)
        is Failure -> Either.left(this.error)
    }

    inline fun <X> fold(success: (V) -> X, failure: (E) -> X): X = when (this) {
        is Success -> success(this.value)
        is Failure -> failure(this.error)
    }

    abstract fun getSuccessUnsafe(): V

    abstract fun isSuccess(): Boolean
    abstract fun isFailure(): Boolean

    class Success<out V : Any>(val value: V) : Result<V, Nothing>() {
        override fun component1(): V? = value

        override fun getSuccessUnsafe(): V = value

        override fun toString() = "[Success: $value]"

        override fun hashCode(): Int = value.hashCode()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Success<*> && value == other.value
        }

        override fun isSuccess() = true

        override fun isFailure() = false
    }

    class Failure<out E : Any>(val error: E) : Result<Nothing, E>() {
        override fun component2(): E? = error

        override fun getSuccessUnsafe() = throw NoSuchElementException("No failure - Result is successful")

        fun getException(): E = error

        override fun toString() = "[Failure: $error]"

        override fun hashCode(): Int = error.hashCode()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Failure<*> && error == other.error
        }

        override fun isSuccess() = false

        override fun isFailure() = true
    }

    companion object {
        // Factory methods
        fun <E : Any> error(ex: E) = Failure(ex)

        fun <V : Any> success(v: V) = Success(v)

        fun <V : Any> of(value: V?, fallback: (() -> Any) = { Unit }): Result<V, Any> =
            value?.let { success(it) } ?: error(fallback())

        fun <V : Any, E : Any> of(f: () -> V): Result<V, E> = success(f())

        fun <V : Any, E : Any> ofSafe(f: () -> V, fallback: (Throwable) -> E): Result<V, E> = try {
            success(f())
        } catch (ex: Throwable) {
            error(fallback(ex))
        }
    }

}
