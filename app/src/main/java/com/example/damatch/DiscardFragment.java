package com.example.damatch;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.damatch.model.Card;
import com.example.damatch.model.CardManager;

/* Fragment Shows front of deck and images from top of discard pile
* */

public class DiscardFragment extends Fragment {
    CardManager cardManager = CardManager.getInstance();
    private int order = cardManager.getOrder();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(order == 2) {
            return inflater.inflate(R.layout.fragment_discard_card_3, container, false);
        }else if (order == 3) {
            return inflater.inflate(R.layout.fragment_discard_card_4, container, false);
        }else{
            return inflater.inflate(R.layout.fragment_discard_card_6, container, false);
        }
    }
}