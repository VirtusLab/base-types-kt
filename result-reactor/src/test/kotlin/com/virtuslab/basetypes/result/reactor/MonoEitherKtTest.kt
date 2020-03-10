package com.virtuslab.basetypes.result.reactor

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.virtuslab.basetypes.Either.reactor.MonoEither
import com.virtuslab.basetypes.Either.reactor.any
import com.virtuslab.basetypes.Either.reactor.flatMapEither
import com.virtuslab.basetypes.Either.reactor.flatMapRight
import com.virtuslab.basetypes.Either.reactor.liftEither
import com.virtuslab.basetypes.Either.reactor.mapLeft
import com.virtuslab.basetypes.Either.reactor.mapRight
import com.virtuslab.basetypes.Either.reactor.toMono
import io.kotlintest.specs.StringSpec
import reactor.core.publisher.Mono

internal class MonoEitherKtTest : StringSpec() {

    init {
        "should map success" {
            val monoEither: MonoEither<Nothing, String> = "Some value".toMonoRight()

            monoEither.mapRight { "Some other value" }
                .test()
                .expectNext(Either.right("Some other value"))
                .verifyComplete()
        }

        "should handle exception when mapping success" {
            val monoEither: MonoEither<String, String> = "Some value".toMonoRight()
            val runtimeException = RuntimeException()

            monoEither.mapRight { if (true) throw runtimeException else "Some other value" }
                .test()
                .verifyError(RuntimeException::class.java)
        }

        "should handle exception when mapping either success" {
            val monoEither: MonoEither<String, String> = "Some value".toMonoRight()
            val runtimeException = RuntimeException()

            monoEither.flatMapEither { if (true) throw runtimeException else Either.right("Some other value") }
                .test()
                .verifyError(RuntimeException::class.java)
        }

        "should handle exception when flatMapping MonoEither" {
            val monoEither: MonoEither<String, String> = "Some value".toMonoRight()
            val runtimeException = RuntimeException()

            monoEither.flatMapRight { if (true) throw runtimeException else "Some other value".toMonoRight() }
                .test()
                .verifyError(RuntimeException::class.java)
        }

        "should keep error when mapping success" {
            val monoEither: MonoEither<String, String> = "Some failure".toMonoLeft()

            monoEither.mapRight { "Some other value" }
                .test()
                .expectNext(Either.left("Some failure"))
                .verifyComplete()
        }

        "should be able to map left in either"{
            val monoEither: MonoEither<String, Nothing> = "exception".toMonoLeft()

            monoEither.mapLeft { "Left from ${it}" }
                .test()
                .expectNext(Either.left("Left from exception"))
                .verifyComplete()
        }

        "should flatMap success"{
            val monoEither: MonoEither<Nothing, String> = "Some value".toMonoRight()

            monoEither.flatMapEither { x: String -> Either.right("$x some other") }
                .test()
                .expectNext(Either.right("Some value some other"))
                .verifyComplete()
        }

        "should keep error when flatMapping either success" {
            val monoEither: MonoEither<String, String> = "Some left".toMonoLeft()

            monoEither.flatMapEither { x: String -> Either.right("$x some other") }
                .test()
                .expectNext(Either.left("Some left"))
                .verifyComplete()
        }

        "should flatMap MonoEither"{
            val monoEither: MonoEither<Nothing, String> = "Some value".toMonoRight()

            monoEither.flatMapRight { x: String -> Either.right("$x some other").toMono() }
                .test()
                .expectNext(Either.right("Some value some other"))
                .verifyComplete()
        }

        "should keep error when flatMapping MonoEither"{
            val monoEither: MonoEither<String, String> = "Some failure".toMonoLeft()

            monoEither.flatMapRight { x: String -> (Either.right("$x some other") as Either<String, String>).toMono() }
                .test()
                .expectNext(Either.left("Some failure"))
                .verifyComplete()
        }

        "should convert Mono with error to MonoEither"{
            val mono = Mono.error<String>(RuntimeException("Runtime exception"))

            mono.liftEither { it.localizedMessage }
                .test()
                .expectNext(Either.left("Runtime exception"))
                .verifyComplete()
        }

        "should convert Mono with success to MonoEither"{
            val mono = Mono.just("Some value")

            mono.liftEither { it.localizedMessage }
                .test()
                .expectNext(Either.right("Some value"))
                .verifyComplete()
        }

        "should convert MonoEither to boolean"{
            val mono = "Some value".toMonoRight()

            mono.any { it == "Some value" }
                .test()
                .expectNext(true)
                .verifyComplete()
        }

        "should flat map MonoEither with error mapper"{
            val monoStrings = "Some value".toMonoRight() as MonoEither<String, String>
            val monoInts = 1.toMonoRight() as MonoEither<Int, Int>

            monoStrings.flatMapRight({ monoInts }, { "$it" })
                .test()
                .expectNext(Either.right(1))
                .verifyComplete()
        }

        "should flat map MonoEither right with error mapper"{
            val monoStrings = "Some value".toMonoRight() as MonoEither<String, String>
            val monoInts = 1.toMonoRight() as MonoEither<Int, Int>

            monoStrings.flatMapRight({ monoInts }, { "$it" })
                .test()
                .expectNext(Either.right(1))
                .verifyComplete()
        }
        "should flat map MonoEither left with error mapper"{
            val monoStrings = "Some value".toMonoRight() as MonoEither<String, String>
            val monoInts = 1.toMonoLeft() as MonoEither<Int, Int>

            monoStrings.flatMapRight({ monoInts }, { "$it" })
                .test()
                .expectNext(Either.left("1"))
                .verifyComplete()
        }
        "should flat map Either right with error mapper"{
            val monoStrings = "Some value".toMonoRight() as MonoEither<String, String>
            val eitherInts = 1.right() as Either<Int, Int>

            monoStrings.flatMapEither({ eitherInts }, { "$it" })
                .test()
                .expectNext(Either.right(1))
                .verifyComplete()
        }
        "should flat map Either left with error mapper"{
            val monoStrings = "Some value".toMonoRight() as MonoEither<String, String>
            val eitherInts = 1.left() as Either<Int, Int>

            monoStrings.flatMapEither({ eitherInts }, { "$it" })
                .test()
                .expectNext(Either.left("1"))
                .verifyComplete()
        }
    }
}

