# Section 4 - Threads

**Understanding lightweight parallelism within processes**

---

## What is a Thread?

**A thread is the smallest unit of execution within a process.**

**Simple definition:** A thread is a unit of execution within a process - a single sequence of code execution.

❗Critical: Every process has at least one thread (the main thread).

**Key insight:** While a process has its own memory space, threads within the same process SHARE the same memory space.

**Analogy:**

- **Process** = A restaurant kitchen
- **Threads** = Multiple chefs working in the same kitchen
  - They share the same ingredients (memory)
  - They share the same equipment (resources)
  - They can work on different tasks simultaneously
  - They need to coordinate to avoid conflicts

---

## Understanding Processes and Threads

### Single-threaded process VS Multi-threaded process

**This is THE most important concept for developers!**

⚠️ Remember: Processes CONTAIN threads. Every process has ≥1 thread.

The comparison below shows:

- Single-threaded process (1 thread) vs Multi-threaded process (many threads)

### Single-threaded P**rocess (Heavy)**

```
┌─────────────────────────────────────┐
│         Process 1                   │
│                                     │
│  ┌──────────────────────────────┐   │
│  │   Memory Space               │   │
│  │  - Stack                     │   │
│  │  - Heap                      │   │
│  │  - Code                      │   │
│  │  - Data                      │   │
│  └──────────────────────────────┘   │
│                                     │
│  Single thread of execution         │
│                                     │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│         Process 2                   │
│                                     │
│  ┌──────────────────────────────┐   │
│  │   SEPARATE Memory Space      │   │
│  │  - Stack                     │   │
│  │  - Heap                      │   │
│  │  - Code                      │   │
│  │  - Data                      │   │
│  └──────────────────────────────┘   │
│                                     │
│  Single thread of execution         │
│                                     │
└─────────────────────────────────────┘

Processes are ISOLATED from each other
Cannot share memory directly
Heavy to create (~1-10 ms)

```

---

### **Multi-threaded Process (Lightweight)**

```
┌─────────────────────────────────────────────────────┐
│              Single Process                         │
│                                                     │
│  ┌──────────────────────────────────────────────┐   │
│  │   SHARED Memory Space                        │   │
│  │                                              │   │
│  │  - Heap (SHARED)                             │   │
│  │  - Code (SHARED)                             │   │
│  │  - Data (SHARED)                             │   │
│  │                                              │   │
│  └──────────────────────────────────────────────┘   │
│                                                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐           │
│  │ Thread 1 │  │ Thread 2 │  │ Thread 3 │           │
│  │          │  │          │  │          │           │
│  │ - Stack  │  │ - Stack  │  │ - Stack  │           │
│  │ - Regs   │  │ - Regs   │  │ - Regs   │           │
│  └──────────┘  └──────────┘  └──────────┘           │
│                                                     │
│  All threads SHARE the same memory!                 │
│                                                     │
└─────────────────────────────────────────────────────┘

Threads share memory within same process
Can communicate easily via shared variables
Lightweight to create (~1-100 microseconds)

```

---

### Comparison: Creating New Process vs Adding Thread to Existing Process

Context: This table compares what happens when you:

- Create a brand new process (which starts with 1 thread)
- vs. Add a thread to an already-running process

| Feature | Process | Thread |
| --- | --- | --- |
| **Memory** | Separate memory space | Shared memory space |
| **Creation time** | Slow (1-10 ms) | Fast (1-100 μs) |
| **Creation overhead** | High (copy memory, resources) | Low (only stack) |
| **Communication** | IPC (pipes, sockets, shared memory) | Direct (shared variables) |
| **Context switch** | Expensive (change memory mappings) | Cheap (same memory space) |
| **Isolation** | Strong (can't access other's memory) | Weak (can access shared memory) |
| **Safety** | Crash in one doesn't affect others | Crash in one crashes all threads |
| **Use case** | Heavy, isolated tasks | Lightweight, cooperative tasks |

---

### **Visual Example: Web Server**

**Multi-Process Approach:**

```
Client 1 → Process 1 (separate memory)
Client 2 → Process 2 (separate memory)
Client 3 → Process 3 (separate memory)

Pros: Crash in one doesn't affect others
Cons: High memory usage (each has full copy)

```

**Multi-Threaded Approach:**

```
Client 1 → Thread 1 ┐
Client 2 → Thread 2 ├─ Same Process (shared memory)
Client 3 → Thread 3 ┘

Pros: Low memory usage, fast creation
Cons: Bug in one thread can crash all

```

---

## Why Threads Exist

⚠️ Problem: Creating a new process for each task is too expensive (1-10ms + memory overhead)

✅ Solution: Use multiple threads within a single process (100x faster to create)

**Example scenarios:**

### **1. Web Server Handling Requests**

**Without threads (sequential):**

**Java:**

```java
while (true) {
    Socket client = serverSocket.accept();
    handleRequest(client);  // Takes 100ms
    // Must finish before accepting next client!
}
// Result: Only 10 requests/second

```

**C#:**

```csharp
while (true) {
    TcpClient client = listener.AcceptTcpClient();
    HandleRequest(client);  // Takes 100ms
    // Must finish before accepting next client!
}
// Result: Only 10 requests/second

```

**JavaScript (Node.js):**

```jsx
// Node.js handles this differently with async/await (single-threaded)
server.on('connection', (socket) => {
    handleRequest(socket);  // Non-blocking by default
});
// Node.js uses event loop instead of threads for I/O

```

