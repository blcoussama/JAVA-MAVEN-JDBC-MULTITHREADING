# Section 5 - CPU Scheduling

**How the OS decides which process/thread runs next**

---

## The Problem

**You have:**

- 8 CPU cores
- 200 running processes
- 500+ threads

⚠️ **Question:** How does the OS decide which ones run and when?

✅ **Answer:** The CPU Scheduler - runs thousands of times per second!

---

## Why Scheduling Matters

**Without scheduler:**

```
Process 1 starts → Runs forever → Others never run ❌
```

**With scheduler:**

```
Process 1 → 10ms → switch
Process 2 → 10ms → switch
Process 3 → 10ms → switch
All make progress! ✅
```

**This is why you can:**

- Browse web while downloading
- Listen to music while coding
- Run 50 Chrome tabs
- Everything feels responsive

---

## Three Process States

**From scheduler's perspective:**

### **RUNNING**

- Currently executing on CPU
- Max 8 processes (on 8-core CPU)

### **READY**

- Ready to run, waiting for CPU
- In the "ready queue"

### **WAITING (BLOCKED)**

- Waiting for I/O (disk, network, input)
- Can't run even if CPU available

**Flow:**

```
[READY QUEUE] → [RUNNING] → Time expires → [READY QUEUE]
                    ↓
                 I/O wait
                    ↓
              [BLOCKED] → I/O done → [READY QUEUE]

```

---

## Time Slice (Quantum)

**Each process gets 10-100ms of CPU before switching.**

**Example:**

```
Process A → runs 10ms → timer interrupt → switch
Process B → runs 10ms → timer interrupt → switch
Process C → runs 10ms → timer interrupt → switch
Cycle repeats...

```

**This is "preemptive multitasking":**

- OS forcibly takes CPU away
- One frozen process doesn't freeze entire system
- Why modern OS stays responsive

---

## Scheduling Algorithms

### **1. Round Robin (Most Common)**

**Idea:** Each process gets equal time slice in circular order.

**Example:**

```
Time slice = 10ms
Process A, B, C in queue

0-10ms:   A runs
10-20ms:  B runs
20-30ms:  C runs
30-40ms:  A runs again
...cycle continues

```

**Pros:**

- ✅ Fair to all
- ✅ No starvation

**Used in:** All modern OS (with priorities)

---

### **2. Priority Scheduling**

**Idea:** Higher priority processes run first.

**Example:**

```
System process: Priority 1 (high)
Browser: Priority 5 (normal)
Background scan: Priority 9 (low)

Order: System → Browser → Scan

```

⚠️ **Problem: Starvation**

```
High priority keeps coming → Low priority never runs

```

✅ **Solution: Priority Aging**

```
Waiting process → priority gradually increases → eventually runs

```

**Real use:**

- Windows Task Manager priorities
- Linux nice values (-20 to 19)

---

### **3. Multi-Level Queue**

**Different queues for different process types.**

**Structure:**

```
Queue 1 (High): Interactive (browsers, editors)
    ↓ if empty
Queue 2 (Med): Background (downloads, updates)
    ↓ if empty
Queue 3 (Low): Batch (scans, indexing)

```

**How OS decides queue:**

**Interactive (I/O-bound):**

- Uses CPU briefly, then waits for I/O
- Needs quick response
- → High priority queue

**Batch (CPU-bound):**

- Uses CPU continuously
- Doesn't need quick response
- → Low priority queue

**Dynamic movement:**

```
Process uses too much CPU → moved to lower queue
Process starts doing I/O → moved to higher queue

```

**This is genius!** OS automatically gives better response to interactive apps.

---

## Real Operating Systems

### **Linux - Completely Fair Scheduler (CFS)**

**Core idea:** Track virtual runtime (vruntime) for each process.

**How it works:**

```
Each process has vruntime counter
Every time it runs → vruntime increases
Process with lowest vruntime runs next

```

**With priorities:**

```
High priority runs 10ms → vruntime += 5ms
Low priority runs 10ms → vruntime += 20ms

Result: High priority runs more often!

```

**Data structure:** Red-black tree (O(log n) operations)

**Result:** Fair, efficient, no starvation!

---

### **Windows - Priority-Based**

**32 priority levels:**

```
Priority 31: Realtime (highest)
Priority 16: High
Priority 8:  Normal (default)
Priority 1:  Low

```

**Dynamic boosting:**

```
I/O completes → boost priority temporarily
Foreground window → higher priority
After boost → gradually decay back

```

**Foreground vs background:**

```
Active window: Longer time slices, higher priority
Background: Shorter slices, lower priority

```

**This is why your active app feels responsive!**

