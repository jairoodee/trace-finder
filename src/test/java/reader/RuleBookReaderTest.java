package reader;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests rulebook validation using CSV lines constructed in memory.
 */
class RuleBookReaderTest {

    /**
     * Verifies that valid CSV lines produce rules with the expected levels
     * and severity scores.
     */
    @Test
    void readLines_validInput_loadsRules() {
        List<String> lines = List.of(
                "level,severity_score",
                "INFO,1",
                "WARNING,2"
        );

        RuleBookReader reader = new RuleBookReader();
        reader.readLines(lines);

        assertEquals(2, reader.getRules().size());
        assertEquals("INFO", reader.getRules().get(0).getLevel());
        assertEquals(1, reader.getRules().get(0).getScore());
        assertEquals("WARNING", reader.getRules().get(1).getLevel());
        assertEquals(2, reader.getRules().get(1).getScore());
    }

    /**
     * Verifies that input without any lines is rejected.
     */
    @Test
    void readLines_emptyInput_throwsIllegalArgumentException() {
        RuleBookReader reader = new RuleBookReader();

        assertThrows(
                IllegalArgumentException.class,
                () -> reader.readLines(List.of())
        );
    }

    /**
     * Verifies that a header without any rules is rejected.
     */
    @Test
    void readLines_headerOnly_throwsIllegalArgumentException() {
        RuleBookReader reader = new RuleBookReader();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reader.readLines(List.of("level,severity_score"))
        );

        assertTrue(exception.getMessage().contains("no rules"));
    }

    /**
     * Verifies that a non-numeric severity score is rejected and the
     * error identifies the offending row.
     */
    @Test
    void readLines_invalidSeverityScore_throwsNumberFormatException() {
        RuleBookReader reader = new RuleBookReader();
        List<String> lines = List.of(
                "level,severity_score",
                "INFO,high"
        );

        NumberFormatException exception = assertThrows(
                NumberFormatException.class,
                () -> reader.readLines(lines)
        );

        assertTrue(exception.getMessage().contains("on row 2"));
    }

    /**
     * Verifies that a row with more than two fields is rejected and
     * the error identifies the offending row.
     */
    @Test
    void readLines_invalidFormat_throwsIllegalArgumentException() {
        RuleBookReader reader = new RuleBookReader();
        List<String> lines = List.of(
                "level,severity_score",
                "INFO,1,EXTRA"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reader.readLines(lines)
        );

        assertTrue(exception.getMessage().contains("on row 2"));
    }

    /**
     * Verifies that duplicate rule levels are rejected and the error
     * identifies the row containing the duplicate.
     */
    @Test
    void readLines_duplicateLevel_throwsIllegalArgumentException() {
        RuleBookReader reader = new RuleBookReader();
        List<String> lines = List.of(
                "level,severity_score",
                "INFO,1",
                "INFO,2"
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reader.readLines(lines)
        );

        assertTrue(exception.getMessage().contains("on row 3"));
    }
}