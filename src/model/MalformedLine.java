package model;

/**
 * Represents a log line that could not be parsed successfully.
 *
 * <p>Stores the original line number and content so that malformed
 * entries can be identified and included in the TraceFinder report.</p>
 */
public class MalformedLine {

    private int lineNumber;
    private String content;

    /**
     * Creates a record of a malformed log line.
     *
     * @param lineNumber the original line number in the log file
     * @param content the original content of the malformed line
     */
    public MalformedLine(int lineNumber, String content) {
        this.lineNumber = lineNumber;
        this.content = content;
    }

    /**
     * Returns the original line number of the malformed entry.
     *
     * @return the line number in the log file
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * Returns the original content of the malformed entry.
     *
     * @return the content of the malformed line
     */
    public String getContent() {
        return this.content;
    }
}