package parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import model.LogEntry;

/**
 * Parses raw log lines into structured log entries.
 *
 * <p>A valid log line must contain exactly five pipe-separated fields:
 * timestamp, level, source IP, target, and action. The timestamp must
 * be in a format accepted by {@link LocalDateTime#parse(CharSequence)}.</p>
 *
 * <p>Lines with an incorrect number of fields or an invalid timestamp
 * are rejected by returning {@code null}.</p>
 */
public class LogParser {

    /**
     * Parses a raw log line into a {@link LogEntry}.
     *
     * <p>Whitespace surrounding the pipe delimiters is ignored. If the
     * line does not contain exactly five fields or its timestamp cannot
     * be parsed, the method returns {@code null}.</p>
     *
     * @param line the raw log line to parse
     * @return a {@link LogEntry} containing the parsed fields, or
     *         {@code null} if the line is invalid
     */
    public LogEntry parse(String line) {
        String[] parts = line.split("\\s*\\|\\s*");

        if (parts.length != 5) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timestamp;
        
        try {
            timestamp = LocalDateTime.parse(parts[0], formatter);
        } catch (DateTimeParseException e) {
            return null;
        }

        LogEntry newLog = new LogEntry(timestamp, parts[1], parts[2], parts[3], parts[4]);
        return newLog;
    }
}