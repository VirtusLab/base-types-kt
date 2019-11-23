package com.virtuslab.basetypes.refined

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

internal class DigitTest : StringSpec() {
    init {
        "of(Int)" {
            forAll(Gen.positiveIntegers()) { a: Int ->
                Digit.of(a).nonEmpty()
            }

            forAll(Gen.negativeIntegers()) { a: Int ->
                Digit.of(a).isEmpty()
            }
        }

        "of(String)" {
            forAll(Gen.positiveIntegers()) { a: Int ->
                Digit.of(a.toString()).nonEmpty()
            }

            forAll(Gen.negativeIntegers()) { a: Int ->
                Digit.of(a.toString()).isEmpty()
            }
        }
    }
}
