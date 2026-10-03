package parser;

import java.util.Arrays;

public class LogParser {
    public LogEntry parse(String line){
        String[] parts = line.split("\\s*\\|\\s*");

        logEntry newLog = new LogEntry(parts[0], parts[1], parts[2], parts[3], parts[4]);
        return newLog;
     
    }
}
