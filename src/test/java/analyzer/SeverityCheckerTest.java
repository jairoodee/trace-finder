package analyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;

import model.LogEntry;
import reader.RuleBookReader;

class SeverityCheckerTest {

    /**
     * Verifies that a known log level returns the severity score
     * configured for that level in the rulebook.
     */
    @Test
    void checkLogEntry_knownLevel_returnsConfiguredScore() throws Exception {
        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readRule("ERROR,4");

        SeverityChecker severityChecker = new SeverityChecker();

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T08:32:10"),
            "ERROR",
            "10.0.0.1",
            "server01",
            "LOGIN"
        );

        assertEquals(4, severityChecker.checkLogEntry(ruleBookReader, logEntry));
    }

    /**
     * Verifies that a log level absent from the rulebook returns
     * a default severity score of zero.
     */
    @Test
    void checkLogEntry_unknownLevel_returnsZero() throws Exception {
        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readRule("ERROR,4");

        SeverityChecker severityChecker = new SeverityChecker();

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T08:32:10"),
            "ALERT",
            "10.0.0.1",
            "server01",
            "LOGIN"
        );

        assertEquals(0, severityChecker.checkLogEntry(ruleBookReader, logEntry));
    }

    /**
     * Verifies that isKnownLevel returns true when the log entry's
     * level matches a rule in the rulebook.
     */
    @Test
    void isKnownLevel_knownLevel_returnsTrue() throws Exception {
        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readRule("ERROR,4");

        SeverityChecker severityChecker = new SeverityChecker();

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T08:32:10"),
            "ERROR",
            "10.0.0.1",
            "server01",
            "LOGIN"
        );

        assertTrue(severityChecker.isKnownLevel(ruleBookReader, logEntry));
    }

    /**
     * Verifies that isKnownLevel returns false when the log entry's
     * level does not match any rule in the rulebook.
     */
    @Test
    void isKnownLevel_unknownLevel_returnsFalse() throws Exception {
        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readRule("ERROR,4");

        SeverityChecker severityChecker = new SeverityChecker();

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T08:32:10"),
            "INFO",
            "10.0.0.1",
            "server01",
            "LOGIN"
        );

        assertFalse(severityChecker.isKnownLevel(ruleBookReader, logEntry));
    }

    /**
     * Verifies that severity matching is case-sensitive.
     *
     * A log entry with a differently-cased level should not match
     * a rulebook entry, even when the letters are otherwise identical.
     */
    @Test
    void checkLogEntry_differentlyCasedLevel_returnsZero() throws Exception {
        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readRule("ERROR,4");

        SeverityChecker severityChecker = new SeverityChecker();

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T08:32:10"),
            "error",
            "10.0.0.1",
            "server01",
            "LOGIN"
        );

        assertEquals(0, severityChecker.checkLogEntry(ruleBookReader, logEntry));
    }

    /**
     * Verifies that a differently-cased log level is not recognized
     * as a known level because matching is case-sensitive.
     */
    @Test
    void isKnownLevel_differentlyCasedLevel_returnsFalse() throws Exception {
        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readRule("ERROR,4");

        SeverityChecker severityChecker = new SeverityChecker();

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T08:32:10"),
            "error",
            "10.0.0.1",
            "server01",
            "LOGIN"
        );

        assertFalse(severityChecker.isKnownLevel(ruleBookReader, logEntry));
    }
}