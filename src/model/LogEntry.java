package model;

import java.time.LocalDateTime;

/**
 * Represents a single parsed log entry.
 *
 * <p>Each log entry contains a timestamp, severity level, source IP address,
 * target, and action. The class provides a constructor for initializing
 * these values and getter methods for accessing them.</p>
 */
public class LogEntry {

    private LocalDateTime timestamp;
    private String level;
    private String sourceIP;
    private String target;
    private String action;

    /**
     * Creates a log entry with the supplied details.
     *
     * @param timestamp the date and time when the event occurred
     * @param level the severity level associated with the event
     * @param sourceIP the IP address from which the event originated
     * @param target the target or destination involved in the event
     * @param action the action performed or attempted during the event
     */
    public LogEntry(LocalDateTime timestamp, String level, String sourceIP, String target, String action) {
        this.timestamp = timestamp;
        this.level = level;
        this.sourceIP = sourceIP;
        this.target = target;
        this.action = action;
    }

    /**
     * Returns the timestamp associated with this log entry.
     *
     * @return the event timestamp
     */
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    /**
     * Returns the severity level of this log entry.
     *
     * @return the log entry's severity level
     */
    public String getLevel() {
        return this.level;
    }

    /**
     * Returns the source IP address associated with this log entry.
     *
     * @return the source IP address
     */
    public String getSourceIP() {
        return this.sourceIP;
    }

    /**
     * Returns the target involved in this log entry.
     *
     * @return the target or destination
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * Returns the action recorded in this log entry.
     *
     * @return the recorded action
     */
    public String getAction() {
        return this.action;
    }
}