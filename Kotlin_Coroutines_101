# Coroutines simplify asynchrounous ( async prpgramming )

- What problems Coroutines solve  
- Dispatchers & withContext  
- What a Coroutines is  
- How coroutines work under the hood  
- Priciples of Structured Concurrency  
- How to create Coroutines  
- Exception Handling  
- When to mark function as suspend  
- Testing Coroutines & TestCoroutineDispatcher  


### Sync  
blocking  

main thread only ( issue )
```kotlin
fun loadData(){
  val data = networkRequest()
  show(data)
}

fun networkRequest(): Data{
  // Blocking network request code 
}
```

### Async  
with callback  

```kotlin
fun loadData() {
  networkReuest { data ->
    show()
  }
}

fun networkRequest(onSuccess: (Data) -> Unit) {
  DefaultScheduler.execute{
    // Blocking network request code
    postToMainThread(onSuccess(result))
  }
}
```

Callback hell 에 빠질 수 있음 ( Complicate )
```kotlin
fun loadData() {
  networkRequest { data ->
    anotherRequest(data) { otherData ->
      networkRequest { data ->
        anotherRequest(data) { otherData ->
        ...
}
```



### Async  
with coroutines

```
suspend fun loadData() {
  val data = networkReuest()
  show(data)
}

suspend fun newworkRequest(): Data =
  withContenxt(Dispatchers.IO) {
    // Blocking network request code
  }
```

Continuations
```

```

                .Main  
Dispatchers     .IO  
                .Default  
  
  
UI/Non-blocking .Main  
Netwrok & Disk  .IO  
           CPU  .Default  


## Structured Concurrency  

## Scopes
  - keep track of coroutines  
  - Ability to cancel them  
  - Is notified of failures  

안드로이드에서 어떻게 사용할까? (동작x)  
```kotlin
fun onButtonClicked() {
  launch {
    loadData()
  }  
}
```

Scope를 만들어야 함  
```kotlin

//Parent
val scope = CoroutineScope(Dispatchers.Main)

fun onButtonClicked() {
  scope.launch {  //Child
    loadData()
  }  
}
```
Cancelling as scope  
  -. Cancels all children coroutines  
  -. Useless, can not start more coroutines  
  
When a suspend functions returns, it has completed all work.    
 
 

## Scopes exception handling  
```kotlin
val scope = CoroutineScope( Dispatchers.Main + Job() )

fun onButtonClicked() {
  scope.launch {
    loadData()
  }  
}
```
When a child fails, it propagates cancellation to other children  
When a failure is notified, the scope propagates the exception up  


```kotlin
val scope = CoroutineScope( Dispatchers.Main + SupversiorJob() )
```

suspend fun returns         work is completed  
    scope cancelled         children cancel  
   coroutine errors         scope notified  


## Handle Exception  
Wrapping in a try-catch block  
```kotlin
scope.launch(Dispatchers.Default) {
  try {
      loggingService.upload(logs)
  } catch(e: Exception) {
      // Handle Exception
  }
}
```


## runBlocking  
until the loack of code finishes

```kotlin
// MyViewModel.kt

val testDispatcher = TestCoroutineDispatcher()

@Test
fun `Test loadData hppy path`() = testDispatcher.runBlockingTest {
  val viewModel = MyViewModel(testDispatcher)
  viewModel.onButtonClicked()
  
  // Assert show did something

}
```

### refernce  
[Kotlin Coroutines 101](https://www.youtube.com/watch?v=ZTDXo0-SKuU)  
