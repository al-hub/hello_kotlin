import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    suspend fun a() {
        delay(1000)
        println("a")
    }

    suspend fun b() {
        delay(1000)
        println("b")
    }

    runBlocking {
        a()
        b()
        println("Done")
    }

}