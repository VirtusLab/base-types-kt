package com.virtuslab.basetypes.rop.rx2

import arrow.core.Either


fun <E, V> SingleEither<E, V>.test() = single.map { it as Either }.test()