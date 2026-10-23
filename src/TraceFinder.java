import java.io.IOException;

import analyzer.LogAnalyzer;
import analyzer.SeverityChecker;
import model.ParsedLogLine;
import reader.LogFileReader;
import reader.RuleBookReader;
import writer.ReportWriter;

/**
 * Entry point for the TraceFinder log analysis application.
 *
 * <p>Coordinates the reading of log files and severity rules, analyzes
 * successfully parsed log entries, and generates a report containing
 * activity summaries, flagged entries, suspicious IP addresses,
 * unknown patterns, and malformed lines.</p>
 *
 * <p>The application expects three command-line arguments: the log file
 * path, the rulebook CSV path, and the output report path. Fatal errors
 * are reported to the console and cause the application to exit with
 * a non-zero status code.</p>
 */
public class TraceFinder {

    /**
     * Runs the TraceFinder application using command-line arguments.
     *
     * <p>Loads the severity rulebook, reads and parses the log file,
     * analyzes valid entries, and writes the resulting report. If the
     * arguments are invalid or a required file operation fails, an
     * error message is printed and the application exits with status
     * code {@code 1}.</p>
     *
     * @param args command-line arguments in the order:
     *             log file path, rulebook CSV path, and report output path
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java -jar tracefinder.jar <log-file> <rules-file> <report-file>");
            System.exit(1);
        }

        RuleBookReader newRuleBook = new RuleBookReader();

        try {
            newRuleBook.readCSV(args[1]);
        } catch (IOException e) {
            System.out.println("Error: Could not read rulebook file: " + args[1]);
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }

        SeverityChecker newCheck = new SeverityChecker();
        LogAnalyzer newAnalysis = new LogAnalyzer();
        LogFileReader newlogReader = new LogFileReader();

        try {
            newlogReader.readFile(args[0]);
        } catch (IOException e) {
            System.out.println("Error: Could not read log file: " + args[0]);
            System.exit(1);
        }

        ReportWriter reportWriter = new ReportWriter();

        // Analyze each successfully parsed log entry while preserving
        // its original line number and raw content.
        for (int i = 0; i < newlogReader.getLogEntries().size(); i++) {
            ParsedLogLine newParsedLogLine = newlogReader.getLogEntries().get(i);

            newAnalysis.analyzeEntry(
                newCheck,
                newParsedLogLine.getLogEntry(),
                newRuleBook,
                newParsedLogLine.getLineNumber(),
                newParsedLogLine.getRawContent()
            );
        }

        // Write the analysis results to the requested report file.
        try {
            reportWriter.writeReport(
                newlogReader.getLogEntries(),
                newlogReader.getMalformedLines(),
                newAnalysis.getFlaggedEntries(),
                newAnalysis.getUnknownPatterns(),
                newRuleBook,
                args[2]
            );
        } catch (IOException e) {
            System.out.println("Error: Could not write report file: " + args[2]);
            System.exit(1);
        }
    }
}