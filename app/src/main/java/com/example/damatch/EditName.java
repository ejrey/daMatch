package com.example.damatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;



public class EditName extends AppCompatActivity {

    public static Intent makeIntent(Context context){
        return new Intent(context, EditName.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        getName();
    }

    private void getName() {
        EditText name = findViewById(R.id.editText_name);

        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId){
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_NEXT:
                    case EditorInfo.IME_ACTION_PREVIOUS:
                        returnNameToGameActivity();
                        return true;
                }
                return false;
            }
        });
    }

    private void returnNameToGameActivity() {

        //get name
        EditText name = findViewById(R.id.editText_name);
        String nameString = name.getText().toString();

        Intent returnIntent = getIntent();
        returnIntent.putExtra("name", nameString);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}