package parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import model.LogEntry;
public class LogParser {
    public LogEntry parse(String line){
        String[] parts = line.split("\\s*\\|\\s*");

        if(parts.length != 5){
           return null;
        }
        
        LocalDateTime timestamp;

        try{
            timestamp = LocalDateTime.parse(parts[0]);
        } catch(DateTimeParseException e){
            return null;
        }
        LogEntry newLog = new LogEntry(timestamp, parts[1], parts[2], parts[3], parts[4]);
        return newLog;
    }
}
