import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main() {

    fun logger(message: String) = println("${Thread.currentThread().name}: $message")

    runBlocking {
        logger("Starting")

        withContext(newSingleThreadContext("A")) {
            logger("running in new context")
        }
        withContext(newSingleThreadContext("B")){
            logger("running in new context")
        }
        logger("Completing")
    }

}