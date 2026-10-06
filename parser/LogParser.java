package parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import analyzer.LogAnalyzer;
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

    public static void main(String[] args){
        LogParser parser = new LogParser();

        String line = "2024-03-15T08:32:10 | WARN | 192.168.1.10 | server01";

        LogEntry log = parser.parse(line);

        // System.out.println(log.getTimestamp());
        // System.out.println(log.getLevel());
        // System.out.println(log.getSourceIP());
        // System.out.println(log.getTarget());
        // System.out.println(log.getAction());
        // System.out.println(log);

        if(log == null){
            System.out.println("This line is malformed");
        } else{
            System.out.println(log.getTimestamp());
        }
    }
}
