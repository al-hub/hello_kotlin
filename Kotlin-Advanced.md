학습 후 자기만의 시간이 필요한것 같음  


## TIP  
- 주석  : shift + / 

- 타입  : ctrl + shift + p : 타입확인  
- 도움말 : ctrl + q ( 설명을 읽을 수 있어야 한다 , T R )

- 탐색   : ctrl + b ( declare 함수 내부로 분석, ctrl ++ left/right 이전 이후 )  

- fun   : f + enter (자동생성) 
- 자동코드 : ctrl + i (implementation)
 
- 복사,삭제 : ctrl + d / x

## 개념
kotlin class 만들면 무조건 final class를 만든다.  
함수를 호출하는 느낌으로 class 사용한다.  

decompile 방법 
kotlin Bytecode  
public final class 기본  


val: value (읽기 전용)  
var: variable  

object 지금당장 객체를 만든다. (singleton)  


data class  
decompile 시, main 쪽은 거의 비슷하나   
component1, component2, copy, toString, hashCode, equals 이 미리 정의되어 있다.(효과)     
println(p) 등을 했을때, 알아서 깔끔하게 정의&출력이 된다.  


predicate: 함수를 통과 시킨다.  
block: () -> Unit 인자로 람다 argument로 사용하는 함수호출 구조

## 설정
추가라이브러리 설치(android studio, kotlin) kotlin-coroutines-android:1.6.4   

## 특징
즉, boilerplate code를 알아서 만들어 준다.  

함수끝에 lambda 는 후행처리로 하는게 낫다.  
(coroutine과 연관)

? -> null 이 될 수 있다.  


## class 상속  
kotlin의 상속은 : 을 사용함  (java Extends )

private class 만들고, 코드 안에서  
anroid studio : code -> generator (Alt + Ins)  
secondary constructor로 전부선택 후, 생성  

private fun  
코틀린은 탑레벨도 private method 로 될 수 도 있다.  


## higher-order function  
하나의 함수를 여러가지 시나리오로 써 먹을수 있다.  
-> 실행시에 함수 결정 (functional program)  -> 즉 개발자가 전달하는 순간에 결정됨   


## 객체지향 프로그래밍  
객체중심  

## 함수형 프로그래밍  
함수중심  


## Function Literal
- Lambda, Anonymous Function (개념: 함수가 필요하면 지금 당장 만들어 쓴다 )
- FutionReference 기존함수를 그대로 써 먹고 싶다.  

## [Scope Functions](https://blog.yena.io/studynote/2020/04/15/Kotlin-Scope-Functions.html)
주요한 확장함수 ( 내부적으로 Lambda를 사용 )  
- apply
  - configuration기능

- let
  - lamdba의 맨 마지막 expression을 return으로 출력 함
   
- run
  - 입력은 apply, 출력은 let 처럼 ( receive 암시 )
   
- with
  - run의 변종
  - receive를 직접넣는다. 
  
- also
  - let하고 비슷, 이것도 하고 저것도 하고
  
 
## Generics  
어떤 타입으로 작동할지 결정되지 않은 상태에서 함수를 만들어 놓고,  
실 사용할 때는 서로다른 종류의 타입에 대해서 실 사용 가능하도록 작성하는 것  
(함수, 클래스 다 사용, 자바에서는 처음에 없었음, c++에서 template, parametric polymorphism 이라고도 부름)  


## Covariance ( out 을 사용한다. )
사과 -> 과일

## Conravariance ( in 을 사용한다 )
과일 <- 사과  
Box<String> would be a supertype BOX<Any>  
 
### out / in 예제
```kotlin
fun main() {
        //당연히 에러 나는 코드
//    val obj1: MyClass1<A> = MyClass1<B>()
//    val obj1: MyClass1<A> = MyClass1<B>()

    //Covariance: out 출력으로만 사용하면 문제 없다. (일반적)
    //               과일           사과
    val obj2: MyClass2<A> = MyClass2<B>()

    //Contravariance
    //               사과           과일
    val obj3: MyClass3<B> = MyClass3<A>()
}

open class A {}

class B : A() {}

class MyClass1<T> {}

class MyClass2<out T> {}

class MyClass3<in T> {}
```

