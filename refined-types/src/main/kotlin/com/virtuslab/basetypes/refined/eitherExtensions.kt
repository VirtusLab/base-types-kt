package com.virtuslab.basetypes.refined

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.right


suspend fun <L : Any, R1 : Any, R2 : Any> Either<L, R1>.mapAsync(mapper: suspend (R1) -> R2): Either<L, R2> =
    when (this) {
        is Left -> this
        is Right -> mapper(this.b).right()
    }

suspend fun <L : Any, R1 : Any, R2 : Any> Either<L, R1>.flatMapAsync(mapper: suspend (R1) -> Either<L, R2>): Either<L, R2> =
    when (this) {
        is Left -> this
        is Right -> mapper(this.b)
    }
