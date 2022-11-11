import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {

    GlobalScope.launch {
        delay(3000)
        println("I am a croutine")
        println("<launch> ${Thread.currentThread().name}")
    }

    println("I am the main thread")
    println("<main> ${Thread.currentThread().name}")
    //Thread.sleep(5000)
}