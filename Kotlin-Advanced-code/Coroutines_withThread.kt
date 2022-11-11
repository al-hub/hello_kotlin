import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors.newSingleThreadExecutor
import java.util.concurrent.ThreadFactory

fun main() {

    runBlocking {

//        java스러운 코드
//        val myobj = newSingleThreadExecutor(object : ThreadFactory {
//            override fun newThread(r: Runnable): Thread {
//                TODO("Not yet implemented")
//            }
//        })

        val executor = newSingleThreadExecutor {
            Thread(it, "my-executor-dispatcher")
        }

        val dispatcher = executor.asCoroutineDispatcher()

        launch(dispatcher) {
            delay(100)
            println(Thread.currentThread().name)
        }

        launch {
            delay(100)
            println(Thread.currentThread().name)
        }
    }

}