# Section 4 - How CPU Executes Code

**Understanding the basic cycle of instruction execution**

---

## The Fetch-Decode-Execute Cycle

Every CPU, no matter how complex, follows the same basic pattern to run programs. This is called the **instruction cycle** or **fetch-decode-execute cycle**.

**The cycle has 4 steps:**

```
1. FETCH    → Get the next instruction from memory
2. DECODE   → Figure out what the instruction means
3. EXECUTE  → Perform the operation
4. STORE    → Write the result back (if needed)

Then repeat billions of times per second...
```

---

## Step-by-Step: What Happens When Code Runs

### Your Code

**Java:**

```java
int x = 5 + 3;

```

**C#:**

```csharp
int x = 5 + 3;

```

**JavaScript:**

```jsx
let x = 5 + 3;

```

### What the CPU Actually Does

**Step 1: FETCH**

- CPU asks: "What's the next instruction?"
- Instruction pointer (register) holds the address of the next instruction
- CPU fetches instruction from memory (hopefully from cache!)
- Example instruction: "ADD 5 and 3, store in x"

**Step 2: DECODE**

- Instruction decoder interprets what "ADD" means
- Figures out: "This is an addition operation"
- Identifies the operands: 5 and 3
- Determines where to store result: variable x

**Step 3: EXECUTE**

- Load value 5 into Register A
- Load value 3 into Register B
- ALU (Arithmetic Logic Unit) adds Register A + Register B
- Result (8) goes into Register C

**Step 4: STORE (Write-back)**

- Take value from Register C
- Write it to memory location for variable x
- Update instruction pointer to next instruction

**Then the cycle repeats for the next instruction.**

---

## From High-Level Code to CPU Instructions

**What you write (Java):**

```java
public int calculate(int a, int b) {
    int result = a + b;
    return result;
}

int answer = calculate(5, 3);

```

