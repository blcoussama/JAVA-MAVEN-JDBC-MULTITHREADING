# JAVA DEVELOPMENT - COMPLETE GUIDE

## 1. 📦 CORE COMPONENTS YOU NEED FOR JAVA

### A. JDK (Java Development Kit) - The Foundation

**What is it?**

- The **complete toolkit** for developing Java applications
- Includes everything: compiler, runtime, libraries, tools

**What's inside a JDK?**

```jsx
JDK (Java Development Kit)
├── JRE (Java Runtime Environment)
│   ├── JVM (Java Virtual Machine) - Runs your code
│   └── Core Libraries - Standard Java libraries
├── javac - Java compiler (turns .java → .class files)
├── jar - Package tool (creates .jar files)
└── Development Tools - Debugger, documentation, etc.
```

**Key Terms:**

- **JVM** (Java Virtual Machine) - The "engine" that runs Java bytecode
- **JRE** (Java Runtime Environment) - JVM + libraries (just for running, not developing)
- **JDK** (Java Development Kit) - JRE + compiler + tools (for development)

**For development, you ONLY need JDK** (it includes everything).

### B. Build Tools (Package Managers)

These automate compiling, dependency management, testing, and packaging.

**Maven (Most Common)**

- XML-based configuration (`pom.xml`)
- Handles dependencies automatically
- Downloads libraries from Maven Central Repository
- Standard folder structure

**Gradle (Modern Alternative)**

- Script-based (Groovy or Kotlin DSL)
- Faster builds than Maven
- More flexible
- Used by Android development

**You need ONE of these** for modern Java projects. Most tutorials use **Maven**.

### C. IDE or Code Editor

**Full IDEs:**

- **IntelliJ IDEA** (Most popular, powerful)
- **Eclipse** (Free, older)
- **VS Code** (Lightweight, with Java extensions)

**You already have VS Code**, which is great for learning!

## 2. 🖥️ JAVA ON DIFFERENT OPERATING SYSTEMS

**The Beautiful Thing About Java:**

**"Write Once, Run Anywhere"** - Java code is the same on ALL operating systems!

```jsx
Your Java Code (.java file)
         ↓
   Java Compiler (javac)
         ↓
   Bytecode (.class file)  ← Platform independent!
         ↓
   JVM (different for each OS)
         ↓
   Runs on: Windows / Linux / macOS
```

The **bytecode** is identical on all platforms. Only the **JVM** is different for each OS.

### A. 🪟 WINDOWS (Native)

**What you need:**

```bash
# 1. Install JDK
- Download from: https://adoptium.net/ or Oracle
- Run the .exe installer
- Sets PATH automatically (usually)

# 2. Install Maven (optional but recommended)
- Download from: https://maven.apache.org/
- Extract to C:\Program Files\Maven
- Add to PATH manually

# 3. Install IDE
- IntelliJ IDEA or VS Code
```

**Key Locations:**

- JDK: `C:\Program Files\Java\jdk-17`
- Maven: `C:\Program Files\Maven`
- Maven local repo: `C:\Users\YourName\.m2\repository`

**Environment Variables:**

- `JAVA_HOME` = `C:\Program Files\Java\jdk-17`
- `PATH` includes `%JAVA_HOME%\bin`

**Verify Installation:**

```bash
java -version
javac -version
mvn -version
```

### B. 🐧 LINUX (Ubuntu/WSL 2)

**What you need:**

```bash
# 1. Update package list
sudo apt update

# 2. Install JDK (OpenJDK is the standard)
sudo apt install openjdk-21-jdk -y

# 3. Install Maven
sudo apt install maven -y

# 4. Configure JAVA_HOME (permanent)
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# 4. Verify everything
java -version       # Runtime
javac -version      # Compiler
mvn -version        # Maven + looks for JAVA_HOME
echo $JAVA_HOME     # Defined Variable

# 4. Verify
java -version
javac -version
mvn -version
```

**Key Locations:**

- JDK: `/usr/lib/jvm/java-17-openjdk-amd64`
- Maven: `/usr/share/maven`
- Maven local repo: `~/.m2/repository`

**Environment Variables:**

Usually set automatically, but you can add to `~/.bashrc`:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

### C. 🍎 macOS

**What you need:**

```bash
# Option 1: Using Homebrew (Recommended)
brew install openjdk@21
brew install maven

# Option 2: Download from Adoptium
# Similar to Windows, but .dmg installer

# Verify
java -version
javac -version
mvn -version
```

**Key Locations:**

- JDK (Homebrew): `/opt/homebrew/opt/openjdk@17`
- Maven local repo: `~/.m2/repository`

**Environment Variables:** (Add to `~/.zshrc` or `~/.bash_profile`)

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
export PATH=$JAVA_HOME/bin:$PATH
```

---

## 3. 🪟+🐧 YOUR SPECIFIC SETUP: WINDOWS + WSL 2 (Ubuntu)

This is where it gets interesting!

### You Have TWO Separate Environments

```jsx
┌─────────────────────────────────────┐
│       Windows (Host)                │
│  - Can install Java here            │
│  - Docker Desktop runs here         │
│  - IntelliJ/VS Code installed here  │
└─────────────────┬───────────────────┘
                  │
                  │ WSL 2
                  ↓
┌─────────────────────────────────────┐
│       Ubuntu (WSL 2)                │
│  - Separate Java installation       │
│  - Your project files               │
│  - Maven here                       │
└─────────────────────────────────────┘
```

### Two Approaches

**🔴 Approach 1: Java ONLY in WSL 2 (Recommended for Learning)**

**Why?**

- Closer to production Linux environments
- Learn Linux commands
- Better for DevOps/Docker workflows
- Cleaner separation

**Setup:**

```bash
# In WSL 2 Ubuntu:
sudo apt update
sudo apt install openjdk-21-jdk maven -y

