
package analyzer;

import reader.RuleBookReader;
import model.Rule;

import java.util.ArrayList;
import java.util.List;

import model.LogEntry;

/**
 * Checks the severity of a log entry against the rules
 * loaded from the rulebook.
 *
 * This class is responsible for severity matching only.
 * It does not read files or modify log entries.
 */
public class SeverityChecker {

    private List<LogEntry> flaggedEntries = new ArrayList<>();

    /**
     * Finds the severity score associated with a log entry's level.
     *
     * Iterates through the rules in the supplied rulebook and compares
     * each rule's level with the log entry's level.
     *
     * The comparison is case-sensitive. Once a matching rule is found,
     * its severity score is retrieved and the search stops.
     *
     * If no matching rule exists, the method returns 0.
     *
     * @param ruleBookReader a RuleBookReader containing previously
     *                       loaded severity rules
     * @param logEntry the log entry whose severity is being checked
     * @return the matching severity score, or 0 if no rule matches
     */
    public int checkLogEntry(RuleBookReader ruleBookReader, LogEntry logEntry){

        // Default severity score when no matching rule exists.
        int severityScore = 0;

        // Iterate through all the rules loaded from the rulebook.
        for(int i = 0; i < ruleBookReader.getRules().size(); i++){

             // Retrieve the rule at the current index.
             Rule rule = ruleBookReader.getRules().get(i);

             // Compare the log entry's level with the current rule's level.
             // String.equals() performs a case-sensitive comparison.
             if (logEntry.getLevel().equals(rule.getLevel())) {

                    // Store the severity score of the matching rule.
                    severityScore = rule.getScore();

                    if(severityScore >= 3){
                        flaggedEntries.add(logEntry);
                    }

                    // Stop searching because a matching rule was found.
                    break;
                }
        }

        // Return the matching score, or 0 if no match was found.
        return severityScore;
    }
}