**With threads:**

**Java:**

```java
while (true) {
    Socket client = serverSocket.accept();
    new Thread(() -> handleRequest(client)).start();
    // Immediately accept next client!
}
// Result: 1000s of requests/second

```

**C#:**

```csharp
while (true) {
    TcpClient client = await listener.AcceptTcpClientAsync();
    Task.Run(() => HandleRequest(client));
    // Immediately accept next client!
}
// Result: 1000s of requests/second

```

---

### **2. GUI Application**

**Without threads:**

**Java (Swing):**

```java
button.addActionListener(e -> {
    downloadLargeFile();  // Takes 30 seconds
    // UI is FROZEN during this time!
});

```

**C# (WPF):**

```csharp
private void Button_Click(object sender, RoutedEventArgs e) {
    DownloadLargeFile();  // Takes 30 seconds
    // UI is FROZEN during this time!
}

```

**With threads:**

**Java (Swing):**

```java
button.addActionListener(e -> {
    new Thread(() -> downloadLargeFile()).start();
    // UI remains responsive!
});

```

**C# (WPF):**

```csharp
private async void Button_Click(object sender, RoutedEventArgs e) {
    await Task.Run(() => DownloadLargeFile());
    // UI remains responsive!
}

```

---

### **3. Data Processing**

**Without threads:**

**Java:**

```java
public void processData(List<Item> items) {
    for (Item item : items) {
        process(item);  // Process one at a time
    }
}
// 1000 items × 1 second = 1000 seconds

```

**C#:**

```csharp
public void ProcessData(List<Item> items) {
    foreach (var item in items) {
        Process(item);  // Process one at a time
    }
}
// 1000 items × 1 second = 1000 seconds

```

**With threads (on 8-core CPU):**

**Java:**

```java
ExecutorService executor = Executors.newFixedThreadPool(8);
for (Item item : items) {
    executor.submit(() -> process(item));
}
executor.shutdown();
// 1000 items ÷ 8 threads ≈ 125 seconds

```

**C#:**

```csharp
Parallel.ForEach(items, new ParallelOptions { MaxDegreeOfParallelism = 8 },
    item => Process(item));
// 1000 items ÷ 8 threads ≈ 125 seconds

```

---

## Single-Threaded vs Multi-Threaded Applications

### **Single-Threaded Application**

```
┌─────────────────────────────────┐
│       Process                   │
│                                 │
│   ┌─────────────────────┐       │
│   │   Main Thread       │       │
│   │                     │       │
│   │   Does everything   │       │
│   │   one at a time     │       │
│   └─────────────────────┘       │ 
│                                 │
└─────────────────────────────────┘

Execution Flow:
Task 1 → Task 2 → Task 3 → Task 4
(Sequential, one after another)

```

**Examples:**

- Simple scripts
- Command-line tools
- Basic applications

**Characteristics:**

- Easy to reason about (no concurrency issues)
- Simple to debug
- Can't utilize multiple CPU cores
- Blocks on I/O operations

---

### **Multi-Threaded Application**

```
┌─────────────────────────────────────────┐
│            Process                      │
│                                         │
│   ┌─────────────┐  ┌─────────────┐      │
│   │  Thread 1   │  │  Thread 2   │      │
│   │             │  │             │      │
│   │  Task 1     │  │  Task 3     │      │ 
│   └─────────────┘  └─────────────┘      │
│                                         │
│   ┌─────────────┐  ┌─────────────┐      │
│   │  Thread 3   │  │  Thread 4   │      │
│   │             │  │             │      │
│   │  Task 2     │  │  Task 4     │      │
│   └─────────────┘  └─────────────┘      │
│                                         │
└─────────────────────────────────────────┘

Execution Flow:
Task 1, Task 2, Task 3, Task 4 (ALL simultaneously)

```

**Examples:**

- Web servers
- Desktop applications with GUI
- Games
- Database servers

**Characteristics:**

- Can utilize multiple CPU cores
- Better responsiveness
- More complex (synchronization needed)
- Harder to debug

---

## Thread Creation and Management

### **Java Example:**

```java
public class ThreadDemo {
    // Method 1: Implement Runnable (PREFERRED)
    static class Worker implements Runnable {
        private int threadId;

        public Worker(int id) {
            this.threadId = id;
        }

        @Override
        public void run() {
            System.out.println("Thread " + threadId + " starting");
            try {
                Thread.sleep(2000);  // Simulate work
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread " + threadId + " finished");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Create threads
        Thread thread1 = new Thread(new Worker(1));
        Thread thread2 = new Thread(new Worker(2));
        Thread thread3 = new Thread(new Worker(3));

        // Start threads
        thread1.start();
        thread2.start();
        thread3.start();

        // Wait for all threads to finish
        thread1.join();
        thread2.join();
        thread3.join();

        System.out.println("All threads completed");
    }
}

```

**Java with Lambda (Modern):**

```java
public static void main(String[] args) throws InterruptedException {
    Thread thread1 = new Thread(() -> {
        System.out.println("Thread 1 starting");
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        System.out.println("Thread 1 finished");
    });

    thread1.start();
    thread1.join();
}

```

---

### **C# Example:**

