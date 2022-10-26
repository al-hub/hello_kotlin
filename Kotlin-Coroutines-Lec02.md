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
- Predefine 또는 Custom ( newSingleThreadContext ) 등 도 쓸 수 있다.  

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

Custom 방식  
```kotlin
@DelicateCoroutinesApi
object Custom_Dispatchers_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {

        val context = newSingleThreadContext("CustomDispatcher 1")
        launch(context) {
            coroutineInfo(0)
            delay(100)
        }.join()
        
        //반듯이 close를 불러줘야 한다.
        context.close() // make sure to close
        
        // Safe way: use를 쓰면 close를 알아서 불러준다.  
        newSingleThreadContext("CustomDispatcher 2").use { ctx ->
            launch(ctx) {
                coroutineInfo(0)
            }.join()
        }

        val context1 = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        launch(context1) {
            coroutineInfo(0)
        }.join()
        context1.close() // make sure to close

        /* TODO */
        // Use `use` to safely close the pool
    }
}
```

2022-10-26  
## Scope Builders  
- CoroutineScope() (기본형으로 사용하는 Factory function 임)
- MainScope() (최근에는 사용할 필요성이 없음)
- GlobalScope (ready made instance로 사용할수 있으나 권고하지 않음, 불필요한 리소스 낭비 가능성 있음)
- viewModelScope/lifecycleScope (Android 에서 사용 ready made object 임 , 유일하게 job 이 없음)
- coroutineScope/supervisorScope (scope function의 개념으로 접근)

## Coroutine Builder  
Coroutine Function의 Extension으로 선언 됨
- lanch
- async
- runBlocking (regular function 임,  extension function 아님)
- runTest (regular function 임)
- withContext (suspending function , withContext에서만 dispatcher를 바꾸어서 사용)

## 성격이 비슷한 녀석만 다시 모아본다면, Coroutine Scope Function (일반사항은 아님)  
목적: scope안에 sub-scope를 생성한다!!  
- coroutineScope
- supervisorScope
- withContext
- withTimeout/withTimeoutOrNull

### coroutineScope  
왜 필요한가? seqencial 하게 수행하지 않고, concurrency 하게 동작하게 하기 위해서
기존방식(두개의 dependency 없는 함수를 합쳐서 결과를 리턴해야하는 경우)  
```kotlin
// Data loaded sequentially, not simultaneously
suspend fun getUserProfile(): UserProfileData {
    val user = getUserData()
    val notifications = getNotifications()
    return UserProfileData(
        user = user,
        notifications = notifications,
    )
}

```

제안방식  
```kotlin
suspend fun getUserProfile(): UserProfileData =
    coroutineScope {
        val user = async { getUserData() }
        val notifications = async { getNotifications() }
        UserProfileData(
            user = user.await(),
            notifications = notifications.await(),
        )
    }
```


다른방법 (  GlobalScope.async , make as an extension function on CoroutineScope )
```kotlin
suspend fun CoroutineScope.getUserProfile(): UserProfileData {
…
}
```
잠재적인 문제점이 있을 수 있음으로 사용하지 말아라!  
‒ Requires passing the scope from function to function.  
‒ Any function that has access to the scope could easily abuse this access and for instance, cancel this scope with the cancel method.  
‒ Parent coroutine that called getUserProfile cancels for no good reason.  

try-catch가 어려운 case가 발행한다.
```kotlin
object Not_What_We_Want {
    data class Details(val name: String, val followers: Int)
    data class Tweet(val text: String)

    private suspend fun getFollowersNumber(): Int {
        delay(100)
        throw Error("Service exception")
    }

    private suspend fun getUserName(): String {
        delay(500)
        return "paula abdul"
    }

    private suspend fun getTweets(): List<Tweet> {
        delay(500)
        return listOf(Tweet("Hello, world"))
    }

    private suspend fun getUserDetails(scope: CoroutineScope): Details {
        val userName = scope.async { getUserName() }
        val followersNumber = scope.async { getFollowersNumber() }
        return Details(userName.await(), followersNumber.await())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val details = try {
            getUserDetails(this)
        } catch (e: Error) {
            null
        }
        log("User: $details")
        val tweets = async { getTweets() }
        log("Tweets: ${tweets.await()}")
    }
// Only Exception...
}
```

coroutineScope 사용 시 특징 - ID
//block스타일로 동작한다.(runBlocking처럼 동작)
//부오와 똑같은 아디이를 가진다. (called in-place) , 자식이 부모행세(역할)를 한다.  
```kotlin
object coroutineScope_Demo1 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("runBlocking: $coroutineContext")

        val a = coroutineScope {
            delay(1000).also {
                log("a: $coroutineContext")
            }
            10
        }
        log("a is calculated = $a")
        val b = coroutineScope {
            delay(1000).also {
                log("b: $coroutineContext")
            }
            20
        }
        log("a = $a, b = $b")
    }
}
```

corountieScope이 실행되는 동안 부모는 suspend 되있다.  
```
id를 출력해보면 부모와 같은 내용임
```

coroutineScope 사용 시 특징 - uncaught exception이 with through 되어서 try-catch 할 수 있다.  
```kotlin
object coroutineScope_Demo3 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("runBlocking begins")

        try {
            coroutineScope {
                log("Launching children ...")

                launch {
                    log("child1 starts")
                    delay(2000)
                }.onCompletion("child1")

                launch {
                    log("child2 starts")
                    delay(1000)
                    throw RuntimeException("Oops")
                }.onCompletion("child2")

                delay(10)

                log("Waiting until children are completed ...")
            }
        } catch (ex: Exception) {
            log("Caught exception: ${ex.javaClass.simpleName}")
        }

        log("Done!")
    }
}
```

coroutineScope is nowadays often used to wrap suspending main body.  
Think of it as the modern replacement for the runBlocking function.  
```
suspend fun main(): Unit = coroutineScope { ... }
```

### supervisorScope

**왕짜증CASE**  
supervisorScope를 쓰게되면, Global Exception Handler와 같이 써라!!  

supervisorScope: child가 fail되면 해당 child만 fail  
(**왕짜증CASE** : child가 fail이면 exception propagation 되고, 본인이 fail이면 with through 가 일어난다)  


### withContext  
실재로 쓸때는 dispathcer를 바꿀 때 주로 쓴다.  
- 물려받은 parent context를 override 하는 느낌  
- 블럭이 끝나면 back to the original dispatcher 로 된다.  
```kotlin  

```

Suspending Convention
suspending functions do not block the caller thread  
특히, 메인쓰레들를 suspend 시키는 짓을 하면 안된다!! → withContext을 써라!!  
(호출하는 side에서는 무지성으로 불러써도 된다!! **Main Safety** 가 된다.)  
예시)
```kotlin
suspend fun findBigPrime(): BigInteger =
    withContext(Dispatchers.Default) {
        BigInteger.probablePrime(4096, Random())
}
```
예시)
```kotlin
suspend fun BufferedReader.readMessage(): Message? =
    withContext(Dispatchers.IO) {
        readLine()?.parseMessage()
    }
```
