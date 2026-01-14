# From Code to Process: Java Edition

## 1. The Building Blocks 🧱

Before a program runs, it exists in two different forms on your disk:

- **Source Code (.java)**: These are the text files you write in your `src` folder. They are human-readable "recipes" that the Operating System (OS) cannot execute directly.
- **Compiled Bytecode (.class)**: Found in your `target/classes` folder, these are the translated instructions for the Java Virtual Machine (JVM).

## 2. The Packaging 📦

In professional projects, all those individual `.class` files are often zipped together into a **.jar** (Java Archive) file.

- **The Program**: The `.jar` file is the "Static Program" sitting on your disk. It represents your entire application bundled and ready to go.

## 3. The Big Transition: Program vs. Process ⚡

The most critical distinction in OS theory is when that file on disk starts moving:

| Concept | State | Location | Analogy |
| --- | --- | --- | --- |
| **Program** | Static / Passive | Hard Drive (Disk) | A cookbook on the shelf. |
| **Process** | Dynamic / Active | RAM (Memory) | Actually cooking the meal. |

## 4. What Happens When You Run? 🚀

When you trigger your code (e.g., clicking "Run" in your IDE):

1. **OS creates a Process**: It assigns a unique **Process ID (PID)**.
2. **Memory Allocation**: The OS sets aside a private "sandbox" in RAM for your app.
3. **Loading**: The instructions from your `.class` files are loaded into that memory space.
4. **Execution**: The CPU begins executing the instructions line-by-line.

## 5. Summary for Notion 📝

- **Code (`src`)** is for humans.
- **Bytecode (`target`)** is the "Program" for the computer.
- **Execution** turns that static "Program" into a living **Process**.

---

We've covered how a static program on your disk becomes an active process in memory. Ready to look at how that process manages its internal work using **Threads**, or should we dive deeper into how the OS handles **Context Switching** between different processes?

1. **Threads**: How one process can do multiple things at once.
2. **Context Switching**: How the OS juggles many processes on one CPU.
