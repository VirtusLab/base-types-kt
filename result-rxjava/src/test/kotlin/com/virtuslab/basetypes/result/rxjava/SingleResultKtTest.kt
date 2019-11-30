package com.virtuslab.basetypes.result.rxjava

import com.github.kittinunf.result.Result
import io.kotlintest.specs.StringSpec
import io.reactivex.Single


internal class SingleResultKtTest: StringSpec() {
    init {
        "should map success" {
            val monoResult: SingleResult<String, Nothing> = "Some value".justSingleResult()

            monoResult.mapSuccess { "Some other value" }
                .test()
                .assertResult(Result.success("Some other value"))
                .assertComplete()
        }

        "should propagate exception when mapping success" {
            val monoResult: SingleResult<String, SomeFailure> = "Some value".justSingleResult()
            val runtimeException = RuntimeException()

            monoResult.mapSuccess { if (true) throw runtimeException else "Some other value" }
                .test()
                .assertError(runtimeException)
        }

        "should propagate exception when mapping result success" {
            val monoResult: SingleResult<String, SomeFailure> = "Some value".justSingleResult()
            val runtimeException = RuntimeException()

            monoResult.flatMapResult { if (true) throw runtimeException else Result.success("Some other value") }
                .test()
                .assertError(runtimeException)
        }

        "should propagate exception when flatMapping SingleResult" {
            val monoResult: SingleResult<String, SomeFailure> = "Some value".justSingleResult()
            val runtimeException = RuntimeException()

            monoResult.flatMapSuccess { if (true) throw runtimeException else "Some other value".justSingleResult<String, SomeFailure>() }
                .test()
                .assertError(runtimeException)
        }

        "should keep error when mapping success" {
            val monoResult: SingleResult<String, SomeFailure> = SomeFailure("Some failure").failedSingleResult()

            monoResult.mapSuccess { "Some other value" }
                .test()
                .assertResult(Result.error(SomeFailure("Some failure")))
                .assertComplete()
        }

        "should be able to map error in result"{
            val monoResult: SingleResult<Nothing, RuntimeException> = Result.error(RuntimeException("exception")).liftSingle()

            monoResult.mapFailure { SomeFailure("Failure from ${it.localizedMessage}") }
                .test()
                .assertResult(Result.error(SomeFailure("Failure from exception")))
                .assertComplete()
        }

        "should flatMap success"{
            val monoResult: SingleResult<String, Nothing> = "Some value".justSingleResult()

            monoResult.flatMapResult { x: String -> Result.success("$x some other") }
                .test()
                .assertResult(Result.success("Some value some other"))
                .assertComplete()
        }

        "should keep error when flatMapping result success" {
            val monoResult: SingleResult<String, SomeFailure> = SomeFailure("Some failure").failedSingleResult()

            monoResult.flatMapResult { x: String -> Result.success("$x some other") }
                .test()
                .assertResult(Result.error(SomeFailure("Some failure")))
                .assertComplete()
        }

        "should flatMap SingleResult"{
            val monoResult: SingleResult<String, Nothing> = "Some value".justSingleResult()

            monoResult.flatMapSuccess { x: String -> Result.success("$x some other").liftSingle() }
                .test()
                .assertResult(Result.success("Some value some other"))
                .assertComplete()
        }

        "should keep error when flatMapping SingleResult"{
            val monoResult: SingleResult<String, SomeFailure> = SomeFailure("Some failure").failedSingleResult()

            monoResult.flatMapSuccess { x: String -> (Result.success("$x some other") as Result<String, SomeFailure>).liftSingle() }
                .test()
                .assertResult(Result.error(SomeFailure("Some failure")))
                .assertComplete()
        }

        "should flatMap Single"{
            val monoResult: SingleResult<String, RuntimeException> = "Some value".justSingleResult()

            monoResult.flatMapSuccess(
                mapper = { x: String -> "$x some other".toSingle() },
                errorMapper = { ex -> RuntimeException(ex) })
                .test()
                .assertResult(Result.success("Some value some other"))
                .assertComplete()
        }

        "should keep error when flatmapping Single"{
            val monoResult: SingleResult<String, SomeFailure> = SomeFailure("Some failure").failedSingleResult()

            monoResult.flatMapSuccess(
                mapper = { x: String -> "$x some other".toSingle() },
                errorMapper = { ex -> SomeFailure(ex.localizedMessage) })
                .test()
                .assertResult(Result.error(SomeFailure("Some failure")))
                .assertComplete()
        }

//         TODO:
//        "should flatMap failed Single"{
//            val monoResult: SingleResult<String, SomeFailure> = "Some value".justSingleResult()
//
//            monoResult.flatMapSuccess(
//                mapper = { x: String -> RuntimeException("$x some other").toSingle<String>() },
//                errorMapper = { ex -> SomeFailure(ex.localizedMessage) })
//                .test()
//                .assertResult(Result.error(SomeFailure("Some value some other")))
//                .assertComplete()
//        }

        "should liftmap result"{
            val result = Result.success("Some value")

            result.liftMap { x -> "$x Some value".justSingleResult<String, Nothing>() }
                .test()
                .assertResult(Result.success("Some value Some value"))
                .assertComplete()
        }

        "should convert Single with error to SingleResult"{
            val mono = Single.error<String>(RuntimeException("Runtime exception"))

            mono.liftResult { SomeFailure(it.localizedMessage) }
                .test()
                .assertResult(Result.error(SomeFailure("Runtime exception")))
                .assertComplete()
        }

        "should convert Single with success to SingleResult"{
            val mono = Single.just("Some value")

            mono.liftResult { SomeFailure(it.localizedMessage) }
                .test()
                .assertResult(Result.success("Some value"))
                .assertComplete()
        }
    }

}
