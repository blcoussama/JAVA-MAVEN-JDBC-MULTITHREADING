# Section 3 - Processes

**Understanding what happens when you run your code**

---

## What is a Process?

**A process is a program in execution.**

**Simple definition:** When you run a program, the OS creates a process to execute it.

❗**Key insight:** A program is just a file on disk. A process is that program loaded into memory and running.

---

## Program vs Process: The Critical Difference

### **Program (Static)**

**What it is:**

- Executable file stored on disk (`.exe`, `.jar`, `.dll`)
- Contains instructions and data
- Passive - just sitting there
- One file

**Examples:**

- `chrome.exe` on your hard drive
- `MyApp.jar` (Java application)
- `Program.exe` (C# compiled application)

**Analogy:** A program is like a recipe in a cookbook - just instructions on paper.

---

### **Process (Dynamic)**

**What it is:**

- Program loaded into RAM and executing
- Active - instructions are being executed by CPU
- Has allocated resources (memory, CPU time, file handles)
- Multiple processes can run from the same program

**Examples:**

- Chrome process running in memory (with tabs, extensions)
- Python interpreter process executing your script
- Your web server process handling requests

**Analogy:** A process is like actually cooking the recipe - ingredients out, oven on, actively working.

---

### **One Program, Multiple Processes**

**Example: Opening Chrome Multiple Times**

```
Program (on disk):
📁 chrome.exe (one file)

Processes (in memory):
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│ Chrome      │  │ Chrome      │  │ Chrome      │
│ Process 1   │  │ Process 2   │  │ Process 3   │
│ PID: 1234   │  │ PID: 5678   │  │ PID: 9012   │
└─────────────┘  └─────────────┘  └─────────────┘

Same program, three separate processes!
Each has its own:
- Memory space
- CPU time
- Open files
- Process ID (PID)

```

**Why multiple processes from one program?**

- Each window/instance needs separate resources
- Isolation (if one crashes, others continue)
- Can work independently

---

## What Happens When You Run a Program?

**Let's trace what happens when you run a program:**

### **Before Running:**

```
Your code on disk:

📁 MyApp.java  or  📁 Program.cs  or  📁 app.js

┌──────────────────────────┐
│ System.out.println();    │  (Java)
│ Console.WriteLine();     │  (C#)
│ console.log();           │  (JavaScript)
└──────────────────────────┘

Just files sitting on disk

```

---

### **You Run It:**

```bash
java MyApp        # Java (JVM)
dotnet run        # C# (.NET)
node app.js       # JavaScript (Node.js)

```

---

### **OS Creates a Process - Step by Step:**

**Step 1: OS Allocates Process ID (PID)**

```
Process ID: 4567

```

⚠️ Every process gets a unique number

---

**Step 2: OS Allocates Memory Space**

```
┌─────────────────────────────────┐
│   Process 4567 Memory Space     │
│   (Virtual Address Space)       │
│                                 │
│   Reserved for this process     │
│   Size: 100 MB initially        │
└─────────────────────────────────┘

```

---

**Step 3: OS Loads Program into Memory**

```
From Disk → To RAM

📁 MyApp.jar     ────────→  Memory Space  (Java)
📁 Program.exe   ────────→  Memory Space  (C#/.NET)
📁 app.js        ────────→  Memory Space  (Node.js)

Now program instructions are in RAM

```

---

**Step 4: OS Sets Up Process Structure**

```
Creates:
- Stack (for function calls, local variables)
- Heap (for dynamic memory allocation)
- Code section (program instructions)
- Data section (global variables)

```

---

**Step 5: OS Creates Process Control Block (PCB)**

```
┌─────────────────────────────────┐
│ Process Control Block (PCB)     │
├─────────────────────────────────┤
│ PID: 4567                       │
│ State: Ready                    │
│ Program Counter: 0x00401000     │
│ CPU Registers: [saved values]   │
│ Memory Limits: 0x00000000 to... │
│ Open Files: [stdin, stdout]     │
│ Parent Process: 1234            │
└─────────────────────────────────┘

OS keeps track of everything!

```

---

**Step 6: OS Adds Process to Scheduler**

```
Ready Queue:
[Process 1234] → [Process 4567] → [Process 7890]
                     ↑
                 Your process
                 waiting for CPU

```

---

**Step 7: CPU Executes Your Code**

**Java:**

```java
// CPU starts executing line by line
System.out.println("Hello");  // Executes
int x = 10;                   // Executes
int result = x * 2;           // Executes
System.out.println(result);   // Executes

```

**C#:**

```csharp
// CPU starts executing line by line
Console.WriteLine("Hello");   // Executes
int x = 10;                   // Executes
int result = x * 2;           // Executes
Console.WriteLine(result);    // Executes

```

**JavaScript:**

```jsx
// CPU starts executing line by line
console.log("Hello");         // Executes
let x = 10;                   // Executes
let result = x * 2;           // Executes
console.log(result);          // Executes

```

---

**Step 8: Process Terminates**

```
OS cleans up:
- Frees memory
- Closes file handles
- Removes from process table
- Returns resources to system

Process 4567 no longer exists

```

⚠️ **All this happens in milliseconds to seconds!**

---

## Process Memory Layout

⚠️ **Every process has its own memory space, organized into sections:**

```
High Memory Address (e.g., 0xFFFFFFFF)
┌─────────────────────────────────────┐
│          KERNEL SPACE               │ ← OS code, not accessible to process
│      (Protected from process)       │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│          STACK                      │ ← Grows downward ↓
│  - Function call frames             │
│  - Local variables                  │
│  - Return addresses                 │
│  - Function parameters              │
│                                     │
│  Example:                           │
│  ┌──────────────────────┐          │
│  │ main() frame         │          │
│  │ - local var x        │          │
│  ├──────────────────────┤          │
│  │ calculate() frame    │          │
│  │ - local var y        │          │
│  │ - return address     │          │
│  └──────────────────────┘          │
│                                     │
│          ↓ ↓ ↓                     │
│         (grows down)                │
│                                     │
│         ↑ ↑ ↑                      │
│        (grows up)                   │
│                                     │
│          HEAP                       │ ← Grows upward ↑
│  - Dynamically allocated memory    │
│  - malloc() / new allocations      │
│  - Objects created at runtime      │
│                                     │
│  Example:                           │
│  - User object (allocated)         │
│  - Array of 1000 items             │
│  - String buffers                  │
│                                     │
└─────────────────────────────────────┘
│          BSS SEGMENT                │
│  - Uninitialized global variables  │
│  - Static variables (zero-init)    │
└─────────────────────────────────────┘
│          DATA SEGMENT               │
│  - Initialized global variables    │
│  - Static variables (with values)  │
│  - Constants                        │
└─────────────────────────────────────┘
│          CODE/TEXT SEGMENT          │
│  - Program instructions             │
│  - Compiled machine code            │
│  - Read-only                        │
└─────────────────────────────────────┘
Low Memory Address (e.g., 0x00000000)

```

---

### **Memory Sections Explained:**

### **1. Stack**

**What it stores:**

- Local variables in functions
- Function parameters
- Return addresses (where to go after function finishes)
- Function call frames

**Characteristics:**

- Fast allocation/deallocation (automatic)
- Limited size (typically 1-8 MB)
- LIFO (Last In, First Out)
- Managed automatically by compiler
- Grows downward from high addresses

**Example:**

**Java:**

```java
public int calculate(int a, int b) {  // Parameters on stack
    int result = a + b;               // Local variable on stack
    return result;                    // Return value via stack
}

public void main() {
    int x = 10;                       // Local variable on stack
    int y = 20;                       // Local variable on stack
    int sum = calculate(x, y);        // Function call uses stack
}

```

**C#:**

```csharp
public int Calculate(int a, int b) {  // Parameters on stack
    int result = a + b;               // Local variable on stack
    return result;                    // Return value via stack
}

public void Main() {
    int x = 10;                       // Local variable on stack
    int y = 20;                       // Local variable on stack
    int sum = Calculate(x, y);        // Function call uses stack
}

```

**JavaScript (Node.js):**

```jsx
function calculate(a, b) {            // Parameters on stack
    const result = a + b;             // Local variable on stack
    return result;                    // Return value via stack
}

function main() {
    const x = 10;                     // Local variable on stack
    const y = 20;                     // Local variable on stack
    const sum = calculate(x, y);      // Function call uses stack
}

```

**Stack frames during execution:**

```
When calculate() is called:

Stack:
┌──────────────────┐ ← Top of stack
│ calculate() frame│
│ - a = 10         │
│ - b = 20         │
│ - result = 30    │
│ - return address │
├──────────────────┤
│ main() frame     │
│ - x = 10         │
│ - y = 20         │
│ - sum = ?        │
└──────────────────┘

When calculate() returns:
Stack:
┌──────────────────┐ ← calculate() frame removed
│ main() frame     │
│ - x = 10         │
│ - y = 20         │
│ - sum = 30       │ ← Now has value
└──────────────────┘

```

⚠️ **Stack overflow:**

- Happens when stack grows too large
- Common cause: Infinite recursion

**Java:**

```java
public void infiniteRecursion() {
    infiniteRecursion();  // No base case!
}
// This will cause StackOverflowError
// Each call adds frame to stack
// Eventually runs out of stack space

```

**C#:**

```csharp
public void InfiniteRecursion() {
    InfiniteRecursion();  // No base case!
}
// This will cause StackOverflowException
// Each call adds frame to stack
// Eventually runs out of stack space

```

**JavaScript (Node.js):**

```jsx
function infiniteRecursion() {
    infiniteRecursion();  // No base case!
}
// This will cause "Maximum call stack size exceeded"
// Each call adds frame to stack
// Eventually runs out of stack space

```

---

### **2. Heap**

**What it stores:**

- Dynamically allocated memory
- Objects created at runtime
- Data structures whose size isn't known at compile time

**Characteristics:**

- Larger than stack (can be GBs)
- Slower allocation/deallocation
- Manual management (malloc/free in C) or Garbage Collection (Python, JavaScript)
- Grows upward from low addresses
- Can fragment over time

**Example:**

**Java:**

```java
// Java - heap allocation happens automatically
List<User> users = new ArrayList<>();  // List object on heap

for (int i = 0; i < 1000; i++) {
    User user = new User("User" + i);  // Each User object on heap
    users.add(user);
}

// All 1000 User objects live on heap
// Java's garbage collector will clean them up when no longer needed

```

**C#:**

```csharp
// C# - heap allocation happens automatically
List<User> users = new List<User>();  // List object on heap

for (int i = 0; i < 1000; i++) {
    User user = new User($"User{i}");  // Each User object on heap
    users.Add(user);
}

// All 1000 User objects live on heap
// .NET garbage collector will clean them up when no longer needed

```

**JavaScript (Node.js):**

```jsx
// JavaScript - heap allocation happens automatically
const users = [];  // Array object on heap

for (let i = 0; i < 1000; i++) {
    const user = new User(`User${i}`);  // Each User object on heap
    users.push(user);
}

// All 1000 User objects live on heap
// V8 garbage collector will clean them up when no longer needed

```

**In C (manual heap management):**

```c
// Allocate 100 bytes on heap
char* buffer = malloc(100);
strcpy(buffer, "Hello");

// Must manually free!
free(buffer);

// If you forget free() = MEMORY LEAK

```

⚠️ **Memory leak:**

- Allocated memory never freed
- Program keeps using more memory
- Eventually runs out of RAM

**Java:**

```java
// Example of memory leak (rare but possible)
static List<byte[]> cache = new ArrayList<>();

public void processData() {
    byte[] data = loadLargeDataset();  // 1GB of data
    cache.add(data);  // Keeps growing, never cleared!
    // Memory leak if cache grows forever
}

for (int i = 0; i < 1000; i++) {
    processData();  // Cache now has 1000GB! System crashes
}

```

**C#:**

```csharp
// Example of memory leak (rare but possible)
static List<byte[]> cache = new List<byte[]>();

public void ProcessData() {
    byte[] data = LoadLargeDataset();  // 1GB of data
    cache.Add(data);  // Keeps growing, never cleared!
    // Memory leak if cache grows forever
}

for (int i = 0; i < 1000; i++) {
    ProcessData();  // Cache now has 1000GB! System crashes
}

```

**JavaScript (Node.js):**

```jsx
// Example of memory leak (rare but possible)
const cache = [];

function processData() {
    const data = loadLargeDataset();  // 1GB of data
    cache.push(data);  // Keeps growing, never cleared!
    // Memory leak if cache grows forever
}

for (let i = 0; i < 1000; i++) {
    processData();  // Cache now has 1000GB! System crashes
}

```

---

### **3. Data Segment**

**What it stores:**

- Global variables (initialized)
- Static variables with values

**Example:**

```c
int global_var = 42;        // Data segment
static int count = 0;       // Data segment

int main() {
    int local_var = 10;     // Stack (NOT data segment)
}

```

---

### **4. BSS Segment**

**What it stores:**

- Uninitialized global/static variables
- Automatically set to zero

**Example:**

```c
int uninitialized_global;      // BSS segment (auto-set to 0)
static int uninitialized_stat; // BSS segment (auto-set to 0)

```

---

### **5. Code/Text Segment**

**What it stores:**

- Program instructions (machine code)
- Compiled functions
- Constants

**Characteristics:**

- Read-only (can't modify code at runtime)
- Shared between multiple processes running same program
- Fixed size

---

### **Stack vs Heap: The Key Differences**

| Feature | Stack | Heap |
| --- | --- | --- |
| **Speed** | Very fast | Slower |
| **Size** | Small (1-8 MB) | Large (GBs) |
| **Lifetime** | Automatic (scope-based) | Manual or GC |
| **Allocation** | Compile-time/automatic | Runtime |
| **Organization** | LIFO (ordered) | Unordered |
| **Fragmentation** | No | Yes (can fragment) |
| **Usage** | Local variables, function calls | Dynamic data, objects |

**When to use what:**

**Java:**

```java
// Stack (automatic) - for known, short-lived data
public int calculate() {
    int x = 10;      // Stack
    int y = 20;      // Stack
    return x + y;
}

// Heap (dynamic) - for unknown size, long-lived data
List<User> users = new ArrayList<>();  // List on heap
for (int i = 0; i < 10000; i++) {
    User user = new User();  // Each object on heap - size unknown at compile time
    users.add(user);
}

```

**C#:**

```csharp
// Stack (automatic) - for known, short-lived data
public int Calculate() {
    int x = 10;      // Stack
    int y = 20;      // Stack
    return x + y;
}

// Heap (dynamic) - for unknown size, long-lived data
List<User> users = new List<User>();  // List on heap
for (int i = 0; i < 10000; i++) {
    User user = new User();  // Each object on heap - size unknown at compile time
    users.Add(user);
}

```

**JavaScript (Node.js):**

```jsx
// Stack (automatic) - for known, short-lived data
function calculate() {
    const x = 10;    // Stack
    const y = 20;    // Stack
    return x + y;
}

// Heap (dynamic) - for unknown size, long-lived data
const users = [];  // Array on heap
for (let i = 0; i < 10000; i++) {
    const user = new User();  // Each object on heap - size unknown at compile time
    users.push(user);
}

```

---

## Process States

**A process transitions through different states during its lifetime:**

```
┌──────────┐
│   NEW    │  Process is being created
└────┬─────┘
     │
     ↓
┌──────────┐
│  READY   │  Process is ready to run, waiting for CPU
└────┬─────┘
     │ ↖───────────────────┐
     ↓                      │
┌──────────┐                │
│ RUNNING  │   Process is executing on CPU
└────┬─────┘                │
     │                      │
     ↓                      │
┌──────────┐                │
│ WAITING  │   Process waiting for I/O or event
│(BLOCKED) │────────────────┘ (becomes READY when I/O completes)
└────┬─────┘
     │
     ↓
┌──────────┐
│TERMINATED│  Process has finished execution
└──────────┘

```

---

### **State Descriptions:**

### **1. NEW**

- Process is being created
- OS is allocating resources
- Loading program into memory
- Setting up process structures

**Duration:** Milliseconds

---

### **2. READY**

- Process is ready to execute
- Waiting in queue for CPU time
- Has all needed resources except CPU
- Can start running immediately if given CPU

**Example:**

```
CPU has 8 cores, but 50 processes want to run
42 processes are in READY state, waiting their turn

```

---

### **3. RUNNING**

- Process is currently executing on CPU
- Instructions are being executed
- Only as many processes can be RUNNING as there are CPU cores

**Example:**

```
8-core CPU:
- 8 processes can be RUNNING simultaneously
- All others must WAIT or be READY

```

---

### **4. WAITING (BLOCKED)**

- Process cannot continue until some event occurs
- Waiting for I/O operation (disk read, network response)
- Waiting for user input
- Waiting for another process

**Example:**

**Java:**

```java
// Process becomes WAITING here:
FileReader file = new FileReader("huge_file.txt");
BufferedReader reader = new BufferedReader(file);
String content = reader.readLine();  // Waiting for disk I/O (10ms - 100ms)

// Process becomes RUNNING again when I/O completes

```

**C#:**

```csharp
// Process becomes WAITING here:
using (StreamReader file = new StreamReader("huge_file.txt")) {
    string content = file.ReadToEnd();  // Waiting for disk I/O (10ms - 100ms)
}

// Process becomes RUNNING again when I/O completes

```

**JavaScript (Node.js):**

```jsx
// Process becomes WAITING here:
const fs = require('fs');
fs.readFile('huge_file.txt', 'utf8', (err, content) => {
    // Waiting for disk I/O (10ms - 100ms)
    // Process becomes RUNNING again when I/O completes
});

```

⚠️ **Why this state exists:**

- Don't waste CPU time on processes waiting for I/O
- Give CPU to processes that can actually do work
- Much more efficient!

---

### **5. TERMINATED**

- Process has finished execution
- OS is cleaning up resources
- Soon will be removed from memory completely

**Duration:** Milliseconds (cleanup)

---

### **State Transitions Example:**

**Running a web server:**

```
1. Start server: java -jar server.jar  OR  dotnet run  OR  node server.js
   State: NEW → READY → RUNNING

2. Server listens for connections
   State: RUNNING (very brief) → WAITING (for incoming requests)

3. Request arrives!
   State: WAITING → READY → RUNNING

4. Server processes request (needs to query database)
   State: RUNNING → WAITING (for database response)

5. Database responds
   State: WAITING → READY → RUNNING

6. Server sends response
   State: RUNNING → WAITING (for next request)

Cycle repeats...

```

---

## Process Control Block (PCB)

**The OS keeps a data structure for each process called the Process Control Block (PCB).**

**Think of PCB as the process's "profile" or "identity card"**

### **What's in a PCB:**

```
┌─────────────────────────────────────────────┐
│        PROCESS CONTROL BLOCK (PCB)          │
├─────────────────────────────────────────────┤
│ Process ID (PID): 4567                      │
│ Process State: RUNNING                      │
│ Program Counter: 0x00401234                 │ ← Next instruction to execute
│ CPU Registers: [saved state]                │ ← Register values
│ CPU Scheduling Info:                        │
│   - Priority: 5                             │
│   - Time slice: 10ms                        │
│   - Time used: 2ms                          │
│ Memory Management Info:                     │
│   - Base address: 0x10000000                │
│   - Limit: 0x10FFFFFF                       │
│   - Page table pointer: 0xFFA00000          │
│ I/O Status:                                 │
│   - Open files: [stdin, file1.txt, db.sock] │
│   - Pending I/O operations: [disk read]     │
│ Accounting Info:                            │
│   - CPU time used: 1.5 seconds              │
│   - Start time: 14:32:05                    │
│   - User: john                              │
│ Parent Process: 1234                        │
│ Child Processes: [4568, 4569]               │
└─────────────────────────────────────────────┘

```

⚠️ **Why PCB matters:**

- OS needs this info to manage the process
- Used during context switching (saving/restoring state)
- Enables multi-tasking
- Allows OS to resume process exactly where it left off

---

### **Context Switching: Using the PCB**

**When OS switches from Process A to Process B:**

**Step 1: Save Process A's state**

```
Current CPU state → Process A's PCB
- Register values saved
- Program counter saved
- Stack pointer saved

```

**Step 2: Load Process B's state**

```
Process B's PCB → CPU
- Restore register values
- Restore program counter
- Restore stack pointer

```

**Step 3: CPU continues executing Process B**

```
CPU picks up exactly where Process B left off

```

**Time cost:** ~1-10 microseconds (overhead of context switching)

**This is how your 8-core CPU can run 100 processes!**

---

## Parent and Child Processes

**Processes can create other processes, forming a tree structure.**

### **Process Hierarchy:**

```
Init/systemd (PID 1)  ← Root process (started by OS)
├── Login Shell (PID 100)
│   ├── VS Code (PID 200)
│   │   ├── Extension Host (PID 201)
│   │   └── Python Language Server (PID 202)
│   ├── Terminal (PID 300)
│   │   └── Python Script (PID 301)
│   └── Chrome (PID 400)
│       ├── Tab 1 Renderer (PID 401)
│       ├── Tab 2 Renderer (PID 402)
│       └── GPU Process (PID 403)
└── Background Services...

```

⚠️ **Terminology:**

- **Parent process:** Process that creates another process
- **Child process:** Process created by another process
- **Orphan process:** Child whose parent has terminated
- **Zombie process:** Terminated child waiting for parent to read exit status

---

### **Process Creation: fork() and exec()**

**In Unix/Linux, processes are created using `fork()` and `exec()` system calls.**

### **fork() - Create Child Process**

**What fork() does:**

1. Creates exact copy of parent process
2. Child gets copy of parent's memory
3. Both processes continue execution
4. Returns different values to parent and child

**Example:**

```c
#include <unistd.h>
#include <stdio.h>

int main() {
    printf("Before fork\\n");

    pid_t pid = fork();  // Create child process

    if (pid == 0) {
        // This code runs in CHILD process
        printf("I am the child! PID: %d\\n", getpid());
    } else {
        // This code runs in PARENT process
        printf("I am the parent! Child PID: %d\\n", pid);
    }

    return 0;
}

```

**Output:**

```
Before fork
I am the parent! Child PID: 1234
I am the child! PID: 1234

```

**What happened:**

```
Before fork:
One process running

After fork:
Two processes running (parent and child)
Both have same code, same variables
Both continue from the line after fork()

```

---

### **exec() - Replace Process with New Program**

**What exec() does:**

1. Replaces current process with new program
2. Loads new program into memory
3. Starts executing new program
4. Original program is gone

**Example:**

```c
#include <unistd.h>

int main() {
    printf("Before exec\\n");

    // Replace this process with 'ls' command
    execl("/bin/ls", "ls", "-l", NULL);

    // This line never executes if exec succeeds!
    printf("After exec\\n");  // Won't print
}

```

**Output:**

```
Before exec
(then ls -l output)

```

---

### **fork() + exec() Pattern: How New Programs Start**

**Typical pattern:**

```c
pid_t pid = fork();  // Create child

if (pid == 0) {
    // Child process
    execl("/bin/program", "program", NULL);  // Run new program
} else {
    // Parent process
    wait(NULL);  // Wait for child to finish
}

```

**This is how your shell runs commands!**

```bash
$ ls -l

What happens:
1. Shell (parent) calls fork()
2. Child process created
3. Child calls exec("/bin/ls", "ls", "-l")
4. ls program replaces child
5. ls executes and shows output
6. ls terminates
7. Shell (parent) resumes

```

---

## Process Termination

**How processes end:**

### **Normal Termination:**

**1. return from main()**

```c
int main() {
    printf("Hello\\n");
    return 0;  // Process terminates normally
}

```

**2. exit() system call**

**Java:**

```java
System.exit(0);  // Terminate with exit code 0 (success)
System.exit(1);  // Terminate with exit code 1 (error)

```

**C#:**

```csharp
Environment.Exit(0);  // Terminate with exit code 0 (success)
Environment.Exit(1);  // Terminate with exit code 1 (error)

```

**JavaScript (Node.js):**

```jsx
process.exit(0);  // Terminate with exit code 0 (success)
process.exit(1);  // Terminate with exit code 1 (error)

```

**Exit codes:**

- `0` = Success
- `1-255` = Error (different codes for different errors)

---

### **Abnormal Termination:**

**1. Segmentation fault (memory access error)**

```c
int* ptr = NULL;
*ptr = 42;  // CRASH! Accessing NULL pointer

```

**2. Killed by user (Ctrl+C)**

```bash
$ java MyApp  # or: dotnet run, or: node app.js
^C  # User presses Ctrl+C, process terminates

```

**3. Killed by OS (out of memory)**

**Java:**

```java
// OS kills process if using too much RAM
List<int[]> data = new ArrayList<>();
while (true) {
    data.add(new int[1000000]);  // Eventually killed by OS (OutOfMemoryError)
}

```

**C#:**

```csharp
// OS kills process if using too much RAM
List<int[]> data = new List<int[]>();
while (true) {
    data.Add(new int[1000000]);  // Eventually killed by OS (OutOfMemoryException)
}

```

**JavaScript (Node.js):**

```jsx
// OS kills process if using too much RAM
const data = [];
while (true) {
    data.push(new Array(1000000).fill(0));  // Eventually killed by OS
}

```

**4. Kill command**

```bash
kill 4567          # Graceful termination (SIGTERM)
kill -9 4567       # Force kill (SIGKILL)

```

---

### **What Happens When Process Terminates:**

```
1. Process state → TERMINATED
   ↓
2. OS stops scheduling it
   ↓
3. Closes all open files
   ↓
4. Frees allocated memory
   ↓
5. Releases other resources (sockets, locks, etc.)
   ↓
6. Returns exit status to parent process
   ↓
7. Removes process from process table
   ↓
8. Process no longer exists

```

---

## Viewing Processes

**How to see what processes are running on your system:**

### **Windows: Task Manager**

**Open:** `Ctrl + Shift + Esc`

**What you see:**

```
Name               PID    Status   CPU    Memory
Chrome.exe         1234   Running  15%    500 MB
Code.exe           5678   Running  5%     300 MB
python.exe         9012   Running  80%    100 MB

```

**Columns explained:**

- **Name:** Program name
- **PID:** Process ID (unique number)
- **Status:** Running, Suspended, etc.
- **CPU:** Percentage of CPU being used
- **Memory:** RAM usage

**Actions:**

- Right-click → End Task (terminate process)
- View → Details (see more info)

---

### **Linux/macOS: ps command**

**Basic usage:**

```bash
$ ps
  PID TTY          TIME CMD
 1234 pts/0    00:00:00 bash
 5678 pts/0    00:00:02 python

```

**See all processes:**

```bash
$ ps aux
USER   PID  %CPU %MEM    VSZ   RSS TTY   STAT START   TIME COMMAND
john  1234   0.1  0.5  25000 10000 pts/0 S    14:30   0:02 python script.py
john  5678   5.0  2.0  80000 40000 pts/1 R    14:35   0:15 node server.js

```

**Columns explained:**

- **USER:** Who owns the process
- **PID:** Process ID
- **%CPU:** CPU usage
- **%MEM:** Memory usage
- **STAT:** State (R=Running, S=Sleeping/Waiting, Z=Zombie)
- **START:** When process started
- **COMMAND:** What program is running

---

### **Linux/macOS: top command**

**Real-time process monitor:**

```bash
$ top

Tasks: 150 total,   2 running, 148 sleeping
%Cpu(s):  5.2 us,  1.5 sy,  0.0 ni, 93.0 id
MiB Mem :   8000.0 total,   2000.0 free,   4000.0 used

  PID USER      PR  NI    VIRT    RES  %CPU %MEM     TIME+ COMMAND
 1234 john      20   0  200000  50000   5.0  0.6   0:15.23 python
 5678 john      20   0  500000 100000  15.0  1.2   0:45.67 chrome

```

**Interactive commands:**

- `q` - Quit
- `k` - Kill process (enter PID)
- `P` - Sort by CPU usage
- `M` - Sort by memory usage

---

### **Windows: PowerShell**

```powershell
# List all processes
Get-Process

# Find specific process
Get-Process python

# Kill process
Stop-Process -Name "python"
Stop-Process -Id 1234

```

---

## Practical Examples

### **Example 1: Web Server**

**Java (Spring Boot):**

```java
@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello World";
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloController.class, args);
    }
}

```

**C# ([ASP.NET](http://asp.net/) Core):**

```csharp
var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

app.MapGet("/", () => "Hello World");

app.Run();

```

**JavaScript (Node.js + Express):**

```jsx
const express = require('express');
const app = express();

app.get('/', (req, res) => {
    res.send('Hello World');
});

app.listen(3000, () => {
    console.log('Server running on port 3000');
});

```

**What happens when you run this:**

```
1. You: java -jar server.jar  or  dotnet run  or  node server.js
   ↓
2. OS creates process:
   - Allocates PID (e.g., 4567)
   - Loads runtime (JVM/.NET/Node.js) into memory
   - Creates stack and heap
   - Sets up PCB
   ↓
3. Process starts:
   - State: RUNNING
   - Loads framework (Spring/ASP.NET/Express)
   - Starts web server
   ↓
4. Server enters waiting loop:
   - State: WAITING (for HTTP requests)
   - CPU usage: ~0%
   - Memory: ~50-200 MB
   ↓
5. Request arrives:
   - State: READY → RUNNING
   - Processes request
   - CPU usage: spikes to 20%
   ↓
6. Response sent:
   - State: RUNNING → WAITING
   - Back to waiting for requests

```

---

### **Example 2: Concurrent File Processing**

**Java:**

```java
// Without processes (slow)
public void processFiles(List<File> files) {
    for (File file : files) {
        process(file);  // Sequential, one at a time
    }
}

// With processes (using ProcessBuilder)
public void processFilesParallel(List<File> files) throws Exception {
    List<Process> processes = new ArrayList<>();

    for (File file : files) {
        ProcessBuilder pb = new ProcessBuilder("java", "FileProcessor", file.getPath());
        Process p = pb.start();
        processes.add(p);
    }

    // Wait for all to finish
    for (Process p : processes) {
        p.waitFor();
    }
}

```

**C#:**

```csharp
// Without processes (slow)
public void ProcessFiles(List<string> files) {
    foreach (var file in files) {
        Process(file);  // Sequential, one at a time
    }
}

// With processes
public async Task ProcessFilesParallel(List<string> files) {
    var processes = new List<Process>();

    foreach (var file in files) {
        var process = Process.Start("dotnet", $"FileProcessor.dll {file}");
        processes.Add(process);
    }

    // Wait for all to finish
    foreach (var p in processes) {
        await p.WaitForExitAsync();
    }
}

```

**JavaScript (Node.js):**

```jsx
// Without processes (slow)
function processFiles(files) {
    for (const file of files) {
        process(file);  // Sequential, one at a time
    }
}

// With child processes
const { spawn } = require('child_process');

function processFilesParallel(files) {
    const processes = [];

    for (const file of files) {
        const p = spawn('node', ['fileProcessor.js', file]);
        processes.push(p);
    }

    // Wait for all to finish
    return Promise.all(processes.map(p =>
        new Promise(resolve => p.on('exit', resolve))
    ));
}

```

**What happens:**

```
Without processes:
Process 1: [File 1] → [File 2] → [File 3] → [File 4]
Time: 40 seconds (10 seconds each)

With processes:
Process 1: [File 1]
Process 2: [File 2]  } All running simultaneously
Process 3: [File 3]
Process 4: [File 4]
Time: 10 seconds (if you have 4+ CPU cores)

```

---

## Key Takeaways - Section 2 - Processes

### **Core Concepts:**

✅ **Process = Program in execution** (program is static file, process is dynamic execution)

✅ **One program can have multiple processes** (e.g., multiple Chrome windows)

✅ **Each process has its own memory space** (isolated from other processes)

### **Memory Layout:**

✅ **Stack:** Local variables, function calls (fast, automatic, small)

✅ **Heap:** Dynamic memory allocation (slower, manual/GC, large)

✅ **Code:** Program instructions (read-only)

✅ **Data:** Global/static variables

### **Process States:**

✅ **NEW:** Being created

✅ **READY:** Ready to run, waiting for CPU

✅ **RUNNING:** Executing on CPU

✅ **WAITING:** Blocked on I/O or event

✅ **TERMINATED:** Finished execution

### **Process Management:**

✅ **PCB (Process Control Block):** OS keeps track of each process

✅ **PID (Process ID):** Unique identifier for each process

✅ **Parent/Child:** Processes can create other processes

✅ **Context switching:** OS switches between processes rapidly

### **Why This Matters:**

✅ **Understanding stack vs heap** helps you write better code

✅ **Knowing process states** helps debug performance issues

✅ **Multiple processes** enable parallelism (use multiple CPU cores)

✅ **Process isolation** provides security and stability

---

## What's Next?

**Threads** - You'll finally understand:

- What threads are (lightweight processes)
- Thread vs Process (the critical difference)
- When to use threads vs processes
- Multi-threading and synchronization
- Race conditions and deadlocks
- Why threading doesn't always make things faster

**Threads are what most developers actually use in their daily code!**

---
