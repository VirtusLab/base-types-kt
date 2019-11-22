# base-types-kt

Set of libraries with some basic types for Kotlin to support **domain-driven functional programming**. 

Incorporated concepts are Railway-Oriented Programming (ROP)
 and Domain-Driven Design (DDD).

### Work in progress

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
        
        
- ROP with `arrow-kt`'s IO monad
    - AsyncResult = IO<Result<T, E>>

### Coming in the future
- ROP with RxJava 2
    - SingleResult = Single<Result<T, E>>
    - CompletableResult
    
