package reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Rule;

/**
 * Reads and validates severity rules from CSV input.
 *
 * Rulebook input must contain a header followed by at least one rule.
 * Each rule consists of a log level and an integer severity score.
 */
public class RuleBookReader {

    /** Stores the rules successfully parsed from the CSV rulebook. */
    private final List<Rule> rules = new ArrayList<>();

    /**
     * Returns an unmodifiable view of the loaded rules.
     *
     * @return an unmodifiable view of the rules
     */
    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    /**
     * Parses a single CSV row and adds the resulting rule.
     *
     * A row must contain exactly two comma-separated fields. Duplicate
     * levels are rejected, and the severity score must be an integer.
     *
     * @param newRule the CSV row containing a level and severity score
     * @throws IllegalArgumentException if the row has an invalid format
     * @throws NumberFormatException if the severity score is not an integer
     * @throws DuplicateException if the level already exists
     */
    public void readRule(String newRule) throws DuplicateException {
        String[] parts = newRule.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    " has an invalid rule format on row ");
        }

        String level = parts[0];

        for (Rule existingRule : rules) {
            if (level.equals(existingRule.getLevel())) {
                throw new DuplicateException(
                        " contains a duplicate level on row ");
            }
        }

        int score = Integer.parseInt(parts[1]);
        rules.add(new Rule(level, score));
    }

    /**
     * Reads a CSV rulebook file and delegates validation to {@link #readLines(List)}.
     *
     * @param filePath the path to the CSV rulebook file
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the rulebook or a rule is invalid
     * @throws NumberFormatException if a severity score is invalid
     */
    public void readCSV(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filePath));

        try {
            readLines(lines);
        } catch (NumberFormatException e) {
            NumberFormatException exception = new NumberFormatException(
                    filePath + e.getMessage());
            exception.initCause(e);
            throw exception;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(filePath + e.getMessage(), e);
        }
    }

    /**
     * Parses rulebook CSV lines supplied directly in memory.
     *
     * The first line is treated as the header and subsequent lines are
     * validated as rules. Error messages identify the one-based source row.
     * This method performs no file input or output.
     *
     * Existing rules are retained, so repeated calls append rules to the
     * current collection. Duplicate levels across calls are still rejected.
     *
     * @param lines CSV lines, including the header as the first line
     * @throws IllegalArgumentException if the input contains no rules,
     *                                  an invalid row, or a duplicate level
     * @throws NumberFormatException if a severity score is not an integer
     */
    public void readLines(List<String> lines) {
        if (lines.size() <= 1) {
            throw new IllegalArgumentException("Rulebook contains no rules.");
        }

        for (int i = 1; i < lines.size(); i++) {
            try {
                readRule(lines.get(i));
            } catch (NumberFormatException e) {
                NumberFormatException exception = new NumberFormatException(
                        " has an invalid severity score on row " + (i + 1));
                exception.initCause(e);
                throw exception;
            } catch (DuplicateException e) {
                throw new IllegalArgumentException(
                        e.getMessage() + (i + 1), e);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        e.getMessage() + (i + 1), e);
            }
        }
    }

    /**
     * Signals that a rulebook row contains a log level that already exists.
     */
    public class DuplicateException extends Exception {

        /**
         * Creates an exception describing a duplicate rule.
         *
         * @param message the duplicate-rule error message
         */
        public DuplicateException(String message) {
            super(message);
        }
    }
}