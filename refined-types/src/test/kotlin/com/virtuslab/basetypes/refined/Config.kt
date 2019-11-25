package com.virtuslab.basetypes.refined

import io.kotlintest.AbstractProjectConfig

object ProjectConfig : AbstractProjectConfig() {
    override fun parallelism(): Int = 4
}
