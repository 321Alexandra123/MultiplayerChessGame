package com.example.multiplayerchess.Plugin;

import android.graphics.Bitmap;

import com.example.multiplayerchess.Plugin.Card;

public class Card2 extends Card {
    private Bitmap image;
    private static final int w = 200;
    private static final int h = 400;
    public static final String abilityCard = "Add knight movement (3 steps forward).";
    public static int manaCard = 9;

    //in order to create the new card we just need to call the constructor of the Card class
    public Card2(Bitmap bmp) {
        super(bmp, manaCard, abilityCard, 2);
    }
}
