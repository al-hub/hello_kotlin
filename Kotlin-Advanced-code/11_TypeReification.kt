package Generics_TypeReification

fun main() {
    runtimeType<Double>()
    runtimeType<String>()

    val list = listOf("green", false, 100, "blue")
    val strings = list.collect<String>() //collect는 extension function, 원래없던것을 만듦
    println(strings)

    printT<Int>(123)
    printT<Int>(1.23)
}

inline fun <reified T> runtimeType() {
    println("My type parameter is " + T::class.qualifiedName)
}

//List<T> 반환함수
inline fun <reified T> List<Any>.collect(): List<T> {
    return this.filter { it is T }.map { it as T }
}

inline fun <reified T> printT(any: Any) {
    if (any is T) println("Type match: $any")
    else println("Type mismatch: $any")
}
