import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class TraceFinderTest {

    /**
     * Verifies that in-memory processing generates all five report sections.
     */
    @Test
    void analyzeLines_validInputs_generatesAllReportSections() {
        List<String> ruleLines = List.of(
            "level,severity_score",
            "INFO,1",
            "WARNING,5",
            "ERROR,10"
        );

        List<String> logLines = List.of(
            "2026-10-30 10:00:00|INFO|192.168.1.10|server|login",
            "2026-10-30 10:01:00|ERROR|192.168.1.20|server|failed_login"
        );

        String report = TraceFinder.analyzeLines(logLines, ruleLines);

        assertAll(
            () -> assertTrue(report.contains("--- Activity Summary ---")),
            () -> assertTrue(report.contains("--- Flagged Entries ---")),
            () -> assertTrue(report.contains("--- Suspicious Activity by IP ---")),
            () -> assertTrue(report.contains("--- Unknown Patterns ---")),
            () -> assertTrue(report.contains("--- Malformed Lines ---"))
        );
    }

    /**
     * Verifies that an empty log collection still produces a report.
     */
    @Test
    void analyzeLines_emptyLogLines_generatesReport() {
        List<String> ruleLines = List.of(
            "level,severity_score",
            "INFO,1",
            "WARNING,5",
            "ERROR,10"
        );

        String report = TraceFinder.analyzeLines(List.of(), ruleLines);

        assertAll(
            () -> assertTrue(report.contains("--- Activity Summary ---")),
            () -> assertTrue(report.contains("--- Flagged Entries ---")),
            () -> assertTrue(report.contains("--- Suspicious Activity by IP ---")),
            () -> assertTrue(report.contains("--- Unknown Patterns ---")),
            () -> assertTrue(report.contains("--- Malformed Lines ---"))
        );
    }

    /**
     * Verifies that an invalid severity score is rejected without file access.
     */
    @Test
    void analyzeLines_invalidSeverityScore_throwsException() {
        List<String> ruleLines = List.of(
            "level,severity_score",
            "INFO,not-a-number"
        );

        assertThrows(
            NumberFormatException.class,
            () -> TraceFinder.analyzeLines(List.of(), ruleLines)
        );
    }
}