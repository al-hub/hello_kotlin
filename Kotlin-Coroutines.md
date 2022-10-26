## coroutine 기본개념
- coroutines are computations that can be suspended without blocking a thread.
- multiple entry/exit point  
(쓰레드 풀에 Runnable 함수 구성하는 방식, 1950년대부터 제안되었고, kotlin에서 API로 사용가능)

## thread vs coroutine
- 장점
  - light-weight thread
  - structured concurrency (job들의 동시성 유지)  
- 단점
  - 익숙하지 않음
  - API가 계속 update 되고 있음

## hello world
```kotlin
fun main(args: Array<String>) = runBlocking {
  launch {
    println("coroutine")
  }
  println("hello")
}
```

## advanced 예제
CoroutineScope, Job(), launch/async, suspend, exception, try-catch, withContext, isActive
```kotlin
@JvmInline
private value class Image(val name: String)

private suspend fun loadImage(name: String): Image {
    log("loadImage")
    if(isActive)
        delay(1000)
    return Image(name)
}

val customDispatcher = newSingleThreadContext("CustomDispatcher1")
private suspend fun loadImageFail(name: String): Image = withContext(customDispatcher){
    log("loadImageFail")
    yield()
    throw RuntimeException("oops")
}

private fun combineImages(image1: Image, image2: Image): Image =
    Image("${image1.name} & ${image2.name}")

object Advanced_DEMO {
    private suspend fun loadAndCombine(name1: String, name2: String): Image = coroutineScope {
        log("loadAndCombine")
        val deferred1 = async(start = CoroutineStart.LAZY) { loadImage(name1) }
        val deferred2 = async() { loadImageFail(name2) }          //try to check loadImageFail(name2)
        deferred1.start()                                         //or deferred1.join()
        combineImages(deferred1.await(), deferred2.await())
    }

    @JvmStatic
    fun main(args: Array<String>) {

        var image: Image? = null
        val scope = CoroutineScope(Job() + CoroutineName("MyScope")) //You should choose Job() or SupervisorJob()

        val job = scope.launch(Dispatchers.IO) {
            log("Level-1 Coroutine")

            launch(CoroutineName("Level-2")) {
                log("Level-2 Coroutine")

                //structured concurency
                //try-catch가 없더라도, error만 dump되고, coroutine에서 isCancelled = true 처리를 해 준다.
                try {
                    image = loadAndCombine("apple", "kiwi")
                } catch (e: Exception) {
                    log("Caught: $e")
                }

            }.onCompletion("Level-2")

        }.onCompletion("Level-1")

        runBlocking { job.join() }

        log("combined image = $image")
    }

}

//utils
val log: Logger = LoggerFactory.getLogger("Coroutines")

fun log(msg: Any?) {
    log.info(msg.toString())
}
fun spaces(level: Int) = "\t".repeat(level)

fun Job.onCompletion(name: String, level: Int = 0): Job = apply {
    invokeOnCompletion {
        log("${spaces(level)}$name: isCancelled = $isCancelled, exception = ${it?.javaClass?.name}")
    }
}
```

<details>
<summary>결과</summary>

loadImage(name2)(RuntimeException 없을때),
```
22:45:53.954 [DefaultDispatcher-worker-1 @MyScope#1] INFO Coroutines - Level-1 Coroutine
22:45:53.969 [DefaultDispatcher-worker-2 @Level-2#3] INFO Coroutines - Level-2 Coroutine
22:45:53.973 [DefaultDispatcher-worker-2 @Level-2#3] INFO Coroutines - loadAndCombine
22:45:53.978 [DefaultDispatcher-worker-5 @Level-2#4] INFO Coroutines - loadImage
22:45:53.978 [DefaultDispatcher-worker-3 @Level-2#5] INFO Coroutines - loadImage
22:45:55.024 [DefaultDispatcher-worker-1 @Level-2#3] INFO Coroutines - Level-2: isCancelled = false, exception = null
22:45:55.024 [DefaultDispatcher-worker-1 @Level-2#3] INFO Coroutines - Level-1: isCancelled = false, exception = null
22:45:55.039 [main] INFO Coroutines - combined image = Image(name=apple & kiwi)

Process finished with exit code 0
```
	
