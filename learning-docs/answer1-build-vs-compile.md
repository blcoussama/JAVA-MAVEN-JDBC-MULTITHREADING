# Build vs Compile - Understanding Java Project Lifecycle

---

## Quick Answer

| Term | What It Means | Scope |
|------|---------------|-------|
| **Compile** | `.java` → `.class` | ONE step in the process |
| **Build** | Full process: compile + test + package | ENTIRE process |

**Build INCLUDES compile** (compile is just one part of building)

---

## 1. Compile - One Specific Step

### **What it is:**
**Converting Java source code into bytecode**

### **The transformation:**
```
Input:  Livre.java          (Human-readable Java code)
   ↓
Process: javac Livre.java   (Java compiler)
   ↓
Output: Livre.class         (JVM bytecode - machine code for JVM)
```

### **Maven command:**
```bash
mvn compile
```

### **What happens:**
```bash
# Maven reads pom.xml dependencies
# Downloads missing JARs to ~/.m2/
# Runs javac on ALL .java files in src/main/java/
# Creates .class files in target/classes/

src/main/java/com/bibliotech/models/Livre.java
   ↓ javac
target/classes/com/bibliotech/models/Livre.class
```

### **Result:**
✅ `.class` files in `target/classes/`
❌ No JAR file yet
❌ Tests NOT run
❌ Nothing packaged

---

## 2. Build - The Complete Process

### **What it is:**
**The ENTIRE process from source code to deployable artifact**

### **The full pipeline:**
```
Source Code (.java)
   ↓ 1. Compile
Bytecode (.class)
   ↓ 2. Test
Tested Code
   ↓ 3. Package
Deployable Artifact (.jar)
   ↓ 4. (Optional) Install
Local Maven Repository (~/.m2/)
   ↓ 5. (Optional) Deploy
Remote Server / Maven Repository
```

### **Maven command:**
```bash
mvn package    # Build up to packaging
# OR
mvn install    # Build + install to local ~/.m2/
# OR
mvn clean install  # Clean first, then full build
```

### **What happens:**
```bash
# Phase 1: validate   (check pom.xml is valid)
# Phase 2: compile    (src/main/java → target/classes)
# Phase 3: test       (run tests)
# Phase 4: package    (create .jar file)
# Phase 5: verify     (run integration tests)
# Phase 6: install    (copy .jar to ~/.m2/)
```

### **Result:**
✅ `.class` files in `target/classes/`
✅ Tests executed
✅ JAR file created: `target/BiblioTech-1.0.jar`
✅ Ready to deploy

---

## 3. All Related Terms Explained

### **A. Clean**

**What it does:** Deletes the `target/` directory

```bash
mvn clean
```

**Use case:**
- Remove old compiled files
- Start fresh build
- Fix weird compilation errors

**What gets deleted:**
```bash
target/   # Everything inside deleted
```

---

### **B. Validate**

**What it does:** Checks if `pom.xml` is valid

```bash
mvn validate
```

**Checks:**
- XML syntax correct?
- Required fields present (groupId, artifactId, version)?
- Dependencies formatted correctly?

**You rarely run this manually** (Maven runs it automatically)

---

### **C. Compile**

**What it does:** Compiles production code only

```bash
mvn compile
```

**Input:**
```
src/main/java/**/*.java
src/main/resources/**/*
```

**Output:**
```
target/classes/**/*.class
target/classes/**/* (resources copied)
```

**Does NOT compile tests!**

---

### **D. Test-Compile**

**What it does:** Compiles test code

```bash
mvn test-compile
```

**Input:**
```
src/test/java/**/*.java
src/test/resources/**/*
```

**Output:**
```
target/test-classes/**/*.class
target/test-classes/**/* (test resources copied)
```

**Automatically runs after `compile` phase**

---

### **E. Test**

**What it does:** Runs unit tests

```bash
mvn test
```

**Process:**
1. Compiles production code (if not done)
2. Compiles test code (if not done)
3. Executes all `*Test.java` files using JUnit
4. Generates test reports in `target/surefire-reports/`

**If ANY test fails:**
```bash
[ERROR] Tests run: 10, Failures: 1, Errors: 0, Skipped: 0
[ERROR] BUILD FAILURE
# Maven STOPS! No JAR created.
```

**To skip tests (not recommended):**
```bash
mvn package -DskipTests   # Compiles tests but doesn't run them
mvn package -Dmaven.test.skip=true   # Doesn't even compile tests
```

---

### **F. Package**

**What it does:** Creates the final `.jar` or `.war` file

```bash
mvn package
```

**Process:**
1. Runs all previous phases (validate, compile, test)
2. Takes `target/classes/` contents
3. Bundles into a single `.jar` file
4. Adds `META-INF/MANIFEST.MF` with metadata

