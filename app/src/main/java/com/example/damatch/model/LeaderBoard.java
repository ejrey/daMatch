package com.example.damatch.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
Leaderboard stores and saves the top 5 scores
*/

public class LeaderBoard {
    public static final int NUM_HIGH_SCORES = 5;
    private List<Score> topScores = new ArrayList<>();

    public void populateDefault(){
        topScores.add(new Score("Matt James", 68.9, "April 20, 2020"));
        topScores.add(new Score("Larry Anderson", 74.2, "December 15, 2020"));
        topScores.add(new Score("Jane Brown",  85.8, "July 1, 2020"));
        topScores.add(new Score("John Doe", 97.1, "May 22, 2020"));
        topScores.add(new Score("Barry Jones", 104.3, "October 24, 2019"));
    }

    // Creates new high score and places in array appropriately
    public boolean isNewHighScore(double newGameScore) {
        if(newGameScore == 0) {
            return false;
        }

        for(Score score : topScores) {
            if(score.getScore() > newGameScore) {
                return true;
            }
        }
        return false;
    }

    
    // Using comparator https://www.geeksforgeeks.org/comparator-interface-java/
    private class sortTime implements Comparator<Score> {
        @Override
        public int compare(Score o1, Score o2) {
            return Double.compare(o1.getScore(), o2.getScore());
        }
    }

    // Sorting the array
    public void recordNewHighScore(Score score) {
        topScores.add(score);
        Collections.sort(topScores, new sortTime());
        topScores.remove(NUM_HIGH_SCORES);
    }

    // Reset display of scores
    public void resetScores() {
        topScores.clear();
        populateDefault();
    }

    // Get Scores from array (As done in Assignment 2)
    public Score get(int i) {
        return topScores.get(i);
    }

    public void add(Score score) {
        topScores.add(score);
    }

    public List<Score> getTopScores() {
        return topScores;
    }

    public boolean topScoresIsEmpty(){
        return topScores.isEmpty();
    }

    // Singleton implementation
    private static LeaderBoard instance;
    private LeaderBoard() {
    }

    public static LeaderBoard getInstance() {
        if (instance == null) {
            instance = new LeaderBoard();
        }
        return instance;
    }
}
