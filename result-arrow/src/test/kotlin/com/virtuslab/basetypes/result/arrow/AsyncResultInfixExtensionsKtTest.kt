package com.virtuslab.basetypes.result.arrow

import com.virtuslab.basetypes.result.Result
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import org.junit.jupiter.api.Test

internal class AsyncResultInfixExtensionsKtTest {


    @Test
    fun `Successful Result should be thenable`() {
        Result.success("Some success value")
            .then { Result.success("Some other value") }
            .getSuccessUnsafe() shouldBe "Some other value"
    }

    @Test
    fun `given first Result is failure when doing then then it whole should return failure`() {
        shouldThrow<RuntimeException> {
            Result.error(RuntimeException("Boo"))
                .then { Result.success("Some other value") }
                .getSuccessUnsafe()
        }
    }

    @Test
    fun `given second Result is failure when doing then then it whole should return failure`() {
        shouldThrow<RuntimeException> {
            Result.success("Some value")
                .then { Result.error(RuntimeException("Boo")) }
                .getSuccessUnsafe()
        }
    }

    @Test
    fun `given first AsyncResult is failure when doing then then it whole should return failure`() {
        shouldThrow<RuntimeException> {
            Result.error(RuntimeException("Boo")).liftAsync()
                .then { Result.success("Some other value").liftAsync() }
                .unsafeRunSync()
                .getSuccessUnsafe()
        }
    }

    @Test
    fun `given second AsyncResult is failure when doing then then it whole should return failure`() {
        shouldThrow<RuntimeException> {
            Result.success("Some value").liftAsync()
                .thenSync { Result.error(RuntimeException("Boo")) }
                .unsafeRunSync()
                .getSuccessUnsafe()
        }
    }

    @Test
    fun `given first AsyncResult is failure when doing then thenDoAsync it whole should return failure`() {
        shouldThrow<RuntimeException> {
            Result.error(RuntimeException("Boo"))
                .thenAsync { Result.success("Some other value").liftAsync() }
                .unsafeRunSync()
                .getSuccessUnsafe()
        }
    }


    @Test
    fun `AsyncResult should be thenable with Result`() {
        Result.success("Some success value").liftAsync()
            .thenSync { Result.success("Some other value") }
            .unsafeRunSync().getSuccessUnsafe() shouldBe "Some other value"
    }

    @Test
    fun `AsyncResult should be thenable with AsyncResult`() {
        Result.success("Some success value").liftAsync()
            .then { Result.success("Some other value").liftAsync() }
            .unsafeRunSync().getSuccessUnsafe() shouldBe "Some other value"
    }

    @Test
    fun `Result should be thenable with AsyncResult`() {
        Result.success("Some success value")
            .thenAsync { Result.success("Some other value").liftAsync() }
            .unsafeRunSync().getSuccessUnsafe() shouldBe "Some other value"
    }

    @Test
    fun `any type should be thenable to Result`() {
        val result = "Success" to { Result.success("A") }
        result.getSuccessUnsafe() shouldBe "A"
    }

    @Test
    fun `any type should be thenable to AsyncResult`() {
        val result = "Success" toAsyncResult { Result.success("A").liftAsync() }
        result.unsafeRunSync().getSuccessUnsafe() shouldBe "A"
    }
}
