# Deep dive into coroutines (2022-10-25)  

- Main Building Block of Corouties  
    - CoroutineScope  
    - CoroutineContext  
      - Job  
      - Coroutine Dispatchers  
      - CoroutineName  
      - CoroutineExceptionHandler  
    - CoroutineBuilder  
 
- Coroutines ...
  - (비슷하다)without bloacking the thread ( light-weight )
    - threads는 100개 정도는 괜찮겠지만 100,000 정도면 die....
    - trehads MByte, coroutine kByte
  - (비슷하다)Empowered Runnable ( thread에 의해서 실행되는 코드 )
    - 동일한 coroutine이 서로다른 thread에 의해서 실행될 수 도 있다!!  
    - CPS (Continuation Passing Style)

thread: 136615 ms
```kotlin
fun main() {
  repeat(200_000) {
    thread {
    println("Hello thread $it")
    }
  }
}
```

thread: 437 ms
```kotlin
fun main() = runBlocking{
  repeat(200_000) {
    launch {
      println("Hello coroutine $it")
    }
  }
}
```

## CPS (Continuation Passing Style)  
(suspend)를 해주면, 내부적으로 CPS로 동작되도록 구성된다.  

It is a sequence of callbacks.  
In kotlin, coroutine suspension/resume is implemented as a state machine.  

classic  
```kotlin
fun add(a: Int, b: Int): Int = a + b
fun mult(a: Int, b: Int): Int = a * b

fun doWork(): Int {
    // label1
    val step1 = add(3, 4)
    // label2
    val step2 = add(5, 6)
    // label3
    val step3 = mult(step1, step2)
    return step3
}
```

CPS (Continuation Passing Style)  
```kotlin
fun <R> addCPS(a: Int, b: Int, cont: (Int) -> R): R {
    return cont(add(a, b))
}
fun <R> multCPS(a: Int, b: Int, cont: (Int) -> R): R {
    return cont(mult(a, b))
}

fun doWorkCPS(): Int =
    addCPS(3, 4) { step1 ->
        addCPS(5, 6) { step2 ->
            multCPS(step1, step2) { step3 ->
                step3
            }
        }
    }
```

CPS (Continuation Passing Style) + identity functin
```kotlin
```

kotlin 에서 (suspend) 함수를 하면,  
```kolin
suspend fun createPost(token: Token, item: Item): Post { … }
```

Java/JVM 에서 ( CPS ) 로 바꾸면서, State Machine을 이용한다.    
```java
Object createPost(Token token, Item item, Continuation<Post> cont) { … }
```

## CPS Transform   
coroutine이 kotline에서는 어떻게 구현하고 있을까?
state machine과 labeld을 이용하여 suspend - resume style로 내부에서 구현하고 있다.  
결론, suspend function 함수 body내에서 불리는 곳을 suspension points 라고 부른다고 할 수 있다.  
```kotlin
fun postItem(item: Item, completion: Continuation<Unit>): Any {
    val token = requestToken()
    val post = createPost(token, item)
    completion.resume(showPost(post))
}
```

suspension point는 label을 사용해서 각 state를 나타낼 수있다.
```kotlin
fun postItem(item: Item, completion: Continuation<Any?>) {
    // Label 0 -> first execution
    val token = requestToken()
    // Label 1 -> resumes from requestToken
    val post = createPost(token, item)
    // Label 2 -> resumes from createPost
    completion.resume(showPost(post))
}
```

좀 더, realistic하게 표현한다면( Continuation에 들어 있는 label을 이용하여 suspend-resume이 동작 )  
```kotlin  
fun postItem(item: Item, completion: Continuation<Any?>) {
    when(label) {
        0 -> { // Label 0 -> first execution
            requestToken()
        }
        1 -> { // Label 1 -> resumes from requestToken
            createPost(token, item)
        }
        2 -> { // Label 2 -> resumes from createPost
            completion.resume(showPost(post))
        }
        else -> throw IllegalStateException(...)
    }
}

```

주요 저장되는 정보는 다음과 같다. ( 컴파일러가 생성하는 private class 내용 )  
```kotlin  
fun postItem(item: Item?, completion: Continuation<Any?>) {

    class PostItemStateMachine(
        completion: Continuation<Any?> // callback to the fun that called postItem
    ): CoroutineImpl(completion) {
        // Local variables of the suspend function
        var token: Token? = null
        var post: Post? = null
        // Common objects for all CoroutineImpls
        var result: Any? = null
        var label: Int = 0
        // this function calls the `postItem` again to trigger the
        // state machine (label will be already in the next state)
        override fun invokeSuspend(result: Any?) {
            this.result = result // result of the previous state's computation
            postItem(null, this)
        }
    }
    
    //...
    
    val continuation = completion as? PostItemStateMachine
                                    ?: PostIemStateMachine(completion)

    when(continuation.label) {
        0 -> {
            throwOnFailure(continuation.result) // Checks for failures
            // next time this coroutine is called, it should go to state 1
            continuation.label = 1
            // The continuation object is passed to requestToken to resume
            // this state machine's execution when it finishes
            requestToken(continuation)
        }
        1 -> {
            throwOnFailure(continuation.result)
            // Gets the result of the previous state
            continuation.token = continuation.result as Token
            continuation.label = 2
            createPost(continuation.token, item, continuation)
        }
            //... leaving out the last state on purpose
    }
    
}

```

