# hello_kotlin

structured concurrency  
: control flow (Scope) 을 도입하여, cancel(exception) propagation이 간결히 이루어지게 한다.([예시](https://suhwan.dev/2022/01/21/Kotlin-coroutine-structured-concurrency/))    

dispatcher  
: 어떤 threadpool에서 사용할 지, thread에 coroutine을 보낸다.([예시](https://kotlinworld.com/141))     

- coroutine 
  - scope  
  - lanch, async, runBlocking  
  - job  
  - dispatcher  

- kotlin
  - [let, with](https://www.youtube.com/watch?v=RBGHA1cYsRM&list=PLg3A12oL1JCO5YhYFqDUM-_NcBy32-Bd2&index=11), apply, also
  - [callback](https://stackoverflow.com/questions/824234/what-is-a-callback-function)  
  - block, non-block  
  - sync , async  
  - [함수타입(+lambda)](https://youtu.be/xZrSadIO6Mg?list=PLg3A12oL1JCNke34RZ-WApabuvQsfSWPv&t=751)
  - lambda with recieve  

- Try This
  - JUnit
  - LiveData (observerable view holder) study 필요  
  - Kotlin flow
    
  
  
- 기타  
  - Boilerplate 
  - <details>
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

