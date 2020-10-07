package com.example.damatch;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.damatch.model.Card;
import com.example.damatch.model.CardManager;
import com.example.damatch.flickr.FlickrManager;
import com.example.damatch.flickr.PhotoGalleryActivity;
import com.example.damatch.model.LeaderBoard;
import com.example.damatch.model.Score;
import com.example.damatch.model.SettingsLogic;

public class MainActivity extends AppCompatActivity {

    public static final int TOP_SCORE_SIZE = 5;
    public static final int TOP_FIVE_SCORES = 5;
    public static final int DEFAULT_INT = 0;
    private String[] namesList = new String[TOP_SCORE_SIZE];
    private Double[] timeList = new Double[TOP_SCORE_SIZE];
    private String[] dateList = new String[TOP_SCORE_SIZE];
    private FlickrManager flickrManager = FlickrManager.getInstance();
    private LeaderBoard leaderboard;
    private CardManager cardManager;
    private SettingsLogic settingsLogic = SettingsLogic.getInstance();

    private static final String APP_PREFS_ONE = "AppPrefsOne";
    private static final String APP_PREFS_TWO = "AppPrefsTwo";
    private static final String APP_PREFS_THREE = "AppPrefsThree";

    private static final String DIRECTORY_PREFS = "DirPrefs";
    private static final String DIRECTORY_KEY = "Directory";

    private static final String APP_PREFS_ONE_SIZE = "PrefsOneSize";
    private static final String APP_PREFS_TWO_SIZE = "PrefsTwoSize";
    private static final String APP_PREFS_THREE_SIZE = "PrefsThreeSize";

    private static final String ID_PREFS = "IDPrefs";
    private static final String ID_KEY = "ID";
    private static final String ID_KEY_SIZE = "ID_KEY_SIZE";

    private static final String APP_PREFS_FOUR = "State";
    private static final String VALUE = "checkState";

    public static Intent makeIntent(Context context){
        return new Intent(context, MainActivity.class);
    }

    public static Context context;
    public static Context getContext(){
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);
        loadSavedImages();

        context = getApplicationContext();

        leaderboard = LeaderBoard.getInstance();
        cardManager = CardManager.getInstance();

        setUpFlickrButton();
        setUpHelpButton();
        setupLeadBoardButton();
        setupSettingsButton();
        setupPlayButton();

        WebView webView = findViewById(R.id.bground_help);
        setBackgroundGif(webView);

        startAnimation();
        getSavedSettings();

