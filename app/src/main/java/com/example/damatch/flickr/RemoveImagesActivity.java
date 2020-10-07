package com.example.damatch.flickr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.damatch.MainActivity;
import com.example.damatch.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/*
* Remove activity to allow user the ability to remove images from the flickr set. Update the saved deck incase app closes.
* */

public class RemoveImagesActivity extends AppCompatActivity {

    private WebView bground_gif;
    private static final int ZERO = 0;
    private static final String ID_PREFS = "IDPrefs";
    private static final String ID_KEY = "ID";
    private static final String ID_KEY_SIZE = "ID_KEY_SIZE";

    private String imageFromStorage;
    private String currentImageToDisplay;
    private String imageIDWithFormat;

    private FlickrManager flickrManager = FlickrManager.getInstance();
    private int indexOfImageIDArray = 0;

    public static Intent makeIntent(Context context) {
        return new Intent(context, RemoveImagesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_images);

        // Background for activity
        bground_gif = findViewById(R.id.bground_remove_images);
        MainActivity.setBackgroundGif(bground_gif);

        // Load First URL
        imageFromStorage = flickrManager.getImageID(indexOfImageIDArray) + ".jpg";
        loadImageFromStorage(flickrManager.getPathToImageStorage(), imageFromStorage);

        removeButton();
        nextButton();
        prevButton();
    }

    private void removeButton() {
        ImageButton btn = findViewById(R.id.removeBtn);
        btn.setOnClickListener(v -> {
            if (flickrManager.imageIDArraySize() > ZERO) {
                int chosenImageToRemove = flickrManager.getImageID(indexOfImageIDArray);
                imageIDWithFormat = chosenImageToRemove + ".jpg";
                removeImageFromStorage(flickrManager.getPathToImageStorage(), imageIDWithFormat);
                flickrManager.removeImageFromIDArray(indexOfImageIDArray);
                updateSavedImageValues();
                Toast.makeText(RemoveImagesActivity.this, "Image Removed!", Toast.LENGTH_SHORT).show();
            }
            if (flickrManager.imageIDArraySize() == ZERO) {
                Toast.makeText(RemoveImagesActivity.this, "All images removed!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                indexOfImageIDArray = 0;
                currentImageToDisplay = flickrManager.getImageID(indexOfImageIDArray) + ".jpg";
                loadImageFromStorage(flickrManager.getPathToImageStorage(), currentImageToDisplay);
            }
        });
    }

    private void removeImageFromStorage(String pathToImageStorage, String parseImageID) {
        File f = new File(pathToImageStorage, parseImageID);
        f.delete();
    }

    private void updateSavedImageValues() {
        SharedPreferences prefsID = getSharedPreferences(ID_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editorID = prefsID.edit();

        editorID.clear();
        editorID.putInt(ID_KEY_SIZE, flickrManager.getImageIDArray().size());

        for (int i = 0; i < flickrManager.getImageIDArray().size(); i++) {
            editorID.putInt(ID_KEY + i, flickrManager.getImageID(i));
        }

        editorID.apply();
    }

    private void prevButton() {
        ImageButton btn = findViewById(R.id.prevBtn);
        btn.setOnClickListener(v -> iterateArray(false));
    }

    private void nextButton() {
        ImageButton btn = findViewById(R.id.nextBtn);
        btn.setOnClickListener(v -> iterateArray(true));
    }

    private void iterateArray(boolean buttonPressed) {
        if (buttonPressed) {
            indexOfImageIDArray++;
            if (indexOfImageIDArray == flickrManager.imageIDArraySize()) {
                indexOfImageIDArray = 0;
            }
        } else {
            indexOfImageIDArray--;
            if (indexOfImageIDArray == -1) {
                indexOfImageIDArray = flickrManager.imageIDArraySize() - 1;
            }
        }
        currentImageToDisplay = flickrManager.getImageID(indexOfImageIDArray) + ".jpg";
        loadImageFromStorage(flickrManager.getPathToImageStorage(), currentImageToDisplay);
    }

    private void loadImageFromStorage(String path, String id) {
        try {
            File f = new File(path, id);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = (ImageView) findViewById(R.id.imageArray);
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}