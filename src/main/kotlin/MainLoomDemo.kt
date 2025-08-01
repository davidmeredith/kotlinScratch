import kotlinx.coroutines.*
import java.util.concurrent.Executors

// Create a CoroutineDispatcher backed by Java Virtual Threads
val LOOM: CoroutineDispatcher
    get() = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

// Note this function is NOT suspending, it internally blocks the thread.
fun blockingNetworkCall(): String {
    println("Performing blocking network call on ${Thread.currentThread()}")
    Thread.sleep(1000) // Simulate a blocking I/O operation
    return "Data from network"
}

fun main() = runBlocking {
    println("Starting main on ${Thread.currentThread()}")

    // Launch coroutines on the LOOM dispatcher
    val job1 = launch(LOOM) {
        println("Coroutine 1 start on ${Thread.currentThread()}")
        val result = blockingNetworkCall()
        println("Coroutine 1 finish, result received: $result on ${Thread.currentThread()}")
    }

    val job2 = launch(LOOM) {
        println("Coroutine 2 start on ${Thread.currentThread()}")
        delay(500) // This is a coroutine suspension point
        println("Coroutine 2 finish on ${Thread.currentThread()}")
    }

    job1.join()
    job2.join()
    println("All coroutines finished on ${Thread.currentThread()}")
}
/*
Sample output:
Notice that Coroutine 1 starts, blocks on the VT, then finishes on the same VT.
Coroutine 2 starts and finishes on different virtual threads due to the 'delay' suspension point.

Starting main on Thread[#1,main,5,main]
Coroutine 1 start on VirtualThread[#21]/runnable@ForkJoinPool-1-worker-1
Performing blocking network call on VirtualThread[#21]/runnable@ForkJoinPool-1-worker-1
Coroutine 2 start on VirtualThread[#25]/runnable@ForkJoinPool-1-worker-2
Coroutine 2 finish on VirtualThread[#27]/runnable@ForkJoinPool-1-worker-2
Coroutine 1 finish, result received: Data from network on VirtualThread[#21]/runnable@ForkJoinPool-1-worker-2
All coroutines finished on Thread[#1,main,5,main]
 */