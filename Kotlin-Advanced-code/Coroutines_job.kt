import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    var x = 0
    val job = GlobalScope.launch {
        while (true) {
            x += 1
            delay(1000)
            println(x)
        }
    }

    runBlocking {
        delay(3000)
        job.cancel()
    }
}
