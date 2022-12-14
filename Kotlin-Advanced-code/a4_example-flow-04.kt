package exampleFlow04

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

fun simple(): Flow<Int> = flow { // flow builder
    for (i in 1..3) {
        delay(1000) // pretend we are doing something useful here
        emit(i) // emit next value
    }
}

fun main() = runBlocking {
    // Launch a concurrent coroutine to check
    // if the main thread is blocked
/*    launch {
        for (k in 1..3) {
            println("I'm not blocked $k")
            delay(1000)
        }
    }*/
    // Collect the flow
    val flow = simple()
    flow.collect { value -> println(value) }

    //재사용 가능하고,
    flow.collect { value -> println(value) }

    //중간에 cancel도 가능하다.
    withTimeoutOrNull(1500) {
        flow.collect {
            value -> println(value)

        }
    }
}
