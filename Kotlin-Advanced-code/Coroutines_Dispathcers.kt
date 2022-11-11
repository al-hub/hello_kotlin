import kotlinx.coroutines.*

fun main() {
//    runBlocking(Dispatchers.IO) {
//        println(Thread.currentThread().name)
//    }
/*
    GlobalScope.launch {
        launch(Dispatchers.Default) {
            delay(100)
            println("Default:" + Thread.currentThread().name)
        }
        launch(Dispatchers.IO) {
            delay(100)
            println("IO:" +Thread.currentThread().name)
        }
        launch(Dispatchers.Unconfined) {
            println("Unfonfined 1:" +Thread.currentThread().name)
            delay(100)
            println("Unfonfined 2:" +Thread.currentThread().name)
        }
//        launch(Dispatchers.Main) {
//            delay(100)
//            println(Thread.currentThread().name)
//        }

    }*/


    runBlocking(Dispatchers.IO) {
        println(Thread.currentThread().name)

        launch(Dispatchers.Default) {
            delay(100)
            println("Default:" + Thread.currentThread().name)
        }
    }
    Thread.sleep(1000)
}