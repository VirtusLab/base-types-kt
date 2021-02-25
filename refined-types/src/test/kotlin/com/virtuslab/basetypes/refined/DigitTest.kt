package com.virtuslab.basetypes.refined

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

fun Gen.Companion.integersOver9() =
    int().filter { it > 9 }

internal class DigitTest : StringSpec() {
    init {
        "of(Int)" {
            forAll(Gen.choose(0, 10)) { a: Int ->
                Digit.of(a) != null
            }

            forAll(Gen.negativeIntegers()) { a: Int ->
                Digit.of(a) == null
            }

            forAll(Gen.integersOver9()) { a: Int ->
                Digit.of(a) == null
            }
        }

        "of(String)" {
            forAll(Gen.choose(0, 10)) { a: Int ->
                Digit.of(a.toString()) != null
            }

            forAll(Gen.negativeIntegers()) { a: Int ->
                Digit.of(a.toString()) == null
            }

            forAll(Gen.integersOver9()) { a: Int ->
                Digit.of(a) == null
            }
        }
    }
}
