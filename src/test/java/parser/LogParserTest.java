package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import model.LogEntry;

class LogParserTest{
    @Test
    void parse_validLine_returnsLogEntry(){
        LogParser logParserObject = new LogParser();
        LogEntry parsedResult =  logParserObject.parse("2024-03-15T08:32:10 | INFO | 10.0.0.1 | server01 | LOGIN");
        assertNotNull(parsedResult);
        assertEquals("INFO", parsedResult.getLevel());
        assertEquals("10.0.0.1", parsedResult.getSourceIP());
        assertEquals("server01", parsedResult.getTarget());
        assertEquals("LOGIN", parsedResult.getAction());
        assertEquals(LocalDateTime.parse("2024-03-15T08:32:10"), parsedResult.getTimestamp());
    }

    @Test
    void parse_missingField_returnsNull(){
        LogParser logParserObject = new LogParser();
        LogEntry parsedResult =  logParserObject.parse("2024-03-15T08:32:10 | WARN | 192.168.1.10 | server01");
        assertNull(parsedResult);
    }

    @Test
    void parse_invalidTimestamp_returnsNull(){
        LogParser logParserObject = new LogParser();
        LogEntry parsedResult =  logParserObject.parse("not-a-timestamp | WARN | 192.168.1.10 | server01 | Login failed");
        assertNull(parsedResult);
    }

    @Test
    void parse_blankLine_returnsNull(){
        LogParser logParserObject = new LogParser();
        assertNull(logParserObject.parse(""));
    }
}
