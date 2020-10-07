package com.example.damatch;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.damatch.model.CardManager;

/* Fragment Shows front of deck and image buttons from top of draw pile
 * */

public class DrawCardFragment extends Fragment {
    CardManager cardManager = CardManager.getInstance();
    private int order = cardManager.getOrder();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //if order = 2
        if(order == 2) {
            return inflater.inflate(R.layout.fragment_draw_card_3, container, false);
        }else if (order == 3) {
            return inflater.inflate(R.layout.fragment_draw_card_4, container, false);
        }else{
            return inflater.inflate(R.layout.fragment_draw_card_6, container, false);
        }
    }


    //reference: https://stackoverflow.com/questions/32779405/save-part-of-activity-fragment-as-image
    public static Bitmap getBitmapFromDrawable(Drawable view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(260, 260,Bitmap.Config.ARGB_8888);

        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);

        //Set the view's background
        canvas.drawColor(Color.BLUE);

        // draw the view on the canvas
        view.draw(canvas);

        //return the bitmap
        return returnedBitmap;
    }
}