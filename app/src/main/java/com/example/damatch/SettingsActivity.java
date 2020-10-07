package com.example.damatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damatch.flickr.FlickrManager;
import com.example.damatch.model.Card;
import com.example.damatch.model.CardManager;

import com.example.damatch.model.DownloadImages;
import com.example.damatch.model.SettingsLogic;

import java.util.Set;

/* Allows User to change what deck they want to use
and potentially change other settings in the future
* */

public class SettingsActivity extends AppCompatActivity {
    WebView bground_gif;
    CardManager cardManager = CardManager.getInstance();
    FlickrManager flickrManager = FlickrManager.getInstance();
    SettingsLogic settingsLogic = SettingsLogic.getInstance();

    enum NumberOfCards {
        ALL_CARDS,
        TWENTY_CARDS,
        FIFTEEN_CARDS,
        TEN_CARDS,
        FIVE_CARDS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        createGameMode();
        createCardType();
        createOrderNum();
        createDrawPileSize();
        createLevels();
        setBackBtn();
        bground_gif = findViewById(R.id.bground_settngs);
        MainActivity.setBackgroundGif(bground_gif);
        setLinks();
        setupDownloadCards();
    }

    private void createLevels() {
        RadioGroup group = findViewById(R.id.radioG_levels);
        String[] GameLevels = getResources().getStringArray(R.array.gameLevels);
        for (final String Levels : GameLevels) {
            final RadioButton btn = new RadioButton(this);
            btn.setText(Levels);
            btn.setOnClickListener(v -> {
                settingsLogic.setLevels(Levels);
                saveGameLevel(Levels);
                }
            );
            group.addView(btn);
            // Select default button
            if (Levels.equals(getGameLevel(this))) {
                btn.setChecked(true);
            }
        }
    }

