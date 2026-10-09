package model;

public class MalformedLine {
    // Declare the two private fields
    private int lineNumber;
    private String content;

    // Create a constructor that accepts both fields
    public MalformedLine(int lineNumber, String content){
        this.lineNumber = lineNumber;
        this.content = content;
    }
    // Create a getter for each field
    public int getLineNumber(){
        return this.lineNumber;
    }

    public String getContent(){
        return this.content;
    }
}
