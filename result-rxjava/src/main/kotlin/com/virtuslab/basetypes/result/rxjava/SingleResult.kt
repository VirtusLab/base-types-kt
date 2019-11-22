package com.virtuslab.basetypes.result.rxjava

import com.github.kittinunf.result.Result
import io.reactivex.Single

typealias SingleResult<T, E> = Single<Result<T, E>>
fun func() {

}