```csharp
using System;
using System.Threading;

class ThreadDemo
{
    static void Worker(object threadId)
    {
        Console.WriteLine($"Thread {threadId} starting");
        Thread.Sleep(2000);  // Simulate work
        Console.WriteLine($"Thread {threadId} finished");
    }

    static void Main()
    {
        // Create threads
        Thread thread1 = new Thread(Worker);
        Thread thread2 = new Thread(Worker);
        Thread thread3 = new Thread(Worker);

        // Start threads
        thread1.Start(1);
        thread2.Start(2);
        thread3.Start(3);

        // Wait for all threads to finish
        thread1.Join();
        thread2.Join();
        thread3.Join();

        Console.WriteLine("All threads completed");
    }
}

```

**C# with Task (Modern - PREFERRED):**

```csharp
using System;
using System.Threading.Tasks;

class ThreadDemo
{
    static async Task Worker(int threadId)
    {
        Console.WriteLine($"Thread {threadId} starting");
        await Task.Delay(2000);  // Simulate work
        Console.WriteLine($"Thread {threadId} finished");
    }

    static async Task Main()
    {
        Task task1 = Worker(1);
        Task task2 = Worker(2);
        Task task3 = Worker(3);

        await Task.WhenAll(task1, task2, task3);

        Console.WriteLine("All threads completed");
    }
}

```

---

### **JavaScript Example (Node.js):**

```jsx
// JavaScript is single-threaded by default
// But can use Worker Threads for true parallelism

const { Worker } = require('worker_threads');

function runWorker(threadId) {
    return new Promise((resolve, reject) => {
        // Create worker with inline code
        const worker = new Worker(`
            const { parentPort, workerData } = require('worker_threads');
            console.log('Thread', workerData, 'starting');
            setTimeout(() => {
                console.log('Thread', workerData, 'finished');
                parentPort.postMessage('done');
            }, 2000);
        `, { eval: true, workerData: threadId });

        worker.on('message', resolve);
        worker.on('error', reject);
    });
}

// Run multiple workers in parallel
Promise.all([
    runWorker(1),
    runWorker(2),
    runWorker(3)
]).then(() => {
    console.log('All threads completed');
});

```

**Output (all three languages):**

```
Thread 1 starting
Thread 2 starting
Thread 3 starting
(2 seconds pass)
Thread 1 finished
Thread 2 finished
Thread 3 finished
All threads completed

```

**What happened:**

1. Created 3 threads
2. All started simultaneously
3. All ran for 2 seconds in parallel
4. Total time: ~2 seconds (not 6!)

---

## Shared Memory Between Threads

**This is the key difference from processes!**

### **Threads Share:**

- ✅ Heap memory
- ✅ Global variables
- ✅ Code section
- ✅ Open file descriptors
- ✅ Signal handlers

### **Threads DON'T Share:**

- ❌ Stack (each thread has its own)
- ❌ Registers (each thread has its own)
- ❌ Thread ID

---

### **Example: Shared Counter (Race Condition)**

**Java:**

```java
public class RaceCondition {
    static int counter = 0;  // Shared global variable!

    static void increment() {
        for (int i = 0; i < 100000; i++) {
            counter++;  // Race condition!
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> increment());
        Thread thread2 = new Thread(() -> increment());

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Counter: " + counter);
        // Expected: 200000
        // Actual: ??? (probably less than 200000!)
    }
}

```

**C#:**

```csharp
class RaceCondition
{
    static int counter = 0;  // Shared global variable!

    static void Increment()
    {
        for (int i = 0; i < 100000; i++)
        {
            counter++;  // Race condition!
        }
    }

    static void Main()
    {
        Thread thread1 = new Thread(Increment);
        Thread thread2 = new Thread(Increment);

        thread1.Start();
        thread2.Start();

        thread1.Join();
        thread2.Join();

        Console.WriteLine($"Counter: {counter}");
        // Expected: 200000
        // Actual: ??? (probably less than 200000!)
    }
}

```

**JavaScript (Node.js with Worker Threads):**

```jsx
// Shared memory in Node.js requires SharedArrayBuffer
const { Worker, isMainThread, parentPort, workerData } = require('worker_threads');

if (isMainThread) {
    const sharedBuffer = new SharedArrayBuffer(4);
    const sharedArray = new Int32Array(sharedBuffer);

    const worker1 = new Worker(__filename, { workerData: sharedBuffer });
    const worker2 = new Worker(__filename, { workerData: sharedBuffer });

    Promise.all([
        new Promise(resolve => worker1.on('exit', resolve)),
        new Promise(resolve => worker2.on('exit', resolve))
    ]).then(() => {
        console.log('Counter:', sharedArray[0]);
        // Expected: 200000
        // Actual: ??? (probably less!)
    });
} else {
    const sharedArray = new Int32Array(workerData);
    for (let i = 0; i < 100000; i++) {
        sharedArray[0]++;  // Race condition!
    }
}

```

**Expected result:** 200,000
**Actual result:** Varies! (Could be 150,000, 180,000, etc.)

**Why?** → **Race condition!**

---

## Thread Synchronization Problems

### **Problem 1: Race Conditions**

**A race condition occurs when the outcome depends on the timing of thread execution.**

**What happens in the counter example:**

```
Initial: counter = 0

Thread 1 reads counter: 0
Thread 2 reads counter: 0  (both read same value!)
Thread 1 increments: 0 + 1 = 1
Thread 2 increments: 0 + 1 = 1  (both write 1!)
Thread 1 writes counter: 1
Thread 2 writes counter: 1  (overwrites!)

Result: counter = 1 (should be 2!)

```

**At CPU level, `counter++` is actually 3 instructions:**

```
LOAD counter into register    # Read
ADD 1 to register             # Increment
STORE register to counter     # Write

```

