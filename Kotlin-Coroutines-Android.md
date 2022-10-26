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


