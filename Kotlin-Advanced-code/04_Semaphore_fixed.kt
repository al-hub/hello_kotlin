//package _4_Concurrency_Semaphore

import java.util.*
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread

fun main() {
//    brokenBufferImpl()

//    semaphore1()

    semaphore2()
}

fun brokenBufferImpl() {
    val buffer = mutableListOf<Int>()
    val maxSize = 8

    (1..2).forEach { _ ->
        thread {
            val random = Random()
            while (true) {
                if (buffer.size < maxSize)
                    buffer.add(random.nextInt(1, 100))
            }
        }
    }

    (1..2).forEach { _ ->
        thread {
            while (true) {
                if (buffer.size > 0) {
                    val item = buffer.removeAt(0)
                    println("Consumed item $item")
                }
            }
        }
    }
}

fun semaphore1() {
    val emptyCount = Semaphore(8)
    val fillCount = Semaphore(0)
    val buffer = mutableListOf<Int>()

    (1..2).forEach { _ ->
        thread {
            val random = Random()
            while (true) {
                emptyCount.acquire()
                buffer.add(random.nextInt(1, 100))
                fillCount.release()
            }
        }
    }

    (1..2).forEach { _ ->
        thread {
            while (true) {
                fillCount.acquire()
                val item = buffer.removeAt(0)
                println("Consumed item $item")
                emptyCount.release()
            }
        }
    }
}

fun semaphore2() {
    val emptyCount = Semaphore(8)
    val fillCount = Semaphore(0)
    val mutex = Semaphore(1)
    val buffer = mutableListOf<Int>()

    (1..2).forEach { _ ->
        thread {
            val random = Random()
            while (true) {
                emptyCount.acquire()
                mutex.acquire()
                buffer.add(random.nextInt(1, 100))
                mutex.release()
                fillCount.release()
            }
        }
    }

    (1..2).forEach { _ ->
        thread {
            while (true) {
                fillCount.acquire()
                mutex.acquire()
                val item = buffer.removeAt(0)
                mutex.release()
                println("Consumed item $item")
                emptyCount.release()
            }
        }
    }
}
