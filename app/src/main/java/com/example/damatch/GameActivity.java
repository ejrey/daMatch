package com.example.damatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.damatch.flickr.FlickrManager;
import com.example.damatch.model.Card;
import com.example.damatch.model.CardManager;
import com.example.damatch.model.GameLogic;
import com.example.damatch.model.LeaderBoard;
import com.example.damatch.model.Score;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/* Game Class controls UI on the game activity
- Shows draw and discard fragments
- starts timer when card is clicked
- resets cards when last card is reached
* */

public class GameActivity extends AppCompatActivity {
    private long time;

    private GameLogic gameLogic = new GameLogic();
    private CardManager cardManager = CardManager.getInstance();
    private LeaderBoard leaderboard = LeaderBoard.getInstance();
    private FlickrManager flickrManager = FlickrManager.getInstance();

    private ArrayList<Integer> rotationNum = new ArrayList<>();
    private ArrayList<Double> resizingNum = new ArrayList<>();

    private final int order = cardManager.getOrder();
    public static Intent makeIntent(Context context){
        return new Intent(context, GameActivity.class);
    }

    public enum GameLevel{
        EASY_MODE,
        NORMAL_MODE,
        HARD_MODE,
    }

    public enum OrderNum{
        ORDER_TWO,
        ORDER_THREE,
        ORDER_FIVE,
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //resource: android documentation
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        TextView textView = findViewById(R.id.txt_score);
        textView.setText(R.string.tap_to_play);

        setBackbtn();
        startOnClick();

    }

