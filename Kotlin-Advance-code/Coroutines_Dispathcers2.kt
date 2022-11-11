import kotlinx.coroutines.*

fun main() {
    runBlocking() {
        println("A:" + Thread.currentThread().name)

        launch(Dispatchers.Default) {
            println("B:" + Thread.currentThread().name)
        }

        launch() {
            println("C:" + Thread.currentThread().name)
        }
    }
    Thread.sleep(1000)
}