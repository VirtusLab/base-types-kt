package com.virtuslab.basetypes.result.reactor

import arrow.core.Either
import arrow.fx.reactor.FluxK

typealias FluxEither<E, V> = FluxK<Either<E, V>>