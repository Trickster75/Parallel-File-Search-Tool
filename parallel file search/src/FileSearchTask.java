import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Callable task that searches one file for a keyword.
 *
 * KEY DIFFERENCE from Runnable:
 *   Runnable  → run()  returns void  (can't send back a result)
 *   Callable  → call() returns T     (can return a result or throw an exception)
 *
 * This class returns a SearchResult object containing all matching lines.
 */
public class FileSearchTask implements Callable<SearchResult> {

    private final File file;
    private final String keyword;
    private final AtomicInteger globalCounter; // shared counter across all threads

    public FileSearchTask(File file, String keyword, AtomicInteger globalCounter) {
        this.file          = file;
        this.keyword       = keyword.toLowerCase();
        this.globalCounter = globalCounter;
    }

    /**
     * This is what each thread executes.
     * Reads the file line by line and collects matching lines.
     *
     * @return SearchResult with matches (or error info if something went wrong)
     */
    @Override
    public SearchResult call() {
        SearchResult result = new SearchResult(file.getName());
        String threadName = Thread.currentThread().getName();

        System.out.println("  [" + threadName + "] Searching: " + file.getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.toLowerCase().contains(keyword)) {
                    result.addMatch(lineNumber, line);
                }
            }

        } catch (IOException e) {
            result.setError("Could not read file: " + e.getMessage());
        }

        globalCounter.incrementAndGet(); // AtomicInteger is thread-safe (no synchronized needed)
        return result;
    }
}
