package Generics_Contravariance2

/*
import java.math.BigDecimal

fun main() {
    val loggingListener = object : Listener<Any> {
        override fun onNext(t: Any) = println(t)
    }
    EventStream<Double>(loggingListener).start()
    EventStream<BigDecimal>(loggingListener).start()
}

interface Listener<in T> {
    fun onNext(t: T): Unit
}

class EventStream<T>(val listener: Listener<T>) {
    fun start(): Unit = TODO()
    fun stop(): Unit = TODO()
}
*/