**Output:**
```bash
target/BiblioTech-1.0-SNAPSHOT.jar
```

**JAR structure:**
```
BiblioTech-1.0-SNAPSHOT.jar
├── META-INF/
│   └── MANIFEST.MF         (Main-Class: com.bibliotech.main.TestConnection)
├── com/bibliotech/
│   ├── models/Livre.class
│   ├── dao/LivreDAO.class
│   └── ...
└── database.properties     (from src/main/resources)
```

**Can run it:**
```bash
java -jar target/BiblioTech-1.0-SNAPSHOT.jar
```

---

### **G. Verify**

**What it does:** Runs integration tests (if configured)

```bash
mvn verify
```

**Difference from `test`:**
- `test`: Unit tests (fast, isolated)
- `verify`: Integration tests (slower, may need database, external services)

**Most projects don't use this unless they have integration tests**

---

### **H. Install**

**What it does:** Copies JAR to your local Maven repository

```bash
mvn install
```

**Process:**
1. Runs all phases up to `package`
2. Copies `.jar` to `~/.m2/repository/`

**Where it goes:**
```bash
~/.m2/repository/com/bibliotech/BiblioTech/1.0-SNAPSHOT/
├── BiblioTech-1.0-SNAPSHOT.jar       # Your JAR
├── BiblioTech-1.0-SNAPSHOT.pom       # Your pom.xml
└── maven-metadata-local.xml          # Metadata
```

**Why?**
So OTHER projects on your computer can use your project as a dependency:

```xml
<!-- In another project's pom.xml: -->
<dependency>
    <groupId>com.bibliotech</groupId>
    <artifactId>BiblioTech</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<!-- Maven finds it in ~/.m2/repository/ ✅ -->
```

---

### **I. Deploy**

**What it does:** Uploads JAR to a remote Maven repository

```bash
mvn deploy
```

**Where it goes:**
- Maven Central (for open-source libraries)
- Private company Maven repository (Nexus, Artifactory)
- Cloud storage (AWS S3, etc.)

**Configuration required in pom.xml:**
```xml
<distributionManagement>
    <repository>
        <id>company-releases</id>
        <url>https://maven.company.com/releases</url>
    </repository>
</distributionManagement>
```

**Why?**
Share your library with your team or the world!

**You don't use this for regular development** (only for publishing libraries)

---

## 4. Maven Build Lifecycle - Complete Phases

### **Default Lifecycle (Most Common):**

```bash
mvn <phase>   # Runs ALL phases up to <phase>
```

| Phase | What It Does | Runs Previous Phases? |
|-------|--------------|----------------------|
| `validate` | Check pom.xml is valid | No (first phase) |
| `compile` | Compile `src/main/java/` | Yes (validate) |
| `test` | Run unit tests | Yes (validate, compile) |
| `package` | Create `.jar` file | Yes (all above) |
| `verify` | Run integration tests | Yes (all above) |
| `install` | Copy to `~/.m2/` | Yes (all above) |
| `deploy` | Upload to remote repo | Yes (all above) |

### **Clean Lifecycle:**

```bash
mvn clean   # Deletes target/
```

### **Site Lifecycle:**

```bash
mvn site   # Generates project documentation website
```

---

## 5. Common Maven Commands - What They Actually Do

### **Development Workflow:**

```bash
# 1. Start fresh (delete old compiled files)
mvn clean

# 2. Compile and run tests
mvn test

# 3. Create JAR (if tests pass)
mvn package

# 4. Install to local repository (so other projects can use it)
mvn install

# 5. Most common: clean + install in one command
mvn clean install
```

---

### **What Each Command Does:**

#### **`mvn compile`**
```bash
Phases executed: validate, compile
Result: target/classes/ created
Tests: Not run
JAR: Not created
```

#### **`mvn test`**
```bash
Phases executed: validate, compile, test-compile, test
Result: target/classes/ + target/test-classes/
Tests: Executed ✅
JAR: Not created
```

#### **`mvn package`**
```bash
Phases executed: validate, compile, test-compile, test, package
Result: target/classes/ + target/BiblioTech-1.0-SNAPSHOT.jar
Tests: Executed ✅
JAR: Created ✅
~/.m2/: Not updated
```

#### **`mvn install`**
```bash
Phases executed: validate, compile, test, package, install
Result: target/BiblioTech-1.0-SNAPSHOT.jar
Tests: Executed ✅
JAR: Created ✅
~/.m2/: Updated ✅ (JAR copied)
```

#### **`mvn clean install`**
```bash
Phase 1: clean (delete target/)
Phase 2: validate, compile, test, package, install
Result: Fresh build + JAR in ~/.m2/
Most common command for full build ✅
```

