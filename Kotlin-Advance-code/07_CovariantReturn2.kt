package Generics_CovariantReturn2

fun main() {
    val sheepFarm = Farm()

    //특징 get 을 할때, type 을 할 수 도 있다.
    //val animal: Animal = sheepFarm.get<Sheep>()
    val animal = sheepFarm.get<Sheep>()
}

open class Animal
class Sheep : Animal()
class Frog : Animal()

//class 내부의 개별함수에 대해 Generics도 가능하다.
class Farm {

    //inline에서만 reified(구체화) 유지가 가능한 keyword 임
    inline fun <reified T> get(): T {
        return T::class.java.newInstance()                              //둘 다 가능함
        //return T::class.java.getDeclaredConstructor().newInstance()
    }
}