    private void setCardFragments() {
        Fragment drawCardFragment = new DrawCardFragment();
        Fragment discardFragment = new DiscardFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_draw_card, drawCardFragment);
        fragmentTransaction.add(R.id.fragment_discard, discardFragment).commit();
    }

    private void setBackbtn() {
        ImageButton btn = findViewById(R.id.backBtnn);
        btn.setOnClickListener(v -> finish());
    }

    public void initializeRotationNum()
    {
        int angle = 0;
        for (int i =0 ; i <= order; i++)
            angle = (int) (Math.random() * 360);
            rotationNum.add(angle);
    }
    public void initializeResizing()
    {
        double size = 1.0;
        for (int i =0 ; i <= order; i++)
            size = (Math.random() * 0.4) + 0.8;
            resizingNum.add(size);
    }

    //what to do when image clicked is correct
    private void imageMatch() {
        TextView remainingCards = findViewById(R.id.txt_remaining_cards);
        if (cardManager.drawSize() > 1) {
            //play 'correct' sound
            MediaPlayer correctMP = MediaPlayer.create(this, R.raw.correct);
            correctMP.start();

            gameLogic.discardTopDrawCard();
            if(SettingsActivity.getCardTypeEnum() == Card.DeckTypes.FLICKR_DECK) {
                setNewFlickrImages();
            }else {
                setNewImages();
            }

            remainingCards.setText(getString(R.string.remaining_cards, cardManager.drawSize()));
        }
        else {
            remainingCards.setText(getString(R.string.remaining_cards, cardManager.drawSize()-1));
            endGame();
        }
    }


    //populate imageviews on the cards
    private void setNewImages() {
        final Card topDiscard = cardManager.getTopDiscard(); //get top card
        final int[] discardImages = topDiscard.getCardImages(); //get images off cards

        final Card topDraw = cardManager.getTopDraw(); //get top card
        final int[] drawCardImages = topDraw.getCardImages(); //get images off cards

        //set up imageView's on discard card
        ImageView imageView;
        for (int i = 0; i <= order; i++) {
            String name = "img_discard_order_" + order + "_" + i;
            int id = getResources().getIdentifier(name, "id", this.getPackageName());

            imageView = findViewById(id);
            imageView.setImageResource(discardImages[i]);

            if (SettingsActivity.getGameLevelEnum() == GameLevel.NORMAL_MODE){
                initializeRotationNum();
                rotateImageViews(imageView, i);
            } else if (SettingsActivity.getGameLevelEnum() == GameLevel.HARD_MODE){
                initializeRotationNum();
                rotateImageViews(imageView, i);
                initializeResizing();
                scaleImageViews(imageView, i);
            }
        }
        rotationNum.clear();
        resizingNum.clear();

        //set up imageButtons on draw card
        for (int i = 0; i <= order; i++) {
            String name = "img_draw_order_" + order + "_" + i;
            int id = getResources().getIdentifier(name, "id", this.getPackageName());

            ImageButton imagebutton = findViewById(id);
            imagebutton.setImageResource(drawCardImages[i]);

            final int finalI = i;
            imagebutton.setOnClickListener(v -> {
                if (topDiscard.cardIncludes(topDraw.curIndex[finalI])) {
                    imageMatch();
                } else {
                    MediaPlayer incorrectMP = MediaPlayer.create(this, R.raw.incorrect);
                    incorrectMP.start();
                }
            });

            if (SettingsActivity.getGameLevelEnum() == GameLevel.NORMAL_MODE){
                rotateImageButtons(imagebutton);
            } else if (SettingsActivity.getGameLevelEnum() == GameLevel.HARD_MODE){
                rotateImageButtons(imagebutton);
                scaleImageButtons(imagebutton);
            }
        }
    }

    private void setNewFlickrImages() {
        final Card topDiscard = cardManager.getTopDiscard(); //get top card
        final int[] discardImages = topDiscard.getCardImages(); //get images off cards

        final Card topDraw = cardManager.getTopDraw(); //get top card
        final int[] drawCardImages = topDraw.getCardImages(); //get images off cards

        ImageView imageView;
        for(int i = 0; i <= order; i++){
            String name = "img_discard_order_" + order + "_" + i;
            int id = getResources().getIdentifier(name, "id", this.getPackageName());

            imageView = findViewById(id);
            String discardImageFormat = discardImages[i] + ".jpg";
            //Load Image here
            Bitmap bitmap = getImageBitmap(flickrManager.getPathToImageStorage(), discardImageFormat);
            imageView.setImageBitmap(bitmap);

             if (SettingsActivity.getGameLevelEnum() == GameLevel.NORMAL_MODE){
                initializeRotationNum();
                rotateImageViews(imageView, i);
            }
            else if(SettingsActivity.getGameLevelEnum() == GameLevel.HARD_MODE){
                initializeRotationNum();
                rotateImageViews(imageView, i);
                initializeResizing();
                scaleImageViews(imageView, i);
            }
        }
        rotationNum.clear();
        resizingNum.clear();


        //set up imageButtons on draw card
        for(int i = 0; i <= order; i++) {
            String name = "img_draw_order_" + order + "_" + i;
            int id = getResources().getIdentifier(name, "id", this.getPackageName());

            String drawCardImageFormat = drawCardImages[i] + ".jpg";

            // Load image here
            ImageButton imagebutton = findViewById(id);
            Bitmap bitmap = getImageBitmap(flickrManager.getPathToImageStorage(), drawCardImageFormat);
            imagebutton.setImageBitmap(bitmap);
                final int finalI = i;
                imagebutton.setOnClickListener(v -> {
                    if (topDiscard.cardIncludes(topDraw.curIndex[finalI]))
                        imageMatch();
                });

            if (SettingsActivity.getGameLevelEnum() == GameLevel.NORMAL_MODE) {
                rotateImageButtons(imagebutton);
            }
            else if(SettingsActivity.getGameLevelEnum() == GameLevel.HARD_MODE){
                rotateImageButtons(imagebutton);
                scaleImageButtons(imagebutton);
            }
        }
    }


    public void rotateImageViews(ImageView image, int i) { ;
        int degree = rotationNum.get(i);
        image.setRotation(degree);
    }
    public void rotateImageButtons(ImageButton imageButton) {
        Random rand = new Random();
        int randomDegree = rand.nextInt(360);
        imageButton.setRotation(randomDegree);
        rotationNum.add(randomDegree);
    }

    public void scaleImageButtons(ImageButton imageButton) {
        double size = (Math.random() * 0.5) + 0.7;
        resizingNum.add(size);
        int height = imageButton.getLayoutParams().height;
        int width = imageButton.getLayoutParams().width;
        imageButton.getLayoutParams().height = (int) (height*size);
        imageButton.getLayoutParams().width = (int) (width*size);
        imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public void scaleImageViews(ImageView images, int i) {
        double scale = resizingNum.get(i);
        int height = images.getLayoutParams().height;
        int width = images.getLayoutParams().width;
        images.getLayoutParams().height = (int) (height*scale);
        images.getLayoutParams().width = (int) (width*scale);
        images.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    // Code taken from https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    public static Bitmap getImageBitmap(String directoryPath, String imageID) {
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        try{
            File file = new File(directoryPath, imageID);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void startOnClick() {

        cardManager.setPiles(); //creates draw and discard piles with chosen deck
        setCardFragments();
        if(SettingsActivity.getCardTypeEnum() == Card.DeckTypes.FLICKR_DECK) {
            setNewFlickrImages();
        }else{
            setNewImages();
        }

        final ImageButton button = findViewById(R.id.draw_card_back);
        button.setOnClickListener(v -> {
            MediaPlayer incorrectMP = MediaPlayer.create(this, R.raw.start_game);
            incorrectMP.start();

            TextView cardsRemaining = findViewById(R.id.txt_remaining_cards);
            cardsRemaining.setVisibility(View.VISIBLE);
            cardsRemaining.setText(getString(R.string.remaining_cards, cardManager.drawSize()));

            gameLogic.startTimeInMs();
            button.setBackgroundResource(R.drawable.deck_front);
            button.setElevation(-1);
            button.setClickable(false);

            ImageView imageView;
            for(int i = 0; i < order +1 ; i++){
                String name = "img_draw_order_" + order + "_" + i;
                int id = getResources().getIdentifier(name, "id", GameActivity.this.getPackageName());

                imageView = findViewById(id);
                imageView.setVisibility(View.VISIBLE);
            }

            TextView score = findViewById(R.id.txt_score);
            score.setText("");

        });
    }

    private void endGame() {
        //play 'you win' sound
        MediaPlayer correctMP = MediaPlayer.create(this, R.raw.you_win);
        correctMP.start();

        //get time
        gameLogic.endTimeInMs();
        time = gameLogic.getGameDuration();
        double convertTime = time / 1000.0;
        double score = getScore(convertTime);

        TextView textView = findViewById(R.id.txt_score);
        textView.setText(getString(R.string.score, score));

        resetBoard();

        if(leaderboard.isNewHighScore(score)) {
            //get name
            Intent intent = EditName.makeIntent(this);
            startActivityForResult(intent, 1);
        }
    }

    //stores highscores after name has been entered
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //get name
                String name = data.getStringExtra("name");

                //get time
                double convertTime = time / 1000.0;
                double score = getScore(convertTime);

                TextView textView = findViewById(R.id.txt_score);
                textView.setText(getString(R.string.score, score));

                //get date
                //resource: https://stackoverflow.com/questions/8654990/how-can-i-get-current-date-in-android/15698784
                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                String formattedDate = df.format(c);

                //create new high score
                leaderboard.recordNewHighScore(new Score(name, score, formattedDate));
            }
            finish();
        }
    }

    private double getScore(double convertTime) {
        int numCards = cardManager.getNumCards();
        int maxCards = order*order+order+1;
        double factor = Math.sqrt((maxCards+1)-numCards);

        double score = convertTime;
        if (order == 3) {
            score = 4 * convertTime;
        } else if (order == 2) {
            score = 12 * convertTime;
        }

        if(SettingsActivity.getGameModeEnum() == Card.GameMode.WITH_TEXT){
         score = score/2;
        }

        return score * factor;
    }

    private void resetBoard() {

        cardManager.setPiles();

        ImageButton imageButton = findViewById(R.id.draw_card_back);
        imageButton.setBackgroundResource(R.drawable.deck_back);
        imageButton.setClickable(true);

        //hide images until restart game
        ImageView imageView;
        for(int i = 0; i < order +1 ; i++){
            String name = "img_draw_order_" + order + "_" + i;
            int id = getResources().getIdentifier(name, "id", GameActivity.this.getPackageName());

            imageView = findViewById(id);
            imageView.setVisibility(View.GONE);
        }

        TextView cardsRemaining = findViewById(R.id.txt_remaining_cards);
        cardsRemaining.setVisibility(View.GONE);
    }
}