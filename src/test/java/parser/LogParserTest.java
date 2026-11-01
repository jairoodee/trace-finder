package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.LogEntry;

class LogParserTest {

    /**
     * Verifies that a valid five-field log line produces a LogEntry
     * containing the expected timestamp, level, source IP, target,
     * and action.
     */
    @Test
    void parse_validLine_returnsLogEntry() {
        LogParser parser = new LogParser();

        LogEntry parsedResult = parser.parse(
            "2024-03-15 02:14:08 | INFO | 192.168.1.45 | /login | success"
        );

        assertNotNull(parsedResult);
        assertEquals("INFO", parsedResult.getLevel());
        assertEquals("192.168.1.45", parsedResult.getSourceIP());
        assertEquals("/login", parsedResult.getTarget());
        assertEquals("success", parsedResult.getAction());
        assertEquals(
            LocalDateTime.parse("2024-03-15T02:14:08"),
            parsedResult.getTimestamp()
        );
    }

    /**
     * Verifies that a log line with fewer than five fields
     * is rejected by returning null.
     */
    @Test
    void parse_missingField_returnsNull() {
        LogParser parser = new LogParser();

        LogEntry parsedResult = parser.parse(
            "2024-03-15 08:32:10 | WARN | 192.168.1.10 | server01"
        );

        assertNull(parsedResult);
    }

    /**
     * Verifies that a log line containing an invalid timestamp
     * is rejected by returning null.
     */
    @Test
    void parse_invalidTimestamp_returnsNull() {
        LogParser parser = new LogParser();

        LogEntry parsedResult = parser.parse(
            "not-a-timestamp | WARN | 192.168.1.10 | server01 | Login failed"
        );

        assertNull(parsedResult);
    }

    /**
     * Verifies that a blank log line is rejected by returning null.
     */
    @Test
    void parse_blankLine_returnsNull() {
        LogParser parser = new LogParser();

        assertNull(parser.parse(""));
    }

    /**
     * Verifies that a log line containing more than five fields
     * is rejected by returning null.
     */
    @Test
    void parse_extraField_returnsNull() {
        LogParser parser = new LogParser();

        LogEntry parsedResult = parser.parse(
            "2024-03-15 08:32:10 | WARN | 192.168.1.10 | server01 | Login failed | extra"
        );

        assertNull(parsedResult);
    }
}