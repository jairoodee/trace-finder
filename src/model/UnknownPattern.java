
package model;

/**
 * Represents a valid log entry whose pattern or level is unknown.
 *
 * Stores the original line number and raw content so the report
 * can identify where the unknown pattern appeared in the log file.
 */
public class UnknownPattern {

    /** The original, one-based line number in the log file. */
    private int lineNumber;

    /** The original, unmodified content of the log line. */
    private String content;

    /**
     * Creates an UnknownPattern using its original line information.
     *
     * @param lineNumber the original line number in the log file
     * @param content the original raw content of the log line
     */
    public UnknownPattern(int lineNumber, String content){
        this.lineNumber = lineNumber;
        this.content = content;
    }

    /**
     * Returns the original line number.
     *
     * @return the one-based line number in the log file
     */
    public int getLineNumber(){
        return this.lineNumber;
    }

    /**
     * Returns the original raw log line.
     *
     * @return the unmodified content of the log line
     */
    public String getContent(){
        return this.content;
    }
}