**Threads can interleave these instructions:**

```
Thread 1: LOAD counter (0)
Thread 2: LOAD counter (0)    ← Both read 0!
Thread 1: ADD 1 (register = 1)
Thread 2: ADD 1 (register = 1)
Thread 1: STORE 1
Thread 2: STORE 1             ← Lost an increment!

Counter = 1 (should be 2)

```

---

### **Real-World Example: Banking System**

**Java:**

```java
static int balance = 1000;

static void withdraw(int amount) {
    int temp = balance;        // Read
    temp = temp - amount;      // Calculate
    balance = temp;            // Write
}

// Two threads withdraw simultaneously
Thread thread1 = new Thread(() -> withdraw(100));
Thread thread2 = new Thread(() -> withdraw(100));

```

**C#:**

```csharp
static int balance = 1000;

static void Withdraw(int amount) {
    int temp = balance;        // Read
    temp = temp - amount;      // Calculate
    balance = temp;            // Write
}

// Two threads withdraw simultaneously
Thread thread1 = new Thread(() => Withdraw(100));
Thread thread2 = new Thread(() => Withdraw(100));

```

**What could happen:**

```
Initial balance: $1000

Thread 1 reads: $1000
Thread 2 reads: $1000  (both read same!)
Thread 1 calculates: $1000 - $100 = $900
Thread 2 calculates: $1000 - $100 = $900
Thread 1 writes: $900
Thread 2 writes: $900

Final balance: $900 (should be $800!)
Lost $100!

```

❗**This is why banking systems use locks!**

---

## Locks, Mutexes, and Semaphores

**Solution to race conditions: Synchronization primitives**

### **1. Locks (Mutexes)**

**Mutex = Mutual Exclusion**

**Concept:** Only ONE thread can hold the lock at a time.

```
Lock is like a bathroom key:
- Only one person can have it
- Others must wait
- When done, return key

```

---

### **Using Locks in Java:**

```java
public class ThreadSafeCounter {
    private int counter = 0;
    private final Object lock = new Object();

    public void increment() {
        for (int i = 0; i < 100000; i++) {
            synchronized(lock) {  // Get lock
                counter++;
            }  // Release lock automatically
        }
    }

    public int getCounter() {
        synchronized(lock) {
            return counter;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadSafeCounter tsc = new ThreadSafeCounter();

        Thread thread1 = new Thread(() -> tsc.increment());
        Thread thread2 = new Thread(() -> tsc.increment());

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Counter: " + tsc.getCounter());
        // Now always: 200000 (correct!)
    }
}

```

**Java with ReentrantLock:**

```java
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeCounter {
    private int counter = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public void increment() {
        for (int i = 0; i < 100000; i++) {
            lock.lock();  // Acquire lock
            try {
                counter++;
            } finally {
                lock.unlock();  // Always release!
            }
        }
    }
}

```

---

### **Using Locks in C#:**

```csharp
class ThreadSafeCounter
{
    private int counter = 0;
    private readonly object lockObject = new object();

    public void Increment()
    {
        for (int i = 0; i < 100000; i++)
        {
            lock(lockObject)  // Acquire lock
            {
                counter++;
            }  // Release lock automatically
        }
    }

    public int GetCounter()
    {
        lock(lockObject)
        {
            return counter;
        }
    }

    static void Main()
    {
        ThreadSafeCounter tsc = new ThreadSafeCounter();

        Thread thread1 = new Thread(tsc.Increment);
        Thread thread2 = new Thread(tsc.Increment);

        thread1.Start();
        thread2.Start();

        thread1.Join();
        thread2.Join();

        Console.WriteLine($"Counter: {tsc.GetCounter()}");
        // Now always: 200000 (correct!)
    }
}

```

**C# with Interlocked (for simple operations):**

```csharp
class ThreadSafeCounter
{
    private int counter = 0;

    public void Increment()
    {
        for (int i = 0; i < 100000; i++)
        {
            Interlocked.Increment(ref counter);  // Atomic operation!
        }
    }
}

```

---

### **Using Atomics in JavaScript:**

```jsx
const { Worker, isMainThread, parentPort, workerData } = require('worker_threads');

if (isMainThread) {
    const sharedBuffer = new SharedArrayBuffer(4);
    const sharedArray = new Int32Array(sharedBuffer);

    const worker1 = new Worker(__filename, { workerData: sharedBuffer });
    const worker2 = new Worker(__filename, { workerData: sharedBuffer });

    Promise.all([
        new Promise(resolve => worker1.on('exit', resolve)),
        new Promise(resolve => worker2.on('exit', resolve))
    ]).then(() => {
        console.log('Counter:', sharedArray[0]);
        // Now always: 200000 (correct!)
    });
} else {
    const sharedArray = new Int32Array(workerData);
    for (let i = 0; i < 100000; i++) {
        Atomics.add(sharedArray, 0, 1);  // Atomic operation!
    }
}

```

---

### **How Locks Work:**

```
Thread 1 tries to acquire lock:
    ↓
Lock available? → Yes → Thread 1 gets lock
    ↓
Thread 1 executes critical section (counter++)
    ↓
Thread 2 tries to acquire lock:
    ↓
Lock available? → No (Thread 1 has it)
    ↓
Thread 2 BLOCKS (waits)
    ↓
Thread 1 releases lock
    ↓
Lock available? → Yes → Thread 2 gets lock
    ↓
Thread 2 executes critical section
    ↓
Thread 2 releases lock

```

