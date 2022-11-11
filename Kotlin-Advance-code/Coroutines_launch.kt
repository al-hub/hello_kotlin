import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        println("Parent starting")
        GlobalScope.launch {
            println("Child a starting")
            for (n in 1..10000) {
                println("Child a : $n")
            }
            delay(1000)
            println("<Child a> ${Thread.currentThread().name}")
            println("Child a complete")
        }
        GlobalScope.launch {
            println("Child b starting")
            delay(1000)
            repeat(1000){
                println("Child b : $it")
            }
            println("<Child b> ${Thread.currentThread().name}")
            println("Child b complete")
        }
        println("<runBlocking> ${Thread.currentThread().name}")
        println("Parent complete")
    }

    Thread.sleep(5000)
    println("Parent return")
}