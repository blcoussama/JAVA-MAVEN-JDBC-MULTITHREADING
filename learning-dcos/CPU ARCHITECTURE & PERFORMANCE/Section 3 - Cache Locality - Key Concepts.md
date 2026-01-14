# Section 3 - Cache Locality - Key Concepts

**Understanding why data access patterns matter for performance**

---

## What is Cache Locality?

**Cache locality** means accessing data in ways that keep it in the fast CPU cache rather than having to fetch it from slow RAM.

**Two types of locality:**

1. **Spatial Locality** - Accessing data that's close together in memory
2. **Temporal Locality** - Reusing the same data multiple times

**Why this matters:**

- Data in cache = Access in ~1-15 nanoseconds (FAST)
- Data in RAM = Access in ~100 nanoseconds (100x SLOWER)
- Good locality = More cache hits = Faster program

---

## Spatial Locality: The "Neighborhood" Principle

**Concept:** If you access memory address X, you'll likely access nearby addresses soon after.

**Why it's powerful:**

- Remember from Section 2: Caches load data in 64-byte chunks (cache lines)
- When you access 1 byte, the CPU loads 64 bytes into cache
- Accessing the next 63 bytes is FREE (already in cache!)

### Example: Arrays vs Random Access

**Good spatial locality (FAST):**

**Java:**

```java
int[] numbers = {1, 2, 3, 4, 5, ...};  // Array in memory

int total = 0;
for (int num : numbers) {
    total += num;  // Sequential access - excellent locality!
}

```

**C#:**

```csharp
int[] numbers = {1, 2, 3, 4, 5, ...};  // Array in memory

int total = 0;
foreach (int num in numbers) {
    total += num;  // Sequential access - excellent locality!
}

```

**JavaScript:**

```jsx
const numbers = [1, 2, 3, 4, 5, ...];  // Array in memory

let total = 0;
for (const num of numbers) {
    total += num;  // Sequential access - excellent locality!
}

```

**Memory layout:**

```
[1][2][3][4][5][6][7][8]... (all next to each other)

```

**What happens:**

- Access numbers[0] → Cache loads numbers[0-15]
- Access numbers[1] → Already in cache! (FREE)
- Access numbers[2] → Already in cache! (FREE)
- ...continues with very few cache misses

---

**Poor spatial locality (SLOW):**

**Java:**

```java
// Accessing random locations in memory
Random random = new Random();
for (int i = 0; i < 1000; i++) {
    int index = random.nextInt(1000000);
    int value = hugeArray[index];  // Random jumps across memory!
}

```

**C#:**

```csharp
// Accessing random locations in memory
Random random = new Random();
for (int i = 0; i < 1000; i++) {
    int index = random.Next(1000000);
    int value = hugeArray[index];  // Random jumps across memory!
}

```

**JavaScript:**

```jsx
// Accessing random locations in memory
for (let i = 0; i < 1000; i++) {
    const index = Math.floor(Math.random() * 1000000);
    const value = hugeArray[index];  // Random jumps across memory!
}

```

**What happens:**

- Each random access likely jumps to a new memory region
- Constant cache misses
- Must go to RAM every time (100x slower)

---

## The Classic Example: Matrix Traversal

**In memory, 2D arrays are stored row-by-row:**

```
matrix[0][0], matrix[0][1], matrix[0][2], ...
matrix[1][0], matrix[1][1], matrix[1][2], ...

```

### Row-major order (GOOD - Sequential)

**Java:**

```java
for (int row = 0; row < rows; row++) {
    for (int col = 0; col < cols; col++) {
        process(matrix[row][col]);  // Sequential access!
    }
}

```

**C#:**

```csharp
for (int row = 0; row < rows; row++) {
    for (int col = 0; col < cols; col++) {
        Process(matrix[row, col]);  // Sequential access!
    }
}

```

**JavaScript:**

```jsx
for (let row = 0; row < rows; row++) {
    for (let col = 0; col < cols; col++) {
        process(matrix[row][col]);  // Sequential access!
    }
}

```

**Excellent cache locality** - accessing memory in order it's stored

### Column-major order (BAD - Jumping)

**Java:**

```java
for (int col = 0; col < cols; col++) {
    for (int row = 0; row < rows; row++) {
        process(matrix[row][col]);  // Jumping across memory!
    }
}

```

**C#:**

```csharp
for (int col = 0; col < cols; col++) {
    for (int row = 0; row < rows; row++) {
        Process(matrix[row, col]);  // Jumping across memory!
    }
}

```

**JavaScript:**

```jsx
for (let col = 0; col < cols; col++) {
    for (let row = 0; row < rows; row++) {
        process(matrix[row][col]);  // Jumping across memory!
    }
}

```

**Poor cache locality** - jumping by thousands of elements each access

**Performance difference:** Can be **10-30x slower** for large matrices!

⚠️ **Same algorithm, same data, massively different performance - just from access order!**

---

## Temporal Locality: The "Reuse" Principle

**Concept:** If you access data now, you'll likely access it again soon.

**Why it's powerful:**

