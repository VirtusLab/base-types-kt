package com.virtuslab.basetypes.rop.reactor

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.kotlintest.specs.StringSpec
import reactor.core.publisher.Flux

internal class FluxEitherKtTest : StringSpec() {

    init {
        "should map success" {
            val fluxEither: FluxEither<Nothing, String> = "Some value".toFluxRight()

            fluxEither.mapRight { "Some other value" }
                .test()
                .expectNext(Either.right("Some other value"))
                .verifyComplete()
        }

        "should handle exception when mapping success" {
            val fluxEither: FluxEither<String, String> = "Some value".toFluxRight()
            val runtimeException = RuntimeException()

            fluxEither.mapRight { if (true) throw runtimeException else "Some other value" }
                .test()
                .verifyError(RuntimeException::class.java)
        }

        "should handle exception when mapping either success" {
            val fluxEither: FluxEither<String, String> = "Some value".toFluxRight()
            val runtimeException = RuntimeException()

            fluxEither.flatMapEither { if (true) throw runtimeException else Either.right("Some other value") }
                .test()
                .verifyError(RuntimeException::class.java)
        }

        "should handle exception when flatMapping FluxEither" {
            val fluxEither: FluxEither<String, String> = "Some value".toFluxRight()
            val runtimeException = RuntimeException()

            fluxEither.flatMapRight { if (true) throw runtimeException else "Some other value".toFluxRight() }
                .test()
                .verifyError(RuntimeException::class.java)
        }

        "should keep error when mapping success" {
            val fluxEither: FluxEither<String, String> = "Some failure".toFluxLeft()

            fluxEither.mapRight { "Some other value" }
                .test()
                .expectNext(Either.left("Some failure"))
                .verifyComplete()
        }

        "should be able to map left in either"{
            val fluxEither: FluxEither<String, Nothing> = "exception".toFluxLeft()

            fluxEither.mapLeft { "Left from ${it}" }
                .test()
                .expectNext(Either.left("Left from exception"))
                .verifyComplete()
        }

        "should flatMap success"{
            val fluxEither: FluxEither<Nothing, String> = "Some value".toFluxRight()

            fluxEither.flatMapEither { x: String -> Either.right("$x some other") }
                .test()
                .expectNext(Either.right("Some value some other"))
                .verifyComplete()
        }

        "should keep error when flatMapping either success" {
            val fluxEither: FluxEither<String, String> = "Some left".toFluxLeft()

            fluxEither.flatMapEither { x: String -> Either.right("$x some other") }
                .test()
                .expectNext(Either.left("Some left"))
                .verifyComplete()
        }

        "should flatMap FluxEither"{
            val fluxEither: FluxEither<Nothing, String> = "Some value".toFluxRight()

            fluxEither.flatMapRight { x: String -> Either.right("$x some other").toFluxK() }
                .test()
                .expectNext(Either.right("Some value some other"))
                .verifyComplete()
        }

        "should keep error when flatMapping FluxEither"{
            val fluxEither: FluxEither<String, String> = "Some failure".toFluxLeft()

            fluxEither.flatMapRight { x: String -> (Either.right("$x some other") as Either<String, String>).toFluxK() }
                .test()
                .expectNext(Either.left("Some failure"))
                .verifyComplete()
        }

        "should convert Flux with error to FluxEither"{
            val flux = Flux.error<String>(RuntimeException("Runtime exception"))

            flux.liftEither { it.localizedMessage }
                .test()
                .expectNext(Either.left("Runtime exception"))
                .verifyComplete()
        }

        "should convert Flux with success to FluxEither"{
            val flux = Flux.just("Some value")

            flux.liftEither { it.localizedMessage }
                .test()
                .expectNext(Either.right("Some value"))
                .verifyComplete()
        }

        "should flat map FluxEither with error mapper"{
            val FluxStrings = "Some value".toFluxRight() as FluxEither<String, String>
            val FluxInts = 1.toFluxRight() as FluxEither<Int, Int>

            FluxStrings.flatMapRight({ FluxInts }, { "$it" })
                .test()
                .expectNext(Either.right(1))
                .verifyComplete()
        }

        "should flat map FluxEither right with error mapper"{
            val FluxStrings = "Some value".toFluxRight() as FluxEither<String, String>
            val FluxInts = 1.toFluxRight() as FluxEither<Int, Int>

            FluxStrings.flatMapRight({ FluxInts }, { "$it" })
                .test()
                .expectNext(Either.right(1))
                .verifyComplete()
        }
        "should flat map FluxEither left with error mapper"{
            val FluxStrings = "Some value".toFluxRight() as FluxEither<String, String>
            val FluxInts = 1.toFluxLeft() as FluxEither<Int, Int>

            FluxStrings.flatMapRight({ FluxInts }, { "$it" })
                .test()
                .expectNext(Either.left("1"))
                .verifyComplete()
        }
        "should flat map Either right with error mapper"{
            val FluxStrings = "Some value".toFluxRight() as FluxEither<String, String>
            val eitherInts = 1.right() as Either<Int, Int>

            FluxStrings.flatMapEither({ eitherInts }, { "$it" })
                .test()
                .expectNext(Either.right(1))
                .verifyComplete()
        }
        "should flat map Either left with error mapper"{
            val FluxStrings = "Some value".toFluxRight() as FluxEither<String, String>
            val eitherInts = 1.left() as Either<Int, Int>

            FluxStrings.flatMapEither({ eitherInts }, { "$it" })
                .test()
                .expectNext(Either.left("1"))
                .verifyComplete()
        }
    }
}

fun <T> T.toFluxRight(): FluxEither<Nothing, T> = this.right().toFluxK()
fun <T> T.toFluxLeft(): FluxEither<T, Nothing> = this.left().toFluxK()