import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun main() {
    suspend fun a(): Int {
        delay(1000)
        return Random.nextInt()
    }

    suspend fun b(a: Int) {
        println("Using $a")
    }

    runBlocking {
        val data = a()
        b(data)
    }

}