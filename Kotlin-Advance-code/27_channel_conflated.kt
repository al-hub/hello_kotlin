package _5_Coroutines_2

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val channel = Channel<Int>(CONFLATED)

        launch {
            for (k in 1..500) {
                delay(10)
                channel.send(k)
                println("Sent element $k")
            }
        }

        launch {
            while (true) {
                delay(25)
                println("Received element " + channel.receive())
            }
        }
    }
}