**Time impact:**

```
Without lock: 0.1 seconds
With lock: 0.5 seconds (5x slower!)

Why? Threads must wait for each other
Serializes access to shared resource

```

---

### **Critical Section**

**Critical section:** Code that accesses shared resources

**Java:**

```java
// Not critical (no shared data)
int x = 10;
int y = 20;
int result = x + y;

// CRITICAL (accesses shared counter)
synchronized(lock) {
    counter++;  // Must be protected!
}

```

**C#:**

```csharp
// Not critical (no shared data)
int x = 10;
int y = 20;
int result = x + y;

// CRITICAL (accesses shared counter)
lock(lockObject) {
    counter++;  // Must be protected!
}

```

❗**Rule:** Keep critical sections as SMALL as possible!

**Java - BAD vs GOOD:**

```java
// BAD: Lock held too long
synchronized(lock) {
    data = fetchFromDatabase();  // Slow! (100ms)
    counter++;
}

// GOOD: Lock held minimally
data = fetchFromDatabase();  // Outside lock
synchronized(lock) {
    counter++;  // Only critical part locked
}

```

---

### **2. Semaphores**

**Semaphore:** Like a lock but allows N threads to enter

**Java Example:** Limit concurrent database connections

```java
import java.util.concurrent.Semaphore;

Semaphore dbSemaphore = new Semaphore(5);  // Allow max 5 threads

void accessDatabase() throws InterruptedException {
    dbSemaphore.acquire();  // Only 5 threads can be here at once
    try {
        // Database operation
        queryDatabase();
    } finally {
        dbSemaphore.release();
    }
}

// Create 20 threads
for (int i = 0; i < 20; i++) {
    new Thread(() -> {
        try { accessDatabase(); }
        catch (InterruptedException e) {}
    }).start();
}
// Only 5 will run simultaneously, others wait

```

**C# Example:**

```csharp
SemaphoreSlim dbSemaphore = new SemaphoreSlim(5);  // Allow max 5 threads

async Task AccessDatabase()
{
    await dbSemaphore.WaitAsync();  // Only 5 threads can be here at once
    try
    {
        // Database operation
        await QueryDatabase();
    }
    finally
    {
        dbSemaphore.Release();
    }
}

```

**Use cases:**

- Rate limiting
- Resource pools
- Connection pools

---

### **3. CountDownLatch / ManualResetEvent**

**Java - CountDownLatch:**

```java
import java.util.concurrent.CountDownLatch;

CountDownLatch latch = new CountDownLatch(3);  // Wait for 3 events

// Thread 1
new Thread(() -> {
    doWork();
    latch.countDown();  // Signal completion
}).start();

// Thread 2
new Thread(() -> {
    doWork();
    latch.countDown();  // Signal completion
}).start();

// Thread 3
new Thread(() -> {
    doWork();
    latch.countDown();  // Signal completion
}).start();

latch.await();  // Wait for all 3 to complete
System.out.println("All threads completed!");

```

**C# - ManualResetEvent:**

```csharp
ManualResetEvent resetEvent = new ManualResetEvent(false);

Thread waiter = new Thread(() => {
    Console.WriteLine("Waiting for event...");
    resetEvent.WaitOne();  // Block until event is set
    Console.WriteLine("Event received!");
});

Thread setter = new Thread(() => {
    Thread.Sleep(2000);
    Console.WriteLine("Setting event...");
    resetEvent.Set();  // Signal waiting threads
});

waiter.Start();
setter.Start();

```

---

## Deadlocks

**Deadlock:** Two or more threads waiting for each other forever.

**Classic example: The Dining Philosophers Problem**

```
5 philosophers sitting at table
5 forks (one between each pair)
Each needs 2 forks to eat

Philosopher picks up left fork, then right fork

```

**Deadlock scenario:**

```
Philosopher 1: Picks up fork 1, waits for fork 2
Philosopher 2: Picks up fork 2, waits for fork 3
Philosopher 3: Picks up fork 3, waits for fork 4
Philosopher 4: Picks up fork 4, waits for fork 5
Philosopher 5: Picks up fork 5, waits for fork 1

Everyone is waiting!
Nobody can proceed!
DEADLOCK!

```

---

### **Code Example:**

**Java:**

```java
public class DeadlockDemo {
    private final Object lockA = new Object();
    private final Object lockB = new Object();

    public void method1() {
        synchronized(lockA) {
            System.out.println("Thread 1: Holding lock A");
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            synchronized(lockB) {  // Waits for lockB
                System.out.println("Thread 1: Holding lock A & B");
            }
        }
    }

    public void method2() {
        synchronized(lockB) {
            System.out.println("Thread 2: Holding lock B");
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            synchronized(lockA) {  // Waits for lockA
                System.out.println("Thread 2: Holding lock A & B");
            }
        }
    }

    public static void main(String[] args) {
        DeadlockDemo demo = new DeadlockDemo();

        new Thread(() -> demo.method1()).start();
        new Thread(() -> demo.method2()).start();

        // DEADLOCK!
        // Thread 1 waits for lockB, Thread 2 waits for lockA
    }
}

```

**C#:**

