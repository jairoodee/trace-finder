package model;

import java.time.LocalDateTime;

public class LogEntry {
    private LocalDateTime timestamp;
    private String level;
    private String sourceIP;
    private String target;
    private String action;

    public LogEntry(LocalDateTime timestamp, String level, String sourceIP, String target, String action){
        this.timestamp = timestamp;
        this.level = level;
        this.sourceIP = sourceIP;
        this.target = target;
        this.action = action;
    }

    public LocalDateTime getTimestamp(){
        return this.timestamp;
    }

    public String getLevel(){
        return this.level;
    } 
    
    public String getSourceIP(){
        return this.sourceIP;
    } 

    public String getTarget(){
        return this.target;
    } 

    public String getAction(){
        return this.action;
    } 
}
