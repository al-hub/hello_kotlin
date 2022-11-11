import kotlinx.coroutines.*
import java.lang.RuntimeException

fun main() {
    suspend fun child1() {
        println("Starting child 1")
        delay(1000)
        throw RuntimeException("Boom")
    }

    suspend fun child2() {
        println("Starting child 2")
        delay(2000)
        println("Completing child 2")
    }

    suspend fun child3() {
        println("Starting child 3")
        delay(500)
        println("Completing child 3")
    }

    suspend fun child4() {
        println("Starting child 4")
        delay(1000)
        println("Completing child 4")
    }

    runBlocking {

        supervisorScope {

            launch {
                child1()
            }
            launch {
                child2()
            }
        }

        println("Done#1")
        coroutineScope {
            launch {
                child3()
            }
            launch {
                child4()
            }
        }
        println("Done#2")
    }

}