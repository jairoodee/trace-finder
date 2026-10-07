```java
package reader;

// IOException is used when a file-reading operation fails.
import java.io.IOException;

// Files provides methods for reading file contents.
import java.nio.file.Files;

// Path represents the location of a file.
import java.nio.file.Path;

// List is the interface used to represent a collection of items.
import java.util.List;

// ArrayList is a resizable list that grows as items are added.
import java.util.ArrayList;

// Collections provides utility methods, including unmodifiableList().
import java.util.Collections;

// MalformedLine represents a log line that could not be parsed.
import model.MalformedLine;

// LogParser is responsible for converting a raw line into a LogEntry.
import parser.LogParser;

// LogEntry represents a successfully parsed log record.
import model.LogEntry;

public class LogFileReader {

    // Stores all successfully parsed log entries.
    // The ArrayList can grow as more valid entries are discovered.
    private List<LogEntry> logEntries = new ArrayList<>();

    // Stores malformed lines, preserving their line numbers and original content.
    private List<MalformedLine> malformedLines = new ArrayList<>();

    // Returns an unmodifiable view of the valid log entries.
    // Callers can inspect the list but cannot modify it through this reference.
    public List<LogEntry> getLogEntries() {
        List<LogEntry> readOnlyLogEntries =
                Collections.unmodifiableList(this.logEntries);

        return readOnlyLogEntries;
    }

    // Returns an unmodifiable view of the malformed lines.
    // The original collection remains private to this class.
    public List<MalformedLine> getMalformedLines() {
        List<MalformedLine> readOnlyMalformedLines =
                Collections.unmodifiableList(this.malformedLines);

        return readOnlyMalformedLines;
    }

    // Reads the log file at the supplied file path.
    // IOException is declared so the calling code can handle file-reading failures.
    public void readFile(String FilePath) throws IOException {

        // Convert the supplied path string into a Path object.
        Path path = Path.of(FilePath);

        // Read all file lines into a list of strings.
        // Each element represents one line from the file.
        List<String> lines = Files.readAllLines(path);

        // Create one parser to process the lines in this file.
        LogParser logParser = new LogParser();

        // Process each line using its zero-based list index.
        for (int i = 0; i < lines.size(); i++) {

            // Convert the zero-based index into a human-readable line number.
            // The first line of the file is line 1, not line 0.
            int lineNumber = i + 1;

            // Retrieve the original content of the current line.
            String line = lines.get(i);

            // Ask LogParser to convert the raw line into a LogEntry.
            // A null result indicates that the parser rejected the line.
            LogEntry newLogEntry = logParser.parse(line);

            // If parsing failed, record the line as malformed.
            if (newLogEntry == null) {

                // Preserve the original line number and original line content.
                MalformedLine newMalformedLine =
                        new MalformedLine(lineNumber, line);

                // Add the malformed record to the appropriate collection.
                malformedLines.add(newMalformedLine);

            } else {

                // Parsing succeeded, so store the resulting LogEntry.
                logEntries.add(newLogEntry);
            }
        }
    }

    // Temporary test entry point for checking the reader's behavior.
    // The final application entry point will coordinate the other components.
    public static void main(String[] args) throws IOException {

        // Create a reader instance.
        LogFileReader thisFile = new LogFileReader();

        // Read and process the test log file.
        thisFile.readFile("test.txt");

        // Print the number of successfully parsed entries.
        System.out.println(thisFile.getLogEntries().size());

        // Check whether any malformed lines were recorded.
        if (thisFile.getMalformedLines().isEmpty()) {

            // Report when there are no malformed lines.
            System.out.println("No Malformed Lines Found");

        } else {

            // Print the original content of the first malformed line.
            System.out.println(
                    thisFile.getMalformedLines().get(0).getContent());

            // Print that line's original position in the file.
            System.out.println(
                    thisFile.getMalformedLines().get(0).getLineNumber());
        }
    }
}