```csharp
class DeadlockDemo
{
    private readonly object lockA = new object();
    private readonly object lockB = new object();

    public void Method1()
    {
        lock(lockA)
        {
            Console.WriteLine("Thread 1: Holding lock A");
            Thread.Sleep(100);

            lock(lockB)  // Waits for lockB
            {
                Console.WriteLine("Thread 1: Holding lock A & B");
            }
        }
    }

    public void Method2()
    {
        lock(lockB)
        {
            Console.WriteLine("Thread 2: Holding lock B");
            Thread.Sleep(100);

            lock(lockA)  // Waits for lockA
            {
                Console.WriteLine("Thread 2: Holding lock A & B");
            }
        }
    }

    static void Main()
    {
        DeadlockDemo demo = new DeadlockDemo();

        new Thread(demo.Method1).Start();
        new Thread(demo.Method2).Start();

        // DEADLOCK!
        // Thread 1 waits for lockB, Thread 2 waits for lockA
    }
}

```

**Visualizing the deadlock:**

```
Thread 1: [Has Lock A] → Waiting for Lock B
Thread 2: [Has Lock B] → Waiting for Lock A

Circular dependency = DEADLOCK

```

---

### **Deadlock Conditions (All 4 must be true):**

1. **Mutual Exclusion:** Resources can't be shared
2. **Hold and Wait:** Thread holds resource while waiting for another
3. **No Preemption:** Can't force thread to release resource
4. **Circular Wait:** Chain of threads waiting for each other

**Break ANY of these = no deadlock possible**

---

### **Preventing Deadlocks:**

**Solution 1: Lock Ordering**

**Java:**

```java
// Always acquire locks in same order!
public void method1() {
    synchronized(lockA) {  // Always A first
        synchronized(lockB) {  // Then B
            System.out.println("Thread 1");
        }
    }
}

public void method2() {
    synchronized(lockA) {  // Always A first (not B!)
        synchronized(lockB) {  // Then B
            System.out.println("Thread 2");
        }
    }
}
// No deadlock possible!

```

**C#:**

```csharp
public void Method1() {
    lock(lockA) {  // Always A first
        lock(lockB) {  // Then B
            Console.WriteLine("Thread 1");
        }
    }
}

public void Method2() {
    lock(lockA) {  // Always A first (not B!)
        lock(lockB) {  // Then B
            Console.WriteLine("Thread 2");
        }
    }
}
// No deadlock possible!

```

**Solution 2: Timeout**

**Java:**

```java
ReentrantLock lockA = new ReentrantLock();
ReentrantLock lockB = new ReentrantLock();

public void method1() {
    try {
        if (lockA.tryLock(1, TimeUnit.SECONDS)) {
            try {
                if (lockB.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        System.out.println("Thread 1");
                    } finally {
                        lockB.unlock();
                    }
                }
            } finally {
                lockA.unlock();
            }
        } else {
            System.out.println("Couldn't get locks, retry later");
        }
    } catch (InterruptedException e) {}
}

```

**Solution 3: Avoid holding multiple locks**

```java
// Better design: Don't hold multiple locks!
public void method1() {
    synchronized(lockA) {
        // Do work with A
    }

    // Release A before getting B
    synchronized(lockB) {
        // Do work with B
    }
}

```

---

## Architecture Choice: Multi-Threaded vs Multi-Process

⚠️ Important: Processes CONTAIN threads! The choice is NOT 'threads OR processes.

### **Use Threads When:**

✅ **Tasks need to share data**

**Java:**

```java
List<String> sharedList = new ArrayList<>();

Thread worker = new Thread(() -> {
    sharedList.add(data);  // Easy to share!
});

```

**C#:**

```csharp
List<string> sharedList = new List<string>();

Thread worker = new Thread(() => {
    sharedList.Add(data);  // Easy to share!
});

```

✅ **Lightweight, frequent creation**

**Java:**

```java
// Web server creating thread per request
while (true) {
    Socket request = serverSocket.accept();
    new Thread(() -> handle(request)).start();  // Fast to create!
}

```

**C#:**

```csharp
// Web server creating thread per request
while (true) {
    var request = await listener.AcceptTcpClientAsync();
    Task.Run(() => Handle(request));  // Fast to create!
}

```

✅ **I/O-bound tasks**

**Java:**

```java
// Multiple network requests
List<Thread> threads = new ArrayList<>();
for (String url : urls) {
    Thread t = new Thread(() -> download(url));
    threads.add(t);
    t.start();
}
// All downloads happen simultaneously!

```

**C#:**

```csharp
// Multiple network requests
var tasks = urls.Select(url => Task.Run(() => Download(url)));
await Task.WhenAll(tasks);
// All downloads happen simultaneously!

```

✅ **GUI applications**

**Java (Swing):**

```java
button.addActionListener(e -> {
    new Thread(() -> longRunningTask()).start();
    // UI doesn't freeze!
});

```

**C# (WPF):**

```csharp
private async void Button_Click(object sender, RoutedEventArgs e) {
    await Task.Run(() => LongRunningTask());
    // UI doesn't freeze!
}

```

---

### **Use Processes When:**

✅ **CPU-bound tasks (heavy computation)**

**Java:**

```java
// Multi-core computation
for (Dataset data : datasets) {
    ProcessBuilder pb = new ProcessBuilder("java", "ComputeWorker", data.getPath());
    Process p = pb.start();
}
// Each process uses separate CPU core!

```

**C#:**

```csharp
// Multi-core computation
foreach (var data in datasets) {
    Process.Start("dotnet", $"ComputeWorker.dll {data.Path}");
}
// Each process uses separate CPU core!

```

✅ **Need true isolation**

```java
// If one crashes, others continue
for (Task task : tasks) {
    ProcessBuilder pb = new ProcessBuilder("java", "RiskyTask");
    pb.start();
}

```

