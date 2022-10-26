## Cancellation Mechanism
언제 작동되는가?  at the first suspension point  
scope를 cancel 하면 재사용할 수 없다.  
scope의 job에 대해서 cancel 하면, scope를 재상용할 수 있다.  

Cancellation이 되려면, cooperative 해야 한다.
(모든 suspend library function 은 재시작시에 확인한다.)

Quiz1. 어떻게 cooperative 할 수 있는가?
```kotlin  
object Uncooperative_Cancellation {

    private suspend fun printTwice() = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        var nextPrintTime = startTime
        while (true) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                log("I'm working..")
                nextPrintTime += 500
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            printTwice()
        }

        delay(1500)
        log("Cancelling job ...")
        job.cancelAndJoin()
    }
}
```

<details>
<summary>suspend library function 은 재시작시에 확인한다!!</summary>

```kotline
        while (isActive) {
```

```kotlin
        ensureActive()
```

```kotlin
        delay(1)
```

**이 녀석은 안됨**
```kotlin
        delay(0)
```

```kotlin
        yield()
```
</details>

## A coroutine in the cancelling state is not able to suspend!
To be able to call suspend functions when a coroutine is cancelled, switch the cleanup work in a NonCancellable coroutine context.

```kotlin
val job = launch {
    try {
        work()
    } catch (e: CancellationException){
        println(“Work cancelled!”)
    } finally {
        withContext(NonCancellable){
            delay(1000L) ЃѮ or some other suspend fun
            println(“Cleanup done!”)
        }
    }
}
```


## CancellationException 
We consume the CancellationException and prevent the coroutine from being cancelled properly.  
```kotlin
private suspend fun <T> fetchData(action: suspend () -> T) =
try {
liveData.value = Resource.Success(action())
} catch (ex: Exception) {
liveData.value = Resource.Error(ex.message)
if (ex is CancellationException) {
throw ex
}
}
```
