package Generics_CovariantReturn1

fun main() {
    val farm: Farm = SheepFarm()
    val animal1: Animal = farm.get()
    val sheepFarm: SheepFarm = SheepFarm()
    val animal2: Sheep = sheepFarm.get()
}

open class Animal
class Sheep : Animal()
class Frog : Animal()

abstract class Farm {
    abstract fun get(): Animal
}

class SheepFarm : Farm() {
    override fun get(): Sheep {
        return Sheep()
    }
}
