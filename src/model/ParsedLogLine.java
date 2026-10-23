package model;

/**
 * Represents a successfully parsed log line together with its original
 * position and raw content in the source file.
 *
 * This wrapper preserves source information without adding those
 * responsibilities to the LogEntry model.
 */
public class ParsedLogLine {

    /** The parsed representation of the log line. */
    private LogEntry logEntry;

    /** The original one-based line number in the source file. */
    private int lineNumber;

    /** The original, unmodified content of the source line. */
    private String rawContent;

    /**
     * Creates a ParsedLogLine containing the parsed entry and its
     * original source information.
     *
     * @param logEntry the parsed log entry
     * @param lineNumber the original line number
     * @param rawContent the original raw line content
     */
    public ParsedLogLine(LogEntry logEntry,
                         int lineNumber,
                         String rawContent){
        this.logEntry = logEntry;
        this.lineNumber = lineNumber;
        this.rawContent = rawContent;
    }

    /**
     * Returns the parsed log entry.
     *
     * @return the LogEntry represented by this object
     */
    public LogEntry getLogEntry(){
        return this.logEntry;
    }

    /**
     * Returns the original line number.
     *
     * @return the one-based source line number
     */
    public int getLineNumber(){
        return this.lineNumber;
    }

    /**
     * Returns the original raw line content.
     *
     * @return the unmodified source line
     */
    public String getRawContent(){
        return this.rawContent;
    }
}
