package com.virtuslab.basetypes.result.arrow

import com.github.kittinunf.result.Result
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

internal class AsyncResultKtTest {


    @Test
    fun `Successful Result should be thenable`() {
        Result.success("Some success value")
            .then { Result.success("Some other value") }
            .get() shouldBe "Some other value"
    }

    @Test
    fun `given first Result is failure when doing then then it whole should return failure`() {
        shouldThrow<RuntimeException> {
            Result.error(RuntimeException("Boo"))
                .then { Result.success("Some other value") }
                .get()
        }
    }

    @Test
    fun `given second Result is failure when doing then then it whole should return failure`() {
        shouldThrow<RuntimeException> {
            Result.success("Some value")
                .then { Result.error(RuntimeException("Boo")) }
                .get()
        }
    }

    @Test
    fun `given first AsyncResult is failure when doing then then it whole should return failure`() {
        shouldThrow<RuntimeException> {
            Result.error(RuntimeException("Boo")).liftAsync()
                .then { Result.success("Some other value").liftAsync() }
                .unsafeRunSync()
                .get()
        }
    }

    @Test
    fun `given second AsyncResult is failure when doing then then it whole should return failure`() {
        shouldThrow<RuntimeException> {
            Result.success("Some value").liftAsync()
                .thenSync { Result.error(RuntimeException("Boo")) }
                .unsafeRunSync()
                .get()
        }
    }

    @Test
    fun `given first AsyncResult is failure when doing then thenDoAsync it whole should return failure`() {
        shouldThrow<RuntimeException> {
            Result.error(RuntimeException("Boo"))
                .thenDoAsync { Result.success("Some other value").liftAsync() }
                .unsafeRunSync()
                .get()
        }
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
