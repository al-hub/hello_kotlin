package Generics_Contravariance3

//fun main() {
//    val generator = object : Generator<Fruit> {
//        override fun generate(): Fruit = Tomato()  // random fruit
//    }
//    val picker = OrangePicker(generator)
//    picker.pick()
//}
//
//open class Fruit {
//    fun isSafeToEat(): Boolean = true
//}
//
//class Apple : Fruit()
//class Orange : Fruit()
//class Tomato : Fruit()
//
//interface Generator<in T> {
//    fun generate(): T
//}
//
//class OrangePicker(val generator: Generator<Orange>) {
//    fun pick() {
//        val orange = generator.generate()
//        peel(orange)
//    }
//
//    fun peel(orange: Orange): Unit {}  // peel the orange
//}
