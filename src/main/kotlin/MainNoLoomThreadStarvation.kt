import kotlinx.coroutines.*
import kotlin.random.Random

// This demo shows a potential thread pool starvation issue when using legacy/blocking code
// with coroutine Dispatchers.IO. Integration with LOOM solves this issue.


// Simulate a legacy, blocking I/O operation
// Note, this function is NOT suspending, it internally blocks the thread.
fun performLegacyBlockingIo(id: Int): String {
    val sleepTime = Random.nextLong(100, 500) // Simulate variable I/O time
    println("Thread ${Thread.currentThread()}: Starting blocking I/O for ID $id, sleeping for $sleepTime ms...")
    Thread.sleep(sleepTime) // This is the blocking call
    println("Thread ${Thread.currentThread()}: Finished blocking I/O for ID $id.")
    // note, to add a suspending function like delay, we would need make this function 'suspend'
    // delay(500) // This would be a coroutine suspension point
    return "Data from Legacy IO for $id"
}

fun main() = runBlocking {
    val startTime = System.currentTimeMillis()
    val numTasks = 10 // concurrent tasks, try
    val results = mutableListOf<String>()

    // Using Dispatchers.IO for blocking operations
    // This dispatcher uses a limited pool of platform threads (typically 64)
    val jobs = (1..numTasks).map { i ->
        launch(Dispatchers.IO) { // Explicitly switch to IO dispatcher for blocking calls
            // ⚠️Each call of 'performLegacyBlockingIo' will block a separate dispatcher IO thread.
            // If numTasks is significantly greater than 64, you would exhaust the Dispatchers.IO thread pool
            // causing blocking and a significant slowdown as threads get queued.
            val data = performLegacyBlockingIo(i)
            results.add(data)
        }
    }

    jobs.joinAll()

    val totalTime = System.currentTimeMillis() - startTime
    println("\n--- Traditional Coroutines with Dispatchers.IO ---")
    println("All $numTasks blocking tasks completed in $totalTime ms")
}

/*
Sample output - notice that for the same task ID, the 'Starting thread #id number' is the same as the
'Finished thread #id number' indicating blocking of the thread, e.g. for ID 10, this is Thread[#30]:

Thread Thread[#30,DefaultDispatcher-worker-10,5,main]: Starting blocking I/O for ID 10, sleeping for 139 ms...
Thread Thread[#29,DefaultDispatcher-worker-9,5,main]: Starting blocking I/O for ID 9, sleeping for 347 ms...
Thread Thread[#23,DefaultDispatcher-worker-3,5,main]: Starting blocking I/O for ID 2, sleeping for 205 ms...
Thread Thread[#21,DefaultDispatcher-worker-1,5,main]: Starting blocking I/O for ID 1, sleeping for 103 ms...
Thread Thread[#28,DefaultDispatcher-worker-8,5,main]: Starting blocking I/O for ID 8, sleeping for 375 ms...
Thread Thread[#22,DefaultDispatcher-worker-2,5,main]: Starting blocking I/O for ID 3, sleeping for 479 ms...
Thread Thread[#27,DefaultDispatcher-worker-7,5,main]: Starting blocking I/O for ID 7, sleeping for 146 ms...
Thread Thread[#26,DefaultDispatcher-worker-6,5,main]: Starting blocking I/O for ID 4, sleeping for 175 ms...
Thread Thread[#24,DefaultDispatcher-worker-4,5,main]: Starting blocking I/O for ID 6, sleeping for 321 ms...
Thread Thread[#25,DefaultDispatcher-worker-5,5,main]: Starting blocking I/O for ID 5, sleeping for 256 ms...
Thread Thread[#21,DefaultDispatcher-worker-1,5,main]: Finished blocking I/O for ID 1.
Thread Thread[#30,DefaultDispatcher-worker-10,5,main]: Finished blocking I/O for ID 10.
Thread Thread[#27,DefaultDispatcher-worker-7,5,main]: Finished blocking I/O for ID 7.
Thread Thread[#26,DefaultDispatcher-worker-6,5,main]: Finished blocking I/O for ID 4.
Thread Thread[#23,DefaultDispatcher-worker-3,5,main]: Finished blocking I/O for ID 2.
Thread Thread[#25,DefaultDispatcher-worker-5,5,main]: Finished blocking I/O for ID 5.
Thread Thread[#24,DefaultDispatcher-worker-4,5,main]: Finished blocking I/O for ID 6.
Thread Thread[#29,DefaultDispatcher-worker-9,5,main]: Finished blocking I/O for ID 9.
Thread Thread[#28,DefaultDispatcher-worker-8,5,main]: Finished blocking I/O for ID 8.
Thread Thread[#22,DefaultDispatcher-worker-2,5,main]: Finished blocking I/O for ID 3.
 */