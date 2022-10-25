# Kotlin Coroutines (2022-10-24)  

- Coroutine 테스트 시, MockK 권고 (Mockito 도 가능은 함)  
  - JUnit도 사용  
  - LiveData (java에서 ViewModel - View 사이에 safe한 관리를 위해 사용): kotlin에서는 kotlin flow가 사용  
  - Reactive Stream, RxJava, RxKotlin

- 느낌
  - stop 느낌
  - 기차길 느낌
  - Trampoline느낌, flatmap: list of list를 하나의 list로 펼칠 때 -> controller를 줬다 받았다 하면서 진행

- 디버깅설정: Edit configuration -> Edit configuration Template  
  \-Dkotlinx.coroutines.debug
  
- coroutine 종료 시, builder에 callback method를 등록시켜 놓을 수 있다. 예시) .onCompletion("child1")  
사전에 정의 해 놓고, 사용해야 함  
```kotlin
// DO NOT APPLY LIKE THIS: CoroutineScope(Job()).onCompletion("scope"); use scope.completeStatus() instead!!
fun CoroutineScope.onCompletion(name: String): CoroutineScope = apply {
    coroutineContext.job.invokeOnCompletion {
        log("$name: isCancelled = ${coroutineContext.job.isCancelled}, exception = ${it?.javaClass?.name}")
    }
}
```
- 기타
  - Generators
  - The Art of computer Programming (읽지않는(?) 필수책)  
  - category theory 를 공부해둬야 한다. (언제 어떻게 적용하는지 알면 java, android 등 여러군데에서 쓰인다.)    
flatmap, map  


## 이론
- 개념: coroutine은 multiple entry/exit point  
- 공식: Basically, coroutines are computations that can be suspended without blocking a thread  
- A coroutine is a sequence of computations,
  each of which may be suspended(or paused) and resulmed at some point,
  without blocking the thread that executes it.
  
- block의 의미: blocking io api call을 했을 때, (readline, fscanf 등등... )
               하지만, CPU-intensive computation (CPU-bound task)

blocking IO
```kotlin
fun BufferedReader.readMessage(): Message? =
  readLine()?.parseMessage()
```

CPU-intensive
```kotlin
fun findBigPrime(): BigInteger =
  BigInteger.probablePrime(4096, Random())
```

- blocking code 부분이 어떻게 실행되는가?  
임의의 thread T가 살행한다고 생각해보자.  
coroutine body가 실행된다고 할 때, 본이 직접 실행하다가, suspend point를 만나면,  
block된 main thread는 다른 일을 하면 된다. 그런데 누군가는 suspend를 해 줘야하는데, background thread가 suspend된 task를 받아서 실행한다.  
완료되면 main thread에게 돌려준다. 완료가 되면 실행시키고 다시 suspend point를 만나면 background thread 중에 하나를 시킨다.  
즉, main thread는 block 된적이 없다. -> non-blocking suspend computation이라 부를 수 있다.  
(필요하다면 background thread에게 시키지 않고, main thread에게 시킬 수 도 있다.)  

자 코드로 해 보자!! explicit하게 표현해줘야 한다. (suspending function)    
```kotlin
suspend fun createPost(token: Token, item: Item): Post {...}
```
**<span style="color:red">주의</span>**, suspend 만 붙인다고 되는것은 아님! ( 예제 해보기 !!)

## Asynchronous
- callbacks, future/promise/rx, coroutines  
- 복잡하다.
  - Race Conditions, Back Pressure, Leaked Resources ...

## Sync vs Async

block style
```kotlin
fun postItem(item: Item) {
  val token = requestToken()
  val post =  createPost(token, item)
  showPost(post)
}
```

non-block style (callback) : 자칫하면 callback hell (exception handling 어렵다)  
```kotlin
fun postItem(item: Item) {
  requestToken() { token -> 
      createPost(token, item) { post ->
          showPost(post)
      }
  }  
}
```

non-block style (promise/future)  
```kotlin
fun postItem(item: Item) {
    requestToKen()
      .thenCompose { token ->
            createPost(token, item) }
      .thenAccept { post ->
            showPost(post)  }
}
```

non-block style (RxJava) : operator가 복잡하다.
```kotlin
fun requestToken(): Single<Token>
fun createPost(token: Token, item: Item): Single<Post>
fun showPost(post: Post)
fun postItem(item: Item) {
    requestToken()
        .flatMap { token ->
            createPost(token, item)
        }
        .subscribeOn(Schedulers.io())
        .observerOn(AndroidSchedulers.mainThread())
        .subscribe { post ->
            showPost(post)
        }
}

```

non-block style (Coroutine) , direct style 즉 non-block 안에서는 sequencial 한 코드 유지가 가능하다!
```kotlin
suspend fun postItem(item: Item) {
  val token = requestToken()
  val post = createPost(token, item)
  showPost(post)
}
```
**<span style="color:red">주의</span>**, suspend 만 붙인다고 되는것은 아님!  
(body안에 suspend func 이 있어야 함)