    private void setLinks() {
        TextView citations = findViewById(R.id.txt_citations);
        citations.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setBackBtn(){
       ImageButton btn = findViewById(R.id.backBtn);
       btn.setOnClickListener(v -> finish());
   }

    public static Intent makeIntent(Context context){
        return new Intent(context, SettingsActivity.class);
    }

    // code from https://www.youtube.com/watch?v=_yaP4etGKlU&feature=youtu.be
    private void createCardType() {
        RadioGroup group = findViewById(R.id.radioG_card_type);

        String[] PictureType = getResources().getStringArray(R.array.picture_type);

        for (final String picture : PictureType) {
            final RadioButton btn = new RadioButton(this);
            btn.setText(getString(R.string.deck_theme, picture));
            btn.setOnClickListener(v -> {
                settingsLogic.setCardType(picture);
                if (!picture.equals("Flickr")) {
                    saveCardType(picture);
                }
                else {
                    if (settingsLogic.checkFlickrWithText()) {
                        gameModeFalse(btn);
                    }
                    else if (settingsLogic.checkFlickrArraySizeisEmpty()) {
                        btn.setChecked(false);
                        Toast.makeText(SettingsActivity.this, "Add images from Flickr!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        saveCardType(picture);
                    }

                }

            });

            group.addView(btn);

            // Select default button
            if (picture.equals(getCardType(this))) {
                btn.setChecked(true);
            }
        }
    }

    private void gameModeFalse(RadioButton btn) {
        btn.setChecked(false);
        Toast.makeText(SettingsActivity.this, "Game mode unavailable", Toast.LENGTH_SHORT).show();
    }

    private void createGameMode() {
        RadioGroup group = findViewById(R.id.radioG_game_mode);
        String[] GameMode = getResources().getStringArray(R.array.gameMode);
        for (final String Mode : GameMode) {
            final RadioButton btn = new RadioButton(this);
            btn.setText(Mode);
            btn.setOnClickListener(v -> {
                settingsLogic.setGameMode(Mode);
                if (settingsLogic.checkFlickrWithText()) {
                    gameModeFalse(btn);
                } else {
                    saveGameMode(Mode);
                }
            });

            group.addView(btn);

            // Select default button
            if (Mode.equals(getGameMode(this))) {
                btn.setChecked(true);
            }
        }

    }

    private void saveGameMode(String mode){
        SharedPreferences prefs = this.getSharedPreferences("modeGame", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Mode", mode);
        editor.apply();
        settingsLogic.setGameMode(mode);
    }

    private void saveGameLevel(String level){
        SharedPreferences prefs = this.getSharedPreferences("gameLevel", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Level", level);
        editor.apply();
        settingsLogic.setGameMode(level);
    }

    static public String getGameMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("modeGame",MODE_PRIVATE);
        return prefs.getString("Mode", "Without Text");
    }

    static public String getGameLevel(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("gameLevel",MODE_PRIVATE);
        return prefs.getString("Level", "Easy");
    }

    static public GameActivity.GameLevel getGameLevelEnum()
    {
        SettingsLogic settingsLogic = SettingsLogic.getInstance();
        String gameLevelString = settingsLogic.getLevels();
        GameActivity.GameLevel gameLevel;
        gameLevel = convertGameLevelStringToEnum(gameLevelString);

        return gameLevel;
    }

    private static GameActivity.GameLevel convertGameLevelStringToEnum(String level) {
        GameActivity.GameLevel gameLevel;

        if (level.equals("Easy")){
            gameLevel = GameActivity.GameLevel.EASY_MODE;
        } else if (level.equals("Normal")){
            gameLevel = GameActivity.GameLevel.NORMAL_MODE;
        }else{
            gameLevel = GameActivity.GameLevel.HARD_MODE;
        }

        return gameLevel;
    }

    //find better way to get enum from saved
    static public Card.GameMode getGameModeEnum() {
        SettingsLogic settingsLogic = SettingsLogic.getInstance();
        String gameModeString = settingsLogic.getGameMode();
        Card.GameMode gameMode;

        gameMode = convertGameModeStringToEnum(gameModeString);

        return gameMode;
    }

    private static Card.GameMode convertGameModeStringToEnum(String cardtype){
        Card.GameMode gameMode;

        if (cardtype.equals("With Text")){
            gameMode = Card.GameMode.WITH_TEXT;
        } else {
            gameMode = Card.GameMode.WITHOUT_TEXT;
        }

        return gameMode;
    }

    // save value clicked
    private void saveCardType(String picture) {
        SharedPreferences prefs = this.getSharedPreferences("CardPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Card Type", picture);
        editor.apply();
        settingsLogic.setCardType(picture);
    }

    static public String getCardType(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CardPref",MODE_PRIVATE);
        return prefs.getString("Card Type", "Sports");
    }

    //find better way to get enum from saved
    static public Card.DeckTypes getCardTypeEnum() {
        SettingsLogic settingsLogic = SettingsLogic.getInstance();
        String deckTypeString = settingsLogic.getCardType();
        Card.DeckTypes deckType;

        deckType = convertCardTypeStringToEnum(deckTypeString);

        return deckType;
    }

    private static Card.DeckTypes convertCardTypeStringToEnum(String cardtype){
        Card.DeckTypes deckType;

        if (cardtype.equals("Sports")){
            deckType = Card.DeckTypes.SPORT_DECK;
        } else if (cardtype.equals("Food")) {
            deckType = Card.DeckTypes.FOOD_DECK;
        } else {
            deckType = Card.DeckTypes.FLICKR_DECK;
        }

        return deckType;
    }

    private void createOrderNum() {
        RadioGroup group = findViewById(R.id.radioG_card_order);

        int[] num_order = getResources().getIntArray(R.array.order_num);
        // Create buttons
        for (final int OrderNum : num_order) {
            final RadioButton btn = new RadioButton(this);
            btn.setText(getString(R.string.card_order, OrderNum));
            btn.setOnClickListener(v -> {

                if (getCardTypeEnum() != Card.DeckTypes.FLICKR_DECK) {
                    if (settingsLogic.getDrawPileSize().equals("All") || settingsLogic.checkDrawPileMaxDraw()) {
                        settingsLogic.setOrderNum(OrderNum);
                        saveOrderNum(OrderNum);
                        settingsLogic.setDrawPileSize("All");
                    }
                    else {
                        createFail(btn);
                    }
                }
                else {
                    if (settingsLogic.checkMinFlickrArraySize()) {
                        settingsLogic.setOrderNum(OrderNum);
                        saveOrderNum(OrderNum);
                    }
                    else {
                        btn.setChecked(false);
                        Toast.makeText(SettingsActivity.this, getString(R.string.need_more_images), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            group.addView(btn);
            if (OrderNum == getOrderNum(this)) {
                btn.setChecked(true);
            }
        }
    }

    private void saveOrderNum(int orderNum) {
        SharedPreferences prefs = this.getSharedPreferences("OrderPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Num order", orderNum);
        editor.apply();
        cardManager.updateOrder(orderNum);
        settingsLogic.setOrderNum(orderNum);
    }

    static public int getOrderNum(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("OrderPref", MODE_PRIVATE);
        return prefs.getInt("Num order", 2);
    }

    static public GameActivity.OrderNum getOrderNumEnum()
    {
        SettingsLogic settingsLogic = SettingsLogic.getInstance();
        int orderNumber = settingsLogic.getOrderNum();
        GameActivity.OrderNum getOrder;
        getOrder = convertOrderToEnum(orderNumber);

        return getOrder;
    }

    private static GameActivity.OrderNum convertOrderToEnum(int orderNumber) {
        GameActivity.OrderNum order;
        if (orderNumber == 2){
            order = GameActivity.OrderNum.ORDER_TWO;
        } else if (orderNumber == 3){
            order = GameActivity.OrderNum.ORDER_THREE;
        }else{
            order = GameActivity.OrderNum.ORDER_FIVE;
        }
        return order;
    }

    private void createDrawPileSize() {
        RadioGroup group = findViewById(R.id.radioG_draw_pile_size);

        String[] draw_pile_num = getResources().getStringArray(R.array.draw_pile_num);

        for (final String DrawNum : draw_pile_num) {
            final RadioButton btn = new RadioButton(this);
            btn.setText(getString(R.string.draw_pile_cards, DrawNum));
            btn.setOnClickListener(v -> {
                if (getCardTypeEnum() != Card.DeckTypes.FLICKR_DECK) {
                    if (DrawNum.equals("All") || settingsLogic.checkDrawPileMaxDraw(DrawNum)) {
                        settingsLogic.setDrawPileSize(DrawNum);
                        saveDrawNum(DrawNum);
                    }
                    else {
                        createFail(btn);
                    }
                }
                else {
                    if (settingsLogic.getDrawPileSizeInt() <= flickrManager.imageIDArraySize() &&
                        settingsLogic.checkDrawPileMaxDraw(DrawNum)) {
                        settingsLogic.setDrawPileSize(DrawNum);
                        saveDrawNum(DrawNum);
                    }
                    else if (DrawNum.equals("All") && settingsLogic.checkMinFlickrArraySize()) {
                        saveDrawNum(DrawNum);
                    }
                    else {
                        btn.setChecked(false);
                        Toast.makeText(SettingsActivity.this, getString(R.string.need_more_images), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            group.addView(btn);

            if (DrawNum.equals(getDrawPileSize(this))) {
                btn.setChecked(true);
            }
        }
    }

    private void saveDrawNum(String drawNum) {
        SharedPreferences prefs = this.getSharedPreferences("DrawPilePref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Draw pile num", drawNum);
        editor.apply();

        // set drawpilenum
        if (drawNum.equals("All")) {
            cardManager.setDrawPileSize(settingsLogic.getMaxDrawSize());
        }
        else {
            cardManager.setDrawPileSize(settingsLogic.getDrawPileSizeInt());
        }
        settingsLogic.setDrawPileSize(drawNum);
    }

    static public String getDrawPileSize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("DrawPilePref", MODE_PRIVATE);
        return prefs.getString("Draw pile num", "All");
    }


    private void createFail(RadioButton btn) {
        btn.setChecked(false);
        Toast.makeText(this, "Unable to create cards", Toast.LENGTH_SHORT).show();
    }

    private void setupDownloadCards() {
        ImageButton btn = findViewById(R.id.btn_download);
        btn.setOnClickListener(v -> {
            int order = cardManager.getOrder();
            cardManager.createCards();
            int numCards = order * order + order + 1;
            Bitmap bitmap;
            String newDirectory = "";
            try {
                for(int i = 0; i < numCards; i++) {
                    DownloadImages downloadImages = new DownloadImages();
                    bitmap = downloadImages.createBitmapFromCard(cardManager.getCard(i));
                    newDirectory = getCardType(SettingsActivity.this) + " " + getGameMode(SettingsActivity.this) + " Order-" + order + " " + getGameLevel(SettingsActivity.this);
                    downloadImages.saveImageToExternalStorage(SettingsActivity.this, newDirectory, "Card_" + i +".png", bitmap);
                }
                Toast.makeText(SettingsActivity.this, "Saved Cards in Directory: " + newDirectory, Toast.LENGTH_SHORT).show();
            } catch(Exception e){
                Toast.makeText(SettingsActivity.this, "Failed to Download Images", Toast.LENGTH_LONG).show();
            }
        });
    }

}
