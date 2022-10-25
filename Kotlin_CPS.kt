//CPS style
package com.scarlet.coroutines.advanced

object Continuation_Passing_Style_Demo {

    private fun add(a: Int, b: Int): Int = a + b
    private fun mul(a: Double, b: Double): Double = a * b

    //func 프로그램에서 type error만 발생하지 않도록 만들어 내면 그 코드는 올바른 코드일 가능성이 높다.
    //이것 이외에는 방법이 없기 때문이다. (작성하고나니 callback 스타일 임)
    private fun <R> addCPS(a: Int, b: Int, cont: (Int) -> R): R = cont(a + b)
    private fun <R> mulCPS(a: Double, b: Double, cont: (Double) -> R): R = cont(a * b)

    // (1 + 2) * (3 + 4)
    private fun <R> evaluateCPS(cont: (Double) -> R): R {
        // Label 0
        return addCPS(1, 2) { step1 ->
            // Label 1
            addCPS(3,4) { step2 ->
                // Label 2
                mulCPS(step1.toDouble(), step2.toDouble()){ step3 ->
                    cont(step3)
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(evaluateCPS({i -> i})) //identity function

        println(factCPS(10){ i -> i}) //identity function

        println(
            (0..10).map { fibCPS(it.toLong()) { i -> i} }.joinToString(", ") //identity function
        )
    }

    // Exercise 1: Convert this to CPS style
    private fun <R> factCPS(n: Long, cont: (Long) -> R): R =
        when (n) {
            0L -> cont(1L)
            else -> factCPS(n - 1) { prev ->
                cont(n*prev)
            }
        }

    // Exercise 2: Convert this to CPS style
    private fun <R> fibCPS(n: Long, cont: (Long) -> R): R =
        when (n) {
            0L, 1L -> cont(n)
            else -> fibCPS(n - 1) { prev1 ->
                fibCPS(n - 2) { prev2 ->
                    cont(prev1 + prev2)
                }
            }
        }

}
