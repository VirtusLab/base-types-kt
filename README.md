# base-types-kt
[![Build Status](https://travis-ci.org/VirtusLab/base-types-kt.svg?branch=master)](https://travis-ci.org/VirtusLab/base-types-kt)
[![codecov](https://codecov.io/gh/VirtusLab/base-types-kt/branch/master/graph/badge.svg)](https://codecov.io/gh/VirtusLab/base-types-kt)
[![Release](https://img.shields.io/github/v/release/VirtusLab/base-types-kt.svg)](https://github.com/VirtusLab/base-types-kt/releases)

Set of libraries with some basic types for Kotlin to support **domain-driven functional programming**. 

Incorporated [concepts](#References) are Railway-Oriented Programming (ROP)
 and Domain-Driven Design (DDD).

## Getting started

```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation("com.github.VirtusLab.base-types-kt:rop-reactor:<version>")
    implementation("com.github.VirtusLab.base-types-kt:rop-rx2:<version>")
    implementation("com.github.VirtusLab.base-types-kt:refined-types:<version>")
}
```

## Docs

- Refined data types
    - `RawText`
    - `NonEmptyText`
    - `NonEmptySet`
    - `Digit`
    - `WholeNumber`
    - `NaturalNumber`
    - `NonNegativeRealNumber`
    - ...
    

- ROP with Project Reactor
    - `MonoResult` = `MonoK<Either<E, V>>`
    - `FluxResult` = `FluxK<Either<E, V>>`
    - `CompletableResult` = `MonoResult<E, Nothing>`    
    - ...
        

- ROP with RxJava 2
    - `SingleResult` = `SingleK<Result<T, E>>`
    - ...
    
## Contribution
Everyone is more than welcome to contribute!

To build, run: `./gradlew clean build`

1. Create a GitHub issue for a feature or a bug fix
2. Fork project
3. Raise a Pull Request
    
## References

1. Refinement types concept: https://en.wikipedia.org/wiki/Refinement_type
2. Refinement types for Haskell: https://nikita-volkov.github.io/refined/
3. Railway-oriented programming: https://fsharpforfunandprofit.com/posts/recipe-part2/