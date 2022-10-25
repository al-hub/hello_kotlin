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
It is a sequence of callbacks.  
n kotlin, coroutine suspension/resume is implemented as a state machine.  

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
