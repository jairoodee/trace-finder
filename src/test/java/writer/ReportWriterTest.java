package writer;

import model.LogEntry;
import model.MalformedLine;
import model.ParsedLogLine;
import model.UnknownPattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reader.RuleBookReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportWriterTest {

    @TempDir
    Path tempDir;

    /**
     * Verifies that the report contains all five sections when no data is provided.
     */
    @Test
    void writeReport_emptyData_includesAllFiveSections() throws IOException {
        Path reportFile = tempDir.resolve("report.txt");

        List<ParsedLogLine> logEntries = List.of();
        List<MalformedLine> malformedLines = List.of();
        List<LogEntry> flaggedEntries = List.of();
        List<UnknownPattern> unknownPatterns = List.of();

        RuleBookReader ruleBookReader = new RuleBookReader();
        ReportWriter reportWriter = new ReportWriter();

        reportWriter.writeReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader,
            reportFile.toString()
        );

        String report = Files.readString(reportFile);
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

        Path reportFile = tempDir.resolve("level-counts-report.txt");

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

        reportWriter.writeReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader,
            reportFile.toString()
        );

        String report = Files.readString(reportFile);

        assertTrue(report.contains("INFO: 2"));
        assertTrue(report.contains("ERROR: 1"));
    }

    /**
     * Verifies that flagged entries include their timestamp, level, and source IP.
     */
    @Test
    void writeReport_flaggedEntries_includesEntryDetails() throws IOException {
        Path reportFile = tempDir.resolve("report.txt");

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

        reportWriter.writeReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader,
            reportFile.toString()
        );

        String report = Files.readString(reportFile);

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
        Path reportFile = tempDir.resolve("report.txt");

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

        reportWriter.writeReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader,
            reportFile.toString()
        );

        String report = Files.readString(reportFile);

        assertTrue(report.contains("10.0.0.5: 2 entries"));
        assertFalse(report.contains("10.0.0.8: 1 entries"));
    }

    /**
     * Verifies that unknown patterns retain their original line numbers and content.
     */
    @Test
    void writeReport_unknownPatterns_includesLineNumberAndContent()
        throws IOException {

        Path reportFile = tempDir.resolve("report.txt");

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

        reportWriter.writeReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader,
            reportFile.toString()
        );

        String report = Files.readString(reportFile);

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

        Path reportFile = tempDir.resolve("report.txt");

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

        reportWriter.writeReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader,
            reportFile.toString()
        );

        String report = Files.readString(reportFile);

        assertTrue(report.contains("Line 12:"));
        assertTrue(report.contains("INVALID LOG ENTRY WITHOUT REQUIRED FIELDS"));
    }
}