        if(leaderboard.topScoresIsEmpty()){
            if(!(getSaveState(this))) {
                leaderboard.populateDefault();
                saveState(true);
            }else if(previousSavedScoreExist()) {
                leaderboard.populateDefault();
            }else{
                loadSavedScores();
            }
        }

    }

    private void getSavedSettings() {
        settingsLogic.setCardType(SettingsActivity.getCardType(this));
        settingsLogic.setGameMode(SettingsActivity.getGameMode(this));
        settingsLogic.setLevels(SettingsActivity.getGameLevel(this));
        settingsLogic.setOrderNum(SettingsActivity.getOrderNum(this));
        settingsLogic.setDrawPileSize(SettingsActivity.getDrawPileSize(this));
    }


    private void loadSavedImages() {
        SharedPreferences prefsDirectory = getSharedPreferences(DIRECTORY_PREFS, MODE_PRIVATE);
        SharedPreferences prefsID = getSharedPreferences(ID_PREFS, MODE_PRIVATE);

        int savedImageIDArraySize = prefsID.getInt(ID_KEY_SIZE, DEFAULT_INT);
        if(!(prefsDirectory.getString(DIRECTORY_KEY, "").equals(""))) {
            flickrManager.setPathToImageStorage(prefsDirectory.getString(DIRECTORY_KEY, ""));
        }

        if(savedImageIDArraySize > 0) {
            for(int i = 0; i < savedImageIDArraySize; i++) {
                flickrManager.addImageID(prefsID.getInt(ID_KEY + i, DEFAULT_INT));
            }
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    public static void setBackgroundGif(WebView v) {

        //Code from: https://www.youtube.com/watch?v=n7X0iL7ZfjM
        WebSettings webSettings = v.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String file = "file:android_asset/resized_background.gif";
        v.loadUrl(file);
        // Code from: https://stackoverflow.com/questions/2527899/disable-scrolling-in-webview
        v.setVerticalScrollBarEnabled(false);
        v.setHorizontalScrollBarEnabled(false);
        v.setOnTouchListener((v1, event) -> (event.getAction() == MotionEvent.ACTION_MOVE));

    }

    private void setUpFlickrButton() {
        ImageButton btn = findViewById(R.id.flickrBtn);
        btn.setOnClickListener(v -> {
            Intent intent = PhotoGalleryActivity.makeIntent(MainActivity.this);
            startActivity(intent);
        });
    }

    private void setupLeadBoardButton() {
        ImageButton btn = findViewById(R.id.leadButton);
        btn.setOnClickListener (v -> {
                Intent intent = LeaderBoardActivity.makeIntent(MainActivity.this);
                startActivity(intent);
        });
    }

    private void setupSettingsButton() {
        ImageButton btn = findViewById(R.id.btnSettings);
        btn.setOnClickListener(v -> {
            Intent intent = SettingsActivity.makeIntent(MainActivity.this);
            startActivity(intent);
        });
    }
    private void setUpHelpButton(){
        ImageButton btn = findViewById(R.id.btnHelp);
        btn.setOnClickListener(v -> {
            Intent intent = HelpScreen.makeIntent(MainActivity.this);
            startActivity(intent);
        });
    }

    private void setupPlayButton() {

        ImageButton btn = findViewById(R.id.btnPlay);
        btn.setOnClickListener(v -> {
            Card.DeckTypes deckType = SettingsActivity.getCardTypeEnum();

            if(deckType != Card.DeckTypes.FLICKR_DECK){
                    cardManager.createCards();
                    Intent intent = GameActivity.makeIntent(MainActivity.this);
                    startActivity(intent);
                }
                else if(flickrManager.imageIDArraySize() >= settingsLogic.getMaxDrawSize()) {
                    cardManager.createCards();
                    Intent intent = GameActivity.makeIntent(MainActivity.this);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Not enough Flickr Images", Toast.LENGTH_SHORT).show();
                }
        });
    }

    //Fade animation from: https://www.youtube.com/watch?v=f6n4jrx6J48
    private void startAnimation() {
        ImageView hotdogImg, baseballImg, appleImg, ttennisImg;
        Animation fadeIn1,fadeIn2,fadeIn3, fadeIn4,fadeIn5;

        fadeIn1  = AnimationUtils.loadAnimation(this, R.anim.fade_in_ani);
        fadeIn2  = AnimationUtils.loadAnimation(this, R.anim.fade_in_2);
        fadeIn3  = AnimationUtils.loadAnimation(this, R.anim.fade_in_3);
        fadeIn4  = AnimationUtils.loadAnimation(this, R.anim.fade_in_4);
        fadeIn5  = AnimationUtils.loadAnimation(this, R.anim.fade_in_5);

        ImageButton setbtn = findViewById(R.id.btnSettings);
        ImageButton helpbtn = findViewById(R.id.btnHelp);
        ImageButton playbtn = findViewById(R.id.btnPlay);
        hotdogImg = findViewById(R.id.hotdog);
        appleImg = findViewById(R.id.apple);
        baseballImg = findViewById(R.id.baseball);
        ImageButton leadbtn = findViewById(R.id.leadButton);
        ttennisImg = findViewById(R.id.ttennisButton);
        ImageButton flickrBtn = findViewById(R.id.flickrBtn);


        setbtn.setAnimation(fadeIn1);
        helpbtn.setAnimation(fadeIn3);
        playbtn.setAnimation(fadeIn2);
        appleImg.setAnimation(fadeIn1);
        hotdogImg.setAnimation(fadeIn2);
        baseballImg.setAnimation(fadeIn3);
        leadbtn.setAnimation(fadeIn4);
        ttennisImg.setAnimation(fadeIn4);
        flickrBtn.setAnimation(fadeIn5);
    }

    private void loadSavedScores() {
        for(int i = 0; i < TOP_FIVE_SCORES; i++) {
            namesList[i] = LeaderBoardActivity.getSaveNames(this)[i];
            timeList[i] =  Double.parseDouble(LeaderBoardActivity.getSaveTimes(this)[i]);
            dateList[i] = LeaderBoardActivity.getSaveDates(this)[i];
            leaderboard.add(new Score(namesList[i], timeList[i], dateList[i]));
        }
    }

    private boolean previousSavedScoreExist() {
        SharedPreferences prefsName = getSharedPreferences(APP_PREFS_ONE, MODE_PRIVATE);
        SharedPreferences prefsTime = getSharedPreferences(APP_PREFS_TWO, MODE_PRIVATE);
        SharedPreferences prefsDate = getSharedPreferences(APP_PREFS_THREE, MODE_PRIVATE);

        int savedNameSize = prefsName.getInt(APP_PREFS_ONE_SIZE, 0);
        int savedTimeSize = prefsTime.getInt(APP_PREFS_TWO_SIZE, 0);
        int savedDateSize = prefsDate.getInt(APP_PREFS_THREE_SIZE, 0);

        return savedNameSize == 0 && savedTimeSize == 0 && savedDateSize == 0;
    }

    public void saveState(boolean state) {
        SharedPreferences prefs = this.getSharedPreferences(APP_PREFS_FOUR, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(VALUE, state);
        editor.apply();
    }

    static public boolean getSaveState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS_FOUR, MODE_PRIVATE);
        return prefs.getBoolean(VALUE, false);
    }
}