- Exception Handling
  - Exception and error handling is an integral part of asynchronous programming.
  - It’s important to know how errors and exceptions are propagated through the process.


## Exception Propagation  
An uncaught exception, instead of being re-thrown, is “propagated up the job hierarchy”  

## Exception Re-throwing vs. Propagation  
In Kotlin, functions by default re-throw all the exceptions that were not caught inside them  
Therefore, the exception from the failingMethod can be caught in the parent try-catch block.  


일반적인 Exception (Re-Thrown)  
```kotlin
fun main() {
  try {
    failingMethod()
  } catch (ex: Exception) {
    println("Caught: $ex")
  }
}
```
```kotlin
launch {
  try {
    println("1. Exception thrown inside launch")
    throw RuntimeException()
  } catch (ex: Exception) {
    println("Exception ${ex.javaClass.simpleName} caught ...")
  }
}
```

But Exception Propagation up to …  
```kotlin
fun main() {
  val scope = CoroutineScope(Job())
  scope.launch {
    try {
      launch {
        throw RuntimeException(“…") //위쪽 방향(부모에게)으로 propagation 된다. 
      }
    } catch (ex: Exception) {
      // do something …
      // 원하는데로 작동이 안 된다.. - - 
    }
  }
  Thread.sleep(100)
}
```

```kotin
@Test
fun `Uncaught exceptions propagate`() = runBlocking {
  val scope = CoroutineScope(Job())
  val job = scope.launch {
    println("1. Exception thrown inside launch")
    // handled by Thread.defaultUncaughtExceptionHandler
    throw RuntimeException()
  }
  println("2. Wait for child to finish")
  job.join()
  println("3. Joined failed job: Now reachable code")
}
```

결과 (죽는것은 아니다. 왜냐면 부모가 scope 이기때문에 exception 만나면 cancel, 화면에만 dump)
```
2. Wait for child to finish
1. Exception thrown inside launch
Exception in thread "DefaultDispatcher-worker-1 …
…
3. Joined failed job: Now reachable cod
```

더 이상 전파을 멈추려면,
```kotlin
  val scope = CoroutineScope(SupervisorJob())
```

**However, when the parent of a coroutine is another coroutine, the parent Job will always be of type Job**

## Root coroutine
- Top Level Scope에 의해서 생성(launch) 되는 coroutine
- supervisorScope에 내에서 생성(launch) 되는 coroutine (supervisorScope 의 direct child)

## Exception Handling properties of supervisorScope

supervisorScope라 하더라도, Exception은 여전히 윗 쪽으로 propagation 된다...  
즉, Exception 제어에 대해서는 별도로 생각해야 한다.  
```
val scope = CoroutineScope(Job())
  scope.launch {
    val job1 = launch {
      println("starting Coroutine 1")
  }
  supervisorScope {
    val job2 = launch(ehandler) {
      throw RuntimeException("oops")
    }
    val job3 = launch {
      println("starting Coroutine 3")
    }
  }
}
```

