import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        delay(3000)
        println("Hello World")
        println("<runBlocking> ${Thread.currentThread().name}")
    }
    println("Done")
}