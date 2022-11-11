package _4_Concurrency_Executors

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val executor = Executors.newFixedThreadPool(4)
//    for (k in 1..10) {
//        executor.submit {
//            Thread.sleep(1000)
//            println("Processing element $k on thread ${Thread.currentThread()}")
//        }
//    }

    (1..10).forEach {
        executor.submit {
            Thread.sleep(1000)
            println("Processing element $it on thread ${Thread.currentThread()}")
        }
    }

    executor.shutdown()

    //모든 작업이 끝날때까지 기다린다.
    executor.awaitTermination(1, TimeUnit.MINUTES)
}
