
import java.util.*

fun main() {
    val randomGreeting1 = random1("hello", "willkommen", "bonjour")
    println(randomGreeting1)
    println((randomGreeting1 as String).length)

    val randomGreeting2 = random2("hello", "willkommen", "bonjour")
    println(randomGreeting2)
    println(randomGreeting2.length)

    val randomObj = random2("a", 1, false)
    println(randomObj)
}

fun random1(one: Any, two: Any, three: Any): Any {
    val r = Random()
    return when (r.nextInt(0, 3)) {
        0 -> one
        1 -> two
        2 -> three
        else -> throw Exception()
    }
}

fun <T> random2(one: T, two: T, three: T): T {
    val r = Random()
    return when (r.nextInt(0, 3)){
        0 -> one
        1 -> two
        2 -> three
        else -> throw Exception()
    }
}

fun <K, V> put(key: K, value: V): Unit {}