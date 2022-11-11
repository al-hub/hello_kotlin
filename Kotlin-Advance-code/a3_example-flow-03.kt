package exampleFlow03

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

suspend fun simple(): List<Int> {
    delay(1000) // pretend we are doing something asynchronous here
    return listOf(1, 2, 3)
}

fun main() = runBlocking {
    simple().forEach { value -> println(value) }
}
