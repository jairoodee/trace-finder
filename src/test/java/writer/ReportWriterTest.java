package writer;

import model.LogEntry;
import model.MalformedLine;
import model.ParsedLogLine;
import model.UnknownPattern;
import org.junit.jupiter.api.Test;
import reader.RuleBookReader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportWriterTest {

    /**
     * Verifies that the report contains all five sections when no data is provided.
     */
    @Test
    void writeReport_emptyData_includesAllFiveSections() throws IOException {
        List<ParsedLogLine> logEntries = List.of();
        List<MalformedLine> malformedLines = List.of();
        List<LogEntry> flaggedEntries = List.of();
        List<UnknownPattern> unknownPatterns = List.of();

        RuleBookReader ruleBookReader = new RuleBookReader();
        ReportWriter reportWriter = new ReportWriter();

        String report = reportWriter.generateReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader
        );

        assertTrue(report.contains("=== Log Analysis Report ==="));
        assertTrue(report.contains("--- Activity Summary ---"));
        assertTrue(report.contains("--- Flagged Entries ---"));
        assertTrue(report.contains("--- Suspicious Activity by IP ---"));
        assertTrue(report.contains("--- Unknown Patterns ---"));
        assertTrue(report.contains("--- Malformed Lines ---"));
    }

    /**
     * Verifies that the report correctly counts entries for each configured log level.
     */
    @Test
    void writeReport_logEntries_countsEntriesByLevel()
        throws IOException, RuleBookReader.DuplicateException {
        
        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readRule("INFO,1");
        ruleBookReader.readRule("ERROR,3");

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T08:33:10"),
            "INFO",
            "10.0.0.1",
            "server01",
            "LOGIN"
        );

        LogEntry logEntry2 = new LogEntry(
            LocalDateTime.parse("2024-04-15T08:32:10"),
            "INFO",
            "10.0.0.7",
            "server02",
            "LOGOUT"
        );

        LogEntry logEntry3 = new LogEntry(
            LocalDateTime.parse("2024-07-15T08:32:10"),
            "ERROR",
            "10.0.0.4",
            "server03",
            "LOGIN"
        );

        ParsedLogLine parsedLog1 = new ParsedLogLine(
            logEntry,
            1,
            "2024-03-15T08:33:10 | INFO | 10.0.0.1 | server01 | LOGIN"
        );

        ParsedLogLine parsedLog2 = new ParsedLogLine(
            logEntry2,
            2,
            "2024-04-15T08:32:10 | INFO | 10.0.0.7 | server02 | LOGOUT"
        );

        ParsedLogLine parsedLog3 = new ParsedLogLine(
            logEntry3,
            3,
            "2024-07-15T08:32:10 | ERROR | 10.0.0.4 | server03 | LOGIN"
        );

        List<ParsedLogLine> logEntries =
            List.of(parsedLog1, parsedLog2, parsedLog3);
        List<MalformedLine> malformedLines = List.of();
        List<LogEntry> flaggedEntries = List.of();
        List<UnknownPattern> unknownPatterns = List.of();

        ReportWriter reportWriter = new ReportWriter();

        String report = reportWriter.generateReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader
        );

        assertTrue(report.contains("INFO: 2"));
        assertTrue(report.contains("ERROR: 1"));
    }

    /**
     * Verifies that flagged entries include their timestamp, level, and source IP.
     */
    @Test
    void writeReport_flaggedEntries_includesEntryDetails() throws IOException { 
        RuleBookReader ruleBookReader = new RuleBookReader();
        ReportWriter reportWriter = new ReportWriter();

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:15:00"),
            "ERROR",
            "10.0.0.5",
            "server03",
            "DELETE"
        );

        List<LogEntry> flaggedEntries = List.of(logEntry);
        List<ParsedLogLine> logEntries = List.of();
        List<MalformedLine> malformedLines = List.of();
        List<UnknownPattern> unknownPatterns = List.of();

        String report = reportWriter.generateReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader
        );

        assertTrue(report.contains("2024-03-15T09:15"));
        assertTrue(report.contains("ERROR"));
        assertTrue(report.contains("10.0.0.5"));
    }

    /**
     * Verifies that suspicious IP counts include all parsed entries from that IP,
     * including entries that are not flagged.
     */
    @Test
    void writeReport_suspiciousIP_countsAllEntriesFromIP() throws IOException {
        RuleBookReader ruleBookReader = new RuleBookReader();
        ReportWriter reportWriter = new ReportWriter();

        LogEntry logEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:15:00"),
            "ERROR",
            "10.0.0.5",
            "server03",
            "DELETE"
        );

        LogEntry logEntry2 = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:20:00"),
            "INFO",
            "10.0.0.5",
            "server03",
            "DELETE"
        );

        LogEntry logEntry3 = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:25:00"),
            "INFO",
            "10.0.0.8",
            "server03",
            "DELETE"
        );

        ParsedLogLine parsedLog1 = new ParsedLogLine(
            logEntry,
            1,
            "2024-03-15T09:15:00 | ERROR | 10.0.0.5 | server03 | DELETE"
        );

        ParsedLogLine parsedLog2 = new ParsedLogLine(
            logEntry2,
            2,
            "2024-03-15T09:20:00 | INFO | 10.0.0.5 | server03 | DELETE"
        );

        ParsedLogLine parsedLog3 = new ParsedLogLine(
            logEntry3,
            3,
            "2024-03-15T09:25:00 | INFO | 10.0.0.8 | server03 | DELETE"
        );

        List<LogEntry> flaggedEntries = List.of(logEntry);
        List<ParsedLogLine> logEntries =
            List.of(parsedLog1, parsedLog2, parsedLog3);
        List<MalformedLine> malformedLines = List.of();
        List<UnknownPattern> unknownPatterns = List.of();

        String report = reportWriter.generateReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader
        );

        assertTrue(report.contains("10.0.0.5: 2 entries"));
        assertFalse(report.contains("10.0.0.8: 1 entries"));
    }

    /**
     * Verifies that unknown patterns retain their original line numbers and content.
     */
    @Test
    void writeReport_unknownPatterns_includesLineNumberAndContent()
        throws IOException {

        RuleBookReader ruleBookReader = new RuleBookReader();
        ReportWriter reportWriter = new ReportWriter();

        UnknownPattern unknownPattern = new UnknownPattern(
            7,
            "2024-03-15T10:30:00 | NOTICE | 10.0.0.9 | server04 | RESTART"
        );

        List<LogEntry> flaggedEntries = List.of();
        List<ParsedLogLine> logEntries = List.of();
        List<MalformedLine> malformedLines = List.of();
        List<UnknownPattern> unknownPatterns = List.of(unknownPattern);

        String report = reportWriter.generateReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader
        );

        assertTrue(report.contains("Line 7:"));
        assertTrue(report.contains(
            "2024-03-15T10:30:00 | NOTICE | 10.0.0.9 | server04 | RESTART"
        ));
    }

    /**
     * Verifies that malformed lines retain their original line numbers and content.
     */
    @Test
    void writeReport_malformedLines_includesLineNumberAndContent()
        throws IOException {

        MalformedLine malformedLine = new MalformedLine(
            12,
            "INVALID LOG ENTRY WITHOUT REQUIRED FIELDS"
        );

        List<LogEntry> flaggedEntries = List.of();
        List<ParsedLogLine> logEntries = List.of();
        List<MalformedLine> malformedLines = List.of(malformedLine);
        List<UnknownPattern> unknownPatterns = List.of();

        RuleBookReader ruleBookReader = new RuleBookReader();
        ReportWriter reportWriter = new ReportWriter();

        String report = reportWriter.generateReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader
        );

        assertTrue(report.contains("Line 12:"));
        assertTrue(report.contains("INVALID LOG ENTRY WITHOUT REQUIRED FIELDS"));
    }

    /**
     * Verifies that flagged entries are listed in descending order of severity score.
     *
     * @throws IOException if writing or reading the generated report fails
     * @throws RuleBookReader.DuplicateException if a duplicate rule is configured
     */
    @Test
    void writeReport_flaggedEntries_sortsBySeverityScoreDescending()
        throws IOException, RuleBookReader.DuplicateException {

        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readRule("INFO,1");
        ruleBookReader.readRule("WARNING,3");
        ruleBookReader.readRule("ERROR,4");
        ruleBookReader.readRule("CRITICAL,5");

        // Deliberately supply entries in the wrong order.
        LogEntry warningEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:15:00"),
            "WARNING",
            "10.0.0.1",
            "server01",
            "LOGIN"
        );

        LogEntry criticalEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:20:00"),
            "CRITICAL",
            "10.0.0.2",
            "server02",
            "DELETE"
        );

        LogEntry errorEntry = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:25:00"),
            "ERROR",
            "10.0.0.3",
            "server03",
            "UPDATE"
        );

        List<LogEntry> flaggedEntries =
            List.of(warningEntry, criticalEntry, errorEntry);

        ReportWriter reportWriter = new ReportWriter();

        String report = reportWriter.generateReport(
            List.of(),
            List.of(),
            flaggedEntries,
            List.of(),
            ruleBookReader
        );

        // Isolate the flagged section so other occurrences of levels do not
        // affect the assertions.
        int flaggedStart = report.indexOf("--- Flagged Entries ---");
        int suspiciousStart =
            report.indexOf("--- Suspicious Activity by IP ---");

        String flaggedSection =
            report.substring(flaggedStart, suspiciousStart);

        int criticalPosition = flaggedSection.indexOf("CRITICAL");
        int errorPosition = flaggedSection.indexOf("ERROR");
        int warningPosition = flaggedSection.indexOf("WARNING");

        assertTrue(criticalPosition < errorPosition,
            "CRITICAL should appear before ERROR");

        assertTrue(errorPosition < warningPosition,
            "ERROR should appear before WARNING");
    }

        /**
     * Verifies that suspicious IP addresses are listed in descending order
     * of their total parsed entry counts.
     *
     * @throws IOException if writing or reading the generated report fails
     */
    @Test
    void writeReport_suspiciousIP_sortsByEntryCountDescending()
        throws IOException {

        RuleBookReader ruleBookReader = new RuleBookReader();
        ReportWriter reportWriter = new ReportWriter();

        // IP with the highest count: three entries.
        LogEntry highCount1 = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:15:00"),
            "ERROR", "10.0.0.1", "server01", "LOGIN"
        );

        LogEntry highCount2 = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:20:00"),
            "INFO", "10.0.0.1", "server01", "LOGOUT"
        );

        LogEntry highCount3 = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:25:00"),
            "INFO", "10.0.0.1", "server01", "LOGIN"
        );

        // IP with the lower count: two entries.
        LogEntry lowCount1 = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:30:00"),
            "ERROR", "10.0.0.2", "server02", "DELETE"
        );

        LogEntry lowCount2 = new LogEntry(
            LocalDateTime.parse("2024-03-15T09:35:00"),
            "INFO", "10.0.0.2", "server02", "LOGIN"
        );

        List<ParsedLogLine> logEntries = List.of(
            new ParsedLogLine(highCount1, 1, "high IP entry 1"),
            new ParsedLogLine(highCount2, 2, "high IP entry 2"),
            new ParsedLogLine(highCount3, 3, "high IP entry 3"),
            new ParsedLogLine(lowCount1, 4, "low IP entry 1"),
            new ParsedLogLine(lowCount2, 5, "low IP entry 2")
        );

        // Both IPs are suspicious because each has at least one flagged entry.
        List<LogEntry> flaggedEntries = List.of(lowCount1, highCount1);

        String report = reportWriter.generateReport(
            logEntries,
            List.of(),
            flaggedEntries,
            List.of(),
            ruleBookReader
        );

        // Isolate the suspicious activity section.
        int sectionStart =
            report.indexOf("--- Suspicious Activity by IP ---");
        int sectionEnd = report.indexOf("--- Unknown Patterns ---");

        String suspiciousSection =
            report.substring(sectionStart, sectionEnd);

        int highCountPosition =
            suspiciousSection.indexOf("10.0.0.1: 3 entries");
        int lowCountPosition =
            suspiciousSection.indexOf("10.0.0.2: 2 entries");

        assertTrue(highCountPosition >= 0,
            "The IP with three entries should be included");

        assertTrue(lowCountPosition >= 0,
            "The IP with two entries should be included");

        assertTrue(highCountPosition < lowCountPosition,
            "The IP with the higher entry count should appear first");
    }
}