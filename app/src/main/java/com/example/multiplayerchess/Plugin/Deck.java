package com.example.multiplayerchess.Plugin;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.multiplayerchess.Plugin.Card;
import com.example.multiplayerchess.Plugin.Card1;

import java.util.ArrayList;

public class Deck {
    public static int nrCardsMax = 12;
    private final boolean opponent;
    private ArrayList<Card> deck = new ArrayList<Card>();
    private final Bitmap image;
    private static int w = 320;
    private static int h = 450;
    private int x, y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public Deck(Bitmap bmp, ArrayList<Card> deck) {
        this.opponent = true;

        w = Resources.getSystem().getDisplayMetrics().widthPixels / 6;
        h = Resources.getSystem().getDisplayMetrics().heightPixels / 9;

        x = Resources.getSystem().getDisplayMetrics().widthPixels - w - 30;
        if(!opponent)
            y = Resources.getSystem().getDisplayMetrics().heightPixels / 2 - Resources.getSystem().getDisplayMetrics().widthPixels / 2 - h - 130;
        else
            y = Resources.getSystem().getDisplayMetrics().heightPixels / 2 + Resources.getSystem().getDisplayMetrics().widthPixels / 2 + 130;

        //set and resize the image
        image = Bitmap.createScaledBitmap(bmp, w, h, true);

        this.deck = deck;
        shuffle();
    }

    public Deck(Bitmap bmp, ArrayList<Bitmap> bmpCards, boolean opponent) {
        this.opponent = opponent;

        w = Resources.getSystem().getDisplayMetrics().widthPixels / 6;
        h = Resources.getSystem().getDisplayMetrics().heightPixels / 9;

        x = Resources.getSystem().getDisplayMetrics().widthPixels - w - 30;
        if(!opponent)
            y = Resources.getSystem().getDisplayMetrics().heightPixels / 2 - Resources.getSystem().getDisplayMetrics().widthPixels / 2 - h - 130;
        else
            y = Resources.getSystem().getDisplayMetrics().heightPixels / 2 + Resources.getSystem().getDisplayMetrics().widthPixels / 2 + 130;

        //set and resize the image
        image = Bitmap.createScaledBitmap(bmp, w, h, true);
        create(bmpCards);
        shuffle();
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    //remove the first card of the deck
    public void popDeck() {
        deck.remove(0);
    }

    //initialize the deck with all of the cards
    public void create(ArrayList<Bitmap> bmpCards) {
        for(int i = 0; i < nrCardsMax; i++) {
            Card card = new Card1(bmpCards.get(i));
            deck.add(card);
        }
    }

    //shuffle the cards in the deck
    public void shuffle() {
        ArrayList<Card> shuffledDeck = new ArrayList<Card>();
        while (deck.size() > 0) {
            int index = (int) (Math.random() * deck.size());
            shuffledDeck.add(deck.remove(index));
        }
        deck = shuffledDeck;
    }

    public void draw(Canvas canvas) {
        //draw the image, setting the position of the card
        canvas.drawBitmap(image, x, y, null);
    }
}
