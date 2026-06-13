# 🔍 Parallel File Search Tool

A command-line Java tool that searches for a keyword across multiple files **simultaneously** using a thread pool. Demonstrates real-world multithreading with `ExecutorService`, `Callable`, and `Future`.

---

## ✨ Features

- 🔎 Search for any keyword in any directory on your computer
- ⚡ Searches multiple files **concurrently** using a configurable thread pool
- 📁 Filter by file extension (`.java`, `.txt`, `.md`, etc.) or search **all files**
- 🛡️ Automatically detects and skips binary files (`.exe`, `.class`, images, etc.)
- 📊 Shows which line(s) matched, with line numbers
- 🧵 Displays which thread searched which file — great for visualizing concurrency

---

## 🧠 Concepts Covered

| Concept | Where |
|---|---|
| `ExecutorService` / thread pools | `Executors.newFixedThreadPool(n)` |
| `Callable<T>` vs `Runnable` | `FileSearchTask implements Callable<SearchResult>` |
| `Future<T>` | Collecting async results with `future.get()` |
| `AtomicInteger` | Thread-safe shared counter without `synchronized` |
| Java Records | `SearchResult.LineMatch` |
| `java.nio.file` API | Recursive directory traversal with `Files.walk()` |

---

## 📂 Project Structure

```
project17-file-search/
└── src/
    ├── ParallelFileSearch.java   # main() — entry point, coordinates the search
    ├── FileSearchTask.java       # Callable — searches one file, returns a SearchResult
    ├── SearchResult.java         # Data class — holds matches for one file
    └── samplefiles/              # sample .txt files for testing
        ├── algorithms.txt
        ├── databases.txt
        ├── intro_to_java.txt
        ├── networking.txt
        ├── oop_concepts.txt
        └── python_notes.txt
```

---

## ▶️ How to Run

```bash
cd project17-file-search/src
javac *.java
java ParallelFileSearch
```

You'll be prompted for:

```
Enter directory to search (default: samplefiles):
Enter keyword to search for (default: objects):
Enter number of threads to use (default: 4):
(e.g. ,java,md  |  leave empty for ALL files):
```

Press **Enter** on any prompt to use the default shown in brackets.

### Examples

**Search the bundled sample files for "java":**
```
samplefiles
java
4
```

**Search your whole Documents folder for "TODO" in source code:**
```
C:\Users\YourName\Documents
TODO
8
```

**Search everything (no extension filter):**
```
.
config
4

```
*(leave the extensions line empty to search ALL files)*

---

## 🖥️ Sample Output

```
╔══════════════════════════════════════════╗
║       PARALLEL FILE SEARCH TOOL          ║
╚══════════════════════════════════════════╝
  Directory : samplefiles
  Keyword   : java
  Threads   : 4

Found 6 file(s) to search.

  [pool-1-thread-1] Searching: intro_to_java.txt
  [pool-1-thread-2] Searching: python_notes.txt
  [pool-1-thread-3] Searching: databases.txt
  [pool-1-thread-4] Searching: networking.txt

  RESULTS: java found in 5 of 6 file(s)

  📄 intro_to_java.txt  (4 match(es))
       Line   1: Java is a high-level, class-based, object-oriented programming language.
       Line   3: Java applications are compiled to bytecode that runs on the Java Virtual Machine (JVM).
       ...
```

---

## 🔧 How It Works

```
main()
  │
  ├─ Collect matching files (filtered by extension)
  │
  ├─ For each file:
  │     create FileSearchTask(file, keyword, counter)
  │     submit to thread pool → returns Future<SearchResult>
  │
  ├─ Thread pool runs tasks concurrently
  │     each task reads its file and returns a SearchResult
  │
  └─ Collect all Future results → print matches / errors / skipped files
```

- **`FileSearchTask`** = the *action* — "go search this one file"
- **`SearchResult`** = the *outcome* — "here's what I found in that file"

---

## 🚀 Ideas for Extension

- [ ] Exclude folders like `node_modules`, `.git`, `target` for faster large-directory searches
- [ ] Support multiple keywords / regex search
- [ ] Benchmark: compare search time with 1 thread vs 4 vs 8

---

## 📋 Requirements

- Java 17 or higher (uses `record` types)
