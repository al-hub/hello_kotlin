2022-10-26  

- Test의 대상을 class나 interface로 국한시킬 필요는 없다.  
- 객체지향은 서로 interaction 하면서 진행하기 때문에 dependency를 가지는 것이 정상이다.  
- 따라서 테스트를 위한 request ( query 또는 command 형태 ) 전에 미리 상황을 설정해야 한다.  
  - collaraborator는 fake object(stub, lock)로 미리 설정을 해 둔다.  
  - Mockito(MockK) 사용하면 편리하게 할 수 있다. ( annotation 기반 @ )

## Unit Test
- 기본조건: FIRST principle 
- Fast(느리면기피함), Isolated/Independent(주변과격리), Repeatable(일관), Self-Validating(결과자동), Thorought(철저하게)
- efficient, stable 해야한다.
- In Coroutine,
  - How to build a coroutine from the unit tests.
  - How to make unit tests wait until all the jobs in the coroutine have finished.
  - How to make unit test run as fast as possible, and not sit around waiting for a coroutine delay to finish.

테스트API가 계속 바뀌고 있다.  
```kotlin
Dependencies {
  testImplementation        "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
  androidTestImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version“
}
```

- 4가지 핵심요소 (kotlinx-coroutines-test)
    - *runTest**
      - 테스트형 Top-Level에 해당하는 Coroutine build ( runBlocking과 유사, 기존이름 runBlockingTest() )
    - TestScope
    - TestDispatcher 
      - StandardTestDispatcher
      - UnconfinedTestDispatcher 
    - **TestCoroutineScheduler**
      - 가상의 시간(virtual time managerment)을 이용해야 한다.

## Test형 Coroutine을 어떻게 생성 할 것인가?
- kotlinx.coroutines.test.runTest (가장일반적)
- kotlinx.coroutines.runBlocking
- kotlinx.coroutines.test.~~runBlockingTest~~ (deprecated)


단순방법(실제 3초를 기다리겠다. & 새로운 Coroutine이 없을 때)-대상
```kotlin
suspend fun loadData() {
  val articles = networkRequest()
  show(articles)
}
suspend fun networkRequest(): List<Article> {
  return apiService.getArticles()
}
private fun show(
  articles: List<Article>
) {
  _articles.value = articles //LiveData
}
```

단순방법-테스트
given-when-then style (triple A)
```kotlin
@Test fun `test loadData`() = runBlocking {
//given part (Arrange)
  coEvery { 
    apiService.getArticles()
  } returns testArticles
  
//when part   (Act)
  viewModel.loadData()

//then part   (Asset)                                
  val articles = viewModel.articles.getValueForTest() //kotlin: getValueForTest() Object객체,생성,등록,release가 자동으로 된다. LiveData Test Haapy!!
                                                      //java:   getOrAwaitValue( ... 좀 더 복잡했었다.
  assertThat(articles).isEqualTo(testArticles) //(assertJ 같은것) truth라이브러리로 assertThat사용
}
```

딜레이가 있는-대상
```kotlin
class MyViewModel(val apiService:ApiService) : ViewModel() {
  val scope = CoroutineScope(SupervisorJob())
  fun onButtonClicked() {
  scope.launch {
    loadData()
  }
}

```

딜레이가 있는-테스트(실행시면, fail이 난다.)
```kotlin
@Test fun `test onButtonClicked`() = runBlocking {
  coEvery { apiService.getArticles() } coAnswers {
    delay(3_000) //네트웍 딜레이만큼 시뮬레이션 함
    testArticles
  }
  viewModel.onButtonClicked()
  val articles = viewModel.articles.getValueForTest()
  assertThat(articles).isEqualTo(testArticles)
  //벌써 메인은 여기까지와 있다.. 즉, 제대로 테스트가 안 될꺼다.
}

```
기대는 \[Article(id=T001, … 을 했으나, null 이 나온다.  
main에 delay를 넣으면 기대처럼 될 수도 있으나 테스트가 느려지고, 코드맞추기도 어렵다.  
이럴때, runTest를 쓰자

딜레이가 있는-테스트(runTest)
```kotlin
@Test fun `test onButtonClicked`() = runTest {
  coEvery { apiService.getArticles() } coAnswers {
    delay(3000); testArticles
  }
  
  val testDispatcher = ...
  viewModel = ArticleViewModel(apiService, testDispatcher)
  viewModel.onButtonClicked()
  advanceUntilIdle() // this is also OK
  
  val articles = viewModel.articles.getValueForTest()
  assertThat(articles).isEqualTo(testArticles)
}
```

- VirtualTimeControl
  - advanceTimeBy(1000) 
    - 1초 만큼 전진 시켜라 \[ ) exclusive 한 시간이다.
  - runCurrent() 
    - inclusive한 현재시간까지 들어오게 하려면 runCurrent() 사용
  - advanceUntilIdle()
    - 갈때까지 끝까지 다가라  

예시
```kotlin
    @Test
    fun `runCurrent & advanceUntilIdle demo`() = runTest {
        var state = 0
        launch {
            state = 1
            yield()
            state = 2
            delay(1000)
            state = 3
            delay(1000)
            state = 4
            delay(1000)
            state = 5
        }
        assertThat(state).isEqualTo(0)
        log("$currentTime")
        
        runCurrent()
        assertThat(state).isEqualTo(2)
        log("$currentTime")

        advanceUntilIdle()
        assertThat(state).isEqualTo(5)
        log("$currentTime")
    }
```

Quiz1(StandardTestDispatcher)
```
    @Test
    fun `virtual time control - StandardTestDispatcher`() = runTest {
        var state = 0

        launch {
            state = 1
            delay(1000)
            state = 2
        }

        assertThat(state).isEqualTo(TODO())
        log("$currentTime")
    }
```
Quiz1-Ans: TODO() is '0'

Quiz2(UnconfinedCoroutineDispatcher)
```
    @Test
    fun `virtual time control - UnconfinedCoroutineDispatcher - eager`() = runTest(UnconfinedTestDispatcher()) {
        var state = 0

        launch {
            state = 1
            delay(1000)
            state = 2
        }

        assertThat(state).isEqualTo(TODO())
        log("$currentTime")
    }
```
Quiz2-Ans: TODO() is '1'