- Recently used data stays in cache
- Accessing it again = cache hit = FAST
- No need to reload from RAM

### Example: Loop Variables

**Good temporal locality:**

**Java:**

```java
int total = 0;  // This variable gets reused 1000 times
for (int i = 0; i < 1000; i++) {
    total += array[i];  // 'total' stays in cache/register
}
```

**C#:**

```csharp
int total = 0;  // This variable gets reused 1000 times
for (int i = 0; i < 1000; i++) {
    total += array[i];  // 'total' stays in cache/register
}
```

**JavaScript:**

```jsx
let total = 0;  // This variable gets reused 1000 times
for (let i = 0; i < 1000; i++) {
    total += array[i];  // 'total' stays in cache/register
}
```

**What happens:**

- Variable `total` accessed 1000 times
- After first use, stays in fastest cache level
- All subsequent accesses = instant (in cache)

---

**Poor temporal locality:**

**Java:**

```java
// First loop
for (int i = 0; i < 1000; i++) {
    result[i] = array[i] * 2;
}

// ... lots of other code runs here ...
// (fills cache with other data, evicting 'result')

// Second loop (much later)
for (int i = 0; i < 1000; i++) {
    result[i] += 10;  // 'result' was evicted, must reload from RAM
}

```

**C#:**

```csharp
// First loop
for (int i = 0; i < 1000; i++) {
    result[i] = array[i] * 2;
}

// ... lots of other code runs here ...
// (fills cache with other data, evicting 'result')

// Second loop (much later)
for (int i = 0; i < 1000; i++) {
    result[i] += 10;  // 'result' was evicted, must reload from RAM
}

```

**Better approach:**

**Java:**

```java
// Process data while it's still hot in cache
for (int i = 0; i < 1000; i++) {
    result[i] = array[i] * 2;
    result[i] += 10;  // Use immediately while still in cache!
}

```

**C#:**

```csharp
// Process data while it's still hot in cache
for (int i = 0; i < 1000; i++) {
    result[i] = array[i] * 2;
    result[i] += 10;  // Use immediately while still in cache!
}

```

---

## Arrays vs Linked Lists: A Cache Perspective

**This explains why arrays are usually much faster than linked lists.**

### Arrays: Excellent Cache Locality

**Java:**

```java
int[] array = {1, 2, 3, 4, 5, 6, 7, 8, ...};

for (int item : array) {
    process(item);
}

```

**C#:**

```csharp
int[] array = {1, 2, 3, 4, 5, 6, 7, 8, ...};

foreach (int item in array) {
    Process(item);
}

```

**JavaScript:**

```jsx
const array = [1, 2, 3, 4, 5, 6, 7, 8, ...];

for (const item of array) {
    process(item);
}

```

**Memory layout:**

```
[1][2][3][4][5][6][7][8]... (contiguous, sequential)

```

**Cache behavior:**

- Sequential access = perfect spatial locality
- One cache line load gets many elements
- Very few cache misses
- **FAST!**

---

### Linked Lists: Poor Cache Locality

**Java:**

```java
class Node {
    int data;
    Node next;

    Node(int data) {
        this.data = data;
        this.next = null;
    }
}

// Nodes scattered randomly in memory
Node current = head;
while (current != null) {
    process(current.data);
    current = current.next;  // Jump to random memory location
}

```

**C#:**

```csharp
class Node {
    public int Data { get; set; }
    public Node Next { get; set; }
}

// Nodes scattered randomly in memory
Node current = head;
while (current != null) {
    Process(current.Data);
    current = current.Next;  // Jump to random memory location
}

```

**JavaScript:**

```jsx
class Node {
    constructor(data) {
        this.data = data;
        this.next = null;
    }
}

// Nodes scattered randomly in memory
let current = head;
while (current) {
    process(current.data);
    current = current.next;  // Jump to random memory location
}

```

**Memory layout:**

```
Node 1: Address 0x1000
Node 2: Address 0x5FF0 (random location!)
Node 3: Address 0x2A00 (random location!)
...

```

**Cache behavior:**

- Each node is at unpredictable memory location
- Following pointers = jumping to random addresses
- Almost every access = cache miss
- **SLOW!**

**Performance:** Arrays can be **5-10x faster** for traversal, even though both are O(n)!

**Key insight:** Big O notation doesn't capture cache effects!

---

## Data Structures and Cache Performance

**Cache-friendly structures:**

- ✅ Arrays - Sequential, contiguous memory
- ✅ ArrayList/List - Same benefits as arrays
- ✅ Strings - Sequential characters

**Cache-unfriendly structures:**

- ❌ LinkedList - Nodes scattered in memory
- ❌ Trees - Pointer chasing through random locations
- ❌ HashMap/Dictionary - Random bucket access

⚠️ **Note:** This doesn't mean "never use these structures!" They have other benefits (insertion, deletion, etc.). Just understand the trade-offs.

---

## Practical Guidelines for Writing Cache-Friendly Code

### Rule 1: Access Memory Sequentially

✅ **Good:**

**Java:**

