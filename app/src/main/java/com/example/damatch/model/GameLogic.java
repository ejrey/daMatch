package com.example.damatch.model;

import java.util.Date;

/*  Game Logic starts and stops the time, moves cards between decks and creates new high score if necessary
* */

public class GameLogic {
    private   CardManager cardManager = CardManager.getInstance();
    private long startTimeInMs;
    private long endTimeInMs;
    public static Card card;
    public void startTimeInMs(){
        Date date = new Date();
        startTimeInMs = date.getTime();
    }

    public void endTimeInMs(){
        Date date = new Date();
        endTimeInMs = date.getTime();
    }

    public long getGameDuration(){
        return endTimeInMs - startTimeInMs;
    }

    // Moves top of draw pile to top of discard pile
    //if no more cards, end game
    public void discardTopDrawCard() {
        Card card2 = cardManager.getTopDraw();
        cardManager.setTopDiscard(card2);
        cardManager.removeTopDraw();
    }

}