## Nothing Type
//Nothing 비어있는 것 ( 모든것의 subclass )  
//Type으로만 사용, 객체가 있는것은 아니다. (다른 모든 것과 호환된다.)  
 

## Type Projection
일반형을 Covarinace 또는 Contravariance 관계로 만들어 써 보자  
```kotlin
class Crate<T>(val elements: MutableList<T>) {
    fun add(t: T) = elements.add(t)
    fun last(): T = elements.last()
}

fun isSafe2(crate: Crate<out Fruit>): Boolean =
    crate.elements.all { it.isSafeToEat() }
```
 
```kotlin
import java.math.BigDecimal
 
fun main() {
    /* Contravariance sample */
    val loggingListener = object : Listener2<Any> {
        override fun onNext(t: Any) = println(t)
    }
    EventStream2<Double>(loggingListener).start()
    EventStream2<BigDecimal>(loggingListener).start()
}
 
interface Listener2<T> {
    fun onNext(t: T): Unit
}

class EventStream2<T>(val listener: Listener2<in T>) {
    fun start(): Unit = TODO()
    fun stop(): Unit = TODO()
}
```

## reified 
```kotlin  
package Generics_TypeReification

fun main() {
    runtimeType<Double>()
    runtimeType<String>()

    val list = listOf("green", false, 100, "blue")
    val strings = list.collect<String>() //collect는 extension function, 원래없던것을 만듦
    println(strings)

    printT<Int>(123)
    printT<Int>(1.23)
}

inline fun <reified T> runtimeType() {
    println("My type parameter is " + T::class.qualifiedName)
}

//List<T> 반환함수
inline fun <reified T> List<Any>.collect(): List<T> {
    return this.filter { it is T }.map { it as T }
}

inline fun <reified T> printT(any: Any) {
    if (any is T) println("Type match: $any")
    else println("Type mismatch: $any")
}
```
 
## Concurrency  
- Thread
  - 기본방식: val t = thread { } , flag에 **@Volatile** 사용한 부분 인지할 것( 컴파일러에 따라 annotation 없으면 무시될 수 있음)
  - producer, consumer 방식에서 단순 flag만 사용 시, queue.take() 로 인해 consumer가 안 끝날 수 도 있음
  - Thread.interrupted() 로 해결 될 수 있음  
 
- Executor  
  - executor.submit { }
  - NetworkService 예제: ExecutorService.execute 로 사용 ( submit 과 비슷 )  
 
- RaceConditions  
  - synchronized  
  - val lock = ReentrantLock(), lock.tryLock(), lock.unlock() 재진입 가능 함  
  - lock.lockInterruptibly()  
  - lock.withLock  
 
- semaphore  
  - 가용자원의 유지관리 (sing thread 를 쓰면서 자료보호)  
  - 변수로 var sem = 가용 자원의 갯 수, sem+=1, sem-=1 으로 제어하다가 sem==0 이면, 잠자게 구현은 못 한다. (매우어렵다)  
 
  - brokenBufferImpl 일반적인 list 방식으로는 안전하지 않다. 
    - 문제있는부분 [producer]: if (buffer.size < maxSize) , [consumer]: if (buffer.size > 0) {  
 
  - semaphore1 예시) val emptyCount = Semaphore(8), val fillCount = Semaphore(0)
    - [producer]: emptyCount.acquire() - fillCount.release()
    - [consumer]: fillCount.acquire()  - emptyCount.release()  
 
  - semaphore2 예시) val mutex = Semaphore(1) 추가 (mutual exclusion, synchronized로도 구현해도 됨)
    - [producer]: emptyCount.acquire() mutex.acquire() - mutex.release() fillCount.release()
    - [consumer]: fillCount.acquire()  mutex.acquire() - mutex.release() emptyCount.release()
 
- ConcurrentCollection
  - mutableListOf 등은 thread 환경에서 안전하지 못하다.
  - 성능을 좀 낮추더라도 안정적으로 사용하는 방법
  - import java.util.concurrent.LinkedBlockingQueue
  - 핵심 val buffer = LinkedBlockingQueue<Int>()
 
