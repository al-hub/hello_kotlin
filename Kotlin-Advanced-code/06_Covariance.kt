package Generics_Covariance


// 과일     <------- 사과
// 과일 상자 <------- 사과 상자
// 내용

fun main() {
    val oranges = Crate(mutableListOf(Orange(), Orange()))
    //isSafe(oranges)  // error!

    val oranges2 = CovariantCrate(listOf(Orange(), Orange()))
    isSafe2(oranges2)
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

//(정확히)과일상자를 입력 받아, Boolean을 return으로 보낸다.
fun isSafe(crate: Crate<Fruit>): Boolean = crate.elements.all {
    it.isSafeToEat()
}

//오렌지 상자를 과일상자로 만들어보자.
//특이사항, out을 붙인다. 출력용 타입만으로 만든다.
class CovariantCrate<out T>(val elements: List<T>) { //출력용이기 때문에 읽기로만 쓴다. Mutable -> Immutable로 쓰자.
    //fun add(t: T) = elements.add(t) //출력용 T이기 때문에 안된다.
    fun last(): T = elements.last()
}
//왜 이렇게하면 되는가!!??
//out 쓰면 직관적인 형태로 사용된다. -> 로 일단 이해

fun isSafe2(crate: CovariantCrate<Fruit>): Boolean = crate.elements.all {
    it.isSafeToEat()
}
