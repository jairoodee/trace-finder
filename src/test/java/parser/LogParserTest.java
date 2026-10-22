package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
            "2024-03-15T08:32:10 | INFO | 10.0.0.1 | server01 | LOGIN"
        );

        assertNotNull(parsedResult);
        assertEquals("INFO", parsedResult.getLevel());
        assertEquals("10.0.0.1", parsedResult.getSourceIP());
        assertEquals("server01", parsedResult.getTarget());
        assertEquals("LOGIN", parsedResult.getAction());
        assertEquals(
            LocalDateTime.parse("2024-03-15T08:32:10"),
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
            "2024-03-15T08:32:10 | WARN | 192.168.1.10 | server01"
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
}