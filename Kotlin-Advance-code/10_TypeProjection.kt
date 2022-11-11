package Generics_TypeProjection

import java.math.BigDecimal

fun main() {
    /* Covariance sample */
    val oranges = Crate(mutableListOf(Orange(), Orange()))
    isSafe2(oranges)

    /* Contravariance sample */
    val loggingListener = object : Listener2<Any> {
        override fun onNext(t: Any) = println(t)
    }
    EventStream2<Double>(loggingListener).start()
    EventStream2<BigDecimal>(loggingListener).start()
}

open class Fruit {
    fun isSafeToEat(): Boolean = true
}

class Apple : Fruit()
class Orange : Fruit()

class Crate<T>(val elements: MutableList<T>) {
    fun add(t: T) = elements.add(t)
    fun last(): T = elements.last()
}

fun isSafe2(crate: Crate<out Fruit>): Boolean =
    crate.elements.all { it.isSafeToEat() }

interface Listener2<T> {
    fun onNext(t: T): Unit
}

class EventStream2<T>(val listener: Listener2<in T>) {
    fun start(): Unit = TODO()
    fun stop(): Unit = TODO()
}