## CoroutineContext vs. CoroutineScope  
https://youtu.be/w0kfnydnFWI?t=87  

### CoroutineContext  
- Every coroutine has a coroutine context which is immutable.
   - CoroutineContext can be inherited from parent to child.
   - The CoroutineContext is an indexed set of elements (set + map) 
       - Element들로 구성되어어 있고, key와 value가 있다. (UID가 있다)
       - Job: controls the lifecycle of the corountine.
       - CoroutineDispatcher: dispatchers work to the appropriate thread
       - CoroutineName: name of the coroutine (for debugging)
       - CoroutineExceptionHandler: handles uncaught exceptions.
       - Element는 '+' 를 사용할 수 있다.
       
프로그램상에서는 CoroutineContext를 어떻게 접근할 수 있는가?  
```kotlin
fun main() = runBlocking {
    println(Thread.currentThread().name)
    println("$coroutineContext")
    println("${coroutineContext[Job]}")
    println("${coroutineContext[ContinuationInterceptor]}") //dispatcher
}
```
```
main
[BlockingCoroutine{Active}@335eadca, BlockingEventLoop@210366b4]
BlockingCoroutine{Active}@335eadca
BlockingEventLoop@210366b4
```
(hash번호는 object number 임)     

### CoroutineScope  
- Every coroutine must be created inside the coroutine scope to control lifecycle.
- coroutines 은 scope.cancel() 로도 cancel 할 수 있다.
- 안드로이드에서는 viewModelScope, lifecycleScope 을 사용할 수 도 있다.

manullay 생성 할 때는(Factory Function을 사용해서 할 수 있다),  
```
val scope = CoroutineScope(Job() + Dispatchers.Main)
val job = scope.launch {
    // new coroutine
}
```

## [Job](https://medium.com/androiddevelopers/exceptions-in-coroutines-ce8da1ec060c)  
- A coroutine itself is represented by a Job  
- Responsible for coroutine’s lifecycle, cancellation, and parent-child relations. 
- Coroutine builders (launch or async) returns a Job instance that uniquely identifies the coroutine.  
- You can also pass a Job to a CoroutineScope to keep a handle on its lifecycle. 

Job lifecycle
![image](https://user-images.githubusercontent.com/56526241/197700988-60dd30c6-20ad-4f55-9eef-b693f279fb4e.png)

State  
현재, Job의 상태는 isActive, isCompleted, isCancelled 의 flag로 유추해야 한다.[ref](https://proandroiddev.com/kotlin-coroutine-job-hierarchy-finish-cancel-and-fail-2d3d42a768a9)
![image](https://user-images.githubusercontent.com/56526241/197701352-4fa08038-33ff-482e-94be-3b3d4f19b9f2.png)

**엄청나게 중요한 부분**  
- 새로 생성된 corountine의 CorountineContext  
    - 새로운 corountine은 항상 새로운 job이 assign 된다.  

Quiz
```kotlin
val scope = CoroutineScope(Job())
scope.launch(SupervisorJob()) {
    launch {
        // child 1
    }
    
    launch {
        // child 2
    }
}
```
Q: Given the following code snippet, can you identify what kind of Job “child 1” has as a parent?  
   Job or SupervisorJob?  
A: 새로 생성된는 job은 normal job 이다.  
![image](https://user-images.githubusercontent.com/56526241/197709215-9d74858d-d924-4395-84fc-df4f71fdd1be.png)
 
   
inheritance에 관련된 오해와 진실
![image](https://user-images.githubusercontent.com/56526241/197704183-dabc9962-0efc-4616-9615-deb42b4a1a0f.png)
Scope Context ≠ Parent context  
부모의 Scope Context + Additional Context -> Parent context 를 그대로 물려 받되, Child Job은 새로 생성 됨  
즉, Child의 parent context는 parent context와 다를 수 있다  

## SupervisorJob
propagation 을 멈춘다.
```kotlin
object Standalone_SupervisorJob_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val supervisorJob = SupervisorJob()

        val child1 = launch(supervisorJob) {
            log("child1")
            delay(500)
            throw RuntimeException("Oops")
        }.onCompletion("child1")

        val child2 = launch(supervisorJob) {
            log("child2")
            delay(1000)
        }.onCompletion("child2")

        joinAll(child1, child2)
        log("Done")
    }
}
```

## Dispatchers  
theadpool을 선택  

- Dispatchers.Default
    - CPU-intensive computation
- Dispatchers.Main
    - UI events
    - Need to include dependencies like Android, Swing, JavaFX, etc.
- Dispatchers.IO ( default 64개 )
    - Network IO, Disk IO, etc.
- Dispatchers.Unconfined
    - Not recommended
```kotlin
object DefaultDispatchers_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        log("# processors = ${Runtime.getRuntime().availableProcessors()}")

        repeat(20) {
            launch(Dispatchers.Default) {
                // To make it busy
                List(1000) { Random.nextLong() }.maxOrNull()

                log("Running on thread: ${Thread.currentThread().name}")
            }
        }
    }
}
```
