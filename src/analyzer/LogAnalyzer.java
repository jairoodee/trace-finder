package analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.LogEntry;
import reader.RuleBookReader;

/**
 * Analyzes log entries and identifies entries that meet the
 * minimum severity threshold for flagging.
 *
 * This class uses SeverityChecker to determine the severity
 * score of each entry and stores entries with a score of 3 or higher.
 */
public class LogAnalyzer {

    /**
     * Stores all log entries whose severity score is at least 3.
     * Entries accumulate as analyzeEntry() processes additional logs.
     */
    private List<LogEntry> flaggedEntries = new ArrayList<>();

    /**
     * Returns an unmodifiable view of the flagged log entries.
     *
     * The returned list allows callers to inspect flagged entries
     * without directly adding or removing entries through the list.
     *
     * @return an unmodifiable list of flagged log entries
     */
    public List<LogEntry> getFlaggedEntries(){
        List<LogEntry> readonlyLogEntries = Collections.unmodifiableList(this.flaggedEntries);
        return readonlyLogEntries;
    }

    /**
     * Checks a log entry's severity and stores it if it meets
     * the flagging threshold.
     *
     * An entry is flagged when its severity score is greater than
     * or equal to 3. Entries below this threshold are not added.
     * Existing flagged entries remain in the collection.
     *
     * @param severityChecker checks the severity score of a log entry
     * @param logEntry the log entry being analyzed
     * @param rulebookReader provides the rules used to determine severity
     */
    public void analyzeEntry(SeverityChecker severityChecker, 
                                LogEntry logEntry, 
                                RuleBookReader rulebookReader){
        int severityScore = severityChecker.checkLogEntry(rulebookReader, logEntry);
        if(severityScore >= 3){
            flaggedEntries.add(logEntry);
        }
    }
}
