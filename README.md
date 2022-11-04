# [hello_kotlin](https://medium.com/mj-studio/%EC%BD%94%ED%8B%80%EB%A6%B0-%EC%9D%B4%EB%A0%87%EA%B2%8C-%EC%9E%91%EC%84%B1%ED%95%98%EC%8B%9C%EB%A9%B4-%EB%90%A9%EB%8B%88%EB%8B%A4-94871a1fa646)

- environment
  - *.kt -> class -> kotlin
  - *.kt -> jar -> java
  - vim editor auto create
  

structured concurrency  
: control flow (Scope) 을 도입하여, cancel(exception) propagation이 간결히 이루어지게 한다.([예시](https://suhwan.dev/2022/01/21/Kotlin-coroutine-structured-concurrency/))    

dispatcher  
: 어떤 threadpool에서 사용할 지, thread에 coroutine을 보낸다.([예시](https://kotlinworld.com/141))     

- [coroutine](https://myungpyo.medium.com/reading-coroutine-official-guide-thoroughly-part-0-20176d431e9d) 
  - scope  
  - lanch, async, runBlocking  
  - job  
  - dispatcher  

- [kotlin basic](https://developer.android.com/codelabs/basic-android-kotlin-compose-function-types-and-lambda?hl=ko#0) [2](https://develop-writing.tistory.com/65)
  - type val var
  - class Persion
  - NullException이 중요한 이유
  - [스트림](https://madplay.github.io/post/difference-between-map-and-flatmap-methods-in-java)
  - flatMap
  - [List<List<>>](https://codechacha.com/ko/java-initialize-list-of-list/)
  - [object](https://codechacha.com/ko/kotlin-object-vs-class/)
  - [let, with](https://www.youtube.com/watch?v=RBGHA1cYsRM&list=PLg3A12oL1JCO5YhYFqDUM-_NcBy32-Bd2&index=11), [also](https://0391kjy.tistory.com/50), apply
  - [callback](https://stackoverflow.com/questions/824234/what-is-a-callback-function) 
  - block, non-block  
  - sync , async  
  - [함수타입(+lambda)](https://youtu.be/xZrSadIO6Mg?list=PLg3A12oL1JCNke34RZ-WApabuvQsfSWPv&t=751)
  - lambda with recieve  
  - [invoke:람다의비밀](https://wooooooak.github.io/kotlin/2019/03/21/kotlin_invoke/)

<details>
<summary>callbackInKotlin</summary>

```kotlin
fun caller(callback: () -> Unit) {
    println("\nrun")
    callback()
}

fun cbClassic() {
    println("cb_classic")
}

val cbModern = {
    println("cb_mordern")
}

fun main() {
    caller(::cbClassic) //refer to function: use funtion reference operation in kotlin
    caller(cbModern)
    
    caller({ println("lambda: directly into a function") })
    caller() { println("lambda: after the closing parenthesis") } 
}
```

https://stackoverflow.com/questions/48181751/get-name-of-current-function-in-kotlin
https://www.techiedelight.com/ko/get-name-current-function-kotlin/
https://stackoverflow.com/questions/45165143/get-type-of-a-variable-in-kotlin
https://kotlinlang.org/docs/reflection.html#bound-class-references
```
Thread.currentThread().stackTrace[1].methodName
val name = object{}.javaClass.enclosingMethod.name
 class Main
    val name: String = Main::class.java.enclosingMethod.name

val name = Throwable().stackTrace[0].methodName
val name = Exception().stackTrace[0].methodName
val name = Thread.currentThread().stackTrace[1].methodName


println(Int::class.simpleName)    // "Int"
println(Int::class.qualifiedName) // "kotlin.Int"
 
val value="value"
println(value::class.java.typeName)

//instance::method.name

fun main() {
    val test = Test()
    test.methodA()
    println("The name of method is ${test::methodA.name}")
}

class Test {
    fun methodA() {
        println("Executing method ${this::methodA.name}")
        println("Executing method ${::methodA.name} - without explicit this")
    }
}
```

CPS
```
fun addCPS(a: Int, b:Int, cont: (Int) -> Int): Int = cont(a+b)

fun factCPS(n: Long, cont: (Long) -> Long): Long =
    when(n) {
        0L -> cont(1L)
        else -> factCPS(n-1) { prev ->
            cont(n+prev)
        }
    }

fun main() {
    //val ret1 = addCPS(1, 2, { it })
    //val ret1 = addCPS(1, 2){ it }

    val ret1 = addCPS(1, 2) { i -> i } 
    println("${ret1}")
 
	/* 
    val ret2 = addCPS(1, 2, { step1 ->
        			addCPS(3, 4, { step2 ->
        			    addCPS(step1, step2, { it })
                    })        
    			})
    */
    val ret2 = addCPS(1, 2) { step1 ->
        			addCPS(3, 4) { step2 ->
        			    addCPS(step1, step2) { it } 
        			}        
    			}
    
    println("${ret2}")
    
    val fact = factCPS(10) { it }
    println("${fact}")
}
```
-개발 환경 설정
-클래스 구현
-인터페이스 구현
-프로퍼티 구현
  
-함수 지원 문법
-고차원함수
-인라인 함수
-클로저 개념
  
-제네릭 함수
-제네릭 타입
-타입 Variance
-타입 Projection
  
-스레드
-Executor
-Lock
-Semaphore
  
-기본개념
-취소와 타임아웃
-일시 중단함수
-컨텍스트와 디스패처
  
-비동기 Flow
-채널, 예외처리
-코루틴과 Flow 응용
-코루틴과 Flow 디버깅 

</details>

- Try This
  - JUnit
  - LiveData (observerable view holder) study 필요  
  - Kotlin flow
    
  
  
- [Boilerplate](https://gmunch.github.io/2019/07/15/why-kotlin.html)

<details>
<summary> java vs kotlin </summary>

```java
  public class DataExample {
    private final String name;
    private int age;
    private double score;
    private String[] tags;

    public DataExample(String name) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }

    void setAge(int age) {
      this.age = age;
    }

    //...
    public String[] getTags() {
      return this.tags;
    }

    public void setTags(String[] tags) {
      this.tags = tags;
    }

    @Override public String toString() {
      return "DataExample(" + this.getName() + ", "
        + this.getAge() + ", " + this.getScore() + ", "
        + Arrays.deepToString(this.getTags()) + ")";
    }

    protected boolean canEqual(Object other) {
      return other instanceof DataExample;
    }

    @Override public boolean equals(Object o) {
      //...
      return true;
    }

    @Override public int hashCode() {
     //...
    }
```
```kotlin
  data class DataExample(
    val name: String, var score: String?,
    var tags: Array<String>?
)
```
</details>    

- 플락션(flection) 코드 ??  

구글에서 설명하는 코루틴 : https://www.youtube.com/watch?v=w0kfnydnFWI&t=17s  
코루틴 대장(Roman Elizarov) :  https://www.youtube.com/watch?v=_hfBv0a09Jc&t=22s
