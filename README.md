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
