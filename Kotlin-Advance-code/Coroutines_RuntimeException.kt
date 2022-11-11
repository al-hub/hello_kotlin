import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.lang.RuntimeException

fun main() {
    var job: Job? = null
    try {
        runBlocking {
            job = launch {
                delay(1000)
                println("I ma a child")
            }
            delay(100)
            throw RuntimeException("boom")
        }
    } catch (e: Exception) {
        println("Catching exception")
    }
    println("Cancelled=" + job!!.isCancelled)

}

