package com.virtuslab.basetypes.refined

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec
import java.util.*

internal class NonEmptyTextTest : StringSpec() {
    init {
        "of" {
            forAll(Gen.uuid()) { notEmptyString: UUID ->
                NonEmptyText(notEmptyString.toString()).isNotEmpty()
            }
        }
    }
}
