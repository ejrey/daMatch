package com.example.damatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class HelpScreen extends AppCompatActivity {

    public static Intent makeIntent(Context context){
        return new Intent(context, HelpScreen.class);
    }


    ImageView fingerPoint;
    WebView background;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);

        setBackbtn();
        setScoringGuideBtn();

        //Rotating the image by a certain angle
        fingerPoint = findViewById(R.id.finger_point);
        fingerPoint.setRotation(120);
        background = findViewById(R.id.bground_help);

        MainActivity.setBackgroundGif(background);

    }

    private void setScoringGuideBtn() {
        ImageButton btn = findViewById(R.id.btn_scoring_guide);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                ScoringGuide dialog = new ScoringGuide();
                dialog.show(manager, "ScoringGuide");
            }
        });
    }

    private void setBackbtn() {
        ImageButton btn = findViewById(R.id.backBtn_help);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}