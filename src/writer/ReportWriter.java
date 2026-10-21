
package writer;

import model.MalformedLine;
import model.LogEntry;
import model.UnknownPattern;
import model.ParsedLogLine;
import model.Rule;
import reader.RuleBookReader;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Builds and writes the TraceFinder analysis report.
 *
 * <p>The report contains five sections: counts by known log level,
 * flagged entries, suspicious IP addresses, unknown patterns, and
 * malformed log lines.</p>
 *
 * <p>This class receives the results of reading and analysis rather
 * than performing those responsibilities itself. An {@link IOException}
 * is propagated to the caller if writing the report fails.</p>
 */
public class ReportWriter {

    /**
     * Generates the five-section report and writes it to the specified path.
     *
     * <p>Suspicious IP addresses are derived from the flagged entries.
     * Each suspicious IP is listed once, and its count includes every
     * successfully parsed log entry associated with that IP, not only
     * flagged entries.</p>
     *
     * <p>All five section headings are included even when a section has
     * no entries.</p>
     *
     * @param logEntries successfully parsed log lines, including their
     *                   original line numbers and raw content
     * @param malformedLines log lines that could not be parsed
     * @param flaggedEntries known-level log entries whose severity meets
     *                      the configured flagging threshold
     * @param unknownPatterns entries whose log level is not in the rulebook
     * @param ruleBookReader provides the configured rules for known levels
     *                       and level counts
     * @param outputPath destination path for the generated report
     * @throws IOException if the report cannot be written to the destination
     */
    public void writeReport(
        List<ParsedLogLine> logEntries,
        List<MalformedLine> malformedLines,
        List<LogEntry> flaggedEntries,
        List<UnknownPattern> unknownPatterns,
        RuleBookReader ruleBookReader,
        String outputPath
    ) throws IOException {

        // Build the report in memory before writing it to the destination.
        StringBuilder report = new StringBuilder();
        report.append("1. Counts by Known Log Level\n");

        // Count successfully parsed entries for each level defined in the rulebook.
        for (int i = 0; i < ruleBookReader.getRules().size(); i++) {
            int count = 0;
            Rule rule = ruleBookReader.getRules().get(i);

            for (int j = 0; j < logEntries.size(); j++) {
                LogEntry logEntry = logEntries.get(j).getLogEntry();

                if (logEntry.getLevel().equals(rule.getLevel())) {
                    count++;
                }
            }

            report.append(rule.getLevel() + ": " + count + "\n");
        }

        // List entries whose severity scores meet the flagging threshold.
        report.append("\n2. Flagged Entries\n");

        for (int i = 0; i < flaggedEntries.size(); i++) {
            report.append(
                flaggedEntries.get(i).getTimestamp() + " | " +
                flaggedEntries.get(i).getLevel() + " | " +
                flaggedEntries.get(i).getSourceIP() + "\n"
            );
        }

        // Derive unique suspicious IPs from the flagged entries.
        report.append("\n3. Suspicious IP Addresses\n");

        List<String> flaggedIPList = flaggedEntries.stream()
            .map(LogEntry::getSourceIP)
            .distinct()
            .collect(Collectors.toList());

        // Count all parsed entries belonging to each suspicious IP.
        for (int i = 0; i < flaggedIPList.size(); i++) {
            int counter = 0;

            for (int j = 0; j < logEntries.size(); j++) {
                if (logEntries.get(j).getLogEntry().getSourceIP()
                        .equals(flaggedIPList.get(i))) {
                    counter++;
                }
            }

            // Append one final count per suspicious IP after counting is complete.
            report.append(flaggedIPList.get(i) + ": " + counter + " entries\n");
        }

        // Preserve the original line number and content of unknown-level entries.
        report.append("\n4. Unknown Patterns\n");

        for (int i = 0; i < unknownPatterns.size(); i++) {
            report.append(
                "Line " +
                unknownPatterns.get(i).getLineNumber() +
                ": " +
                unknownPatterns.get(i).getContent() + "\n"
            );
        }

        // Preserve the original line number and content of malformed entries.
        report.append("\n5. Malformed Lines\n");

        for (int i = 0; i < malformedLines.size(); i++) {
            report.append(
                "Line " +
                malformedLines.get(i).getLineNumber() +
                ": " +
                malformedLines.get(i).getContent() + "\n"
            );
        }

        // Write the complete report to the caller-supplied destination.
        // IOException is allowed to propagate to the caller for error handling.
        Path filePath = Path.of(outputPath);
        Files.writeString(filePath, report.toString());
    }
}
