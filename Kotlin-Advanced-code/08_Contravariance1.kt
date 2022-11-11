package Generics_Contravariance1

// 본래
// 과일     <------- 사과
// 과일 상자 <------- 사과 상자
// 인데,

// 과일 상자 -------> 사과 상자 가 필요할 때,


import java.math.BigDecimal
import java.util.*
import java.util.function.ToDoubleFunction

fun main() {

    // object 문법
    val stringListener = object : Listener<String> {
        override fun onNext(t: String) = println(t)
    }
    val stringStream = EventStream<String>(stringListener)
    stringStream.start()


    // object 문법
    val dateListener = object : Listener<Date> {
        override fun onNext(t: Date) = println(t)
    }
    val dateStream = EventStream<Date>(dateListener)
    dateStream.start()


    // 이전에는 String, Data를 사용했으나,
    // 이번에 Any를 써보자.
    // 하나만 만들어 사용해보자는 의도
    /*
    val loggingListener = object : Listener<Any> {
        override fun onNext(t: Any) = println(t)
    }

    //Type 추정이 안되기 때문에, Double, BigDecimal 쓰자
    EventStream<Double>(loggingListener).start()  // error!
    EventStream<BigDecimal>(loggingListener).start()  // error!
    */

    //하지만 문제가 생긴다.
    //왜그럴까?
    //type mismatch

    // 좀 더 구체적인 type을 상위 clas라고 생각하고 넣어주어 보자.
    // 개념
    // 사과 상자 <--------- 과일 상자
    //  Double                Any

    // 해결방식은 2번에 예제에서 보자.
}

interface Listener<T> {
    fun onNext(t: T): Unit
}


class EventStream<T>(val listener: Listener<T>) {
    fun start(): Unit = TODO()
    fun stop(): Unit = TODO()

}