```java
for (int i = 0; i < array.length; i++) {
    sum += array[i];  // Sequential
}

```

**C#:**

```csharp
for (int i = 0; i < array.Length; i++) {
    sum += array[i];  // Sequential
}

```

❌ **Bad:**

**Java:**

```java
Random random = new Random();
for (int i = 0; i < array.length; i++) {
    sum += array[random.nextInt(array.length)];  // Random jumps
}

```

---

### Rule 2: Process Data While It's Hot (in cache)

✅ **Good:**

**Java:**

```java
for (int i = 0; i < data.length; i++) {
    int value = data[i];
    value = value * 2;    // Use it
    value = value + 10;   // Use it again
    result[i] = value;    // Use it once more
}

```

**C#:**

```csharp
for (int i = 0; i < data.Length; i++) {
    int value = data[i];
    value = value * 2;    // Use it
    value = value + 10;   // Use it again
    result[i] = value;    // Use it once more
}

```

❌ **Bad:**

**Java:**

```java
// First pass
for (int i = 0; i < data.length; i++) {
    temp[i] = data[i] * 2;
}

// Second pass (data no longer in cache)
for (int i = 0; i < temp.length; i++) {
    result[i] = temp[i] + 10;
}

```

---

### Rule 3: Prefer Arrays Over Linked Lists

⚠️ Unless you specifically need frequent insertions/deletions in the middle, arrays are almost always faster.

**Why?**

- Better cache locality
- Better memory efficiency (no pointer overhead)
- Better prefetching by CPU

---

### Rule 4: Keep Working Set Small

**Working set** = amount of data your code actively uses

**If possible, keep it smaller than cache size:**

- L1 cache: ~32-64 KB
- L2 cache: ~256-512 KB
- L3 cache: ~8-32 MB

**Example:**

**Java:**

```java
// Small working set (fits in cache)
int[] smallLookup = new int[100];  // 400 bytes

for (int i = 0; i < 1000000; i++) {
    sum += smallLookup[i % 100];  // Always in cache!
}

// Large working set (doesn't fit in cache)
int[] hugeLookup = new int[1000000];  // 4 MB

for (int i = 0; i < 1000000; i++) {
    sum += hugeLookup[i];  // Constant cache misses!
}

```

**C#:**

```csharp
// Small working set (fits in cache)
int[] smallLookup = new int[100];  // 400 bytes

for (int i = 0; i < 1000000; i++) {
    sum += smallLookup[i % 100];  // Always in cache!
}

// Large working set (doesn't fit in cache)
int[] hugeLookup = new int[1000000];  // 4 MB

for (int i = 0; i < 1000000; i++) {
    sum += hugeLookup[i];  // Constant cache misses!
}

```

---

## Real-World Performance Impact

**Example benchmarks on large datasets:**

**Scenario 1: Summing 1 million integers**

- Array (sequential): 3 milliseconds
- Linked list (random): 35 milliseconds
- **12x difference!**

**Scenario 2: Matrix multiplication (1000×1000)**

- Row-major order: 500 milliseconds
- Column-major order: 15,000 milliseconds
- **30x difference!**

**Scenario 3: Processing image pixels**

- Sequential row-by-row: 100 milliseconds
- Random pixel order: 2,000 milliseconds
- **20x difference!**

**These are the same operations, just different access patterns!**

---

## Key Takeaways - Section 3 - Cache Locality

### Core Concepts

✅ **Spatial locality** = Accessing nearby memory addresses (sequential access is fast)

✅ **Temporal locality** = Reusing the same data (keep data hot in cache)

✅ **Cache lines load 64 bytes at once** → Nearby data comes for FREE

✅ **Sequential access = cache-friendly** (arrays, row-major order)

✅ **Random access = cache-unfriendly** (linked lists, random jumps, column-major order)

### Performance Impact

✅ **Arrays are 5-10x faster than linked lists** for traversal (cache locality)

✅ **Access order can cause 10-30x performance difference** (matrix example)

✅ **These differences are often BIGGER than algorithm optimizations!**

✅ **Big O notation doesn't capture cache effects** - O(n) with good locality beats O(n) with poor locality

### Practical Guidelines

✅ **Use arrays over linked lists** when possible (sequential access)

✅ **Access arrays sequentially**, not randomly

✅ **Process data immediately** after loading (temporal locality)

✅ **For matrices: traverse row-by-row**, not column-by-column

✅ **Keep working set small** when possible (fit in cache)

### The Big Picture

✅ **Modern CPUs optimize for sequential access** - work with this, not against it

✅ **Data structure choice affects performance** beyond just Big O

✅ **Memory access patterns matter** - sometimes more than algorithm choice

✅ **Most compilers optimize for this automatically** - but understanding it helps you make better design decisions

### Important Note

✅ **For most applications, you won't manually optimize for cache locality**

✅ **But understanding WHY arrays are fast and linked lists are slow** helps you make informed decisions

✅ **When performance matters, these principles can give you 10-100x improvements**

---

**You now understand the fundamentals of cache locality. This knowledge helps you reason about performance, even if you don't manually optimize for it daily!**
