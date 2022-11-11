package _4_Concurrency_CyclicBarrier

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val inFiles = mutableListOf<String>()
    for (n in 1..99)
        inFiles.add("C:\\Temp\\${n}.txt")
    val outDirs = listOf(
        "c:\\DataDir1\\", "c:\\DataDir2\\",
        "c:\\DataDir3\\", "c:\\DataDir4\\"
    )
    copyUsingBarrier(inFiles, outDirs)
}

fun copyUsingBarrier(inputFiles: List<String>, outputDirectories: List<String>) {
    val executor = Executors.newFixedThreadPool(outputDirectories.size)
    val barrier = CyclicBarrier(outputDirectories.size)

    for (dir in outputDirectories) {
        executor.submit {
            CopyTask(dir, inputFiles, barrier).run()
        }
    }

    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.MINUTES)
}

class CopyTask(val dir: String, val paths: List<String>, val barrier: CyclicBarrier) {
    fun run() {
        for (path in paths) {
            println("${path}를 ${dir}에 복사 완료! ${Thread.currentThread().name}")
            barrier.await()
        }
        println("모든 파일 복사 완료! ${Thread.currentThread().name}")
    }
}
