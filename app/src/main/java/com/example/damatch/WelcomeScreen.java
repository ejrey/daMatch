package com.example.damatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/* Welcome screen displays authors and game title
* */

public class WelcomeScreen extends AppCompatActivity {
    private TextView BlackTitle, BlueTitle, PinkTitle;
    private ImageView leftCard, rightCard, magnifyingGlass;
    private Animation topAnim, bottomAnim;
    private WebView gifImage;
    private static int SPLASH_SCREEN = 15000;

    Handler handler = new Handler();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.welcome_screen);
            startAnimations();

           //Setup skip button if User wants to skip
            Button skip = findViewById(R.id.next_button);
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = MainActivity.makeIntent(WelcomeScreen.this);
                    startActivity(intent);
                    finish();
                    handler.removeCallbacksAndMessages(null);
                }
            });
            splashScreen();
    }

    private void startAnimations() {
        BlackTitle = findViewById(R.id.title_black);
        BlueTitle = findViewById(R.id.title_blue);
        PinkTitle = findViewById(R.id.title_pink);
        leftCard = findViewById(R.id.left_card);
        rightCard = findViewById(R.id.right_card);
        magnifyingGlass = findViewById(R.id.magni_glass);

        //code from: https://www.youtube.com/watch?v=n7X0iL7ZfjM
        gifImage = findViewById(R.id.gif_obj);
        MainActivity.setBackgroundGif(gifImage);

        //Code from https://www.youtube.com/watch?v=JLIFqqnSNmg
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        BlackTitle.setAnimation(topAnim);
        BlueTitle.setAnimation(topAnim);
        PinkTitle.setAnimation(topAnim);
        leftCard.setAnimation(bottomAnim);
        rightCard.setAnimation(bottomAnim);
        magnifyingGlass.setAnimation(bottomAnim);
    }

    private void splashScreen() {
       handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = MainActivity.makeIntent(WelcomeScreen.this);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        WelcomeScreen.this.finish();
        handler.removeCallbacksAndMessages(null);
    }
}