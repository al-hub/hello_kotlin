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

```kotlin
class ExampleClass {
  val scope = CoroutineScope()
  
  func loadExample() { ... }
  
  func cleanUp() {
    scope.cancle()
  }
  
}
```

### Android Calss (ktx, ViewModelScope, lifecycleScope)   
```kotlin
class ExampleViewModel : ViewModel() {

  fun loadExample() {
    viewModelScope.launch{
      // New coroutine!
      // You can call suspend functions
    }
  }
}
```

```kotlin
class ExampleActivity : AppCompatActivity() {

  fun loadAsset() {
    lifecycleScope.launch(Dispatcher.IO) {
      // New coroutine!
      // You can call suspend functions
    }
  }
}
```

### CoroutineContext  
- CoroutineDispatcher    
  - Dispatchers.IO  
  - Dispatchers.Default  
  - Dispatchers.Main  
- CoroutineExceptionHandler  
- CoroutineName  
- Job  

```kotlin
class ExampleClass {
  val scope = CoroutineScope(
    Job() + Dispatchers.Main + exceptionHandler
  )
  
  func loadExample() {
    // New instance of Job created
    val job = scope.launch {
    //Inherited context!
    //Dispatchers.Main is used   
  }
}

```

```kotlin
class ExampleClass {
  val scope = CoroutineScope( ... )
  
  func loadExample() {
    val job = scope.launch {
      // Using Dispatchers.Main here
      withContenxt(Dispatchers.Default) {
        // Using Dispatchers.Default here
      }
    }
  }
}
```


control lifecycle  
```kotlin
class ExampleViewModel : ViewModel() {

  fun loadExample() {
    val job = viewModelScope.launch{ ... }
    
    try {
      // Some other logic
    } catch(e: Trhowable) {
      job.cancel()
    }
  }
}
```

child fail 되면 다른 child에도 전달  
```kotlin
class ExampleClass {
  val scope = CoroutineScope(Job())
}
```

하나의 child 만 fail 하는 방법  
```kotlin
class ExampleClass {
  val scope = CoroutineScope(SupervisorJob())
}
```

supervisorScope vs coroutineScope
```
class ExampleRepositoiry{
  suspend fun doSomething() {
    // run task in parallel
    // if a task fails, continue with others
    supervisorScope{
      launch { /** task 1*/ }
      launch { /** task 2*/ }
    }
  }
}

```

```
class ExampleRepositoiry{
  suspend fun doSomething() {
    // run task in parallel
    // if a task fails, cancel others
    corountineScope{
      launch { /** task 1*/ }
      launch { /** task 2*/ }
    }
  }
}

```

### refernce  
[The ABC of Coroutines](https://www.youtube.com/watch?v=bM7PVVL_5GM)  
[Kotlin Coroutines 101](https://www.youtube.com/watch?v=ZTDXo0-SKuU)  