---

## Context Switching - The Hidden Cost

**What happens when switching:**

```
1. Save current process:
   - Registers, program counter, stack pointer

2. Load next process:
   - Registers, program counter, stack pointer

3. Sometimes flush caches (expensive!)

```

**Time cost:**

- Thread-to-thread: 0.1-1 microseconds
- Process-to-process: 1-10 microseconds

**Why process switches are slower:**

- Different memory spaces
- Must update memory mappings
- Cache pollution (new data)

---

### **Cache Pollution**

```
Process A running:
- CPU cache full of Process A's data
    ↓
Switch to Process B
    ↓
Cache fills with Process B's data
    ↓
Process A's data evicted!
    ↓
Switch back to Process A
    ↓
Cache misses! Reload from RAM (slow)

```

**This is invisible but significant!**

---

### **Why Time Slice Matters**

**Too short (1ms):**

```
1ms work + 0.5ms switch = 33% overhead ❌

```

**Too long (1000ms):**

```
Poor responsiveness ❌
Feels sluggish

```

**Sweet spot (10-100ms):**

```
10ms work + 0.5ms switch = 5% overhead ✅
Good responsiveness ✅

```

---

## How This Connects to What You Learned

### **From Processes:**

**You learned:** Processes have separate memory spaces

**Scheduling adds:** How OS decides which process runs

- Process-to-process switch = expensive (different memory)
- This is why context switching costs matter

---

### **From Threads:**

**You learned:** Threads share memory within process

**Scheduling adds:** Thread switches are cheaper!

- Thread-to-thread (same process) = fast (same memory)
- Thread-to-thread (different process) = slower

**Thread pools help scheduler:**

```
100 threads (reused) vs 10,000 threads (constant creation)
Less context switching = better performance

```

---

### **From Memory Management:**

**You learned:** Virtual memory, paging, TLB

**Scheduling adds:** Context switches affect memory performance

- Switch processes → flush TLB
- Switch processes → cache pollution
- This is why too many processes hurt performance

---

### **From CPU Architecture:**

**You learned:** Cache locality is critical

**Scheduling adds:** Frequent switches hurt cache performance

- Each switch → lose cache locality
- Time slice trade-off: responsiveness vs overhead

---

## Thread Pool Best Practices (Critical!)

**Bad - No scheduling awareness:**

**Java:**

```java
// Creating 10,000 threads!
for (int i = 0; i < 10000; i++) {
    new Thread(() -> task()).start();
}
// Problems:
// - 10,000 context switches
// - 10GB memory (1MB per stack)
// - Scheduler overwhelmed

```

**Good - Respect scheduler:**

**Java:**

```java
// Reuse 100 threads
ExecutorService pool = Executors.newFixedThreadPool(100);
for (int i = 0; i < 10000; i++) {
    pool.submit(() -> task());
}
// Benefits:
// - 100 threads total
// - Bounded context switching
// - Scheduler works efficiently

```

**C#:**

```csharp
// Let .NET decide optimal thread count
Parallel.For(0, 10000,
    new ParallelOptions { MaxDegreeOfParallelism = 100 },
    i => Task());

```

**JavaScript (Node.js):**

```jsx
// Already optimal - single-threaded event loop!
// No thread context switching for I/O
app.get('/data', async (req, res) => {
    const data = await db.query('SELECT...');
    res.json(data);
});
// Handles thousands of concurrent requests on one thread!

```

---

## Optimal Thread Count

**Rule of thumb:**

**CPU-bound tasks:**

```
Threads = Number of CPU cores

8 cores → 8 threads

```

**I/O-bound tasks:**

```
Threads = Cores × 2

8 cores → 16 threads
(threads mostly wait, not compute)

```

**Example:**

**Java:**

```java
int cores = Runtime.getRuntime().availableProcessors();

// CPU-bound
ExecutorService cpuPool = Executors.newFixedThreadPool(cores);

// I/O-bound
ExecutorService ioPool = Executors.newFixedThreadPool(cores * 2);

```

---

## Scheduler Behavior and I/O vs CPU

**From CPU-Bound vs I/O-Bound section:**

### **CPU-Bound Process:**

**Timeline:**

```
[CPU] [CPU] [CPU] [CPU] [CPU]

```

**Scheduler sees:**

- Uses full time slices
- Not waiting
- Classifies as batch
- **Moves to lower priority queue**

**Example:** Video encoding, compilation

---

### **I/O-Bound Process:**

**Timeline:**

```
[CPU] [wait] [CPU] [wait] [CPU] [wait]

```

**Scheduler sees:**

