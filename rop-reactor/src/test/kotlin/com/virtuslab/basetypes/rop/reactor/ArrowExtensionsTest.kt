package com.virtuslab.basetypes.rop.reactor

import arrow.core.Either
import arrow.fx.IO
import arrow.fx.unsafeRunSync
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import reactor.core.publisher.Mono

internal class ArrowExtensionsTest : StringSpec() {

    init {
        "should convert Mono value to IO" {
            Mono.just(1)
                .toIO()
                .unsafeRunSync() shouldBe 1
        }

        "should convert Mono error to IO" {
            shouldThrow<SomeException> {
                Mono.error<String>(SomeException)
                    .toIO()
                    .unsafeRunSync()
            }
        }
        "should convert MonoEither right to IO" {
            1.toMonoRight()
                .toIO()
                .unsafeRunSyncEither() shouldBe Either.right(1)
        }
        "should convert MonoEither left to IO" {
            1.toMonoLeft()
                .toIO()
                .unsafeRunSyncEither() shouldBe Either.left(1)
        }

        "should convert MonoEither error to IO" {
            shouldThrow<SomeException> {
                Mono.error<String>(SomeException)
                    .liftEither<String, String>()
                    .toIO()
                    .unsafeRunSyncEither()
            }
        }
        "should convert IO value to MonoEither" {
            IO.just(1)
                .toMono()
                .test()
                .expectNext(Either.right(1))
                .verifyComplete()
        }
        "should convert IO error to MonoEither" {
            IO.raiseError<Int, Int>(1)
                .toMono()
                .test()
                .expectNext(Either.left(1))
                .verifyComplete()
        }

        "should convert IO exception to MonoEither" {
            IO.raiseException<Int>(SomeException)
                .toMono()
                .test()
                .expectError(SomeException::class.java)
        }

    }
}

