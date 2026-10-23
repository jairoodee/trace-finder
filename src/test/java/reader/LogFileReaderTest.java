package reader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class LogFileReaderTest {

    @TempDir
    Path tempDir;

    /**
     * Verifies that a valid log line is stored as a parsed entry,
     * preserving its original line number and raw content.
     *
     * @throws IOException if writing or reading the temporary log file fails
     */
    @Test
    void readFile_validLine_storesParsedEntry() throws IOException {
        Path logFile = tempDir.resolve("valid.log");
        String newLog = "2024-03-15T08:32:10 | INFO | 10.0.0.1 | server01 | LOGIN";

        Files.writeString(logFile, newLog);

        LogFileReader logFileReader = new LogFileReader();
        logFileReader.readFile(logFile.toString());

        assertEquals(1, logFileReader.getLogEntries().size());
        assertEquals(1, logFileReader.getLogEntries().get(0).getLineNumber());
        assertEquals(newLog, logFileReader.getLogEntries().get(0).getRawContent());
    }

    /**
     * Verifies that a malformed log line is stored separately,
     * preserving its original line number and content.
     *
     * @throws IOException if writing or reading the temporary log file fails
     */
    @Test
    void readFile_malformedLine_storesMalformedRecord() throws IOException {
        Path logFile = tempDir.resolve("malformed.log");
        String newLog = "2024-03-15T08:32:10 | WARN | 192.168.1.10 | server01";

        Files.writeString(logFile, newLog);

        LogFileReader logFileReader = new LogFileReader();
        logFileReader.readFile(logFile.toString());

        assertEquals(1, logFileReader.getMalformedLines().size());
        assertEquals(1, logFileReader.getMalformedLines().get(0).getLineNumber());
        assertEquals(newLog, logFileReader.getMalformedLines().get(0).getContent());
    }

    /**
     * Verifies that valid and malformed lines in the same file are
     * separated correctly and retain their original line numbers and content.
     *
     * @throws IOException if writing or reading the temporary log file fails
     */
    @Test
    void readFile_mixedLines_separatesValidAndMalformedRecords() throws IOException {
        Path logFile = tempDir.resolve("mixed.log");
        List<String> logs = List.of(
            "2024-03-15T08:32:10 | INFO | 10.0.0.1 | server01 | LOGIN",
            "2024-03-15T08:32:11 | WARN | 192.168.1.10 | server01"
        );

        Files.write(logFile, logs);

        LogFileReader logFileReader = new LogFileReader();
        logFileReader.readFile(logFile.toString());

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
