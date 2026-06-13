import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PROJECT 17: Parallel File Search Tool
 * ========================================
 * Searches for a keyword across many files simultaneously
 * using a thread pool (ExecutorService).
 *
 * Concepts covered:
 *  - ExecutorService and thread pools (Executors.newFixedThreadPool)
 *  - Callable<T> vs Runnable (returns a result)
 *  - Future<T> for collecting results
 *  - AtomicInteger for thread-safe counters
 *  - Files API for directory traversal
 */
public class ParallelFileSearch {

    public static void main(String[] args) throws Exception {

        // ── Configuration ──────────────────────────────────────────
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the directory to search in: ");
        String searchDirectory = scanner.nextLine();
        if(searchDirectory.isEmpty()){
            searchDirectory = "samplefiles";}

        System.out.print("Enter the keyword to search for: ");
        String keyword = scanner.nextLine();
        if(keyword.isEmpty()){
            keyword = "java";}
            
        int threadPoolSize = 5;
        scanner.close();
        // ───────────────────────────────────────────────────────────

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       PARALLEL FILE SEARCH TOOL          ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("  Directory : " + searchDirectory);
        System.out.println("  Keyword   : " + keyword);
        System.out.println("  Threads   : " + threadPoolSize);
        System.out.println();

        // Step 1: Collect all .txt files from the directory
        List<File> files = collectFiles(searchDirectory);
        if (files.isEmpty()) {
            System.out.println("No .txt files found in: " + searchDirectory);
            return;
        }
        System.out.println("Found " + files.size() + " file(s) to search.\n");

        // Step 2: Create a fixed thread pool
        // Instead of one thread per file, we reuse a pool of N threads
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        // Step 3: Submit a search task (Callable) for every file
        // Each Callable returns a SearchResult when done
        List<Future<SearchResult>> futures = new ArrayList<>();
        AtomicInteger filesSearched = new AtomicInteger(0);

        for (File file : files) {
            Callable<SearchResult> task = new FileSearchTask(file, keyword, filesSearched);
            Future<SearchResult> future = executor.submit(task); // non-blocking submit
            futures.add(future);
        }

        // Step 4: Collect results from all futures (blocks until each is done)
        List<SearchResult> matches = new ArrayList<>();
        List<SearchResult> errors  = new ArrayList<>();

        for (Future<SearchResult> future : futures) {
            SearchResult result = future.get(); // waits for that task to finish
            if (result.isError()) {
                errors.add(result);
            } else if (result.hasMatches()) {
                matches.add(result);
            }
        }

        // Step 5: Shutdown the thread pool (important — always do this!)
        executor.shutdown();

        // Step 6: Print results
        printResults(matches, errors, keyword, filesSearched.get());
    }

    /**
     * Recursively collects all .txt files under the given directory.
     */
    private static List<File> collectFiles(String dirPath) throws IOException {
        List<File> result = new ArrayList<>();
        Path startPath = Paths.get(dirPath);

        if (!Files.exists(startPath)) {
            System.out.println("Directory not found: " + dirPath);
            return result;
        }

        Files.walk(startPath)
             .filter(Files::isRegularFile)
             .filter(p -> p.toString().endsWith(".txt"))
             .forEach(p -> result.add(p.toFile()));

        return result;
    }

    /**
     * Prints a clean summary of all search results.
     */
    private static void printResults(List<SearchResult> matches,
                                     List<SearchResult> errors,
                                     String keyword,
                                     int totalSearched) {

        System.out.println("  RESULTS: \"" + keyword + "\" found in "
                + matches.size() + " of " + totalSearched + " file(s)");

        if (matches.isEmpty()) {
            System.out.println("  No matches found.");
        } else {
            for (SearchResult r : matches) {
                System.out.println(" -> " + r.getFileName()
                        + "  (" + r.getMatchCount() + " match(es))");
                for (SearchResult.LineMatch lm : r.getLineMatches()) {
                    System.out.printf("       Line %3d: %s%n", lm.lineNumber(), lm.lineText().trim());
                }
                System.out.println();
            }
        }

        if (!errors.isEmpty()) {
            System.out.println("  ⚠  Errors reading " + errors.size() + " file(s):");
            for (SearchResult e : errors) {
                System.out.println("     - " + e.getFileName() + ": " + e.getErrorMessage());
            }
        }
    }
}
