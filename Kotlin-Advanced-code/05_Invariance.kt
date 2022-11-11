package Generics_Invariance

fun main() {

    val oranges = Crate(mutableListOf(Orange(), Orange()))  //Crate 생성자의 의미
    //foo(oranges)  // error!

    //문제해결법1. Fruit를 명시한다.
    //val oranges = Crate<Fruit>(mutableListOf(Orange(), Orange()))

    //문제해결법2.
    //foo(oranges as Crate<Fruit>)
    //당장은 해결되지만 강제 casting에서 문제가 될 수 있다. -> 안전하지 않다. ( code safeness )

}

// kotlin 기본은 public, finally (상속은 폐쇠적)
// open 상속가능한 class
open class Fruit

class Apple : Fruit() //대부분 부모클래스는 괄호가 있다.

class Orange : Fruit()

//상자의 의미, T 과일이면 과일, T가 애플이면 애플
class Crate<T>(val elements: MutableList<T>) {
    fun add(t: T) = elements.add(t)            //element 추가 함수
    fun last(): T = elements.last()            //last하면 맨 마지막을 리턴하는 것 임
}

fun foo(crate: Crate<Fruit>) {
    crate.add(Apple())
}
