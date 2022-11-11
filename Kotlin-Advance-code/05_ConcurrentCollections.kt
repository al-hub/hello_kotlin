package _4_Concurrency_ConcurrentCollections

import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

fun main() {
    val buffer = LinkedBlockingQueue<Int>()

    thread {
        val random = Random()
        while (true) {
            buffer.put(random.nextInt(1, 100))
        }
    }

    thread {
        while (true) {
            val item = buffer.take()
            println("Consumed item $item")
        }
    }
}
