# Kotlin Coroutines (2022-10-24)  

- Coroutine 테스트 시, MockK 권고 (Mockito 도 가능은 함)  
  - JUnit도 사용  
  - LiveData (java에서 ViewModel - View 사이에 safe한 관리를 위해 사용): kotlin에서는 kotlin flow가 사용  
  - Reactive Stream, RxJava, RxKotlin

- stop 느낌
- 기차길 느낌
- Trampoline느낌, flatmap: list of list를 하나의 list로 펼칠 때 -> controller를 줬다 받았다 하면서 진행
- Generators

- The Art of computer Programming (읽지않는(?) 필수책)  


## 이론
- 개념: coroutine은 multiple entry/exit point  
- 공식: Basically, coroutines are computations that can be suspended without blocking a thread  
- A coroutine is a sequence of computations,
  each of which may be suspended(or paused) and resulmed at some point,
  without blocking the thread that executes it.
  
- block의 의미: blocking io api call을 했을 때, (readline, fscanf 등등... )
               하지만, CPU-intensive computation (CPU-bound task)

blocking IO
```
fun BufferedReader.readMessage(): Message? =
  readLine()?.parseMessage()
```

CPU-intensive
```
fun findBigPrime(): BigInteger =
  BigInteger.probablePrime(4096, Random())
```
