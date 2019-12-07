package com.virtuslab.basetypes.result.arrow

import arrow.fx.IO
import com.virtuslab.basetypes.result.Result
import io.kotlintest.specs.StringSpec

internal class AsyncResultKtTest : StringSpec() {
    init {
        "should map success" {
            val asyncResult: AsyncResult<String, Nothing> = "Some value".justAsyncResult()

            asyncResult.mapSuccess { "Some other value" }
                .test()
                .assertResult(Result.success("Some other value"))
        }

        "should propagate exception when mapping success" {
            val asyncResult: AsyncResult<String, SomeFailure> = "Some value".justAsyncResult()
            val runtimeException = RuntimeException()

            asyncResult.mapSuccess { if (true) throw runtimeException else "Some other value" }
                .test()
                .assertError(runtimeException)
        }

        "should propagate exception when mapping result success" {
            val asyncResult: AsyncResult<String, SomeFailure> = "Some value".justAsyncResult()
            val runtimeException = RuntimeException()

            asyncResult.flatMapResult { if (true) throw runtimeException else Result.success("Some other value") }
                .test()
                .assertError(runtimeException)
        }

        "should propagate exception when flatMapping AsyncResult" {
            val asyncResult: AsyncResult<String, SomeFailure> = "Some value".justAsyncResult()
            val runtimeException = RuntimeException()

            asyncResult.flatMapSuccess { if (true) throw runtimeException else "Some other value".justAsyncResult<String, SomeFailure>() }
                .test()
                .assertError(runtimeException)
        }

        "should keep error when mapping success" {
            val asyncResult: AsyncResult<String, SomeFailure> = SomeFailure("Some failure").failedAsyncResult()

            asyncResult.mapSuccess { "Some other value" }
                .test()
                .assertResult(Result.error(SomeFailure("Some failure")))
        }

        "should be able to map error in result"{
            val asyncResult: AsyncResult<Nothing, RuntimeException> = Result.error(RuntimeException("exception")).liftAsync()

            asyncResult.mapFailure { SomeFailure("Failure from ${it.localizedMessage}") }
                .test()
                .assertResult(Result.error(SomeFailure("Failure from exception")))
        }

        "should flatMap success"{
            val asyncResult: AsyncResult<String, Nothing> = "Some value".justAsyncResult()

            asyncResult.flatMapResult { x: String -> Result.success("$x some other") }
                .test()
                .assertResult(Result.success("Some value some other"))
        }

        "should keep error when flatMapping result success" {
            val asyncResult: AsyncResult<String, SomeFailure> = SomeFailure("Some failure").failedAsyncResult()

            asyncResult.flatMapResult { x: String -> Result.success("$x some other") }
                .test()
                .assertResult(Result.error(SomeFailure("Some failure")))
        }

        "should flatMap AsyncResult"{
            val asyncResult: AsyncResult<String, Nothing> = "Some value".justAsyncResult()

            asyncResult.flatMapSuccess { x: String -> Result.success("$x some other").liftAsync() }
                .test()
                .assertResult(Result.success("Some value some other"))
        }

        "should keep error when flatMapping AsyncResult"{
            val asyncResult: AsyncResult<String, SomeFailure> = SomeFailure("Some failure").failedAsyncResult()

            asyncResult.flatMapSuccess { x: String -> (Result.success("$x some other") as Result<String, SomeFailure>).liftAsync() }
                .test()
                .assertResult(Result.error(SomeFailure("Some failure")))
        }

        "should flatMap Async"{
            val asyncResult: AsyncResult<String, RuntimeException> = "Some value".justAsyncResult()

            asyncResult.flatMapSuccess(
                mapper = { x: String -> "$x some other".toAsync() },
                errorMapper = { ex -> RuntimeException(ex) })
                .test()
                .assertResult(Result.success("Some value some other"))
        }

        "should keep error when flatmapping Async"{
            val asyncResult: AsyncResult<String, SomeFailure> = SomeFailure("Some failure").failedAsyncResult()

            asyncResult.flatMapSuccess(
                mapper = { x: String -> "$x some other".toAsync() },
                errorMapper = { ex -> SomeFailure(ex.localizedMessage) })
                .test()
                .assertResult(Result.error(SomeFailure("Some failure")))
        }

        "should propagate exception when flatMap failed"{
            val asyncResult: AsyncResult<String, SomeFailure> = "Some value".justAsyncResult()

            asyncResult.flatMapSuccess(
                mapper = { x: String -> IO.raiseError<String>(RuntimeException("$x some other")) },
                errorMapper = { ex -> SomeFailure(ex.localizedMessage) }
            )
                .test()
                .assertResult(Result.error(SomeFailure("Some value some other")) as Result<String, SomeFailure>)
        }

        "should liftmap result"{
            val result = Result.success("Some value")

            result.liftMap { x -> "$x Some value".justAsyncResult<String, Nothing>() }
                .test()
                .assertResult(Result.success("Some value Some value"))
        }

        "should convert Async with error to AsyncResult"{
            val async = IO.raiseError<String>(RuntimeException("Runtime exception"))

            async.liftResult { SomeFailure(it.localizedMessage) }
                .test()
                .assertResult(Result.error(SomeFailure("Runtime exception")))
        }

        "should convert Async with success to AsyncResult"{
            val async = IO.just("Some value")

            async.liftResult { SomeFailure(it.localizedMessage) }
                .test()
                .assertResult(Result.success("Some value"))
        }
    }

}
