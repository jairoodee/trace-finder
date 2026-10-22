
package reader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class RuleBookReaderTest {

    @TempDir
    Path tempDir;

    /**
     * Verifies that a valid CSV rulebook loads its rule with the expected
     * level and severity score.
     *
     * @throws IOException if writing or reading the temporary CSV file fails
     */
    @Test
    void readCSV_validFile_loadsRules() throws IOException {
        Path ruleFile = tempDir.resolve("rules.csv");
        String newRule = "INFO,1";

        Files.writeString(ruleFile, newRule);

        RuleBookReader ruleBookReader = new RuleBookReader();
        ruleBookReader.readCSV(ruleFile.toString());

        assertEquals(1, ruleBookReader.getRules().size());
        assertEquals("INFO", ruleBookReader.getRules().get(0).getLevel());
        assertEquals(1, ruleBookReader.getRules().get(0).getScore());
    }

    /**
     * Verifies that an empty CSV rulebook is rejected.
     *
     * @throws IOException if writing or reading the temporary CSV file fails
     */
    @Test
    void readCSV_emptyFile_throwsIllegalArgumentException() throws IOException {
        Path emptyFile = tempDir.resolve("empty.csv");
        RuleBookReader ruleBookReader = new RuleBookReader();

        Files.writeString(emptyFile, "");

        assertThrows(IllegalArgumentException.class, () -> {
            ruleBookReader.readCSV(emptyFile.toString());
        });
    }

    /**
     * Verifies that a non-numeric severity score is rejected and the
     * exception message identifies the file and row.
     *
     * @throws IOException if writing or reading the temporary CSV file fails
     */
    @Test
    void readCSV_invalidSeverityScore_throwsNumberFormatException() throws IOException {
        Path invalidScoreFile = tempDir.resolve("invalid-score.csv");
        String newRule = "INFO,high";

        Files.writeString(invalidScoreFile, newRule);

        RuleBookReader ruleBookReader = new RuleBookReader();
        NumberFormatException exception = assertThrows(
            NumberFormatException.class,
            () -> ruleBookReader.readCSV(invalidScoreFile.toString())
        );

        assertTrue(exception.getMessage().contains("invalid-score.csv"));
        assertTrue(exception.getMessage().contains("on row 1"));
    }

    /**
     * Verifies that a CSV row with an incorrect number of fields is rejected
     * and the exception message identifies the file and row.
     *
     * @throws IOException if writing or reading the temporary CSV file fails
     */
    @Test
    void readCSV_invalidFormat_throwsIllegalArgumentException() throws IOException {
        Path invalidFormatFile = tempDir.resolve("invalid-format.csv");
        String newRule = "INFO,1,EXTRA";

        Files.writeString(invalidFormatFile, newRule);

        RuleBookReader ruleBookReader = new RuleBookReader();
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ruleBookReader.readCSV(invalidFormatFile.toString())
        );

        assertTrue(exception.getMessage().contains("invalid-format.csv"));
        assertTrue(exception.getMessage().contains("on row 1"));
    }

    /**
     * Verifies that duplicate rule levels are rejected and the exception
     * message identifies the row containing the duplicate.
     *
     * @throws IOException if writing or reading the temporary CSV file fails
     */
    @Test
    void readCSV_duplicateLevel_throwsIllegalArgumentException() throws IOException {
        Path duplicateFile = tempDir.resolve("duplicate.csv");
        List<String> lines = List.of(
            "INFO,1",
            "INFO,2"
        );

        Files.write(duplicateFile, lines);

        RuleBookReader ruleBookReader = new RuleBookReader();
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ruleBookReader.readCSV(duplicateFile.toString())
        );

        assertTrue(exception.getMessage().contains("duplicate.csv"));
        assertTrue(exception.getMessage().contains("on row 2"));
    }
}
