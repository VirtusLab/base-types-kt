package com.virtuslab.basetypes.refined

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

internal class WholeNumberTest : StringSpec() {
    init {
        "of" {
            forAll(Gen.positiveIntegers()) { a: Int ->
                WholeNumber.of(a) != null
            }

            forAll(Gen.negativeIntegers()) { a: Int ->
                WholeNumber.of(a) == null
            }
        }

        "inc" {
            forAll(Gen.positiveIntegers()) { a: Int ->
                WholeNumber(a).inc() == WholeNumber(a + 1)
            }
        }

        "decrement" {
            forAll(Gen.positiveIntegers()) { a: Int ->
                WholeNumber(a).dec() == WholeNumber(a - 1)
            }

            forAll(Gen.from(listOf(0))) { zero ->
                WholeNumber(zero).dec() == null
            }
        }

        "minus" {
            forAll(Gen.positiveIntegers(), Gen.positiveIntegers()) { a, b ->
                if (a >= b) {
                    WholeNumber(a) - WholeNumber(b) == WholeNumber(a - b)
                } else {
                    WholeNumber(a) - WholeNumber(b) == null
                }
            }
        }
    }
}
