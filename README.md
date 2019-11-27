# base-types-kt
[![Build Status](https://travis-ci.org/VirtusLab/base-types-kt.svg?branch=master)](https://travis-ci.org/VirtusLab/base-types-kt)
[![codecov](https://codecov.io/gh/VirtusLab/base-types-kt/branch/master/graph/badge.svg)](https://codecov.io/gh/VirtusLab/base-types-kt)

Set of libraries with some basic types for Kotlin to support **domain-driven functional programming**. 

Incorporated [concepts](#References) are Railway-Oriented Programming (ROP)
 and Domain-Driven Design (DDD).

## Docs

- Refined data types
    - RawText
    - NonEmptyText
    - NonEmptySet
    - Digit
    - WholeNumber
    - NaturalNumber
    - NonNegativeRealNumber
    - ...
    

- ROP with Project Reactor
    - MonoResult = Mono<Result<T, E>>
    - CompletableResult = Mono<Result<Unit, E>>    
    - ...
        
- ROP with `arrow-kt`'s IO monad
    - AsyncResult = IO<Result<T, E>>
    - ...

- ROP with RxJava 2
    - SingleResult = Single<Result<T, E>>
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