### Generic Function정의  
```kotlin
fun <T> addNumbers(num1: T, num2: T): T {
  return (num1.toDouble() + num2.toDouble()) as T
}

fun main(args: Array<String>) {
  println(addNumbers(10,20))
  println(addNumbers(10.1, 20.1))
}
```

### Generic Class정의  
```kotlin
class Rectangle <T> (val width: T, val height T) {
}

fun main(args: Array<String>) {
  val rec = Rectangle<Double>(10,2)
  val rec1 = Rectangle<String>("aa","bb")
}
```

[Invariant↔Covariant, Contravariant](https://codechacha.com/ko/generics-class-function-in-kotlin/)  

