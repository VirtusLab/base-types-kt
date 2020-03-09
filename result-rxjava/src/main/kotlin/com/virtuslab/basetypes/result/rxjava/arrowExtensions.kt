package com.virtuslab.basetypes.result.rxjava

import arrow.core.left
import arrow.core.right
import arrow.fx.IO
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.k
import io.reactivex.Single


fun <T : Any> T.toSingleK(): SingleK<T> = Single.just(this).k()

fun <T : Any> T.toSingleRight(): SingleEither<Nothing, T> = this.right().toSingleK()

fun <T : Any> T.toSingleLeft(): SingleEither<T, Nothing> = this.left().toSingleK()

fun <T> Throwable.toSingleK(): SingleK<T> = Single.error<T>(this).k()
