import kotlinx.coroutines.*

fun main() {

    val job = GlobalScope.launch(start = CoroutineStart.LAZY) {
        delay(1000)
        println("World")
    }

    runBlocking {
        delay(1000)
        println("Hello")
        job.join()
    }
}