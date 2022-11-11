import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.RuntimeException

fun main() {
    suspend fun child1() {
        println("Strating child 1")
        delay(1000)
        println("Completing child 1")
        throw RuntimeException("Boom")
    }
    suspend fun child2() {
        println("Strating child 2")
        delay(1000)
        println("Completing child 2")
    }

    try {
        runBlocking {

            launch {
                child1()
            }
            launch {
                child2()
            }
            delay(3000)
            println("Parent Done")

        }
    } catch (e: Exception) {
        println("Failed with ${e.message}")
    }


}