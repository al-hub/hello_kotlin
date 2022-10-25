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