✅ **Security isolation**

```java
// Run untrusted code in separate process
ProcessBuilder pb = new ProcessBuilder("java", "-jar", "untrusted.jar");
Process p = pb.start();
// If it crashes, main program is safe

```

---

### **Quick Decision Guide:**

```
Your task is I/O-bound? (network, disk, database)
    → Use THREADS (or async/await)

Your task is CPU-bound? (calculations, algorithms)
    → Use THREADS with thread pools (Java/C#)
    → For heavy isolation, use PROCESSES

Need to share lots of data?
    → Use THREADS (shared memory)

Need isolation/safety?
    → Use PROCESSES (separate memory)

Java/C# CPU-intensive work?
    → Use thread pools (ExecutorService / Parallel)

JavaScript/Node.js?
    → Use async/await (event loop) for I/O
    → Use Worker Threads only for CPU-intensive tasks

```

---

## Practical Examples

### **Example 1: Web Server (Multi-threaded)**

**Java (Using Thread Pool):**

```java
import java.net.*;
import java.util.concurrent.*;

ExecutorService threadPool = Executors.newFixedThreadPool(100);

ServerSocket serverSocket = new ServerSocket(8000);

while (true) {
    Socket client = serverSocket.accept();
    threadPool.submit(() -> handleRequest(client));
}

void handleRequest(Socket client) {
    // This runs in a separate thread!
    // Send response to client
}

```

**C# ([ASP.NET](http://asp.net/) Core - handles threading automatically):**

```csharp
var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

app.MapGet("/", async () => {
    // Each request handled in separate thread automatically
    return "Hello World";
});

app.Run();

```

**JavaScript (Node.js - Single-threaded event loop):**

```jsx
const express = require('express');
const app = express();

app.get('/', (req, res) => {
    // Single thread handles all requests via event loop
    // Non-blocking I/O
    res.send('Hello World');
});

app.listen(3000);

```

**Why threads?**

- Each request handler shares server resources
- Lightweight (100s of threads possible)
- I/O-bound (waiting for client, database)

---

### **Example 2: Data Processing (Thread Pool)**

**Java:**

```java
import java.util.concurrent.*;
import java.util.*;

List<String> urls = Arrays.asList(
    "<https://api.example.com/data1>",
    "<https://api.example.com/data2>",
    // ... 100 more URLs
);

ExecutorService executor = Executors.newFixedThreadPool(10);

List<Future<String>> futures = new ArrayList<>();
for (String url : urls) {
    futures.add(executor.submit(() -> fetchUrl(url)));
}

// Wait for all to complete
for (Future<String> future : futures) {
    String result = future.get();
}

executor.shutdown();
// Instead of 100 seconds sequential, takes ~10 seconds with 10 threads!

```

**C#:**

```csharp
string[] urls = {
    "<https://api.example.com/data1>",
    "<https://api.example.com/data2>",
    // ... 100 more URLs
};

// Process with max 10 parallel tasks
var tasks = urls.Select(url => Task.Run(() => FetchUrl(url)));
var results = await Task.WhenAll(tasks);

// Instead of 100 seconds sequential, takes ~10 seconds!

```

---

### **Example 3: Producer-Consumer Pattern**

**Java:**

```java
import java.util.concurrent.*;

BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

// Producer thread
Thread producer = new Thread(() -> {
    for (int i = 0; i < 10; i++) {
        try {
            queue.put(i);  // Thread-safe queue!
            System.out.println("Produced " + i);
            Thread.sleep(100);
        } catch (InterruptedException e) {}
    }
});

// Consumer threads
Thread consumer1 = new Thread(() -> {
    while (true) {
        try {
            Integer item = queue.take();  // Blocks until available
            System.out.println("Consumer 1: " + item);
        } catch (InterruptedException e) { break; }
    }
});

Thread consumer2 = new Thread(() -> {
    while (true) {
        try {
            Integer item = queue.take();
            System.out.println("Consumer 2: " + item);
        } catch (InterruptedException e) { break; }
    }
});

producer.start();
consumer1.start();
consumer2.start();

```

**C#:**

```csharp
using System.Collections.Concurrent;

BlockingCollection<int> queue = new BlockingCollection<int>();

// Producer thread
Thread producer = new Thread(() => {
    for (int i = 0; i < 10; i++) {
        queue.Add(i);  // Thread-safe!
        Console.WriteLine($"Produced {i}");
        Thread.Sleep(100);
    }
    queue.CompleteAdding();
});

// Consumer threads
Thread consumer1 = new Thread(() => {
    foreach (var item in queue.GetConsumingEnumerable()) {
        Console.WriteLine($"Consumer 1: {item}");
    }
});

Thread consumer2 = new Thread(() => {
    foreach (var item in queue.GetConsumingEnumerable()) {
        Console.WriteLine($"Consumer 2: {item}");
    }
});

producer.Start();
consumer1.Start();
consumer2.Start();

```

---

### **Example 4: Parallel Downloads**

**Java:**

