package com.virtuslab.basetypes.refined

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

internal class NaturalNumberTest : StringSpec() {
    init {
        "of" {
            forAll(Gen.positiveIntegers()) { a: Int ->
                NaturalNumber.of(a) != null
            }

            forAll(Gen.negativeIntegers()) { a: Int ->
                NaturalNumber.of(a) == null
            }
        }

        "plus" {
            forAll(Gen.positiveIntegers(), Gen.positiveIntegers()) { a: Int, b: Int ->
                NaturalNumber(a) + NaturalNumber(b) == NaturalNumber(a+b)
            }
        }
    }
}
