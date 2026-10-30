import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests TraceFinder's in-memory processing pipeline.
 *
 * <p>All test inputs are constructed in code. These tests do not launch
 * external processes or perform file-system operations.</p>
 */
class TraceFinderIT {

    /**
     * Verifies that valid in-memory inputs generate all five report sections.
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

        assertContainsAllReportSections(report);
    }

    /**
     * Verifies that an empty log collection still produces all report sections.
     */
    @Test
    void analyzeLines_emptyLogLines_generatesAllReportSections() {
        List<String> ruleLines = List.of(
            "level,severity_score",
            "INFO,1",
            "WARNING,5",
            "ERROR,10"
        );

        String report = TraceFinder.analyzeLines(List.of(), ruleLines);

        assertContainsAllReportSections(report);
    }

    /**
     * Verifies that malformed log lines are handled without preventing
     * report generation.
     */
    @Test
    void analyzeLines_malformedLogLine_generatesReport() {
        List<String> ruleLines = List.of(
            "level,severity_score",
            "INFO,1",
            "WARNING,5",
            "ERROR,10"
        );

        List<String> logLines = List.of(
            "2026-10-30 10:00:00|INFO|192.168.1.10|server|login",
            "this is not a valid log entry"
        );

        String report = TraceFinder.analyzeLines(logLines, ruleLines);

        assertAll(
            () -> assertContainsAllReportSections(report),
            () -> assertTrue(
                report.contains("this is not a valid log entry"),
                "Expected the malformed line to appear in the report"
            )
        );
    }

    /**
     * Verifies that an unconfigured log level is handled as an unknown pattern.
     */
    @Test
    void analyzeLines_unknownLogLevel_generatesReport() {
        List<String> ruleLines = List.of(
            "level,severity_score",
            "INFO,1",
            "WARNING,5",
            "ERROR,10"
        );

        List<String> logLines = List.of(
            "2026-10-30 10:00:00|DEBUG|192.168.1.10|server|diagnostic"
        );

        String report = TraceFinder.analyzeLines(logLines, ruleLines);

        assertAll(
            () -> assertContainsAllReportSections(report),
            () -> assertTrue(
                report.contains("DEBUG"),
                "Expected the unknown log level to appear in the report"
            )
        );
    }

    /**
     * Verifies that an invalid severity score is rejected.
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

    /**
     * Asserts that the report contains every required section heading.
     *
     * @param report generated report text
     */
    private void assertContainsAllReportSections(String report) {
        assertAll(
            () -> assertTrue(
                report.contains("=== Log Analysis Report ==="),
                "Report heading is missing"
            ),
            () -> assertTrue(
                report.contains("--- Activity Summary ---"),
                "Activity Summary section is missing"
            ),
            () -> assertTrue(
                report.contains("--- Flagged Entries ---"),
                "Flagged Entries section is missing"
            ),
            () -> assertTrue(
                report.contains("--- Suspicious Activity by IP ---"),
                "Suspicious Activity by IP section is missing"
            ),
            () -> assertTrue(
                report.contains("--- Unknown Patterns ---"),
                "Unknown Patterns section is missing"
            ),
            () -> assertTrue(
                report.contains("--- Malformed Lines ---"),
                "Malformed Lines section is missing"
            )
        );
    }
}