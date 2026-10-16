
package reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import model.LogEntry;
import model.MalformedLine;
import parser.LogParser;
import model.ParsedLogLine;

/**
 * Reads a log file and separates successfully parsed entries from
 * malformed lines.
 *
 * Valid entries are stored as ParsedLogLine objects so their original
 * line numbers and raw content remain available for later analysis.
 * Malformed lines are stored separately with their original source
 * information.
 */
public class LogFileReader {

    /**
     * Stores successfully parsed log entries together with their
     * original line numbers and raw content.
     */
    private List<ParsedLogLine> logEntries = new ArrayList<>();

    /**
     * Stores malformed lines with their original line numbers
     * and unmodified content.
     */
    private List<MalformedLine> malformedLines = new ArrayList<>();

    /**
     * Returns an unmodifiable view of the successfully parsed entries.
     *
     * The returned list allows callers to inspect the entries without
     * adding or removing items through this reference.
     *
     * @return an unmodifiable list of parsed log lines
     */
    public List<ParsedLogLine> getLogEntries() {
        List<ParsedLogLine> readOnlyLogEntries =
                Collections.unmodifiableList(this.logEntries);

        return readOnlyLogEntries;
    }

    /**
     * Returns an unmodifiable view of the malformed lines.
     *
     * Each malformed line retains its original line number and content
     * so the reporting component can identify its source.
     *
     * @return an unmodifiable list of malformed lines
     */
    public List<MalformedLine> getMalformedLines() {
        List<MalformedLine> readOnlyMalformedLines =
                Collections.unmodifiableList(this.malformedLines);

        return readOnlyMalformedLines;
    }

    /**
     * Reads and parses the contents of the file at the supplied path.
     *
     * Each line is assigned its original one-based line number.
     * Successfully parsed lines are stored as ParsedLogLine objects;
     * lines rejected by LogParser are recorded as MalformedLine objects.
     *
     * This method does not suppress file-reading errors. An IOException
     * is propagated to the calling code for handling.
     *
     * Note: calling this method repeatedly on the same instance appends
     * results to the existing collections.
     *
     * @param filePath the path to the log file
     * @throws IOException if the file cannot be read
     */
    public void readFile(String filePath) throws IOException {

        // Convert the supplied path string into a Path object.
        Path path = Path.of(filePath);

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

                // Preserve the parsed entry and its original source information.
                ParsedLogLine newParsedLogLine =
                        new ParsedLogLine(newLogEntry, lineNumber, line);

                // Add the parsed line to the valid-entry collection.
                logEntries.add(newParsedLogLine);
            }
        }
    }
}
