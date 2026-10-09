package model;

public class Rule {
    private String level;
    private int score;

    public Rule(String level, int score){
        this.level = level;
        this.score = score;
    }

    public String getLevel(){
        return this.level;
    }

    public int getScore(){
        return this.score;
    }
}
