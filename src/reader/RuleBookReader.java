
package reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Rule;
import model.UnknownPattern;
import writer.ReportWriter;
import model.LogEntry;
import model.ParsedLogLine;
import analyzer.LogAnalyzer;
import analyzer.SeverityChecker;

public class RuleBookReader {

    // Stores the valid rules loaded from the CSV rulebook.
    private List<Rule> rules = new ArrayList<>();

    /**
     * Returns a read-only view of the rules currently stored.
     *
     * Collections.unmodifiableList prevents callers from adding or
     * removing rules through the returned list. The view reflects
     * changes made to the original rules list.
     *
     * @return an unmodifiable list containing the loaded rules
     */
    public List<Rule> getRules() {
        List<Rule> readOnlyRules = Collections.unmodifiableList(this.rules);
        return readOnlyRules;
    }

    /**
     * Parses one CSV row and creates a Rule object from its contents.
     *
     * Each row must contain exactly two comma-separated fields:
     * the rule level and its integer severity score.
     *
     * @param newRule the CSV row to parse
     * @throws IllegalArgumentException if the row does not contain
     *                                  exactly two fields
     * @throws NumberFormatException if the severity score is not an integer
     */
    public void readRule(String newRule) {
        // Separate the CSV row into its comma-separated fields.
        String[] parts = newRule.split(",");

        // Reject rows that do not contain exactly two fields.
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid Rule");
        } else {
            // Extract the rule level from the first field.
            String level = parts[0];

            // Convert the second field into an integer severity score.
            // Integer.parseInt throws NumberFormatException if conversion fails.
            int score = Integer.parseInt(parts[1]);

            // Create a Rule object using the parsed level and score.
            Rule addRule = new Rule(level, score);

            // Store the new rule in the collection.
            rules.add(addRule);
        }
    }

    /**
     * Reads a CSV rulebook file and converts each row into a Rule object.
     *
     * If a row has an invalid format or a non-numeric severity score,
     * processing stops and an exception identifies the file and the
     * row number. File-reading errors are propagated to the caller.
     *
     * @param filePath the path to the CSV rulebook file
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if a row has an invalid format
     * @throws NumberFormatException if a severity score is invalid
     */
    public void readCSV(String filePath) throws IOException {
        // Convert the supplied file path into a Path object.
        Path path = Path.of(filePath);

        // Read all file lines into a list before processing them.
        List<String> lines = Files.readAllLines(path);

        // Process each CSV row individually.
        for (int i = 0; i < lines.size(); i++) {
            try {
                // Parse the current row and add its rule to the collection.
                this.readRule(lines.get(i));

            } catch (NumberFormatException e) {
                // Include the file name and one-based row number in the error.
                String customMessage = filePath
                        + " contains an invalid severity score on row " + (i + 1);

                // Create a clearer exception while preserving the original cause.
                NumberFormatException customException =
                        new NumberFormatException(customMessage);
                customException.initCause(e);

                // Stop processing and report the invalid severity score.
                throw customException;

            } catch (IllegalArgumentException e) {
                // Report the file and row when the CSV structure is invalid.
                throw new IllegalArgumentException(
                        filePath + " has an invalid rule format on row " + (i + 1));
            }
        }
    }


    /**
     * Temporary test harness for the RuleBookReader.
     *
     * Loads rules.csv and prints each loaded rule's level and score.
     * This main method can later be replaced by TraceFinder's application
     * entry point, which will coordinate the different modules.
     *
     * @param args command-line arguments (currently unused)
     * @throws IOException if the rulebook file cannot be read
     */
    public static void main(String args[]) throws IOException{
        RuleBookReader newRuleBook = new RuleBookReader();
        newRuleBook.readCSV("rules.csv");
        SeverityChecker newCheck = new SeverityChecker();
        LogAnalyzer newAnalysis = new LogAnalyzer();
        LogFileReader newlogReader = new LogFileReader();
        newlogReader.readFile("test.txt");
        ReportWriter reportWriter = new ReportWriter();
       
        for(int i = 0; i < newlogReader.getLogEntries().size(); i++){
            ParsedLogLine newParsedLogLine = newlogReader.getLogEntries().get(i);
            newAnalysis.analyzeEntry(newCheck, newParsedLogLine.getLogEntry(), newRuleBook, newParsedLogLine.getLineNumber(), newParsedLogLine.getRawContent());
        
        }

        reportWriter.writeReport(
            newlogReader.getLogEntries(), 
            newlogReader.getMalformedLines(),
            newAnalysis.getFlaggedEntries(),
            newAnalysis.getUnknownPatterns(),
            newRuleBook, 
"report.txt");
    }
}
