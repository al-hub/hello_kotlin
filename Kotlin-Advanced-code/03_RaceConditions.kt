package _4_Concurrency_RaceConditions

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

fun main() {
//    syncExample()

//    lockOrNot()

    lockOrInterrupt()

//    kotlinLock()
}

fun syncExample() {
    val obj = Any()
    synchronized(obj) {
    }
}

fun lockOrNot() {

    val lock = ReentrantLock() //재진입이 가능하다.
    if (lock.tryLock()) {
        println("I have the lock")
        lock.unlock()
    } else {
        println("I do not have the lock")
    }
}

fun lockOrInterrupt() {
    val lock = ReentrantLock()
    try {
        lock.lockInterruptibly()
        println("I have the lock")
        lock.unlock()
    } catch (e: InterruptedException) {
        println("I was interrupted")
    }
}

fun kotlinLock() {
    val lock = ReentrantLock()
    lock.withLock {
        println("I have the lock")
    }
}
