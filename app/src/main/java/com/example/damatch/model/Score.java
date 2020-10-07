package com.example.damatch.model;

/*
* Score class stores time, name and date of high score
* */

public class Score {
    private String name;
    private double score;
    private String date;

    public Score(String name, double score, String date) {
        this.name = name;
        this.score = score;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public double getScore() { return score; }

    public String getDate() { return date; }

    @Override
    public String toString() {
        return "Name: " + name + "\nScore: " + score + " seconds\nDate: " + date;
    }
}
