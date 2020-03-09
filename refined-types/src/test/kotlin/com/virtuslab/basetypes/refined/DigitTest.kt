package com.virtuslab.basetypes.refined

import com.virtuslab.basetypes.refined.numbers.Digit
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

fun Gen.Companion.integersOver9() =
    int().filter { it > 9 }

internal class DigitTest : StringSpec() {
    init {
        "of(Int)" {
            forAll(Gen.choose(0, 10)) { a: Int ->
                Digit.of(a).nonEmpty()
            }

            forAll(Gen.negativeIntegers()) { a: Int ->
                Digit.of(a).isEmpty()
            }

            forAll(Gen.integersOver9()) { a: Int ->
                Digit.of(a).isEmpty()
            }
        }

        "of(String)" {
            forAll(Gen.choose(0, 10)) { a: Int ->
                Digit.of(a.toString()).nonEmpty()
            }

            forAll(Gen.negativeIntegers()) { a: Int ->
                Digit.of(a.toString()).isEmpty()
            }

            forAll(Gen.integersOver9()) { a: Int ->
                Digit.of(a).isEmpty()
            }
        }
    }
}
