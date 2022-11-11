package exampleFlow06

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

fun simple(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(1000)
        println("Emitting $i")
        emit(i)
    }
}

fun main() = runBlocking {
    withTimeoutOrNull(2500) { // Timeout after 250ms
        simple().collect { value -> println(value) }
    }
    println("Done")
}
