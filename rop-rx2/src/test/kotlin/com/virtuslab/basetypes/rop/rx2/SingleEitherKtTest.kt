package com.virtuslab.basetypes.rop.rx2

import arrow.core.Either
import io.kotlintest.specs.StringSpec
import io.reactivex.Single


internal class SingleEitherKtTest : StringSpec() {
    init {
        "should map success" {
            val singleEither: SingleEither<Nothing, String> = "Some value".toSingleRight()

            singleEither.mapRight { "Some other value" }
                .test()
                .assertResult(Either.right("Some other value"))
                .assertComplete()
        }

        "should handle exception when mapping success" {
            val singleEither: SingleEither<String, String> = "Some value".toSingleRight()
            val runtimeException = RuntimeException()

            singleEither.mapRight { if (true) throw runtimeException else "Some other value" }
                .test()
                .assertError(RuntimeException::class.java)
        }

        "should handle exception when mapping either success" {
            val singleEither: SingleEither<String, String> = "Some value".toSingleRight()
            val runtimeException = RuntimeException()

            singleEither.flatMapEither { if (true) throw runtimeException else Either.right("Some other value") }
                .test()
                .assertError(RuntimeException::class.java)
        }

        "should handle exception when flatMapping SingleEither" {
            val singleEither: SingleEither<String, String> = "Some value".toSingleRight()
            val runtimeException = RuntimeException()

            singleEither.flatMapRight { if (true) throw runtimeException else "Some other value".toSingleRight() }
                .test()
                .assertError(RuntimeException::class.java)
        }

        "should keep error when mapping success" {
            val singleEither: SingleEither<String, String> = "Some failure".toSingleLeft()

            singleEither.mapRight { "Some other value" }
                .test()
                .assertResult(Either.left("Some failure"))
                .assertComplete()
        }

        "should be able to map left in either"{
            val singleEither: SingleEither<String, Nothing> = "exception".toSingleLeft()

            singleEither.mapLeft { "Left from ${it}" }
                .test()
                .assertResult(Either.left("Left from exception"))
                .assertComplete()
        }

        "should flatMap success"{
            val singleEither: SingleEither<Nothing, String> = "Some value".toSingleRight()

            singleEither.flatMapEither { x: String -> Either.right("$x some other") }
                .test()
                .assertResult(Either.right("Some value some other"))
                .assertComplete()
        }

        "should keep error when flatMapping either success" {
            val singleEither: SingleEither<String, String> = "Some left".toSingleLeft()

            singleEither.flatMapEither { x: String -> Either.right("$x some other") }
                .test()
                .assertResult(Either.left("Some left"))
                .assertComplete()
        }

        "should flatMap SingleEither"{
            val singleEither: SingleEither<Nothing, String> = "Some value".toSingleRight()

            singleEither.flatMapRight { x: String -> Either.right("$x some other").liftSingle() }
                .test()
                .assertResult(Either.right("Some value some other"))
                .assertComplete()
        }

        "should keep error when flatMapping SingleEither"{
            val singleEither: SingleEither<String, String> = "Some failure".toSingleLeft()

            singleEither.flatMapRight { x: String -> (Either.right("$x some other") as Either<String, String>).liftSingle() }
                .test()
                .assertResult(Either.left("Some failure"))
                .assertComplete()
        }

        "should liftmap either"{
            val either = Either.right("Some value")

            either.liftMapRight { x -> "$x 2".toSingleRight() }
                .test()
                .assertResult(Either.right("Some value 2"))
                .assertComplete()
        }

        "should convert Single with error to SingleEither"{
            val single = Single.error<String>(RuntimeException("Runtime exception"))

            single.liftEither { it.localizedMessage }
                .test()
                .assertResult(Either.left("Runtime exception"))
                .assertComplete()
        }

        "should convert Single with success to SingleEither"{
            val single = Single.just("Some value")

            single.liftEither { it.localizedMessage }
                .test()
                .assertResult(Either.right("Some value"))
                .assertComplete()
        }
    }

}
