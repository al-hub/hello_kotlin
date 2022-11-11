package _5_Coroutines_2

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val channel = produce {
            for (k in 1..5) {
                send(k)
                delay(1000)
            }
        }
        channel.consumeEach { println(it) }
        println("Done!")
    }
}
