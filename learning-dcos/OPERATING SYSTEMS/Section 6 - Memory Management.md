# Section 6 - Memory Management

**How operating systems manage RAM and why it matters for developers**

---

## The Core Problem

**Your computer has 16GB of RAM, but:**

- 50 programs are running
- Each program thinks it has its own memory
- Total memory requested: 40GB+

**How does this work?** Virtual Memory!

---

## Virtual Memory - The Big Idea

**Every process gets its own "virtual" address space.**

**What the program sees:**

```
MyApp thinks it has:
- Address 0x0000 to 0xFFFFFFFF
- Looks like dedicated, private memory
- 4GB of space (32-bit) or way more (64-bit)

```

**Reality:**

```
OS maps virtual addresses → physical RAM
Multiple programs share same physical RAM
Programs can't see each other's memory

```

**Visual:**

```
Process A thinks:          Process B thinks:
0x0000: My data           0x0000: My data
0x1000: My code           0x1000: My code

Both use "0x0000" but OS maps to different physical RAM!

Physical RAM:
[Process A data][Process B data][Process C data]...

```

**Key benefit:** Isolation! Programs can't corrupt each other's memory.

---

## Heap vs Stack Memory

**Every process has two main memory areas:**

### **Stack**

**What it is:**

- Fast, automatic memory
- Fixed size (usually 1-8MB)
- Grows/shrinks automatically
- Used for local variables and function calls

**Java example:**

```java
public void calculatePrice() {
    int quantity = 5;           // Stack
    double price = 29.99;       // Stack
    double total = quantity * price;  // Stack

    // When function ends, all stack memory freed automatically
}

```

**C# example:**

```csharp
public void ProcessOrder() {
    int orderId = 123;          // Stack
    decimal amount = 99.99m;    // Stack

    // Automatically cleaned up when method ends
}

```

**Characteristics:**

- ✅ Very fast
- ✅ Automatic cleanup
- ❌ Small size limit
- ❌ Can't resize dynamically

**Stack Overflow:**

```java
public void recursiveFunction() {
    recursiveFunction();  // Infinite recursion
    // Eventually: StackOverflowError!
    // Stack memory exhausted
}

```

---

### **Heap**

**What it is:**

