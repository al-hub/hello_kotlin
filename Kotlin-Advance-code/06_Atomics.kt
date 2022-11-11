package _4_Concurrency_Atomics

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

fun main() {
//    atomicCounters()

    atomicReferences()
}

fun atomicCounters() {
    val counter = AtomicLong(0)
    (1..8).forEach {
        thread {
            while (true) {
                val id = counter.incrementAndGet()
                println("Creating item with id $id")
            }
        }
    }
}

private class Connection

private fun openConnection(): Connection {
    return Connection()
}

fun atomicReferences() {
    val ref = AtomicReference<Connection>()
    (1..8).forEach {
        thread {
            ref.compareAndSet(null, openConnection())
            val conn = ref.get()
            println(conn)
        }
    }
}
