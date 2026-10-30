package analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.LogEntry;
import model.UnknownPattern;
import reader.RuleBookReader;

/**
 * Analyzes valid log entries by identifying flagged entries and
 * unknown patterns.
 *
 * Known entries are flagged when their severity score is at least 3.
 * Entries with levels absent from the rulebook are stored separately
 * with their original line numbers and raw content.
 */
public class LogAnalyzer {

    /**
     * Stores log entries whose severity score is at least 3.
     */
    private List<LogEntry> flaggedEntries = new ArrayList<>();

    /**
     * Stores entries with unknown levels, preserving their original
     * line numbers and raw content for reporting.
     */
    private List<UnknownPattern> unknownPatterns = new ArrayList<>();

    /**
     * Returns an unmodifiable view of the flagged entries.
     *
     * Callers can inspect the results but cannot add or remove entries
     * through the returned list.
     *
     * @return an unmodifiable list of flagged log entries
     */
    public List<LogEntry> getFlaggedEntries(){
        List<LogEntry> readonlyLogEntries = Collections.unmodifiableList(this.flaggedEntries);
        return readonlyLogEntries;
    }

    /**
     * Returns an unmodifiable view of the unknown patterns.
     *
     * Each unknown pattern retains the original line number and raw
     * content so the reporting stage can identify its source.
     *
     * @return an unmodifiable list of unknown patterns
     */
    public List<UnknownPattern> getUnknownPatterns(){
        List<UnknownPattern> readOnlyUnknownPatterns = Collections.unmodifiableList(this.unknownPatterns);
        return readOnlyUnknownPatterns;
    }

    /**
     * Classifies one parsed log entry using the loaded rulebook.
     *
     * If the entry's level is unknown, an UnknownPattern is stored
     * using the supplied original line number and raw content.
     * Otherwise, the entry is flagged only when its severity score
     * is greater than or equal to 3.
     *
     * @param severityChecker checks whether the level is known and
     *                        retrieves its severity score
     * @param logEntry the parsed log entry being analyzed
     * @param rulebookReader provides the loaded severity rules
     * @param lineNumber the original one-based line number
     * @param rawContent the original, unmodified log line
     */
    public void analyzeEntry(SeverityChecker severityChecker,
                                LogEntry logEntry,
                                RuleBookReader rulebookReader,
                                int lineNumber,
                                String rawContent){

        // Unknown levels are recorded separately from flagged entries.
        if(severityChecker.isKnownLevel(rulebookReader, logEntry) == false){
            UnknownPattern newunknownPattern = new UnknownPattern(lineNumber, rawContent);
            unknownPatterns.add(newunknownPattern);
        } else{

            // Only known levels are checked against the flagging threshold.
            int severityScore = severityChecker.checkLogEntry(rulebookReader, logEntry);

            if(severityScore >= 3){
                flaggedEntries.add(logEntry);
            }
        }
    }
}
