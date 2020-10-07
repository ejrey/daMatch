package com.example.damatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.damatch.model.LeaderBoard;
import com.example.damatch.model.Score;


public class LeaderBoardActivity extends AppCompatActivity {
    private static final String APP_PREFS_ONE = "AppPrefsOne";
    private static final String APP_PREFS_TWO = "AppPrefsTwo";
    private static final String APP_PREFS_THREE = "AppPrefsThree";

    private static final String APP_PREFS_ONE_SIZE = "PrefsOneSize";
    private static final String APP_PREFS_TWO_SIZE = "PrefsTwoSize";
    private static final String APP_PREFS_THREE_SIZE = "PrefsThreeSize";

    private static final String KEY_ONE = "Names";
    private static final String KEY_TWO = "Times";
    private static final String KEY_THREE = "Dates";
    private LeaderBoard leaderboard;
    private ArrayAdapter<Score> adapter;
    private String[] myItems = new String[5];
    private String[] namesList = new String[5];
    private String[] timeList = new String[5];
    private String[] dateList = new String[5];
    WebView gifImage;

    public static Intent makeIntent(Context context) {
        return new Intent(context, LeaderBoardActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        gifImage = findViewById(R.id.boardBack);
        MainActivity.setBackgroundGif(gifImage);

        leaderboard = LeaderBoard.getInstance();
        display();
        setUpResetButton();
        setUpBackButton();
    }

    private void display() {
        int index = 1;
        for(int i = 0; i < 5; i++) {
            Score score = leaderboard.get(i);
            myItems[i] = index + ". " + score.toString();
            index++;
        }

        for(int i = 0; i < 5; i++) {
            Score score = leaderboard.get(i);
            namesList[i] = score.getName();
            double timeTemp = score.getScore();
            timeList[i] = Double.toString(timeTemp);
            dateList[i] = score.getDate();
        }

        //create adapter and populate list
        adapter = new MyListAdapter();
        ListView list = findViewById(R.id.scoresList);
        list.setAdapter(adapter);

        saveNames(namesList);
        saveTimes(timeList);
        saveDates(dateList);
    }

    private class MyListAdapter extends ArrayAdapter<Score> {
        MyListAdapter(){
            super(LeaderBoardActivity.this, R.layout.activity_list, leaderboard.getTopScores());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.activity_list , parent, false);
            }

            //find car to work with
            Score currentScore = leaderboard.get(position);

            //fill the view
            //name
            TextView nameText = itemView.findViewById(R.id.list_item_name);
            nameText.setText(currentScore.getName());

            //Score
            TextView scoreText = itemView.findViewById(R.id.list_item_score);
            String currentTimeString = Double.toString(currentScore.getScore());
            scoreText.setText(getString(R.string.leaderboard_points, currentTimeString));

            //Date
            TextView dateText = itemView.findViewById(R.id.list_item_date);
            dateText.setText(currentScore.getDate());


            return itemView;
        }
    }


    public void setUpResetButton() {
        ImageButton btn = findViewById(R.id.resetButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaderboard.resetScores();
                display();
            }
        });
    }

    private void setUpBackButton() {
        ImageButton btn = findViewById(R.id.backButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void saveNames(String[] namesList) {
        SharedPreferences prefs = this.getSharedPreferences(APP_PREFS_ONE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(APP_PREFS_ONE_SIZE, namesList.length);

        for(int i = 0; i < namesList.length; i++) {
            editor.putString(KEY_ONE + i, namesList[i]);
        }
        editor.apply();
    }

    static public String[] getSaveNames(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS_ONE, MODE_PRIVATE);

        String[] names = new String[5];
        for(int i = 0; i < names.length; i++) {
            names[i] = prefs.getString(KEY_ONE + i, "");
        }
        return names;
    }

    public void saveTimes(String[] timeList) {
        SharedPreferences prefs = this.getSharedPreferences(APP_PREFS_TWO, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(APP_PREFS_TWO_SIZE, timeList.length);
        for(int i = 0; i < timeList.length; i++) {
            editor.putString(KEY_TWO + i, timeList[i]);
        }
        editor.apply();
    }

    static public String[] getSaveTimes(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS_TWO, MODE_PRIVATE);

        String[] times = new String[5];
        for(int i = 0; i < times.length; i++) {
            times[i] = prefs.getString(KEY_TWO + i, "");
        }
        return times;
    }

    public void saveDates(String[] dateList) {
        SharedPreferences prefs = this.getSharedPreferences(APP_PREFS_THREE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(APP_PREFS_THREE_SIZE, dateList.length);
        for(int i = 0; i < dateList.length; i++) {
            editor.putString(KEY_THREE + i, dateList[i]);
        }
        editor.apply();
    }

    static public String[] getSaveDates(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFS_THREE, MODE_PRIVATE);

        String[] dates = new String[5];
        for(int i = 0; i < dates.length; i++) {
            dates[i] = prefs.getString(KEY_THREE + i, "");
        }
        return dates;
    }
}