**What you write (C#):**

```csharp
public int Calculate(int a, int b) {
    int result = a + b;
    return result;
}

int answer = Calculate(5, 3);

```

**What you write (JavaScript):**

```jsx
function calculate(a, b) {
    const result = a + b;
    return result;
}

const answer = calculate(5, 3);

```

**What the CPU actually executes (simplified assembly):**

```
LOAD R1, 5        ; Load 5 into register 1
LOAD R2, 3        ; Load 3 into register 2
ADD R3, R1, R2    ; Add R1 and R2, store in R3
STORE R3, answer  ; Store R3 to memory location 'answer'

```

**Key insight:** Your one line of code becomes MULTIPLE CPU instructions.

---

## The Instruction Pointer (Program Counter)

**How the CPU knows what to execute next:**

The CPU has a special register called the **Instruction Pointer** (or Program Counter) that holds the memory address of the next instruction.

**Example flow:**

```
Instruction Pointer = 0x1000

Step 1: Execute instruction at 0x1000
Step 2: Increment pointer to 0x1004
Step 3: Execute instruction at 0x1004
Step 4: Increment pointer to 0x1008
...and so on

```

**For loops and branches (if statements):**

- The instruction pointer can JUMP to different addresses
- This is how `if`, `while`, `for` work at the hardware level

**Java:**

```java
if (x > 5) {
    doSomething();
}

```

**C#:**

```csharp
if (x > 5) {
    DoSomething();
}

```

**JavaScript:**

```jsx
if (x > 5) {
    doSomething();
}

```

Becomes (assembly):

```
CMP x, 5          ; Compare x to 5
JLE skip          ; Jump if Less or Equal
CALL do_something ; Call the function
skip:

```

---

## Pipelining: Doing Multiple Steps at Once

**Modern CPUs don't wait for one instruction to finish before starting the next.**

**Without pipelining (slow):**

```
Instruction 1: [FETCH][DECODE][EXECUTE][STORE]
Instruction 2:                                  [FETCH][DECODE][EXECUTE][STORE]
Instruction 3:                                                                    [FETCH][DECODE][EXECUTE][STORE]

```

**With pipelining (fast):**

```
Instruction 1: [FETCH][DECODE][EXECUTE][STORE]
Instruction 2:        [FETCH][DECODE][EXECUTE][STORE]
Instruction 3:               [FETCH][DECODE][EXECUTE][STORE]
Instruction 4:                      [FETCH][DECODE][EXECUTE][STORE]

```

**Result:** Multiple instructions are in different stages simultaneously, making execution much faster.

**Think of it like an assembly line:**

- Station 1: Fetch parts
- Station 2: Assemble
- Station 3: Paint
- Station 4: Package

All stations work simultaneously on different products!

---

## Why Some Operations Are Faster Than Others

**Different operations take different amounts of time:**

**Fast operations (1-2 cycles):**

- Addition, subtraction
- Logical operations (AND, OR, NOT)
- Moving data between registers

**Medium operations (3-10 cycles):**

- Multiplication
- Bit shifting

**Slow operations (20-100+ cycles):**

- Division
- Square root
- Trigonometric functions (sin, cos, etc.)

**Very slow operations (100+ cycles):**

- Memory access (if not in cache)
- Function calls (overhead of saving/restoring state)

**Practical impact:**

**Java:**

```java
// Fast
int result = a + b;

// Slower
int result = a / b;

// Much slower
double result = Math.sqrt(a);

```

**C#:**

```csharp
// Fast
int result = a + b;

// Slower
int result = a / b;

// Much slower
double result = Math.Sqrt(a);

```

**JavaScript:**

```jsx
// Fast
const result = a + b;

// Slower
const result = a / b;

// Much slower
const result = Math.sqrt(a);

```

⚠️ **But for most code, this doesn't matter!** Compilers optimize, and your bottlenecks are usually elsewhere (I/O, database, network).

---

## What Happens When You Run a Program

**Complete flow from double-clicking an app to execution:**

**1. You run the program**

- Java: `java MyApp` or double-click JAR file
- C#: `dotnet run` or double-click EXE file
- Node.js: `node app.js`

**2. Operating system loads the program**

- OS loads the program file from SSD into RAM
- OS creates a process (we'll learn about this in OS section)
- OS allocates memory for the program

**3. CPU starts executing**

- Instruction pointer is set to the program's starting address
- CPU begins fetch-decode-execute cycle

**4. Program runs**

- CPU executes instructions one by one (billions per second)
- Loads data from RAM when needed
- Uses registers for active calculations
- Stores results back to RAM

**5. Program interacts with user**

- Input events (keyboard, mouse) generate interrupts
- CPU temporarily stops current execution
- Handles the input
- Returns to program execution

**6. Program finishes**

- CPU executes final instructions
- OS cleans up memory
- Process terminates

---

## Key Takeaways - Section 4: How CPU Executes Code

### The Basic Cycle

✅ **Every instruction follows: Fetch → Decode → Execute → Store**

✅ **Instruction pointer keeps track of what to execute next**

✅ **Your code (Java, C#, JavaScript) is translated into many simple CPU instructions**

✅ **One line of code = multiple CPU instructions**

### Performance Insights

✅ **Modern CPUs use pipelining** to execute multiple instructions simultaneously

✅ **Different operations have different speeds** (addition is fast, division is slow)

✅ **But for most applications, computation speed doesn't matter** - I/O is the bottleneck

### What Matters for Developers

✅ **Understand that code becomes machine instructions** - helps you reason about what's happening

✅ **Don't micro-optimize operations** (compiler does this better than you)

✅ **Focus on algorithm choice and I/O efficiency** - these matter 100x more

### Moving Forward

- Next: Clock speed and what GHz actually means
- Then: CPU-bound vs I/O-bound (critical for identifying bottlenecks)
- Finally: Operating Systems concepts (processes, threads, scheduling)

---

**This is the conceptual understanding you need. No need to think about instruction-level details in daily development!**
