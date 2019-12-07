package com.virtuslab.basetypes.result.arrow

data class SomeFailure(val errorMessage: String) : Exception()