```kotlin
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

fun main() {
    val buffer = LinkedBlockingQueue<Int>()

    thread {
        val random = Random()
        while (true) {
            buffer.put(random.nextInt(1, 100))
        }
    }

    thread {
        while (true) {
            val item = buffer.take()
            println("Consumed item $item")
        }
    }
}
```
- Atomics
  - 여러개 thread가 접근할 때는 단 하나의 변수도 보호해야 한다.
  - basic
    - val counter = AtomicLong(0)         //초기값은 0
    - val id = counter.incrementAndGet()  //쓰레드 내에서 중간에 다른값을 끼어 들지 않도록 유지함, 같은 숫자는 나올 수 없다.
  - atomicReference
    - val ref = AtomicReference<Connection>()
    - ref.compareAndSet(null, openConnection()) //null 경우만 적용하는 것
 
- CountDownLatch
  - 쓰레드간의 실행 순서제어
  - 부가적인 동기화 기능, Latch는 빗장(걸쇠), 나 하나만 기다린다.
    - val latch = CountDownLatch(feeds.size)
    - latch.countDown() 빗장을 푸는 행위
    - latch.await()
 
- CyclicBarrier
  - 빗장 개념과 빗하긴 함, 모든 쓰레드가 전부다 도달 할 때까지 기다린다. ( 또 어디선가 도달하게 만들수도 있다. ) 
  - 특정 요구되는 지점까지 모든 쓰레드가 기다리게 만들어 보고 싶다.
    - copyUsingBarrier 예제) 파일의 목록을 가지고 있다. 목록이 준비되면, 여러 디렉토리에 복사 할 것 이다.
    - val **barrier** = CyclicBarrier(outputDirectories.size)
    - for (dir in outputDirectories) { executor.submit { CopyTask(dir, inputFiles, barrier) } }
    - class CopyTask(val dir: Path, val paths: List<Path>, val barrier: CyclicBarrier) {
    - fun run() { for(path in paths) { - **barrier.await()** - } }  

## Coroutines  
Cooperation Routines  
  history 유지했다가 resume 하는 개념  
  진입점이 여러개 있다.  
 
콜백을 쓰면 싱글쓰레드로 사용할 수 있으나 많이 쓰면 코드가 복잡해 진다. (callback hell)  

C에도 비슷한 setjmp()와 longjmp() 가 있다.  

blocking 문제를 피하자  
 
- Basic Code
```kotlin
fun main() {
    GlobalScope.launch {
        delay(3000)
        print("I am a croutine")
    }

    println("I am the main thread")
    Thread.sleep(5000)
} 
```
- GlobalScope  
  - public object GlobalScope : CoroutineScope { 
  - object로 만들어진 singleton 이다.
  - 코루틴에서도 상황에 따라 동기화 문제가 발생할 수 있기 때문에 Thread Synchronization 필요할 수 있다.  

- Thread.currentThread().name 출력
```kotlin
fun main() {
    GlobalScope.launch {
        delay(3000)
        println("I am a croutine")
        println("<launch> ${Thread.currentThread().name}")
    }

    println("I am the main thread")
    println("<main> ${Thread.currentThread().name}")
    Thread.sleep(5000)
}
```

- runBlocking
```kotlin
fun main() {
    runBlocking {
        delay(3000)
        println("Hello World")
        println("<runBlocking> ${Thread.currentThread().name}")
    }
    println("Done")
}
```
main thread를 사용하기때문에 실전(android)에서 바로 사용하면 다른기능들이 먹통이 된다.   

- android app 개발 실전예제 시, 주의사항
 
```kotlin
//...
private var x = 0
private var job: Job ? =null
//...     

job?.cancel()
//..

job = GlobalScope.launch { .. delay(1) .. }

```

``` 
Coroutine -- Coroutine Context -- Coroutine Scope  
                                 -----------------   
                                     launch()
```
 
- adroid app 개발 시, interface 상속을 이용하여 사용하자 (android app에서 기본작업)
```kotlin
class MainActivity : AppCompatActivity() , CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job
 
 //<- 이제 launch를 마음대로 쓸수 있다. ...
      
        launch {
            repeat(100) {
                delay(1000)
                println("$it, ${Thread.currentThread().name}")
            }
        }


```
- Dispatcher
```kotlin
fun main() {
    runBlocking(Dispatchers.IO) {
        println(Thread.currentThread().name)
        
        launch(Dispatchers.Default) {
            delay(100)
            println("Default:" + Thread.currentThread().name)
        }
    }
}
```
- CoroutinesWithThread ( java의 thread를 coroutine의 dispatcher로 입력하는 예제)  
```kotlin
fun main() {

    runBlocking {
        val executor = newSingleThreadExecutor { r ->
            Thread(r, "my-executor-dispatcher")
        }
        val dispatcher = executor.asCoroutineDispatcher()

        launch(dispatcher) {
            delay(100)
            println(Thread.currentThread().name)
        }

        launch {
            delay(100)
            println(Thread.currentThread().name)
        }
    }

}
```
- withContext 중간에 dispather를 바꿀 때,
```kotlin
fun main() {

    fun logger(message: String) = println("${Thread.currentThread().name}: $message")

    runBlocking {
        logger("Starting")
        withContext(newSingleThreadContext("A")) {
            logger("running in new context")
            withContext(newSingleThreadContext("B")){
                logger("running in new context")
            }
        }
        logger("Completing")
    }

} 
```
결과
```
main: Starting
A: running in new context
B: running in new context
main: Completing
```
 
- channel  send/receive
```kotlin
fun main() {
    runBlocking {
        val channel = Channel<Int>(3)

        launch {
            for (k in 1..5) {
                channel.send(k)
                println("Sent element $k")
            }
        }

        launch {
            repeat(5) {
                delay(10)
                println("Received element " + channel.receive())
            }
        }
    }
}
```
- produce 기능
```kotlin
fun main() {
    runBlocking {
        val channel = produce {
            for (k in 1..5) {
                send(k)
                delay(1000)
            }
        }
        channel.consumeEach { println(it) }
        println("Done!")
    }
}
```
- conflated 재미있는 예제: 내가 receive 하면 가장 최근것만 받는다.  

```kotlin
fun main() {
    runBlocking {
        val channel = Channel<Int>(CONFLATED)

        launch {
            for (k in 1..500) {
                delay(10)
                channel.send(k)
                println("Sent element $k")
            }
        }

        launch {
            while (true) {
                delay(25)
                println("Received element " + channel.receive())
            }
        }
    }
}
```
## Coroutine 동기화 문제 
suspend fun을 직접만들어 사용하는 동기화 문제 기본예제 (공식사이트에 등록되어 있음)
```kotlin
suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100  // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine
    val time = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
    println("Completed ${n * k} actions in ${time}ms")
}

var counter = 0

fun main() = runBlocking {
    withContext(Dispatchers.Default) {
        massiveRun {
            counter++
        }
    }
    println("Counter = $counter")
}

```
-> counter가 다르게 나온다. (멀티쓰레드 문제로 동기화가 필요하다.)  
 

해결책  (dispatcher를 생성한 하나로만 사용하게 한다.)  
```kotlin
suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100  // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine
    val time = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
    println("Completed ${n * k} actions in ${time}ms")
}

val counterContext = newSingleThreadContext("CounterContext")
var counter = 0

fun main() = runBlocking {
    // confine everything to a single-threaded context
    withContext(counterContext) {
        massiveRun {
            counter++
        }
    }
    println("Counter = $counter")
}
```
## Flow  
channel과 비슷한 개념 (channel은 que이나 flow는 que 아 님 )
생산자, 소비자로 block ( que 없음 )

일반적인 방법 (메모리를 사용한다.)
```kotlin
fun simple(): List<Int> = listOf(1, 2, 3)

fun main() {
    simple().forEach { value -> println(value) }
}
```

coroutine을 쓰면서 메모리를 줄이자
```kotlin
fun simple(): Flow<Int> = flow { // flow builder
    for (i in 1..3) {
        delay(1000) // pretend we are doing something useful here
        emit(i) // emit next value
    }
}

fun main() = runBlocking {
    // Collect the flow
    val flow = simple()
    flow.collect { value -> println(value) }
    
    //재사용 가능하고,
    //flow.collect { value -> println(value) }

    //중간에 cancel도 가능하다.
    //withTimeoutOrNull(1500) { 
    //    flow.collect { value -> println(value) }
    //}
}

```
 
