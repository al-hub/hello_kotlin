import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

fun main() {

    // 쓰레드가 실행되는 실질적인 부분
    thread { NetworkService(9000, 8).run() }
    //run() 으로 쓰레드가 실행되는 것이 아님

}

private class NetworkService(port: Int, poolSize: Int) : Runnable {
    private val serverSocket: ServerSocket
    private val pool: ExecutorService       //현 예제의 핵심사항

    // primary constructor 이외에 초기화
    init {
        serverSocket = ServerSocket(port)
        pool = Executors.newFixedThreadPool(poolSize)
    }

    // 이자체가 thread는 아님.
    override fun run() { // run the service
        try {
            while (true) {
                //excute, submit과 비슷
                pool.execute(Handler(serverSocket.accept())) //접속이 되면, Handler에서 처리
            }
        } catch (ex: IOException) {
            pool.shutdown()
        }
    }
}

private class Handler(private val socket: Socket) : Runnable {
    override fun run() {
        // read and service request on socket
    }
}
