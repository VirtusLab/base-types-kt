package com.virtuslab.basetypes.result.reactor

import com.github.kittinunf.result.Result
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test

internal class AsyncResultKtTest {


    @Test
    fun `Result should be thenable`() {
        Result.success("Some success value")
            .then { Result.success("Some other value") }
            .get() shouldBe "Some other value"
    }

    @Test
    fun `AsyncResult should be thenable with Result`() {
        Result.success("Some success value").liftAsync()
            .thenSync { Result.success("Some other value") }
            .unsafeRunSync().get() shouldBe "Some other value"
    }

    @Test
    fun `AsyncResult should be thenable with AsyncResult`() {
        Result.success("Some success value").liftAsync()
            .then { Result.success("Some other value").liftAsync() }
            .unsafeRunSync().get() shouldBe "Some other value"
    }

    @Test
    fun `Result should be thenable with AsyncResult`() {
        Result.success("Some success value")
            .thenDoAsync { Result.success("Some other value").liftAsync() }
            .unsafeRunSync().get() shouldBe "Some other value"
    }

    @Test
    fun `any type should be thenable to Result`() {
        val result = "Success" to { Result.success("A") }
        result.get() shouldBe "A"
    }

    @Test
    fun `any type should be thenable to AsyncResult`() {
        val result = "Success" toAsyncResult { Result.success("A").liftAsync() }
        result.unsafeRunSync().get() shouldBe "A"
    }
}
