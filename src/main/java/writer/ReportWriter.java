package writer;

import model.MalformedLine;
import model.LogEntry;
import model.UnknownPattern;
import model.ParsedLogLine;
import model.Rule;
import reader.RuleBookReader;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
     * Generates the five-section analysis report in memory.
     *
     * <p>Suspicious IP addresses are derived from flagged entries.
     * Their counts include all successfully parsed entries associated
     * with each suspicious IP.</p>
     *
     * @param logEntries successfully parsed log lines
     * @param malformedLines log lines that could not be parsed
     * @param flaggedEntries entries that meet the flagging threshold
     * @param unknownPatterns entries with unrecognized log levels
     * @param ruleBookReader provides configured severity rules
     * @return the complete report as a string
     */
    public String generateReport(
        List<ParsedLogLine> logEntries,
        List<MalformedLine> malformedLines,
        List<LogEntry> flaggedEntries,
        List<UnknownPattern> unknownPatterns,
        RuleBookReader ruleBookReader
    ) {

        // Build the report in memory before writing it to the destination.
        StringBuilder report = new StringBuilder();

        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();
        // Define the pattern: yyyy-MM-dd HH:mm:ss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format and print the result
        String formattedDateTime = now.format(formatter);

        report.append("=== Log Analysis Report ===").append(System.lineSeparator());
        report.append("Generated: ").append(formattedDateTime);
        report.append(System.lineSeparator());

        // Add a blank line before Activity Summary.
        report.append(System.lineSeparator());

        report.append("--- Activity Summary ---").append(System.lineSeparator());

        // Count successfully parsed entries for each level defined in the rulebook.
        if(ruleBookReader.getRules().isEmpty()){
            report.append("None\n");
        } else{
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
        }
        
        // List flagged entries in descending order of their configured severity scores.
        report.append(System.lineSeparator())
            .append("--- Flagged Entries ---")
            .append(System.lineSeparator());

        if (flaggedEntries.isEmpty()) {
            report.append("None").append(System.lineSeparator());
        } else {
            // Sort a copy so the caller's original list remains unchanged.
            List<LogEntry> sortedFlaggedEntries = new ArrayList<>(flaggedEntries);

            sortedFlaggedEntries.sort(
                Comparator.comparingInt((LogEntry entry) ->
                    ruleBookReader.getRules().stream()
                        .filter(rule -> rule.getLevel().equals(entry.getLevel()))
                        .mapToInt(Rule::getScore)
                        .findFirst()
                        .orElse(Integer.MIN_VALUE)
                ).reversed()
            );

            // Write each entry from highest to lowest severity score.
            for (int i = 0; i < sortedFlaggedEntries.size(); i++) {
                LogEntry entry = sortedFlaggedEntries.get(i);

                report.append(
                    entry.getTimestamp() + " | " +
                    entry.getLevel() + " | " +
                    entry.getSourceIP() + System.lineSeparator()
                );
            }
        }
        
      // Derive unique suspicious IPs from the flagged entries.
        report.append(System.lineSeparator())
            .append("--- Suspicious Activity by IP ---")
            .append(System.lineSeparator());

        List<String> flaggedIPList = flaggedEntries.stream()
            .map(LogEntry::getSourceIP)
            .distinct()
            .collect(Collectors.toList());

        if (flaggedIPList.isEmpty()) {
            report.append("None").append(System.lineSeparator());
        } else {
            // Count all parsed entries belonging to each suspicious IP.
            Map<String, Integer> suspiciousIPCounts = new LinkedHashMap<>();

            for (String ip : flaggedIPList) {
                int counter = 0;

                for (ParsedLogLine parsedLogLine : logEntries) {
                    if (parsedLogLine.getLogEntry().getSourceIP().equals(ip)) {
                        counter++;
                    }
                }

                suspiciousIPCounts.put(ip, counter);
            }

            // Sort suspicious IPs by their total entry counts, highest first.
            List<Map.Entry<String, Integer>> sortedIPCounts =
                new ArrayList<>(suspiciousIPCounts.entrySet());

            sortedIPCounts.sort(
                Map.Entry.<String, Integer>comparingByValue().reversed()
            );

            // Write each IP and its count in descending order.
            for (Map.Entry<String, Integer> entry : sortedIPCounts) {
                report.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" entries")
                    .append(System.lineSeparator());
            }
        }


        // Preserve the original line number and content of unknown-level entries.
        report.append(System.lineSeparator())
        .append("--- Unknown Patterns ---")
        .append(System.lineSeparator());
        if(unknownPatterns.isEmpty()){
            report.append("None\n");
        } else{
            for (int i = 0; i < unknownPatterns.size(); i++) {
                report.append(
                    "Line " +
                    unknownPatterns.get(i).getLineNumber() +
                    ": " +
                    unknownPatterns.get(i).getContent() + "\n"
                );
            }
        }
        // Preserve the original line number and content of malformed entries.
        report.append(System.lineSeparator())
        .append("--- Malformed Lines ---")
        .append(System.lineSeparator());

        if(malformedLines.isEmpty()){
            report.append("None\n");
        } else{
            for (int i = 0; i < malformedLines.size(); i++) {
                report.append(
                    "Line " +
                    malformedLines.get(i).getLineNumber() +
                    ": " +
                    malformedLines.get(i).getContent() + "\n"
                );
            }
        }
        return report.toString();
    }

    /**
     * Generates the report and writes it to the specified destination.
     *
     * @param logEntries successfully parsed log lines
     * @param malformedLines log lines that could not be parsed
     * @param flaggedEntries entries that meet the flagging threshold
     * @param unknownPatterns entries with unrecognized log levels
     * @param ruleBookReader provides configured severity rules
     * @param outputPath destination path for the generated report
     * @throws IOException if the report cannot be written
     */
    public void writeReport(
        List<ParsedLogLine> logEntries,
        List<MalformedLine> malformedLines,
        List<LogEntry> flaggedEntries,
        List<UnknownPattern> unknownPatterns,
        RuleBookReader ruleBookReader,
        String outputPath
    ) throws IOException {

        String report = generateReport(
            logEntries,
            malformedLines,
            flaggedEntries,
            unknownPatterns,
            ruleBookReader
        );

        Files.writeString(Path.of(outputPath), report);
    }
}
