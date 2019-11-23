package com.virtuslab.basetypes.refined

import arrow.core.None
import arrow.core.toOption
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

internal class WholeNumberTest : StringSpec() {
    init {
        "of" {
            forAll(Gen.positiveIntegers()) { a: Int ->
                WholeNumber.of(a).nonEmpty()
            }

            forAll(Gen.negativeIntegers()) { a: Int ->
                WholeNumber.of(a).isEmpty()
            }
        }

        "inc" {
            forAll(Gen.positiveIntegers()) { a: Int ->
                WholeNumber(a).inc() == WholeNumber(a + 1)
            }
        }

        "decrement" {
            forAll(Gen.positiveIntegers()) { a: Int ->
                WholeNumber(a).decrement() == WholeNumber(a - 1).toOption()
            }

            forAll(Gen.from(listOf(0))) { zero ->
                WholeNumber(zero).decrement().isEmpty()
            }
        }

        "minus" {
            forAll(Gen.positiveIntegers(), Gen.positiveIntegers()) { a, b ->
                if (a >= b) {
                    WholeNumber(a) - WholeNumber(b) == WholeNumber(a-b).toOption()
                } else {
                    WholeNumber(a) - WholeNumber(b) == None
                }
            }
        }
    }
}
