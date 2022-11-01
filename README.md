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

- kotlin
  - type val var
  - class Persion
  - NullException이 중요한 이유
  - [스트림](https://madplay.github.io/post/difference-between-map-and-flatmap-methods-in-java)
  - flatMap
  - List<List<>>
  - [let, with](https://www.youtube.com/watch?v=RBGHA1cYsRM&list=PLg3A12oL1JCO5YhYFqDUM-_NcBy32-Bd2&index=11), [also](https://0391kjy.tistory.com/50), apply
  - [callback](https://stackoverflow.com/questions/824234/what-is-a-callback-function)  
  - block, non-block  
  - sync , async  
  - [함수타입(+lambda)](https://youtu.be/xZrSadIO6Mg?list=PLg3A12oL1JCNke34RZ-WApabuvQsfSWPv&t=751)
  - lambda with recieve  

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
