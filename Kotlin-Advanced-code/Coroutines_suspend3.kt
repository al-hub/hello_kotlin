import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() {
    suspend fun printShippingLabels() {
        delay(3000)
        println("After three seconds")
    }

    suspend fun processPayment() {
        delay(2000)
        println("Order after two seconds")
    }

    suspend fun sendEmails() {
        delay(1000)
        println("Payment after one second")
    }

    println("--- Starting")

    val time = measureTimeMillis {
        runBlocking {

//            printShippingLabels()
//            processPayment()
//            sendEmails()

            //runBlock과 launch 설명문서 읽어 봐야 함 : 계속전달됨
            this.launch {
                printShippingLabels()
            }

            launch {
                processPayment()
            }

            launch {
                sendEmails()
            }

        }
        println("runBlocking done")
    }
    println("--- Complete in ${time}ms")

}