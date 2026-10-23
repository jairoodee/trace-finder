package analyzer;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import model.LogEntry;
import reader.RuleBookReader;

/**
 * Unit tests for LogAnalyzer.
 *
 * Tests verify that low-severity entries are not flagged,
 * high-severity entries are flagged, and unknown levels are
 * recorded with their original line numbers and raw content.
 */
class LogAnalyzerTest {

    /**
     * Verifies that a known log entry with a severity score below 3
     * is not added to the flagged entries list.
     *
     * @throws RuleBookReader.DuplicateException if the rule cannot be registered
     */
    @Test
    void analyzeEntry_lowSeverityEntry_doesNotFlagEntry()
            throws RuleBookReader.DuplicateException {

        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readRule("INFO,1");

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T08:32:10"),
            "INFO",
            "10.0.0.1",
            "server01",
            "LOGIN"
        );

        SeverityChecker severityChecker = new SeverityChecker();
        LogAnalyzer logAnalyzer = new LogAnalyzer();

        logAnalyzer.analyzeEntry(
            severityChecker,
            logEntry,
            ruleBookReader,
            1,
            "2024-03-15T08:32:10 | INFO | 10.0.0.1 | server01 | LOGIN"
        );

        assertEquals(0, logAnalyzer.getFlaggedEntries().size());
    }

    /**
     * Verifies that a known log entry with a severity score of 3
     * is added to the flagged entries list.
     *
     * @throws RuleBookReader.DuplicateException if the rule cannot be registered
     */
    @Test
    void analyzeEntry_highSeverityEntry_flagsEntry()
            throws RuleBookReader.DuplicateException {

        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readRule("ERROR,3");

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:15:00"),
            "ERROR",
            "10.0.0.5",
            "server03",
            "DELETE"
        );

        SeverityChecker severityChecker = new SeverityChecker();
        LogAnalyzer logAnalyzer = new LogAnalyzer();

        logAnalyzer.analyzeEntry(
            severityChecker,
            logEntry,
            ruleBookReader,
            1,
            "2024-03-15T09:15:00 | ERROR | 10.0.0.5 | server03 | DELETE"
        );

        assertEquals(1, logAnalyzer.getFlaggedEntries().size());
    }

    /**
     * Verifies that an entry with an unknown level is recorded as an
     * unknown pattern, preserving its original line number and raw content,
     * without adding it to the flagged entries list.
     *
     * @throws RuleBookReader.DuplicateException if the rule cannot be registered
     */
    @Test
    void analyzeEntry_unknownLevel_recordsUnknownPattern()
            throws RuleBookReader.DuplicateException {

        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readRule("INFO,1");

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T10:20:00"),
            "NOTICE",
            "10.0.0.8",
            "server03",
            "ACCESS"
        );

        SeverityChecker severityChecker = new SeverityChecker();
        LogAnalyzer logAnalyzer = new LogAnalyzer();

        logAnalyzer.analyzeEntry(
            severityChecker,
            logEntry,
            ruleBookReader,
            3,
            "2024-03-15T10:20:00 | NOTICE | 10.0.0.8 | server03 | ACCESS"
        );

        assertEquals(1, logAnalyzer.getUnknownPatterns().size());
        assertEquals(3, logAnalyzer.getUnknownPatterns().get(0).getLineNumber());
        assertEquals(
            "2024-03-15T10:20:00 | NOTICE | 10.0.0.8 | server03 | ACCESS",
            logAnalyzer.getUnknownPatterns().get(0).getContent()
        );
        assertEquals(0, logAnalyzer.getFlaggedEntries().size());
    }
}