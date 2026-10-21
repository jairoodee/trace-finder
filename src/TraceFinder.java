import java.io.IOException;

import analyzer.LogAnalyzer;
import analyzer.SeverityChecker;
import model.ParsedLogLine;
import reader.LogFileReader;
import reader.RuleBookReader;
import writer.ReportWriter;

public class TraceFinder {
  public static void main(String[] args){
        if(args.length != 3){
            System.out.println("Usage: java -jar tracefinder.jar <log-file> <rules-file> <report-file>");
            System.exit(1);
        }
        RuleBookReader newRuleBook = new RuleBookReader();
        try{
            newRuleBook.readCSV(args[1]);
        } catch(IOException e){
            System.out.println("Error: Could not read rulebook file: " + args[1]);
            System.exit(1);
        } catch(IllegalArgumentException e){
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }

        SeverityChecker newCheck = new SeverityChecker();
        LogAnalyzer newAnalysis = new LogAnalyzer();
        LogFileReader newlogReader = new LogFileReader();
        try{
            newlogReader.readFile(args[0]);
        } catch(IOException e) {
            System.out.println("Error: Could not read log file: " + args[0]);
            System.exit(1);
        }
        ReportWriter reportWriter = new ReportWriter();
       
        for(int i = 0; i < newlogReader.getLogEntries().size(); i++){
            ParsedLogLine newParsedLogLine = newlogReader.getLogEntries().get(i);
            newAnalysis.analyzeEntry(newCheck, 
                                     newParsedLogLine.getLogEntry(), 
                                     newRuleBook, 
                                     newParsedLogLine.getLineNumber(), 
                                     newParsedLogLine.getRawContent());
        }

        try{
            reportWriter.writeReport(
            newlogReader.getLogEntries(), 
            newlogReader.getMalformedLines(),
            newAnalysis.getFlaggedEntries(),
            newAnalysis.getUnknownPatterns(),
            newRuleBook, 
            args[2]);
        } catch(IOException e) {
            System.out.println("Error: Could not write report file: " + args[2]);
            System.exit(1);
        }
    }
}
