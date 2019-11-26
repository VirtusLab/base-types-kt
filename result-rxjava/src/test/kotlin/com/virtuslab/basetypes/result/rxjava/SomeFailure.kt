package com.virtuslab.basetypes.result.rxjava

data class SomeFailure(val errorMessage: String) : Exception()