# Verify
java -version
javac -version
mvn -version
```

**Your workflow:**

1. Open VS Code on Windows
2. Connect to WSL 2 (using "Remote - WSL" extension)
3. Open your project folder from WSL
4. Code in VS Code (Windows) → Runs in WSL (Ubuntu)
5. Terminal in VS Code uses WSL bash

**Where your files are:**

- Project: `/home/blinuxoussama/DEV-Environement/java-dev/`
- Maven cache: `/home/blinuxoussama/.m2/`

---

### 🟢 Approach 2: Java in BOTH Windows AND WSL 2

**Why?**

- Flexibility to use native Windows tools
- Better performance for some IDEs
- Can use Windows-specific tools

**Setup:**

**On Windows:**

- Download JDK from Adoptium
- Install normally
- Use Windows PATH

**On WSL 2:**

- Same as Approach 1

**Your workflow:**

- Use Windows Java for Windows IDEs (IntelliJ)
- Use WSL Java when working in terminal/WSL

**Drawback:**

- More confusing (which Java are you using?)
- Takes more disk space
- Need to manage two installations

---

## 4. 📦 WHAT GETS INSTALLED & WHERE

### When You Install JDK

**Core Components:**

```jsx
/usr/lib/jvm/java-21-openjdk-amd64/  (Linux)
├── bin/
│   ├── java      ← Run Java programs
│   ├── javac     ← Compile Java code
│   ├── jar       ← Create JAR files
│   ├── jdb       ← Debugger
│   └── javadoc   ← Generate documentation
├── lib/
│   └── [Core Java libraries]
└── include/
    └── [Header files for JNI]
```

### When You Install Maven

**Maven Files:**

```jsx
/usr/share/maven/  (Linux installation)

~/.m2/  (Your user directory)
├── repository/     ← Downloaded dependencies cached here
│   ├── org/
│   ├── com/
│   └── ... (thousands of JAR files)
└── settings.xml    ← Maven configuration (optional)
```

**What Maven Does:**

1. Reads your `pom.xml` (project descriptor)
2. Downloads required libraries from Maven Central
3. Caches them in `~/.m2/repository/`
4. Compiles your code
5. Runs tests
6. Packages into JAR/WAR files

## 5. 🎯 FOR YOUR WINDOWS + WSL 2 SETUP - MY RECOMMENDATION

**Install Java ONLY in WSL 2:**

```bash
# 1. Install JDK 21 (LTS version - stable & modern)
sudo apt update
sudo apt install openjdk-21-jdk -y

# 2. Install Maven
sudo apt install maven -y

# 3. Verify everything
java -version    # Should show: openjdk version "17.x.x"
javac -version   # Should show: javac 17.x.x
mvn -version     # Should show Maven version

# 4. Check JAVA_HOME (should be set automatically)
echo $JAVA_HOME

# If empty, add to ~/.bashrc:
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

**Set Up VS Code:**

```bash
# Install VS Code extensions (in VS Code):
# 1. "Remote - WSL" (Microsoft)
# 2. "Extension Pack for Java" (Microsoft)
# 3. "Spring Boot Extension Pack" (VMware) - for Spring projects
```

**Your Development Flow:**

1. **Open WSL Terminal in VS Code** (`Ctrl + Shift + P` → "WSL: Connect to WSL")
2. **Navigate to your project:**

    ```bash
    cd ~/DEV-Environement/java-dev/crud-job-application-spring-boot-rest-api-microservices
    ```

3. **Open in VS Code:**

    ```bash
    code .
    ```

4. **Work normally** - VS Code on Windows, code runs in WSL

## 6. 📝 QUICK START - VERIFY YOUR SETUP

Run these commands in WSL to make sure everything works:

```bash
# Check Java
java -version

# Check compiler
javac -version

# Check Maven
mvn -version

# Create a test Java file
mkdir -p ~/test-java && cd ~/test-java
cat > Hello.java << 'EOF'
public class Hello {
    public static void main(String[] args) {
        System.out.println("Java works on WSL 2!");
    }
}
EOF

# Compile it
javac Hello.java

# Run it
java Hello

# Should print: "Java works on WSL 2!"
```

## 7. 🔧 COMMON PACKAGES YOU'LL NEED

**Already Installed with JDK:**

- ✅ Java compiler (javac)
- ✅ Java runtime (java)
- ✅ JAR packager
- ✅ Core libraries

**Install Separately:**

- ✅ Maven (build tool) - `sudo apt install maven`
- ✅ Git - `sudo apt install git`
- ⚠️ Docker - Already installed on Windows (Docker Desktop integrates with WSL 2)

**Optional but Useful:**

```bash
# MySQL client (if working with MySQL)
sudo apt install mysql-client

# PostgreSQL client
sudo apt install postgresql-client

# HTTP client for API testing
sudo apt install curl

# Network tools
sudo apt install net-tools
```

## 8. 📚 SUMMARY - WHAT YOU NEED

**Minimum for Java Development:**

1. ✅ **JDK** (Java 17 / 21 recommended)
2. ✅ **Maven or Gradle**
3. ✅ **Text Editor/IDE** (VS Code with Java extensions)

**For Your WSL 2 Setup:**

```bash
# Install everything you need:
sudo apt update
sudo apt install openjdk-21-jdk maven -y

# Then add JAVA_HOME

echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```
