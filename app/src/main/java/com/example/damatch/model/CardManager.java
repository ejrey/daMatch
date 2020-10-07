package com.example.damatch.model;

import com.example.damatch.GameActivity;
import com.example.damatch.MainActivity;
import com.example.damatch.SettingsActivity;
import com.example.damatch.flickr.FlickrManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import static java.lang.System.*;

/*
* Card Manager
Stores draw and discard piles
creates unique cards
Sets up the piles
Shuffles cards
get/set top cards in both piles
*/

public class CardManager {
    private  ArrayList<Card> discard = new ArrayList<>();
    private  ArrayList<Card> draw = new ArrayList<>();
    private ArrayList<Card> deck = new ArrayList<>();


    private int numCards;
    private int order; // 2,3 or 5

    private int[][] cards; //stores indices of a unique sequence of images of order n

    private CardManager(int order) {
        this.numCards = order * order + order + 1;
        this.order = order;
        cards = new int[numCards][order+1];
    }

    public void setPiles() { //populates draw and discard piles
        int drawPileSize = SettingsLogic.getInstance().getDrawPileSizeInt();
        //shuffle cards
        Collections.shuffle(deck);

        //add to piles
        draw.clear();
        discard.clear();
        discard.add(deck.get(0));
        for (int i = 1; i < drawPileSize; i++) {
            draw.add(deck.get(i));
        }
    }

    public void updateOrder(int order) {
        this.order = order;
        this.numCards = order * order + order + 1;
        this.cards = new int[numCards][order+1];
    }

    public int getOrder() {
        return order;
    }

    public Card getTopDiscard(){
        return discard.get(discard.size() - 1);
    }

    public void setTopDiscard(Card card){
        discard.add(card);
    }

    public  Card getTopDraw(){
        return draw.get(draw.size() - 1);
    }

    public int drawSize(){
        return draw.size();
    }

    public Card removeTopDraw(){
        Card card = draw.get(draw.size()-1);
        draw.remove(draw.size() - 1);
        return card;
    }

    public void createCards() {
        Card.DeckTypes cardType = SettingsActivity.getCardTypeEnum();
        Card.GameMode gameMode = SettingsActivity.getGameModeEnum();
        setImageIndices();

        //stores ids of unique sequence of images
        int[] ids = new int[order+1];

        //clear deck
        deck.clear();
        out.println("settings " + SettingsLogic.getInstance().getMaxDrawSize());
        //gets those unique ids from cards[][] then creates decks with those ids
        for(int i = 0; i < SettingsLogic.getInstance().getMaxDrawSize(); i++){
            //get ids for this card
            arraycopy(cards[i], 0, ids, 0, order+1);

            //create and add cards to deck
            deck.add(new Card(ids, cardType, gameMode, order+1));
        }
    }


    //Adapted from: https://github.com/WRadigan/pySpot-It/blob/master/pySpot-It.py
    private void setImageIndices(){
        int index = 0;
        for(int i = 0; i < order; i++){
            for(int j = 0; j<order; j++) {
                cards[i][j] = i*order+j;
            }
            cards[i][order] = order*order;
        }
        for(int i = 0; i < order; i++){
            for(int j = 0; j<order; j++) {
                for(int k=0; k<order; k++){
                    index = order*i+order+j;
                    cards[index][k] = (k * order) + (j + i * k ) % order;
                }
                cards[index][order] = order*order+1+i;
            }
        }

        for(int i = 0 ; i < order+1; i++){
            cards[cards.length - 1][i] = order * order + i;
        }
    }

    //Singleton Implementation
    private static CardManager instance;
    public static CardManager getInstance(){
        if (instance == null){
            instance =  new CardManager(SettingsActivity.getOrderNum(MainActivity.getContext()));
        }
        return instance;
    }

    public void setDrawPileSize(int drawSize) {
        this.numCards = drawSize;
        this.cards = new int[order*order+order+1][order+1];
    }

    public int getNumCards() {
        return numCards;
    }

    public Card getCard(int i ){
        return deck.get(i);
    }
}
