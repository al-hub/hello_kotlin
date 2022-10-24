## [Coroutines Basic](https://kotlinlang.org/docs/coroutines-basics.html#table-of-contents)  

- basic code ( But **GlobalScope** is not recommend )
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

- structured concurrency  
```kotlin
fun main() = runBlocking {

  launch {
    delay(1000L)
    println("World 1")
  }
  
  launch {
    delay(1000L)
    println("World 2")
  }
  
  println("Hello")
}
```

- Scope builder and concurrency  
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

- Coroutines are light-weight  
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
            //yield()
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


## [Coroutines Suspending](https://kotlinlang.org/docs/composing-suspending-functions.html#asynchronous-timeout-and-resources)  
1. Sequential by default (일반코드로 작성해도 순차적으로 찍힌다!! - [안드로이드_테스트코드](https://blogattach.naver.net/bb2ea71404303184af4f2d1125cbb8c56531cd99/20200627_147_blogfile/cenodim_1593231145470_9qf7cv_zip/4_Composing_Suspending_Functions_android.zip))  
```kotlin
fun main() == runBlocking<Unit>{
  val time = measureTimeMillis {
    val one = doSomethingUsefulOne()
    val two = doSomethingUsefulTwo()
    println("The answer is ${one + two}")
  }
  println("Completed in $time ms")
}

suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // pretend we are doing something useful here
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}
```

2. Concurrent using async ( async, await 순차적으로 실행 or 동시 실행등을 제어 할 수 있음 )   
```kotlin
val time = measureTimeMillis {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    println("The answer is ${one.await() + two.await()}")
}
println("Completed in $time ms")
```

3. LAZY start ( LZAY를 걸어서 start 하는 방식 )
```kotlin
val time = measureTimeMillis {
    val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
    val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
    // some computation
    one.start() // start the first one
    two.start() // start the second one
    println("The answer is ${one.await() + two.await()}")
}
println("Completed in $time ms")
```

- Async-style : 이렇게는 하지마시오!!
```kotlin
// The result type of somethingUsefulOneAsync is Deferred<Int>
@OptIn(DelicateCoroutinesApi::class)
fun somethingUsefulOneAsync() = GlobalScope.async {
    doSomethingUsefulOne()
}

// The result type of somethingUsefulTwoAsync is Deferred<Int>
@OptIn(DelicateCoroutinesApi::class)
fun somethingUsefulTwoAsync() = GlobalScope.async {
    doSomethingUsefulTwo()
}

// note that we don't have `runBlocking` to the right of `main` in this example
fun main() {
    val time = measureTimeMillis {
        // we can initiate async actions outside of a coroutine
        val one = somethingUsefulOneAsync()
        val two = somethingUsefulTwoAsync()
        // but waiting for a result must involve either suspending or blocking.
        // here we use `runBlocking { ... }` to block the main thread while waiting for the result
        runBlocking {
            println("The answer is ${one.await() + two.await()}")
        }
    }
    println("Completed in $time ms")
}
```

- structured concurrency 형태로 하라!!
```kotlin
fun main() == runBlocking<Unit>{
  val time = measureTimeMillis {
      println("The answer is ${concurrentSum()}")
  }
  println("Completed in $time ms")
}

suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    one.await() + two.await()
}

suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // pretend we are doing something useful here
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}
```

- tip
```kotlin  
fun <T>println(msg: T) {
  println("$msg [${Thread.currentThread().name}] ")
  //kotlin.io.println("$msg [${Thread.currentThread().name}] ")
}
```
