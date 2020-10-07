package com.example.damatch.model;

import com.example.damatch.SettingsActivity;
import com.example.damatch.flickr.FlickrManager;

/*
 Checks that order number supports draw pile sizes and draw pile sizes supports order number
 Checks that order and draw pile size can support the array of flickr images
 Stores information of selection
 */

public class SettingsLogic {
    FlickrManager flickrManager = FlickrManager.getInstance();

    public static final String DEFAULT_CARD_TYPE = "Sports";
    public static final String DEFAULT_GAME_MODE = "Without Text";
    public static final int DEFAULT_ORDER_NUM = 2;
    public static final String DRAW_PILE_SIZE = "All";
    public static final String DEFAULT_LEVEL = "Easy";

    private String cardType = DEFAULT_CARD_TYPE; // Default values
    private String gameMode = DEFAULT_GAME_MODE;
    private int orderNum = DEFAULT_ORDER_NUM;
    private String drawPileSize = DRAW_PILE_SIZE;
    private int drawPileSizeInt;
    private int maxDrawSize = 7;
    private int minArraySize;
    private String gameLevel = DEFAULT_LEVEL;

    private static SettingsLogic instance;
    public static SettingsLogic getInstance() {
        if (instance == null) {
            instance = new SettingsLogic();
        }
        return instance;
    }

    public boolean checkFlickrWithText() {
        if (SettingsActivity.getCardTypeEnum() == Card.DeckTypes.FLICKR_DECK && SettingsActivity.getGameModeEnum() == Card.GameMode.WITH_TEXT) {
            return true;
        }
        return false;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardType() {
        return this.cardType;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public String getGameMode () {
        return this.gameMode;
    }

    public void setLevels(String gameLevel){this.gameLevel = gameLevel;}

    public String getLevels(){return this.gameLevel;}

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
        this.maxDrawSize = getMaxDrawSize();
    }

    public int getOrderNum() {
        return this.orderNum;
    }

    public int getMaxDrawSize() {
        return orderNum * orderNum + orderNum + 1;
    }

    public void setMinArraySize () {
        this.minArraySize = this.orderNum * 2 + 1;
    }

    public int getMinArraySize() {
        return this.minArraySize;
    }

    public boolean checkFlickrArraySizeisEmpty() {
        if (flickrManager.imageIDArraySize() == 0) {
            return true;
        }
        return false;
    }

    public boolean checkMinFlickrArraySize() {
        if (flickrManager.imageIDArraySize() >= this.minArraySize) {
            return true;
        }
        return false;
    }

    public void setDrawPileSize(String drawNum) {
        this.drawPileSize = drawNum;
        this.drawPileSizeInt = convertDrawPileToInt(drawNum);
    }

    private int convertDrawPileToInt(String drawNum) {
        int size;
        if (!drawNum.equals("All")) {
            size = Integer.parseInt(drawNum);
        }else{
            size = getMaxDrawSize();
        }
        return size;
    }

    public String getDrawPileSize () {
        return this.drawPileSize;
    }

    public int getDrawPileSizeInt() {
        return this.drawPileSizeInt;
    }

    public boolean checkDrawPileMaxDraw () {
        if (this.drawPileSizeInt <= this.maxDrawSize) {
            return true;
        }
        return false;
    }

    public boolean checkDrawPileMaxDraw (String drawPileSize) {
        if (convertDrawPileToInt(drawPileSize) <= this.maxDrawSize) {
            return true;
        }
        return false;
    }
}