#### **`mvn clean package -DskipTests`**
```bash
Phase 1: clean (delete target/)
Phase 2: validate, compile, package
Result: Fresh build + JAR created
Tests: SKIPPED ⚠️ (not recommended)
```

---

## 6. Visual Summary - Build vs Compile

### **Compile (One Step):**

```
.java files  →  [javac]  →  .class files
                  ↑
               COMPILE
```

### **Build (Complete Process):**

```
Source Code (.java)
    ↓
┌───────────────────────────┐
│  1. VALIDATE              │  (Check pom.xml)
│  2. COMPILE     ←─────┐   │  (.java → .class)
│  3. TEST              │   │  (Run tests)
│  4. PACKAGE           │   │  (.class → .jar)
│  5. INSTALL           │   │  (Copy to ~/.m2/)
│                       │   │
│  This is BUILD ───────┘   │
└───────────────────────────┘
    ↓
Final JAR (ready to deploy)
```

---

## 7. Real-World Examples

### **Example 1: Daily Development**

```bash
# You wrote new code
# You want to test it:

mvn test
# Compiles your code
# Compiles tests
# Runs tests
# Shows results
```

### **Example 2: Create Runnable JAR**

```bash
# You finished a feature
# You want to run it:

mvn package
# Compiles everything
# Runs tests
# Creates BiblioTech-1.0.jar

java -jar target/BiblioTech-1.0-SNAPSHOT.jar
# Runs your application ✅
```

### **Example 3: Share Library with Team**

```bash
# You created a utility library
# Your teammate needs it:

mvn install
# Builds everything
# Copies to ~/.m2/repository/

# Teammate's project pom.xml:
<dependency>
    <groupId>com.bibliotech</groupId>
    <artifactId>BiblioTech</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
# Maven finds it in ~/.m2/ ✅
```

### **Example 4: Clean Build (Fix Errors)**

```bash
# Weird compilation errors
# Old .class files causing issues

mvn clean install
# 1. Deletes target/
# 2. Fresh compilation
# 3. Runs tests
# 4. Creates new JAR
# 5. Installs to ~/.m2/
# Usually fixes the issue ✅
```

---

## 8. Key Takeaways

### **Compile vs Build:**

| Aspect | Compile | Build |
|--------|---------|-------|
| What | `.java` → `.class` | Entire process (compile + test + package) |
| Maven Command | `mvn compile` | `mvn package` or `mvn install` |
| Output | `target/classes/` | `target/BiblioTech-1.0.jar` |
| Tests Run? | ❌ No | ✅ Yes |
| Creates JAR? | ❌ No | ✅ Yes |
| When to Use | Quick syntax check | Production build |

### **Most Used Commands:**

```bash
mvn clean install   # Full build (most common)
mvn test           # Quick test
mvn package        # Create JAR
mvn clean          # Delete old files
```

### **Build = Compile + More:**

```
BUILD includes:
├── Compile
├── Test
├── Package
└── (Optional) Install/Deploy
```

---

## 9. What Happens Behind the Scenes

### **When you run `mvn package`:**

```bash
[INFO] --- maven-clean-plugin:3.1.0:clean (default-clean) @ BiblioTech ---
[INFO] Deleting target/

[INFO] --- maven-resources-plugin:3.2.0:resources (default-resources) @ BiblioTech ---
[INFO] Copying 2 resources to target/classes

[INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ BiblioTech ---
[INFO] Compiling 25 source files to target/classes

[INFO] --- maven-resources-plugin:3.2.0:testResources (default-testResources) @ BiblioTech ---
[INFO] Copying 1 resource to target/test-classes

[INFO] --- maven-compiler-plugin:3.11.0:testCompile (default-testCompile) @ BiblioTech ---
[INFO] Compiling 8 test source files to target/test-classes

[INFO] --- maven-surefire-plugin:2.22.2:test (default-test) @ BiblioTech ---
[INFO] Running com.bibliotech.dao.LivreDAOTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0

[INFO] --- maven-jar-plugin:3.3.0:jar (default-jar) @ BiblioTech ---
[INFO] Building jar: target/BiblioTech-1.0-SNAPSHOT.jar

[INFO] BUILD SUCCESS
```

**Each line = a plugin executing a phase!**

---

## Summary

**Compile:** `.java` → `.class` (ONE step)
**Build:** The COMPLETE process from source to deployable JAR

**Build INCLUDES compile** as one of its steps.

**In daily work:**
- `mvn test` → Quick feedback during development
- `mvn package` → Create runnable JAR
- `mvn clean install` → Full fresh build

**Remember:** Every Maven phase runs ALL previous phases automatically!

---

**Next:** Understanding what happens when you run the JAR (JVM, OS, Processes, Threads, CPU execution)! 🚀
