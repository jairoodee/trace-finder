package model;

/**
 * Represents a severity rule used to classify log entries.
 *
 * <p>Each rule associates a log level with a numerical severity score.
 * The score is used by TraceFinder to determine whether a log entry
 * should be flagged for further investigation.</p>
 */
public class Rule {

    private String level;
    private int score;

    /**
     * Creates a severity rule for the specified log level.
     *
     * @param level the log level to which this rule applies
     * @param score the numerical severity score assigned to the level
     */
    public Rule(String level, int score) {
        this.level = level;
        this.score = score;
    }

    /**
     * Returns the log level associated with this rule.
     *
     * @return the configured log level
     */
    public String getLevel() {
        return this.level;
    }

    /**
     * Returns the severity score associated with this rule.
     *
     * @return the configured severity score
     */
    public int getScore() {
        return this.score;
    }
}