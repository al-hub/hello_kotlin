package exampleBasic07

fun main() {
    repeat(100_000) {
        Thread {
            Thread.sleep(5000)
            print(".")
        }.start()
    }
}