```java
import java.net.*;
import java.io.*;
import java.util.*;

void downloadFile(String url, String filename) throws IOException {
    System.out.println("Downloading " + filename + "...");
    URL website = new URL(url);
    try (InputStream in = website.openStream();
         FileOutputStream out = new FileOutputStream(filename)) {
        in.transferTo(out);
    }
    System.out.println("Downloaded " + filename);
}

List<String[]> files = Arrays.asList(
    new String[]{"<https://example.com/file1.pdf>", "file1.pdf"},
    new String[]{"<https://example.com/file2.pdf>", "file2.pdf"},
    new String[]{"<https://example.com/file3.pdf>", "file3.pdf"}
);

List<Thread> threads = new ArrayList<>();
for (String[] file : files) {
    Thread thread = new Thread(() -> {
        try {
            downloadFile(file[0], file[1]);
        } catch (IOException e) {}
    });
    threads.add(thread);
    thread.start();
}

// Wait for all downloads
for (Thread thread : threads) {
    thread.join();
}

System.out.println("All downloads complete!");

```

**C#:**

```csharp
using System.Net.Http;

async Task DownloadFile(string url, string filename)
{
    Console.WriteLine($"Downloading {filename}...");
    using HttpClient client = new HttpClient();
    var data = await client.GetByteArrayAsync(url);
    await File.WriteAllBytesAsync(filename, data);
    Console.WriteLine($"Downloaded {filename}");
}

var files = new[] {
    ("<https://example.com/file1.pdf>", "file1.pdf"),
    ("<https://example.com/file2.pdf>", "file2.pdf"),
    ("<https://example.com/file3.pdf>", "file3.pdf")
};

var tasks = files.Select(f => DownloadFile(f.Item1, f.Item2));
await Task.WhenAll(tasks);

Console.WriteLine("All downloads complete!");

```

**Sequential:** 30 seconds (10 sec each)
**Parallel (threads):** 10 seconds (all at once)

---

## Common Threading Mistakes

### **Mistake 1: Not using locks**

**Java - BAD:**

```java
static int counter = 0;

void increment() {
    counter++;  // Race condition!
}

```

**Java - GOOD:**

```java
static int counter = 0;
static Object lock = new Object();

void increment() {
    synchronized(lock) {
        counter++;  // Safe!
    }
}

```

**C# - BAD:**

```csharp
static int counter = 0;

void Increment() {
    counter++;  // Race condition!
}

```

**C# - GOOD:**

```csharp
static int counter = 0;
static object lockObject = new object();

void Increment() {
    lock(lockObject) {
        counter++;  // Safe!
    }
}

```

---

### **Mistake 2: Holding locks too long**

**Java - BAD:**

```java
synchronized(lock) {
    data = expensiveOperation();  // Lock held during slow operation
    counter++;
}

```

**Java - GOOD:**

```java
data = expensiveOperation();  // Do slow work outside lock
synchronized(lock) {
    counter++;  // Only lock critical section
}

```

---

### **Mistake 3: Forgetting to join threads**

**Java - BAD:**

```java
for (int i = 0; i < 10; i++) {
    new Thread(() -> work()).start();
}
// Program exits before threads finish!

```

**Java - GOOD:**

```java
List<Thread> threads = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    Thread t = new Thread(() -> work());
    threads.add(t);
    t.start();
}
for (Thread t : threads) {
    t.join();  // Wait for all threads!
}

```

---

### **Mistake 4: Creating too many threads**

**Java - BAD:**

```java
for (int i = 0; i < 10000; i++) {
    new Thread(() -> work()).start();  // Too many threads!
}

```

**Java - GOOD:**

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 10000; i++) {
    executor.submit(() -> work());  // Limited threads, reused
}
executor.shutdown();

```

**C# - GOOD:**

```csharp
Parallel.For(0, 10000, new ParallelOptions { MaxDegreeOfParallelism = 10 },
    i => Work());

```

---

## Key Takeaways - Section 3 - Threads

### **Core Concepts:**

✅ **Thread = unit of execution** within a process

✅ **Threads share memory** (same heap, code, data)

✅ **Each thread has its own stack** (local variables)

✅ **Much faster to create** than processes (~100x faster)

### **Process-Thread Relationship:**

✅ **Every process has ≥1 thread** (main thread always exists)

✅ **Processes CONTAIN threads** (not alternatives!)

✅ **Single-threaded process:** One thread doing all work

✅ **Multi-threaded process:** Multiple threads sharing same memory

### **Architecture Choice:**

✅ **Multi-threaded (single process):** Shared memory, fast communication, less isolation

✅ **Multi-process:** Separate memory, strong isolation, more overhead

✅ **Use multi-threaded for:** I/O-bound tasks, shared state needs

✅ **Use multi-process for:** Isolation needs, security, failure containment

### **Synchronization:**

✅ **Race conditions:** Threads interfering with shared data

✅ **Locks/Mutexes:** Protect critical sections (synchronized/lock)

✅ **Deadlocks:** Threads waiting for each other forever

✅ **Semaphores:** Limit concurrent access

### **Best Practices:**

✅ **Keep critical sections small**

✅ **Always acquire locks in same order**

✅ **Use thread pools** instead of creating unlimited threads

✅ **Don't forget to join() threads** or await tasks

✅ **Use thread-safe data structures** (BlockingQueue, ConcurrentHashMap)

### **Language-Specific:**

✅ **Java:** ExecutorService for thread pools, synchronized for locks

✅ **C#:** Task/async-await preferred over raw threads, lock keyword

✅ **Node.js:** Single-threaded event loop by default, Worker Threads for CPU work

---

## What's Next?

**CPU Scheduling** - You'll understand:

- How OS decides which process/thread runs next
- Scheduling algorithms (Round Robin, Priority, etc.)
- Time slices and context switching
- Why your program sometimes "feels slow"
- Priority and fairness

**This explains how 100 threads share 8 CPU cores!**

---
