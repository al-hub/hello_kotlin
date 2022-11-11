//package _4_Concurrency_CountdownLatch

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val feeds = listOf(
        Feed("Vegetable Store", "http://www.greatvegstore.co.uk/items.xml"),
        Feed("Super Food Shop", "http://www.superfoodshop.com/products.csv"),
        Feed("Great Food Shop", "http://www.greatfoodshop.com/products.txt"),
        Feed("Super Local Store", "http://www.superlocalstore.co.uk/list.txt")
    )

    val latch = CountDownLatch(feeds.size)

    val executor = Executors.newCachedThreadPool()
    for (feed in feeds) {
        executor.submit {
            processFeed(feed)
            latch.countDown()
            Thread.sleep(500)
            println("Extra processing in progress...")
            Thread.sleep(500)
        }
    }

    latch.await()
    println("All feeds completed")
    sendNotification()

    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.MINUTES)
}

data class Feed(val name: String, val url: String)

fun processFeed(feed: Feed) {
    println("Processing feed ${feed.name}")
}

fun sendNotification() {
    println("Sending notification")
}
