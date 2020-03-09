package com.virtuslab.basetypes.result.reactor

import arrow.core.Either
import com.virtuslab.basetypes.Either.reactor.MonoEither
import com.virtuslab.basetypes.Either.reactor.flatMapEither
import com.virtuslab.basetypes.Either.reactor.flatMapRight
import com.virtuslab.basetypes.Either.reactor.liftEither
import com.virtuslab.basetypes.Either.reactor.liftMapRight
import com.virtuslab.basetypes.Either.reactor.liftMono
import com.virtuslab.basetypes.Either.reactor.mapLeft
import com.virtuslab.basetypes.Either.reactor.mapRight
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

            monoEither.flatMapRight { x: String -> Either.right("$x some other").liftMono() }
                .test()
                .expectNext(Either.right("Some value some other"))
                .verifyComplete()
        }

        "should keep error when flatMapping MonoEither"{
            val monoEither: MonoEither<String, String> = "Some failure".toMonoLeft()

            monoEither.flatMapRight { x: String -> (Either.right("$x some other") as Either<String, String>).liftMono() }
                .test()
                .expectNext(Either.left("Some failure"))
                .verifyComplete()
        }

        "should liftmap either"{
            val either = Either.right("Some value")

            either.liftMapRight { x -> "$x 2".toMonoRight() }
                .test()
                .expectNext(Either.right("Some value 2"))
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
    }
}
