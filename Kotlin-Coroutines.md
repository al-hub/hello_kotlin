## coroutine 기본개념
- coroutines are computations that can be suspended without blocking a thread.
- multiple entry/exit point  
( 쓰레드 풀에 동작 가능하도록 함수 구성하는 방식, 1950년대부터 제안되었고, kotlin API 제공 됨 )

## thread vs coroutine
- 장점
  - light-weight thread
  - structured concurency
- 단점
  - 익숙하지 않음
  - API가 계속 update 되고 있음

## Basic Code
```kotlin
fun main(args: Array<String>) = runBlocking {
  launch {
    println("coroutine")
  }
  println("hello")
}
```

## Advanced Code
Scope, Job, launch(async), suspend, cancel/exception, try-catch  
```kotlin
```
