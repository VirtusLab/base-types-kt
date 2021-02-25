package com.virtuslab.basetypes.refined

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

internal class NonNegativeRealNumberTest : StringSpec() {
    init {
        val positiveDoubles = listOf(1.0, 1.5, 2.0, 2.5, 3.0, 3.5)

        "of" {
            forAll(Gen.positiveDoubles()) { a: Double ->
                NonNegativeRealNumber.of(a) != null
            }

            forAll(Gen.from(listOf(-1.0, -1.5, -2.0, -2.5, -3.0, -3.5))) { a: Double ->
                NonNegativeRealNumber.of(a) == null
            }
        }

        "plus" {
            forAll(Gen.from(positiveDoubles), Gen.from(positiveDoubles)) { a: Double, b: Double ->
                NonNegativeRealNumber(a) + NonNegativeRealNumber(b) == NonNegativeRealNumber(a + b)
            }

            forAll(Gen.from(positiveDoubles), Gen.from(positiveDoubles)) { a: Double, b: Double ->
                NonNegativeRealNumber(a) + b == NonNegativeRealNumber(a + b)
            }

            forAll(Gen.from(positiveDoubles), Gen.positiveIntegers()) { a: Double, b: Int ->
                NonNegativeRealNumber(a) + b == NonNegativeRealNumber(a + b)
            }
        }

        "minus" {
            forAll(Gen.from(positiveDoubles), Gen.from(positiveDoubles)) { a: Double, b: Double ->
                if (a >= b) {
                    NonNegativeRealNumber(a) - NonNegativeRealNumber(b) == NonNegativeRealNumber(a - b)
                } else {
                    NonNegativeRealNumber(a) - NonNegativeRealNumber(b) == null
                }
            }

            forAll(Gen.from(positiveDoubles.reversed()), Gen.from(positiveDoubles)) { a: Double, b: Double ->
                if (a >= b) {
                    NonNegativeRealNumber(a) - b == NonNegativeRealNumber(a - b)
                } else {
                    NonNegativeRealNumber(a) - b == null
                }
            }

            forAll(Gen.from(positiveDoubles.reversed()), Gen.positiveIntegers()) { a: Double, b: Int ->
                if (a >= b) {
                    NonNegativeRealNumber(a) - b == NonNegativeRealNumber(a - b)
                } else {
                    NonNegativeRealNumber(a) - b == null
                }
            }
        }
    }
}
