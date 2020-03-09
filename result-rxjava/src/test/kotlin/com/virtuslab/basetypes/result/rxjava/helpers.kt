package com.virtuslab.basetypes.result.rxjava

import arrow.core.Either


fun <E, V> SingleEither<E, V>.test() = single.map { it as Either }.test()