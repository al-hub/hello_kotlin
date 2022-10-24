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
주의, suspend 만 붙인다고 되는것은 아님!
