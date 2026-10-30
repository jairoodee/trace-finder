package reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.LogEntry;
import model.MalformedLine;
import model.ParsedLogLine;
import parser.LogParser;

/**
 * Reads and processes log lines, separating successfully parsed entries
 * from malformed lines.
 *
 * Valid entries retain their original line numbers and raw content.
 * Malformed lines are stored separately with the same source information.
 */
public class LogFileReader {

    /**
     * Successfully parsed log entries with their original source information.
     */
    private final List<ParsedLogLine> logEntries = new ArrayList<>();

    /**
     * Malformed log lines with their original source information.
     */
    private final List<MalformedLine> malformedLines = new ArrayList<>();

    /**
     * Returns an unmodifiable view of the successfully parsed entries.
     *
     * @return an unmodifiable list of parsed log lines
     */
    public List<ParsedLogLine> getLogEntries() {
        return Collections.unmodifiableList(logEntries);
    }

    /**
     * Returns an unmodifiable view of the malformed lines.
     *
     * @return an unmodifiable list of malformed lines
     */
    public List<MalformedLine> getMalformedLines() {
        return Collections.unmodifiableList(malformedLines);
    }

    /**
     * Reads a log file and delegates line processing to {@link #readLines(List)}.
     *
     * Each file line is processed using its original one-based line number.
     * Existing results are retained, so repeated calls append to the current
     * collections.
     *
     * @param filePath the path to the log file
     * @throws IOException if the file cannot be read
     */
    public void readFile(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filePath));
        readLines(lines);
    }

    /**
     * Processes log lines supplied directly in memory.
     *
     * Each supplied line is assigned a one-based line number based on its
     * position in this list. Successfully parsed lines are stored as
     * {@link ParsedLogLine}; rejected lines are stored as {@link MalformedLine}.
     * This method performs no file input or output.
     *
     * Note that line numbering starts at 1 for each invocation, while results
     * are appended to any existing entries.
     *
     * @param lines the log lines to process
     */
    public void readLines(List<String> lines) {
        LogParser logParser = new LogParser();

        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;
            String line = lines.get(i);

            LogEntry newLogEntry = logParser.parse(line);

            if (newLogEntry == null) {
                malformedLines.add(new MalformedLine(lineNumber, line));
            } else {
                logEntries.add(new ParsedLogLine(newLogEntry, lineNumber, line));
            }
        }
    }
}