- 장점들
  - regular loop
  - regular exception handling
  - regular higher-order function (foreach, let, apply, let, also, repeat, filter, map, use)  


higher-order function ( 함수를 인자로 사용 시 ) 
```kotlin
suspend fun createPost(token: Token, item: Item): Post {…}

val post = retryIO {
  createPost(token, item)
}

suspend fun <T> retryIO(block: suspend () -> T): T {
  var backOffTime = 1000L // start with 1 sec
  while (true) {
    try {
      return block()
    } catch (e: IOException) {
       e.printStackTrace() // log the error
   }
    delay(backOffTime)
    backOffTime = minOf(backOffTime * 2, 60_000L)
  }
}
```


## Coroutine 사용방법  

### CorountineScope  
**Coroutine** Builder를 호출하기 위한 것!!
-> 아무데서나 호출할 수 없고, 약간의 제약사항(CoroutineScope)이 있다.  
   즉 postItem이 아니라 CoroutineScope.postItem 처럼 extention function에서 호출해야 한다.   

```kotlin
val scope = CoroutineScope(Job())
scope.launch {
  println("Hello, I am coroutine")
}
```

- ready-made scope  
  - lifecycleScope  
  - viewModelScope  
  - GlobalScope ( **not recommend** )  


### Calling Suspending Functions  
일반적인 regular 함수에서는 suspend function 을 부를 수 없다!  
-> suspend 함수는 suspend 함수, regular function 을 부를 수 있다.  


### Coroutine Builder  
regular 함수에서 suspend function을 부르기 위해서는
coroutine builder를 통해서 부른다. ( CoroutineScope의 extention으로 되어 있음)

- **lanuch**       : to fire and forget, **니가 알아서 해**
- **async**        : to get a result asynchronously, **결과값 활용하고 싶을 때** 
- **runBlocking**  : block the current thread **부모역할**


### launch (니가알아서 해, Dispatchers.Default, Default thread pool에 있는 녀석들을 사용할 때)  
```kotlin
fun CoroutineScope.postItem(item: Item) {
  launch {
    val token = requestToken()
    val post = createPost(token, item)
    showPost(post)
  }
}
```

extension function 사용하는 예시
```kotlin
fun CoroutineScope.launch(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit
): Job { … }
```
중요, block은 새로 생성된 coroutine function .() extenstion lambda 함수  
Job: 생성된 coroutine에 대한 handle ( job을 이용해서 controll 할  수 있다. )  

job.cancel()   // cancel the job  
job.join()     // wait for job, completion (마치 fork join 하듯이 )  


**[Don't do this](https://elizarov.medium.com/the-reason-to-avoid-globalscope-835337445abc)**
```kotlin
fun postItem(item: Item) {
  GlobalScope.launch {
    val token = requestToken()
    val post = createPost(token, item)
    showPost(post)
  }
}
```


### async/await (결과를 활용해야 해)  
```kotlin
suspend fun loadAndCombine(name1: String, name2: String): Image =
  coroutineScope {
    val deferred1: Deferred<Image> = async { loadImage(name1) }
    val deferred2: Deferred<Image> = async { loadImage(name2) }
    combineImages(deferred1.await(), deferred2.await())
  }
  
```
Deferred -> future 데... ㅎ    
  
```kotlin
fun <T> CoroutineScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> 
```

요것도 **Don't do this**  
```kotlin
suspend fun loadAndCombine(name1: String, name2: String): Image {
  val deferred1 = GlobalScope.async { loadImage(name1) }
  val deferred2 = GlobalScope.async { loadImage(name2) }
  return combineImages(deferred1.await(), deferred2.await())
}
```

### launch & sync
lambda with recieve 형태ㅣ
```kotlin
fun CoroutineScope.launch(
  …
  block: suspend CoroutineScope.() -> Unit
): Job { … }

```
```kotlin
fun <T> CoroutineScope.async(
  …
  block: suspend CoroutineScope.() -> T
): Deferred<T>
```
-> Coroutine body안에서 parent-child 구조를 만들기 위해서!!

이런게 가능하다!!(Coroutines from a hierarchy, Concurrency의 lifecycle Management 할 때, 가능하다!!)  
parent-child relationship  
```kotlin
scope.launch{
  launch{
    launch{
      launch { }
    }
  }
}
```

### runBlocking ( **부모역할**, top level coroutine)  
기본형  
```kotlin
fun <T> runBlocking(
  context: CoroutineContext = …,
  block: suspend CoroutineScope.() -> T
): T
```

주로 main에서 사용한다. 
일반적인 코드에서는 잘 사용하지 않는다.
```kotlin
fun main() {
  println("Hello,")
  // Create a coroutine, and block the main thread until it completes
  runBlocking {
    delay(2000L) // suspends the current coroutine for 2 seconds
  }
  println("World!") // will be executed after 2 seconds
}
```

전체가 top-level coroutine에서 사용 할 때 쓴다
```kotlin

```

### Dispatcher  
- Main - UI/Non-blocking
- Default - CPU
- IO - network/disk

