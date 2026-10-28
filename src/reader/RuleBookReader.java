package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Rule;

public class RuleBookReader {

    // Stores the rules successfully parsed from the CSV rulebook.
    private List<Rule> rules = new ArrayList<>();

    /**
     * Returns a read-only view of the rules currently stored.
     *
     * The returned list cannot be used by callers to add or remove
     * rules. Because this is a view of the original collection, it
     * reflects any subsequent changes made internally to the rules list.
     *
     * @return an unmodifiable view of the loaded rules
     */
    public List<Rule> getRules() {
        List<Rule> readOnlyRules = Collections.unmodifiableList(this.rules);
        return readOnlyRules;
    }

    /**
     * Parses a single CSV row and adds the resulting rule to the rule list.
     *
     * Each row must contain exactly two comma-separated fields: a log
     * level and its integer severity score. A level that already exists
     * in the rule list is rejected to prevent duplicate rules.
     *
     * @param newRule the CSV row containing a level and severity score
     * @throws IllegalArgumentException if the row does not contain
     *                                  exactly two fields
     * @throws NumberFormatException if the severity score is not an integer
     * @throws DuplicateException if the rule level already exists
     */
    public void readRule(String newRule) throws DuplicateException {
        // Split the row into its comma-separated fields.
        String[] parts = newRule.split(",");

        // Reject rows that do not contain exactly two fields.
        if (parts.length != 2) {
            throw new IllegalArgumentException(" has an invalid rule format on row ");
        }

        // Extract the log level from the first field.
        String level = parts[0];

        // Check existing rules to prevent the same level from being added twice.
        for (int i = 0; i < rules.size(); i++) {
            if (level.equals(rules.get(i).getLevel())) {
                throw new DuplicateException(" contains a duplicate level on row ");
            }
        }

        // Parse the severity score; invalid numeric text causes a NumberFormatException.
        int score = Integer.parseInt(parts[1]);

        // Create a Rule from the parsed level and severity score.
        Rule addRule = new Rule(level, score);

        // Add the validated rule to the collection.
        rules.add(addRule);
    }

    /**
     * Reads a CSV rulebook file and adds its rules to the internal list.
     *
     * Each row is validated for field count, severity score, and duplicate
     * log levels. If a row is invalid, processing stops and an exception
     * identifies the file and the one-based row number. An empty file is
     * rejected. File-reading errors are propagated to the caller.
     *
     * @param filePath the path to the CSV rulebook file
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the file is empty or a row has
     *                                  an invalid format or duplicate level
     * @throws NumberFormatException if a severity score is invalid
     */
    public void readCSV(String filePath) throws IOException {
        // Convert the supplied file path into a Path object.
        Path path = Path.of(filePath);

        // Read all lines from the file before processing individual rows.
        List<String> lines = Files.readAllLines(path);

        // Reject a rulebook that contains no lines.
        if (lines.size() <= 1) {
            throw new IllegalArgumentException(filePath + " contains no rules.");
        }

        // Process each row individually so errors can identify its line number.
        for (int i = 1; i < lines.size(); i++) {
            try {
                // Parse the current row and add its rule to the collection.
                this.readRule(lines.get(i));

            } catch (NumberFormatException e) {
                // Build an error message using the file path and one-based row number.
                String customMessage = filePath
                        + " has an invalid severity score on row " + (i + 1);

                // Preserve the original exception as the cause of the clearer error.
                NumberFormatException customException =
                        new NumberFormatException(customMessage);
                customException.initCause(e);

                // Stop processing and report the invalid severity score.
                throw customException;

            } catch (DuplicateException e) {
                // Report the duplicate level with the file path and current row number.
                throw new IllegalArgumentException(
                        filePath + e.getMessage() + (i + 1));

            } catch (IllegalArgumentException e) {
                // Report an invalid row format with the file path and current row number.
                throw new IllegalArgumentException(
                        filePath + e.getMessage() + (i + 1));
            }
        }
        
    }

    /**
     * Signals that a rulebook row contains a log level that already exists.
     *
     * This checked exception allows readCSV() to distinguish duplicate
     * levels from other rule-validation errors and report the affected row.
     */
    public class DuplicateException extends Exception {

        /**
         * Creates a duplicate-rule exception with a descriptive message.
         *
         * @param message the detail describing the duplicate-rule error
         */
        public DuplicateException(String message) {
            super(message);
        }
    }
}
