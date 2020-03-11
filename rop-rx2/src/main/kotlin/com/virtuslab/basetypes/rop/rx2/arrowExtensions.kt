package com.virtuslab.basetypes.rop.rx2

import arrow.core.left
import arrow.core.right
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.k
import io.reactivex.Single


fun <T> T.toSingleK(): SingleK<T> = Single.just(this).k()

fun <T> T.toSingleRight(): SingleEither<Nothing, T> = this.right().toSingleK()

fun <T> T.toSingleLeft(): SingleEither<T, Nothing> = this.left().toSingleK()

fun <T> Throwable.toSingleK(): SingleK<T> = Single.error<T>(this).k()