loadImageFail(name2)+try-catch가 있을 때,  
```
22:40:49.952 [DefaultDispatcher-worker-1 @MyScope#1] INFO Coroutines - Level-1 Coroutine
22:40:49.967 [DefaultDispatcher-worker-2 @Level-2#3] INFO Coroutines - Level-2 Coroutine
22:40:49.972 [DefaultDispatcher-worker-2 @Level-2#3] INFO Coroutines - loadAndCombine
22:40:49.978 [DefaultDispatcher-worker-5 @Level-2#4] INFO Coroutines - loadImage
22:40:49.984 [CustomDispatcher1 @Level-2#5] INFO Coroutines - loadImageFail
22:40:50.089 [DefaultDispatcher-worker-3 @Level-2#3] INFO Coroutines - Caught: java.lang.RuntimeException: oops
22:40:50.110 [DefaultDispatcher-worker-3 @Level-2#3] INFO Coroutines - Level-2: isCancelled = false, exception = null
22:40:50.110 [DefaultDispatcher-worker-3 @Level-2#3] INFO Coroutines - Level-1: isCancelled = false, exception = null
22:40:50.112 [main] INFO Coroutines - combined image = null
```

loadImageFail(name2)+try-catch가 없을 때,  
```shell
22:44:22.531 [DefaultDispatcher-worker-1 @MyScope#1] INFO Coroutines - Level-1 Coroutine
22:44:22.546 [DefaultDispatcher-worker-2 @Level-2#3] INFO Coroutines - Level-2 Coroutine
22:44:22.550 [DefaultDispatcher-worker-2 @Level-2#3] INFO Coroutines - loadAndCombine
22:44:22.556 [DefaultDispatcher-worker-3 @Level-2#4] INFO Coroutines - loadImage
22:44:22.561 [CustomDispatcher1 @Level-2#5] INFO Coroutines - loadImageFail
22:44:22.683 [DefaultDispatcher-worker-2 @Level-2#3] INFO Coroutines - Level-2: isCancelled = true, exception = java.lang.RuntimeException
Exception in thread "DefaultDispatcher-worker-2 @Level-2#3" java.lang.RuntimeException: oops
	at com.scarlet.coroutines.advanced.C10_ALL_INFO_EXAMPLEKt$loadImageFail$2.invokeSuspend(C10_ALL_INFO_EXAMPLE.kt:23)
	(Coroutine boundary)
	at com.scarlet.coroutines.advanced.C10_ALL_INFO_EXAMPLEKt.loadImageFail(C10_ALL_INFO_EXAMPLE.kt:20)
	at com.scarlet.coroutines.advanced.Advanced_DEMO$loadAndCombine$2$deferred2$1.invokeSuspend(C10_ALL_INFO_EXAMPLE.kt:33)
	at com.scarlet.coroutines.advanced.Advanced_DEMO.loadAndCombine-w76TJ38(C10_ALL_INFO_EXAMPLE.kt:30)
	at com.scarlet.coroutines.advanced.Advanced_DEMO$main$job$1$1.invokeSuspend(C10_ALL_INFO_EXAMPLE.kt:53)
	Suppressed: kotlinx.coroutines.DiagnosticCoroutineContextException: [CoroutineName(MyScope), CoroutineId(1), "MyScope#1":StandaloneCoroutine{Cancelling}@394fcbd6, Dispatchers.Default]
Caused by: java.lang.RuntimeException: oops
	at com.scarlet.coroutines.advanced.C10_ALL_INFO_EXAMPLEKt$loadImageFail$2.invokeSuspend(C10_ALL_INFO_EXAMPLE.kt:23)
	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:304)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:829)
22:44:22.699 [DefaultDispatcher-worker-2 @Level-2#3] INFO Coroutines - Level-1: isCancelled = true, exception = java.lang.RuntimeException
22:44:22.706 [main] INFO Coroutines - combined image = null
```
</details>
