package _4_Concurrency_Thread

import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import kotlin.concurrent.thread

fun main() {
//    immediateStart()

//    delayedStart()

//    val t = StoppableTask()
//    t.run()
//    Thread.sleep(5000)
//    t.running = false

    producerConsumer()

//    InterruptableProducerConsumer()
}

fun immediateStart() {
    thread(start = true, name = "mythread") {
        while (true) {
            println("Hello, I am running on a thread")
            Thread.sleep(1000)
        }
    }
}

fun delayedStart() {
    val t = thread(start = false, name = "mythread") {
        while (true) {
            println("Hello, I am running on a thread sometime later")
            Thread.sleep(1000)
        }
    }
    Thread.sleep(3000)
    t.start()
}

//클래스로 구현, Runnable 만 보증하는 것이지 쓰레드는 아니다.
class StoppableTask : Runnable {
    //@volatile annotation key 주의 ( 이 변수는 또다른 코드 흐름에 의해서 바뀔 수 있다. 휘발성 )
    //컴파일러야 최적화는 하지 말거라.. ( 빼면 컴파일러에 따라 정상동작이 안 될 수 도 있다. C에서도 마찬가지 임)
    @Volatile
    var running = true

    override fun run() {
        thread(start = true, name = "mythread") {
            while (running) {
                println("Hello, I am running on a thread until I am stopped")
                Thread.sleep(1000)
            }
        }
    }
}

fun producerConsumer() {
    val queue = LinkedBlockingDeque<Int>()

    // 코틀린스러운 6회 반복하면서 List 만들기
    val consumerTasks = (1..6).map { ConsumerTask(queue) }
    val producerTask = ProducerTask(queue)

    //실제 쓰레드 만드는 것
    //쓰레드 인자 중 isDaemon false 메인 끝나더라도 계속 살아 있음
    val consumerThreads = consumerTasks.map { thread { it.run() } }
    val producerThread = thread { producerTask.run() }

    // later
    Thread.sleep(5000)

    //하나하나 들어가서 runing flag 변경 But 잠자고 있는 녀석은 it.running = false 을 확인할 길이 없다.
    consumerTasks.forEach { it.running = false }
    //결과적으로 한 놈만 깨어나고, main이 먼저 종료되어, 나머지 놈들은 잠자고 있어서 확인 할 길이 없다.

    producerTask.running = false

    println("Done - main")
}

class ProducerTask(val queue: BlockingQueue<Int>) {
    @Volatile
    var running = true
    private val random = Random()

    fun run() {
        while (running) {
            Thread.sleep(1000)
            queue.put(random.nextInt())
        }
        println("Done - Producer")
    }
}

class ConsumerTask(val queue: BlockingQueue<Int>) {
    @Volatile
    var running = true

    fun run() {
        while (running) {
            val element = queue.take() // 문제가 되는 부분, producer에서 보내줘야지.. - -
                                       // 잠자고 있는 부분
            println("I am processing element $element")
        }
        println("Done - Consumer")
    }
}



fun InterruptableProducerConsumer() {
    val queue = LinkedBlockingDeque<Int>()

    val consumerTasks = (1..6).map { InterruptableConsumerTask(queue) }
    val producerTask = ProducerTask(queue)

    val consumerThreads = consumerTasks.map { thread { it.run() } }
    val producerThread = thread { producerTask.run() }

    // later
    Thread.sleep(5000)
    consumerThreads.forEach { it.interrupt() }
    producerTask.running = false
}

class InterruptableConsumerTask(val queue: BlockingQueue<Int>) : Runnable {
    override fun run() {
        try {
            while (!Thread.interrupted()) {
                val element = queue.take()
                println("I am processing element $element")
            }
        } catch (e: InterruptedException) {
            // shutting down
            println("Done(Interrupted) - Consumer")
            return
        }
        println("Done - Consumer")
    }
}
