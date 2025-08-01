import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.random.Random

// This demo shows how integration with Loom prevents thread starvation that you could
// encounter if you were using the Dispatchers.IO pool, which defaults to 64 threads.
//
// You'll observe that this completes significantly faster and without
// thread starvation even for a very large number of tasks, because
// the virtual threads efficiently "unmount" during Thread.sleep.

// Define a dispatcher backed by Virtual Threads
// (Requires Java 21+ and kotlinx.coroutines 1.7.0+)
// For demonstration we'll create a simple dispatcher.
// In a real app, you might use a more robust way to define it.
val VIRTUAL_THREADS: CoroutineDispatcher
    get() = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

// Note this function is NOT suspending, it internally blocks the thread.
fun performLegacyBlockingIoDemo(id: Int): String {
    val sleepTime = Random.nextLong(100, 500)
    println("Virtual Thread ${Thread.currentThread()}: Starting blocking I/O for ID $id, sleeping for $sleepTime ms...")
    Thread.sleep(sleepTime) // This is the blocking call, but on a Virtual Thread, it will yield the carrier thread.
    println("Virtual Thread ${Thread.currentThread()}: Finished blocking I/O for ID $id.")
    // note, to add a suspending function like delay, we would need make this function 'suspend'
    // delay(500) // This would be a coroutine suspension point
    return "Data from Legacy IO for $id"
}

fun main() = runBlocking {
    val startTime = System.currentTimeMillis()
    val numTasks = 10000 // Much larger number of concurrent tasks

    val results = mutableListOf<String>()

    // Using a dispatcher backed by Virtual Threads
    val jobs = (1..numTasks).map { i ->
        launch(VIRTUAL_THREADS) { // No need to make performLegacyBlockingIo suspend or wrap in withContext
            val data = performLegacyBlockingIoDemo(i) // Calling a "blocking" function directly
            results.add(data)
        }
    }

    jobs.joinAll()

    val totalTime = System.currentTimeMillis() - startTime
    println("\n--- Coroutines with Virtual Threads (Reduced Function Coloring) ---")
    println("All $numTasks blocking tasks completed in $totalTime ms")
}