- Dynamic memory
- Large (can be GBs)
- Manual allocation (but GC helps in Java/C#/JS)
- Used for objects and data structures

**Java example:**

```java
public class OrderService {
    public void processOrders() {
        List<Order> orders = new ArrayList<>();  // Heap

        for (int i = 0; i < 1000000; i++) {
            Order order = new Order();  // Heap - new object
            orders.add(order);
        }

        // Objects stay in heap until garbage collected
    }
}

```

**C# example:**

```csharp
public class UserService {
    public void LoadUsers() {
        List<User> users = new List<User>();  // Heap

        for (int i = 0; i < 1000000; i++) {
            var user = new User();  // Heap allocation
            users.Add(user);
        }
    }
}

```

**JavaScript example:**

```jsx
function loadData() {
    const items = [];  // Heap

    for (let i = 0; i < 1000000; i++) {
        items.push({ id: i, data: 'value' });  // Heap
    }
}

```

**Characteristics:**

- ✅ Large size
- ✅ Dynamic allocation
- ❌ Slower than stack
- ❌ Needs garbage collection (Java/C#/JS) or manual management

---

## Memory Leaks - Critical for Your Career

**Memory leak = Allocated memory never freed**

### **Java Memory Leak Example:**

**Bad - Memory leak:**

```java
public class CacheService {
    private static Map<String, User> cache = new HashMap<>();

    public void addUser(String id, User user) {
        cache.put(id, user);  // Never removed!
        // Cache grows forever → memory leak!
    }
}

// After running for days:
// cache has 10 million entries
// Uses 5GB of RAM
// Eventually: OutOfMemoryError

```

**Good - Prevents leak:**

```java
public class CacheService {
    // WeakHashMap allows GC to remove entries
    private static Map<String, User> cache = new WeakHashMap<>();

    // OR use size limits:
    private static final int MAX_SIZE = 10000;

    public void addUser(String id, User user) {
        if (cache.size() >= MAX_SIZE) {
            // Remove oldest entries
            cache.remove(cache.keySet().iterator().next());
        }
        cache.put(id, user);
    }
}

```

---

### **JavaScript Memory Leak Example:**

**Bad - Memory leak:**

```jsx
// Global array keeps growing
const events = [];

function handleEvent(data) {
    events.push(data);  // Never cleared!
    // Memory leak - events array grows forever
}

// After hours of user interaction:
// events has millions of items
// Browser slows down / crashes

```

**Good - Prevents leak:**

```jsx
const MAX_EVENTS = 1000;
const events = [];

function handleEvent(data) {
    events.push(data);

    // Keep only recent events
    if (events.length > MAX_EVENTS) {
        events.shift();  // Remove oldest
    }
}

```

---

### **C# Memory Leak Example:**

**Bad - Event handler leak:**

```csharp
public class UserService {
    public event EventHandler UserChanged;

    public void Subscribe(Form form) {
        UserChanged += form.OnUserChanged;
        // If form is disposed but event not unsubscribed → leak!
    }
}

// Form closes but UserService keeps reference → memory leak

```

**Good - Proper cleanup:**

```csharp
public class MyForm : Form {
    private UserService _service;

    protected override void OnLoad(EventArgs e) {
        _service.UserChanged += OnUserChanged;
    }

    protected override void Dispose(bool disposing) {
        if (disposing) {
            _service.UserChanged -= OnUserChanged;  // Unsubscribe!
        }
        base.Dispose(disposing);
    }
}

```

---

## Garbage Collection

**Automatic memory management in Java, C#, JavaScript**

**How it works:**

**1. Mark phase:**

```
Start from "roots" (active variables, stack)
Mark all reachable objects

```

**2. Sweep phase:**

```
Delete unmarked objects (garbage)
Free their memory

```

**Example:**

```java
public void processData() {
    User user = new User();  // Heap allocation
    process(user);

    // user goes out of scope
    // No more references to User object
    // Eligible for garbage collection
}

// Later: GC runs, frees User object memory

```

---

### **GC Pauses - Why Your App Freezes**

**Problem:**

```
GC runs → Application pauses → User sees lag

```

**Java example:**

```java
// Creating lots of temporary objects
for (int i = 0; i < 1000000; i++) {
    String temp = "Item " + i;  // New String object
    process(temp);
    // temp immediately becomes garbage
}

// Frequent GC pauses!

```

**Better:**

```java
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000000; i++) {
    sb.setLength(0);  // Reuse!
    sb.append("Item ").append(i);
    process(sb.toString());
}

// Less garbage, fewer GC pauses

```

---

## JVM Memory Settings (Java)

**Critical for Java developers!**

**Setting heap size:**

```bash
# Minimum heap: 512MB
# Maximum heap: 4GB
java -Xms512m -Xmx4g MyApp.jar

```

**Why it matters:**

**Too small:**

```
-Xmx512m for large app
    ↓
Frequent GC
    ↓
OutOfMemoryError

```

**Too large:**

```
-Xmx16g for small app
    ↓
Wastes RAM
    ↓
Other containers starved

```

**Docker example:**

```docker
FROM openjdk:17
# Container has 2GB limit
# Give JVM 1.5GB (leave room for overhead)
CMD ["java", "-Xmx1536m", "-jar", "app.jar"]

```

---

## .NET Memory Settings (C#)

**GC modes:**

```xml
<!-- app.config -->
<configuration>
  <runtime>
    <!-- Server GC: Better for multi-core, web servers -->
    <gcServer enabled="true"/>

    <!-- OR Workstation GC: Lower latency, desktop apps -->
    <gcConcurrent enabled="true"/>
  </runtime>
</configuration>

```

**Docker example:**

```bash
# Limit .NET memory
docker run -e DOTNET_GCHeapHardLimit=1536000000 myapp

```

---

## Paging and Page Faults

**Memory is divided into "pages" (usually 4KB chunks)**

### **What is a Page?**

```
Process memory divided into 4KB pages:
Page 0: [4KB of data]
Page 1: [4KB of data]
Page 2: [4KB of data]
...

```

**Not all pages are in RAM!**

```
RAM (limited):
- Page 0 ✅ In RAM
- Page 1 ✅ In RAM
- Page 5 ✅ In RAM

Disk (swap):
- Page 2 ❌ On disk
- Page 3 ❌ On disk
- Page 4 ❌ On disk

```

---

### **Page Fault**

**What happens when you access memory not in RAM:**

```
Program: "Give me data at address X"
    ↓
OS: "That's in Page 2"
    ↓
OS: "Page 2 is on disk!" (Page fault!)
    ↓
Load Page 2 from disk to RAM (slow!)
    ↓
Continue program

```

**Time:**

- RAM access: ~100 nanoseconds
- Page fault (disk): ~10 milliseconds
- **100,000x slower!**

⚠️ **This is why swapping kills performance!**

---

## Swap Space (Page File)

**What it is:**

- Disk space used as "overflow" RAM
- When RAM full, OS moves pages to disk

**Why it's slow:**

```
RAM full
    ↓
OS moves least-used pages to disk (swap out)
    ↓
Free up RAM
    ↓
Program needs swapped page
    ↓
Page fault! Load from disk (swap in)
    ↓
Very slow!

```

**Real scenario:**

```
Your laptop:
- 8GB RAM
- 50 programs running
- Using 12GB total

What happens:
- 8GB in RAM (fast)
- 4GB swapped to disk (slow!)
- System feels sluggish

```

⚠️ **For production servers: Disable swap or use minimal!**

---

## Docker Memory Limits - DevOps Critical

**Why limit memory:**

```
Without limits:
Container A uses 10GB → Other containers starved → Node crashes

```

**Setting limits:**

```bash
# Limit container to 2GB
docker run --memory=2g myapp

# Limit + reservation
docker run --memory=2g --memory-reservation=1g myapp

```

**What happens when limit reached:**

```
Container uses > 2GB
    ↓
OOM Killer activates
    ↓
Kills container process
    ↓
Container crashes/restarts

```

---

### **Real Docker Example:**

**Java app in Docker:**

```docker
FROM openjdk:17

# Container memory limit: 2GB (set at runtime)
# JVM heap: 1.5GB (leave 500MB for overhead)
CMD ["java", "-Xmx1536m", "-jar", "app.jar"]

```

**Run:**

```bash
docker run --memory=2g --name myapp myapp:latest

```

**Memory breakdown:**

```
Total container: 2GB
├── JVM heap: 1.5GB
├── JVM overhead: 300MB
└── OS overhead: 200MB

```

**If you set -Xmx2g:**

```
Container limit: 2GB
JVM tries: 2GB
Overhead: 300MB
Total: 2.3GB → OOM KILLED! ❌

```

---

## Kubernetes Resource Limits - DevOps Critical

**Pod memory limits:**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-app
spec:
  containers:
  - name: app
    image: myapp:latest
    resources:
      requests:
        memory: "1Gi"      # Guaranteed minimum
      limits:
        memory: "2Gi"      # Hard maximum

```

**What happens:**

**Requests (1Gi):**

- Kubernetes schedules pod on node with ≥1GB free
- Pod guaranteed to get at least 1GB

**Limits (2Gi):**

- Pod can use up to 2GB
- If exceeds → OOM killed

---

### **Real K8s Scenario:**

```yaml
# Production web server
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"

```

**What this means:**

- Scheduled on node with ≥512MB free
- Can burst to 1GB if available
- If uses >1GB → OOM killed
- If uses >500m CPU → throttled (not killed)

---

## OOM (Out Of Memory) Killer

**What it is:**

- Linux kernel feature
- Kills processes when RAM exhausted
- Prevents system crash

**How it works:**

```
System RAM: 99% used
    ↓
New allocation request
    ↓
No free RAM!
    ↓
OOM Killer activates
    ↓
Picks process to kill (highest score)
    ↓
Kills process (SIGKILL)
    ↓
Frees memory

```

**In Docker/Kubernetes:**

```
Container exceeds memory limit
    ↓
OOM Killer kills container
    ↓
Container exits with code 137
    ↓
Kubernetes restarts pod (if restartPolicy: Always)

```

---

### **Checking OOM Kills:**

**Docker:**

```bash
# Check if container was OOM killed
docker inspect myapp | grep OOMKilled

```

**Kubernetes:**

```bash
# Check pod status
kubectl describe pod my-app

# Look for:
# Last State: Terminated
# Reason: OOMKilled
# Exit Code: 137

```

**Logs:**

```bash
# System logs (Linux)
dmesg | grep -i "killed process"

# Shows which process was killed and why

```

---

## Memory Monitoring - Production Essential

### **Java (JVM):**

**Check heap usage:**

```bash
jstat -gc <pid>

# Shows:
# - Heap usage
# - GC count
# - GC time

```

**JVM flags for monitoring:**

```bash
java -Xmx2g \
     -XX:+PrintGCDetails \
     -XX:+PrintGCTimeStamps \
     -jar app.jar

```

---

### **Docker:**

**Real-time stats:**

```bash
docker stats myapp

# Shows:
# CONTAINER   CPU %   MEM USAGE / LIMIT   MEM %
# myapp       5%      1.2GB / 2GB         60%

```

---

### **Kubernetes:**

**Pod metrics:**

```bash
kubectl top pod my-app

# Shows:
# NAME     CPU    MEMORY
# my-app   250m   512Mi

```

**Check limits:**

```bash
kubectl describe pod my-app | grep -A 5 Limits

```

---

## Practical Guidelines

### **For Web/Mobile Development:**

**Java/Spring Boot:**

```
Typical settings:
-Xms512m -Xmx2g

Small app: 512MB-1GB
Medium app: 1-2GB
Large app: 2-4GB

```

**C#/ASP.NET Core:**

```
Usually auto-sized
Monitor with Performance Counters
Watch for memory leaks in long-running services

```

**Node.js:**

```bash
# Default: ~1.5GB on 64-bit
# Increase if needed:
node --max-old-space-size=4096 app.js

```

---

### **For Docker:**

**Rule of thumb:**

```
Container limit = JVM heap + 500MB overhead

Example:
-Xmx1536m → --memory=2g
-Xmx3g → --memory=3.5g

```

---

### **For Kubernetes:**

**Start conservative:**

```yaml
requests:
  memory: "512Mi"
limits:
  memory: "1Gi"

```

**Monitor and adjust:**

```
If OOM killed → increase limits
If under-utilized → decrease requests (save money)

```

---

## Common Issues and Solutions

### **Issue 1: OutOfMemoryError (Java)**

**Symptoms:**

```
java.lang.OutOfMemoryError: Java heap space

```

**Solutions:**

1. Increase heap: `Xmx4g`
2. Check for memory leaks
3. Optimize object creation
4. Use profiler (VisualVM, JProfiler)

---

### **Issue 2: Container Keeps Restarting (Docker/K8s)**

**Check:**

```bash
kubectl describe pod my-app
# Look for: OOMKilled

```

**Solutions:**

1. Increase memory limits
2. Reduce JVM heap
3. Fix memory leaks
4. Add memory requests

---

### **Issue 3: Production Slow (Swapping)**

**Check:**

```bash
free -h
# Look at "Swap" usage

# If swap used heavily → problem!

```

**Solutions:**

1. Add more RAM
2. Reduce memory usage
3. Disable swap on production
4. Scale horizontally (more instances)

---

### **Issue 4: Memory Leak**

**Symptoms:**

- Memory usage grows over time
- Eventually OOM or crash
- GC more frequent

**Finding leaks:**

**Java:**

```bash
# Heap dump
jmap -dump:live,format=b,file=heap.bin <pid>

# Analyze with Eclipse MAT or VisualVM

```

**Node.js:**

```jsx
// Use --inspect flag
node --inspect app.js

// Chrome DevTools → Memory → Take heap snapshot

```

**Common causes:**

- Global caches without limits
- Event handlers not removed
- Circular references
- Large collections never cleared

---

## Key Takeaways

### **Core Concepts:**

✅ **Virtual memory** - Each process has own address space

✅ **Heap vs Stack** - Dynamic vs automatic memory

✅ **Memory leaks** - Allocated memory never freed

✅ **Garbage collection** - Automatic cleanup (with pauses)

✅ **Paging** - Memory in 4KB chunks

✅ **Page faults** - Accessing swapped memory (slow!)

✅ **Swap** - Disk as overflow RAM (kills performance)

---

### **For Development:**

✅ **Avoid memory leaks** - Clear caches, unsubscribe events

✅ **Limit object creation** - Reuse when possible

✅ **Monitor heap usage** - Set appropriate -Xmx

✅ **Profile memory** - Find leaks early

---

### **For DevOps:**

✅ **Set Docker limits** - `--memory=2g`

✅ **Set K8s requests/limits** - Prevent OOM kills

✅ **Leave overhead** - JVM heap + 500MB

✅ **Monitor production** - `kubectl top`, `docker stats`

✅ **Check OOM kills** - Exit code 137

✅ **Disable swap** - On production servers

---

## What You've Completed

**🎉 Operating Systems - DONE!**

```
✅ Page 1: What is an Operating System
✅ Page 2: Processes
✅ Page 3: Threads
✅ Page 4A: CPU Scheduling Basics
✅ Page 5: Memory Management

```

**You now understand:**

- How OS manages processes and threads
- How CPU scheduling works
- How memory is managed
- Why containers get OOM killed
- How to set proper memory limits

**This is CRITICAL knowledge for web dev and DevOps!** 🚀

---