- Short CPU bursts
- Frequently waiting for I/O
- Classifies as interactive
- **Keeps in high priority queue**
- **Boosts priority when I/O completes**

**Example:** Web server, text editor

**This is why web servers stay responsive!**

---

## Real-World Scenarios

### **Video Playback**

**What you see:** Smooth 60fps

**What's happening:**

```
Video decoder:
- Priority: High (interactive)
- Needs CPU every 16ms (60fps)
- Scheduler ensures frequent runs
- Gets priority boosts

```

---

### **Compilation in Background**

**What you see:** IDE stays responsive while compiling

**What's happening:**

```
Compiler:
- Priority: Low (CPU-intensive batch)
- Uses CPU continuously
- Moved to lower queue
- Runs when higher priority idle

IDE:
- Priority: Normal/High
- Waits for keyboard/mouse
- I/O completes → priority boost
- Runs immediately

```

---

### **Gaming**

**What you see:** Smooth 144fps, no lag

**What's happening:**

```
Game:
- Priority: High
- Foreground window
- Longer time slices
- Frequent priority boosts

Background apps:
- Lower priority
- Scheduled around game

```

**Modern OS trick:** Full-screen apps auto-boosted!

---

## Setting Priorities (Use Carefully!)

**Java:**

```java
Thread thread = new Thread(() -> backgroundTask());
thread.setPriority(Thread.MIN_PRIORITY);  // 1-10 scale
thread.start();

```

**C#:**

```csharp
Thread thread = new Thread(BackgroundTask);
thread.Priority = ThreadPriority.Lowest;
thread.start();

```

**When to use:**

- ✅ Background indexing, scans → low priority
- ⚠️ Realtime audio/video → high priority (rare!)
- ❌ Normal business logic → don't touch!

⚠️ **Warning:** Realtime priority can freeze system if thread loops!

---

## Linux Nice Values

**Adjust process priority:**

```bash
nice value -20 = highest priority
nice value 0   = default
nice value 19  = lowest priority

```

**Usage:**

```bash
# Run with lower priority (background work)
nice -n 10 java MyApp.jar
nice -n 10 dotnet run
nice -n 10 node app.js

```

**Why?** Background batch jobs won't interfere with interactive work.

---

## Key Takeaways

### **Core Concepts:**

✅ **Scheduler decides which process runs** - thousands of times/second

✅ **Time slice (10-100ms)** - balance overhead vs responsiveness

✅ **Three states** - Running, Ready, Waiting

✅ **Preemptive multitasking** - OS forces switches (prevents freezing)

---

### **Algorithms:**

✅ **Round Robin** - Fair, equal time slices

✅ **Priority** - Important tasks first (with aging)

✅ **Multi-Level Queue** - Interactive vs batch processes

✅ **Linux CFS** - Virtual runtime, truly fair

✅ **Windows** - 32 levels, dynamic boosting

---

### **Performance Impact:**

✅ **Context switching overhead** (0.1-10 microseconds)

✅ **Cache pollution** - switching evicts cache data

✅ **I/O-bound gets priority** - better responsiveness

✅ **CPU-bound moved to background** - doesn't freeze system

---

### **For Developers:**

✅ **Use thread pools** - don't create thousands of threads

✅ **Optimal thread count:**

- CPU-bound: cores
- I/O-bound: cores × 2

✅ **Async/await helps** - releases threads during I/O

✅ **Let OS manage scheduling** - it's optimized

✅ **Node.js advantage** - single-threaded event loop (no context switching for I/O)

---

### **How It All Connects:**

✅ **Processes** - Separate memory → expensive context switch

✅ **Threads** - Shared memory → cheaper context switch

✅ **Memory Management** - Context switch → TLB flush, cache pollution

✅ **CPU Architecture** - Cache locality hurt by frequent switches

✅ **I/O-Bound vs CPU-Bound** - Scheduler treats differently (priority)

---

## Summary

**CPU Scheduling is how the OS:**

- Shares limited CPU cores among many processes/threads
- Decides execution order and duration
- Balances fairness and responsiveness
- Adapts to process behavior (I/O vs CPU-bound)

**Modern schedulers are smart:**

- Detect interactive vs batch processes
- Boost priority for I/O-bound (responsiveness)
- Lower priority for CPU-bound (fairness)
- Minimize context switching overhead

**For you as a developer:**

- Use thread pools (respect the scheduler)
- Understand I/O vs CPU-bound (different treatment)
- Let OS manage priorities (don't micromanage)
- Profile before optimizing (find real bottleneck)

**The scheduler is why your computer feels fast even when running 200 processes!** 🚀

---

**Now you understand how scheduling integrates with everything you've learned!**
