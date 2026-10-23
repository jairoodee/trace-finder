import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TraceFinderIT {

    @TempDir
    Path tempDir;

    /**
     * Verifies that running TraceFinder without arguments prints the usage
     * message and exits with status code 1.
     *
     * @throws IOException if the process cannot be started or its output
     *                     cannot be read
     * @throws InterruptedException if the current thread is interrupted
     *                              while waiting for the process
     */
    @Test
    void main_noArguments_printsUsageAndExitsWithOne()
            throws IOException, InterruptedException {

        String[] command = {
            "java",
            "-jar",
            "target/tracefinder-1.0-SNAPSHOT.jar"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        String output;
        try (var inputStream = process.getInputStream()) {
            output = new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
            );
        }

        assertTrue(
            output.contains(
                "Usage: java -jar tracefinder.jar <log-file> <rules-file> <report-file>"
            ),
            "Expected the usage message when no arguments are supplied"
        );

        assertEquals(
            1,
            process.waitFor(),
            "Expected the process to exit with status code 1"
        );
    }

    /**
     * Verifies that running TraceFinder with a nonexistent log file prints
     * an appropriate error message and exits with status code 1.
     *
     * @throws IOException if the process cannot be started or its output
     *                     cannot be read
     * @throws InterruptedException if the current thread is interrupted
     *                              while waiting for the process
     */
    @Test
    void main_missingLogFile_printsErrorAndExitsWithOne()
            throws IOException, InterruptedException {

        String[] command = {
            "java",
            "-jar",
            "target/tracefinder-1.0-SNAPSHOT.jar",
            "missing-logs.txt",
            "rules.csv",
            "report.txt"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        String output;
        try (var inputStream = process.getInputStream()) {
            output = new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
            );
        }

        assertTrue(
            output.contains(
                "Error: Could not read log file: missing-logs.txt"
            ),
            "Expected an error message identifying the missing log file"
        );

        assertEquals(
            1,
            process.waitFor(),
            "Expected the process to exit with status code 1"
        );
    }

    /**
     * Verifies that an invalid severity score in the rulebook produces
     * an appropriate error message and exit status code 1.
     *
     * A valid log file is supplied so that the test focuses on rulebook
     * validation rather than log-file handling.
     *
     * @throws IOException if the process cannot be started or its output
     *                     cannot be read
     * @throws InterruptedException if the current thread is interrupted
     *                              while waiting for the process
     */
    @Test
    void main_invalidRulebook_printsErrorAndExitsWithOne()
            throws IOException, InterruptedException {

        String[] command = {
            "java",
            "-jar",
            "target/tracefinder-1.0-SNAPSHOT.jar",
            "logs.txt",
            "invalid-score.csv",
            "report.txt"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        String output;
        try (var inputStream = process.getInputStream()) {
            output = new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
            );
        }

        assertTrue(
            output.contains(
                "Error: invalid-score.csv has an invalid severity score on row 2"
            ),
            "Expected an error message identifying the invalid severity score"
        );

        assertEquals(
            1,
            process.waitFor(),
            "Expected the process to exit with status code 1"
        );
    }

    /**
     * Verifies that TraceFinder successfully processes valid input files,
     * exits with status code 0, and generates a report containing the
     * report heading and all five required sections.
     *
     * The report is written to a temporary directory to avoid overwriting
     * an existing report in the project directory.
     *
     * @throws IOException if the process cannot be started, its output
     *                     cannot be read, or the report cannot be read
     * @throws InterruptedException if the current thread is interrupted
     *                              while waiting for the process
     */
    @Test
    void main_validArguments_generatesReportAndExitsWithZero()
            throws IOException, InterruptedException {

        Path reportFile = tempDir.resolve("final-report.txt");

        String[] command = {
            "java",
            "-jar",
            "target/tracefinder-1.0-SNAPSHOT.jar",
            "logs.txt",
            "rules.csv",
            reportFile.toString()
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        String output;
        try (var inputStream = process.getInputStream()) {
            output = new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
            );
        }

        // Verify that TraceFinder completed successfully.
        assertEquals(
            0,
            process.waitFor(),
            "Expected TraceFinder to exit with status code 0. Output: " + output
        );

        // Verify that the report was created in the temporary directory.
        assertTrue(
            Files.exists(reportFile),
            "Report file was not created"
        );

        // Read the generated report.
        String report = Files.readString(reportFile);

        // Verify the report heading.
        assertTrue(
            report.contains("=== Log Analysis Report ==="),
            "Report heading is missing"
        );

        // Verify that all five required sections are present.
        assertTrue(
            report.contains("--- Activity Summary ---"),
            "Activity Summary section is missing"
        );

        assertTrue(
            report.contains("--- Flagged Entries ---"),
            "Flagged Entries section is missing"
        );

        assertTrue(
            report.contains("--- Suspicious Activity by IP ---"),
            "Suspicious Activity by IP section is missing"
        );

        assertTrue(
            report.contains("--- Unknown Patterns ---"),
            "Unknown Patterns section is missing"
        );

        assertTrue(
            report.contains("--- Malformed Lines ---"),
            "Malformed Lines section is missing"
        );
    }
}