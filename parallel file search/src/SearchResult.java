import java.util.ArrayList;
import java.util.List;

/**
 * Holds the result of searching one file.
 * Returned by FileSearchTask.call() via Future<SearchResult>.
 *
 * Uses a record (Java 16+) for LineMatch — a lightweight immutable data holder.
 */
public class SearchResult {

    // record = immutable class with auto-generated constructor, getters, equals, hashCode
    public record LineMatch(int lineNumber, String lineText) {}

    private final String fileName;
    private final List<LineMatch> lineMatches = new ArrayList<>();
    private String errorMessage = null;

    public SearchResult(String fileName) {
        this.fileName = fileName;
    }

    public void addMatch(int lineNumber, String lineText) {
        lineMatches.add(new LineMatch(lineNumber, lineText));
    }

    public void setError(String message) {
        this.errorMessage = message;
    }

    public String getFileName()            { return fileName; }
    public List<LineMatch> getLineMatches(){ return lineMatches; }
    public int getMatchCount()             { return lineMatches.size(); }
    public boolean hasMatches()            { return !lineMatches.isEmpty(); }
    public boolean isError()               { return errorMessage != null; }
    public String getErrorMessage()        { return errorMessage; }
}
