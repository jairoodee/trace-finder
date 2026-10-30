package reader;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests log-line processing using in-memory input.
 */
class LogFileReaderTest {

    /**
     * Verifies that a valid log line is stored as a parsed entry,
     * preserving its original line number and raw content.
     */
    @Test
    void readLines_validLine_storesParsedEntry() {
        String newLog =
                "2024-03-15 08:32:10 | INFO | 10.0.0.1 | server01 | LOGIN";

        LogFileReader logFileReader = new LogFileReader();
        logFileReader.readLines(List.of(newLog));

        assertEquals(1, logFileReader.getLogEntries().size());
        assertEquals(1, logFileReader.getLogEntries().get(0).getLineNumber());
        assertEquals(newLog, logFileReader.getLogEntries().get(0).getRawContent());
    }

    /**
     * Verifies that a malformed log line is stored separately,
     * preserving its original line number and content.
     */
    @Test
    void readLines_malformedLine_storesMalformedRecord() {
        String newLog =
                "2024-03-15 08:32:10 | WARN | 192.168.1.10 | server01";

        LogFileReader logFileReader = new LogFileReader();
        logFileReader.readLines(List.of(newLog));

        assertEquals(1, logFileReader.getMalformedLines().size());
        assertEquals(1, logFileReader.getMalformedLines().get(0).getLineNumber());
        assertEquals(newLog, logFileReader.getMalformedLines().get(0).getContent());
    }

    /**
     * Verifies that valid and malformed lines are separated correctly
     * while retaining their original line numbers and content.
     */
    @Test
    void readLines_mixedLines_separatesValidAndMalformedRecords() {
        List<String> logs = List.of(
                "2024-03-15 08:32:10 | INFO | 10.0.0.1 | server01 | LOGIN",
                "2024-03-15 08:32:11 | WARN | 192.168.1.10 | server01"
        );

        LogFileReader logFileReader = new LogFileReader();
        logFileReader.readLines(logs);

        assertEquals(1, logFileReader.getLogEntries().size());
        assertEquals(1, logFileReader.getMalformedLines().size());

        assertEquals(1, logFileReader.getLogEntries().get(0).getLineNumber());
        assertEquals(2, logFileReader.getMalformedLines().get(0).getLineNumber());

        assertEquals(
                logs.get(0),
                logFileReader.getLogEntries().get(0).getRawContent()
        );
        assertEquals(
                logs.get(1),
                logFileReader.getMalformedLines().get(0).getContent()
        );
    }
}