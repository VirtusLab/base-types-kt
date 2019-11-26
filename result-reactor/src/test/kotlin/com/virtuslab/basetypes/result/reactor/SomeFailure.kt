package com.virtuslab.basetypes.result.reactor

data class SomeFailure(val errorMessage: String) : Exception()