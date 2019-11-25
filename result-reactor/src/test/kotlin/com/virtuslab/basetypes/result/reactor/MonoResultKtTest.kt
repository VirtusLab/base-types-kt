package com.virtuslab.basetypes.result.reactor

import com.github.kittinunf.result.Result
import org.junit.jupiter.api.Test
import reactor.core.publisher.toMono
import reactor.test.test
import java.lang.IllegalStateException
import java.lang.RuntimeException

class MonoResultKtTest {

    @Test
    fun `should be mappable`() {
        val monoResult: MonoResult<String, Nothing> = "Some value".toMonoResult()

        monoResult.mapResult { "Some other value" }
            .test()
            .expectNext(Result.success("Some other value"))
            .verifyComplete()
    }

    @Test
    fun `should be flatMappable`() {
        val monoResult: MonoResult<String, Nothing> = "Some value".toMonoResult()

        monoResult.flatMapResult { x: String -> Result.success("$x some other") }
            .test()
            .expectNext(Result.success("Some value some other"))
    }

    @Test
    fun `should be able to map error in result`() {
        val monoResult: MonoResult<Nothing, RuntimeException> = Result.error(RuntimeException("Some value")).liftToMono()

        monoResult.mapResultError { IllegalStateException("It was illegal state") }
            .test()
            .expectNext(Result.error(IllegalStateException("It was illegal state")))
    }

    @Test
    fun `should flatmap monoresult`() {
        val monoResult: MonoResult<String, Nothing> = "Some value".toMonoResult()

        monoResult.flatMapMonoResult { x: String -> Result.success("$x some other").liftToMono() }
            .test()
            .expectNext(Result.success("Some other value"))
    }

    @Test
    fun `should biflatmap mono`() {
        val monoResult: MonoResult<String, RuntimeException> = "Some value".toMonoResult()

        monoResult.biflatMap(
            mapper = { x: String -> "$x some other".toMono() },
            errorMapper = { ex -> RuntimeException(ex) })
            .test()
            .expectNext(Result.success("Some value some other"))
    }

    @Test
    fun `should liftmap result`() {
        val result = Result.success("Some value")

        result.liftMap { x -> "$x Some value".toMonoResult<String, Nothing>() }
            .test()
            .expectNext(Result.success("Some value Some value"))
    }
}
