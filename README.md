# hello_kotlin

structured concurrency  
: control flow (Scope) 을 도입하여, cancel(exception) propagation이 간결히 이루어지게 한다.([예시](https://suhwan.dev/2022/01/21/Kotlin-coroutine-structured-concurrency/))    

dispathcer  
: thread에 coroutine을 보낸다.([예시](https://kotlinworld.com/141))     


## [Coroutines Basic](https://kotlinlang.org/docs/coroutines-basics.html#table-of-contents)  

basic code
```kotlin
fun main() {
  val job = GlobalScope.launch{
    delay(1000L)
    println("World!)
  }
  
  println("Hello")
  Thread.sleep(2000L)
  //job.join()
}
```

Scope builder and concurrency  
```kotlin
// Sequentially executes doWorld followed by "Done"
fun main() = runBlocking {
    doWorld()
    println("Done")
}

// Concurrently executes both sections
suspend fun doWorld() = coroutineScope { // this: CoroutineScope
    launch {
        delay(2000L)
        println("World 2")
    }
    launch {
        delay(1000L)
        println("World 1")
    }
    println("Hello")
}
```

Coroutines are light-weight  
```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    repeat(100_000) { // launch a lot of coroutines
        launch {
            delay(5000L)
            print(".")
        }
    }
}
```


## [Coroutines Cancellation](https://kotlinlang.org/docs/cancellation-and-timeouts.html#asynchronous-timeout-and-resources)  
Cancellation is cooperative (취소되기 위해서는 협조적이여 한다)  

1. suspend func (delay or yield)  
```kotlin
val job = launch(Dispatchers.Default) {
    repeat(5) { i ->
        try {
            // print a message twice a second
            println("job: I'm sleeping $i ...")
            delay(500) 
        } catch (e: Exception) {
            // log the exception
            println(e)
        }
    }
}
delay(1300L) // delay a bit
println("main: I'm tired of waiting!")
job.cancelAndJoin() // cancels the job and waits for its completion
println("main: Now I can quit.")
```

2. Making computation code cancellable (isActive)  
```kotlin
val startTime = System.currentTimeMillis()
val job = launch(Dispatchers.Default) {
    var nextPrintTime = startTime
    var i = 0
    while (isActive) { // cancellable computation loop
        // print a message twice a second
        if (System.currentTimeMillis() >= nextPrintTime) {
            println("job: I'm sleeping ${i++} ...")
            nextPrintTime += 500L
        }
        println("isActive $isActive ... ")
    }
}
delay(1300L) // delay a bit
println("main: I'm tired of waiting!")
job.cancelAndJoin() // cancels the job and waits for its completion
println("main: Now I can quit.")
```

3. Closing resources with Finally  
```kotlin
val job = launch {
    try {
        repeat(1000) { i ->
            println("job: I'm sleeping $i ...")
            delay(500L)
        }
    } finally {
        println("job: I'm running finally")
    }
}
delay(1300L) // delay a bit
println("main: I'm tired of waiting!")
job.cancelAndJoin() // cancels the job and waits for its completion
println("main: Now I can quit.")
```


4. Timeout
```kotlin
val result = withTimeoutOrNull(1300L) {
    repeat(1000) { i ->
        println("I'm sleeping $i ...")
        delay(500L)
    }
    "Done" // will get cancelled before it produces this result
}
println("Result is $result")
```
