# hello_kotlin

## Coroutine

-. CoroutineScope  

```kotlin
clas ExampleClass {
  val scope = CoroutineScope()
  
  fun loadExample() {
    scope.launch {
      // New coroutine!
      // You can call suspend functions
    }
  }
  
}

```

### refernce  
[The ABC of Coroutines](https://www.youtube.com/watch?v=bM7PVVL_5GM)  
[Kotlin Coroutines 101](https://www.youtube.com/watch?v=ZTDXo0-SKuU)  
