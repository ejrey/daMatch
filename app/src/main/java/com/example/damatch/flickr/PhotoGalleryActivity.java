package com.example.damatch.flickr;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.damatch.R;

// Launch PhotoGallery Fragments, used mainly for that and a connection from the Main menu for this to launch
// Taken from Android Programming The Big Nerd Ranch Guide (3rd ed) Chp.25 Pg. 476

public class PhotoGalleryActivity extends SingleFragmentActivity {

    public static Intent makeIntent(Context context){
        return new Intent(context, PhotoGalleryActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        setTitle(R.string.flickr);
        return PhotoGalleryFragment.newInstance();
    }
}
