package com.virtuslab.basetypes.result.reactor

import com.github.kittinunf.result.Result
import io.kotlintest.specs.StringSpec
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import reactor.test.test

internal class MonoResultKtTest : StringSpec() {

    init {
        "should map success" {
            val monoResult: MonoResult<String, Nothing> = "Some value".justMonoResult()

            monoResult.mapSuccess { "Some other value" }
                .test()
                .expectNext(Result.success("Some other value"))
                .verifyComplete()
        }

        "should handle exception when mapping success" {
            val monoResult: MonoResult<String, SomeFailure> = "Some value".justMonoResult()
            val runtimeException = RuntimeException()

            monoResult.mapSuccess { if (true) throw runtimeException else "Some other value" }
                .test()
                .verifyError(RuntimeException::class.java)
        }

        "should handle exception when mapping result success" {
            val monoResult: MonoResult<String, SomeFailure> = "Some value".justMonoResult()
            val runtimeException = RuntimeException()

            monoResult.flatMapResult { if (true) throw runtimeException else Result.success("Some other value") }
                .test()
                .verifyError(RuntimeException::class.java)
        }

        "should handle exception when flatMapping MonoResult" {
            val monoResult: MonoResult<String, SomeFailure> = "Some value".justMonoResult()
            val runtimeException = RuntimeException()

            monoResult.flatMapSuccess { if (true) throw runtimeException else "Some other value".justMonoResult<String, SomeFailure>() }
                .test()
                .verifyError(RuntimeException::class.java)
        }

        "should keep error when mapping success" {
            val monoResult: MonoResult<String, SomeFailure> = SomeFailure("Some failure").failedMonoResult()

            monoResult.mapSuccess { "Some other value" }
                .test()
                .expectNext(Result.error(SomeFailure("Some failure")))
                .verifyComplete()
        }

        "should be able to map error in result"{
            val monoResult: MonoResult<Nothing, RuntimeException> = Result.error(RuntimeException("exception")).liftMono()

            monoResult.mapFailure { SomeFailure("Failure from ${it.localizedMessage}") }
                .test()
                .expectNext(Result.error(SomeFailure("Failure from exception")))
                .verifyComplete()
        }

        "should flatMap success"{
            val monoResult: MonoResult<String, Nothing> = "Some value".justMonoResult()

            monoResult.flatMapResult { x: String -> Result.success("$x some other") }
                .test()
                .expectNext(Result.success("Some value some other"))
                .verifyComplete()
        }

        "should keep error when flatMapping result success" {
            val monoResult: MonoResult<String, SomeFailure> = SomeFailure("Some failure").failedMonoResult()

            monoResult.flatMapResult { x: String -> Result.success("$x some other") }
                .test()
                .expectNext(Result.error(SomeFailure("Some failure")))
                .verifyComplete()
        }

        "should flatMap MonoResult"{
            val monoResult: MonoResult<String, Nothing> = "Some value".justMonoResult()

            monoResult.flatMapSuccess { x: String -> Result.success("$x some other").liftMono() }
                .test()
                .expectNext(Result.success("Some value some other"))
                .verifyComplete()
        }

        "should keep error when flatMapping MonoResult"{
            val monoResult: MonoResult<String, SomeFailure> = SomeFailure("Some failure").failedMonoResult()

            monoResult.flatMapSuccess { x: String -> (Result.success("$x some other") as Result<String, SomeFailure>).liftMono() }
                .test()
                .expectNext(Result.error(SomeFailure("Some failure")))
                .verifyComplete()
        }

        "should flatMap Mono"{
            val monoResult: MonoResult<String, RuntimeException> = "Some value".justMonoResult()

            monoResult.flatMapSuccess(
                mapper = { x: String -> "$x some other".toMono() },
                errorMapper = { ex -> RuntimeException(ex) })
                .test()
                .expectNext(Result.success("Some value some other"))
                .verifyComplete()
        }

        "should keep error when flatmapping Mono"{
            val monoResult: MonoResult<String, SomeFailure> = SomeFailure("Some failure").failedMonoResult()

            monoResult.flatMapSuccess(
                mapper = { x: String -> "$x some other".toMono() },
                errorMapper = { ex -> SomeFailure(ex.localizedMessage) })
                .test()
                .expectNext(Result.error(SomeFailure("Some failure")))
                .verifyComplete()
        }

        "should flatMap failed Mono"{
            val monoResult: MonoResult<String, SomeFailure> = "Some value".justMonoResult()

            monoResult.flatMapSuccess(
                mapper = { x: String -> RuntimeException("$x some other").toMono<String>() },
                errorMapper = { ex -> SomeFailure(ex.localizedMessage) })
                .test()
                .expectNext(Result.error(SomeFailure("Some value some other")))
                .verifyComplete()
        }

        "should liftmap result"{
            val result = Result.success("Some value")

            result.liftMap { x -> "$x Some value".justMonoResult<String, Nothing>() }
                .test()
                .expectNext(Result.success("Some value Some value"))
                .verifyComplete()
        }

        "should convert Mono with error to MonoResult"{
            val mono = Mono.error<String>(RuntimeException("Runtime exception"))

            mono.liftResult { SomeFailure(it.localizedMessage) }
                .test()
                .expectNext(Result.error(SomeFailure("Runtime exception")))
                .verifyComplete()
        }

        "should convert Mono with success to MonoResult"{
            val mono = Mono.just("Some value")

            mono.liftResult { SomeFailure(it.localizedMessage) }
                .test()
                .expectNext(Result.success("Some value"))
                .verifyComplete()
        }
    }
}

