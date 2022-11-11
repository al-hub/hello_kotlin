fun main() {

    val a: Int = min(4, 5)
    println(a)

    val b: String = min("world", "hello")
    println(b)

    //val c = min(Pair(10, 20), Pair(20, 30))
}

//T: Comparable<T> -> 타입 T는 비교가 가능해야 한다.
fun <T: Comparable<T>> min(first: T, second: T): T {

    val k = first.compareTo(second)
    return if (k <= 0) first else second //kotlin if () - else - 는 expression

}