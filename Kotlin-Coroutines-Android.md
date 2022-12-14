## Asyn 도구(방법)들

- LiveData (Observable data holder, 한정적)
- RxJava ( 너무 큼 )
- Coroutines ( simplified, comprehension, rubust )  


## Main-Safety ( 안드로이드에서 신경써야하는 것 )
바람직하지 않은 형태
```kotlin
suspend fun loadData() {
  val data = apiService.networkRequest()
  withContext(Dispatchers.Main) { 
  show(data)
  }
}
```

바람직한 형태
```kotlin
suspend fun loadData() {
  val data = withContext(Dispatchers.IO){ 
    apiService.networkRequest()
  }
  show(data)
}
```


## (먼저고려)lifecycleScope and viewModelScope  
### viewModelScope  
절차대로 따르지않고, 바로 실행되더라도 안정적으로 동작되도록 설계  
```kotlin
dependencies {
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
}

public val ViewModel.viewModelScope: CoroutineScope
  get() { …
    CloseableCoroutineScope(
      SupervisorJob() + Dispatchers.Main.immediate))
  } 
```

ready made ( Coroutine 생성없이 바로사용 )의 예시  
```kotlin
class MyViewModel: ViewModel() {
  init {
    viewModelScope.launch {
      // Coroutine that will be canceled when the ViewModel is cleared.
    }
  }
}

override fun onCleared() {
  super.onCleared()
  // viewModelScope.cancel() // you don’t need this! 안써도 안정적으로 동작되도록 ready made  
}

```

### lifecycleScope  
- LifecycleCoroutineScope.launchWhenCreated()
- LifecycleCoroutineScope.launchWhenStarted()
- LifecycleCoroutineScope.launchWhenResumed()

```kotlin
dependencies {
  implementation "androidx.lifecycle:lifecycle-runtime-ktx:$version
}

public val LifecycleOwner.lifecycleScope: LifecycleCoroutineScope
get() = lifecycle.coroutineScope

```

사용예시  
```kotlin
class MyFragment: Fragment() {
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    viewLifecycleOwner.lifecycleScope.launch {
      val params = TextViewCompat.getTextMetricsParams(textView)
      val precomputedText = withContext(Dispatchers.Default) { // 내부에 suspend funtion이라면 좋지않음
          PrecomputedTextCompat.create(longTextContent, params)// normal function이라 현재와 같이 구성된 것으로 추정
      }
      TextViewCompat.setPrecomputedText(textView, precomputedText)
    }
  }
}

```

어떤 method를 쓸 것인지 결정해야한다.  
```kotlin
class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    lifecycleScope.launchWhenResumed {
      doSomeLongRunningJob()
    }
  }
}
```
streaming은 다른방법을 추천함 ( flow를 알아야 함 )


## LiveData with Coroutine  
kotlin에서만 사용 가능함(Kotlin Flow가 대처되는 분위기??)    
View와 View mode 사이에서 사용  

liveData Builder(Default: Dispatchers.Main.immediate)  
```kotlin
dependencies {
  implementation "androidx.lifecycle:lifecycle-livedata-ktx:$version
}

public fun <T> liveData(
  context: CoroutineContext = EmptyCoroutineContext,
  timeout: Duration,
  block: suspend LiveDataScope<T>.() -> Unit
): LiveData<T> = CoroutineLiveData(context, …, block
```

Creating LiveData emitting values (1)  
```kotlin  
val recipe: LiveData<Resource<Recipe>> = liveData {
  emit(Resource.Loading)
  emit(longRunningTask())
}
```

Creating LiveData emitting values (2)  
switchMap은 flatMap과 같다. (flatMap의 signature는 어디에서도 변화지 않는다.)  
하나씩 바꿀 때(emit),  
```kotlin  
// a LiveData that fetches a `User` object based on a `userId`
// and refreshes it every 30 seconds as long as it is observed
val userId: LiveData<String> = MutableLiveData<>()
val user: LiveData<User> = userId.switchMap { id ->
  liveData {
    while(true) {
      // note that `while(true)` is fine because the `delay(30_000)`
      // below will cooperate in cancellation if LiveData is not
      // actively observed anymore
      val user = api.fetch(id) // errors are ignored for brevity
      emit(user)
      delay(30_000)
    }
  }
}
```

동작원리(flatMap의 signature는 어디에서도 변화지 않는다.)     
```scala
Monad
def map    [A, B](fa:     List[A])(f: A =>           B):     List[B] == ???
def flatMap[A, B](fa:     List[A])(f: A =>     List[B]):     List[B] == ???
def flatMap[A, B](fa: LiveData[A])(f: A => LiveData[B]): LiveData[B] == ???

def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] == flatten(map(fa)(f))
def flatten[A](fa: List[List[A]]): List[A] == ???
```


통으로 바꿀때(emitSource),  
```kotlin 
// a LiveData that immediately receives a LiveData<User> from the
// database and yields it as a source but also tries to back-fill
// the database from the server
val user = liveData {
  val fromDb: LiveData<User> = roomDatabase.loadUser(id)
  emitSource(fromDb)
  val updated = api.fetch(id) // errors are ignored for brevity
  // Since we are using Room here, updating the database will
  // update the `fromDb` LiveData that was obtained above.
  roomDatabase.insert(updated)
}
```


## Coroutine are good for ...    
-  One shot requests : Use suspending functions
- Streaming requests : Use Kotlin Flow

[good news](https://medium.com/androiddevelopers/coroutines-on-android-part-iii-real-work-2ba8a2ec2f45):  Room, Retrofit is also main-safe and run on a custom dispatcher  
