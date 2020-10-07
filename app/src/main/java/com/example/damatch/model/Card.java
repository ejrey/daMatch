package com.example.damatch.model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.example.damatch.MainActivity;
import com.example.damatch.R;
import com.example.damatch.flickr.FlickrManager;

import java.util.Random;

/*
*  Card Class
Stores array of image references
Creates new cards
* */

public class Card {

    private FlickrManager flickrManager = FlickrManager.getInstance();
    private Context context = MainActivity.getContext();

    //Store Indices of items added to imageIds
    public int[] curIndex;
    private  int numImages;
    private int[] imageIds;

    private TypedArray sportsImages = context.getResources().obtainTypedArray(R.array.sports_images);
    private TypedArray sportsText = context.getResources().obtainTypedArray(R.array.sports_text);
    private TypedArray foodImages = context.getResources().obtainTypedArray(R.array.food_images);
    private TypedArray  foodText = context.getResources().obtainTypedArray(R.array.food_text);
    private int[] flickrDeck;

    public enum DeckTypes{
        SPORT_DECK,
        FOOD_DECK,
        FLICKR_DECK
    }

    public enum GameMode{
        WITH_TEXT,
        WITHOUT_TEXT
    }


    public Card(int[] permutedIndexes, DeckTypes chosenDeck, GameMode chosenMode, int numImages) {
        imageIds = new int[numImages];
        curIndex = new int[numImages];
        this.numImages = numImages;

        //add image ids to imageids[]
        populateImageIds(permutedIndexes, chosenDeck);

        //add text if necessary
        if(chosenMode == GameMode.WITH_TEXT){
            for (int i = 0; i < Math.floorDiv(this.numImages, 2); i++) {
                randomlyAddText(permutedIndexes, chosenDeck);
            }
        }
    }

    private void populateImageIds(int[] permutedIndexes, DeckTypes chosenDeck) {
        for (int i = 0; i < permutedIndexes.length; i++) {
            if (chosenDeck == DeckTypes.SPORT_DECK) {
                imageIds[i] = sportsImages.getResourceId(permutedIndexes[i], 0);
                curIndex[i] = permutedIndexes[i];

            } else if(chosenDeck == DeckTypes.FOOD_DECK) {
                imageIds[i] = foodImages.getResourceId(permutedIndexes[i], 0);
                curIndex[i] = permutedIndexes[i];

            } else {
                flickrDeck = flickrManager.transferImageID();
                imageIds[i] = flickrDeck[permutedIndexes[i]];
                curIndex[i] = permutedIndexes[i];
            }
        }
    }

    private void randomlyAddText(int[] permutedIndexes, DeckTypes deckType) {
        Random random = new Random();
        int ranIndex = random.nextInt(permutedIndexes.length);

        for (int i = 0; i < permutedIndexes.length; i++) {
            int permutedIndex = permutedIndexes[ranIndex];
            if(deckType == DeckTypes.FOOD_DECK) {
                imageIds[ranIndex] = foodText.getResourceId(permutedIndex, 0);
                } else {
                imageIds[ranIndex] = sportsText.getResourceId(permutedIndex, 0);
            }
        }
    }

    public int[] getCardImages() {
        return imageIds;
    }

    public boolean cardIncludes(int ids) {
       for (int i = 0; i < numImages; i++) {
           if(curIndex[i]==ids){
               return true;
           }
       }
       return false;
    }
}
