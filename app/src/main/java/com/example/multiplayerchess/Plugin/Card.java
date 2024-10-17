package com.example.multiplayerchess.Plugin;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class Card {
    private Bitmap image;
    public static int w = 200;
    public static int h = 400;
    private int x, y;
    private int mana;
    private float tempX, tempY;
    private String ability;
    private int cardIndex;
    private Paint paint;

    public String getAbility() {
        return ability;
    }

    public boolean cardPlayed() {
        return ((tempY + h * 1.5) <= (w * 6 + (h * 9 - w * 6) / 2));
    }

    public int getMana() {
        return mana;
    }

    public Card(Bitmap bmp, int mana, String ability, int cardIndex) {
        paint = new Paint();
        w = Resources.getSystem().getDisplayMetrics().widthPixels / 6;
        h = Resources.getSystem().getDisplayMetrics().heightPixels / 9;
        //set and resize the image
        image = Bitmap.createScaledBitmap(bmp, w, h, true);
        this.mana = mana;
        this.ability = ability;
        this.cardIndex = cardIndex;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    //change the position of the card
    public void position(int x, int y) {
        this.x = x;
        this.y = y;
        this.tempX = x;
        this.tempY = y;
    }

    //reset the card to the position after the player has stopped dragging it
    public void resetTempPos() {
        tempX = x;
        tempY = y;
        image = Bitmap.createScaledBitmap(image, w, h, true);
    }

    //move the card by x, y
    //function used in order to move the card when it is being dragged by the user
    public void moveTempPos(float x, float y) {
        tempX += x;
        tempY += y;
    }

    public void draw(Canvas canvas) {
        //draw the image, setting the position of the card
        int alphaValue;

        //if the card can be played we want to show this to the player by displaying it with half transparecy
        if(!cardPlayed()) {
            alphaValue = 255;
        }
        else {
            alphaValue = 128;
        }

        paint.setAlpha(alphaValue);
        canvas.drawBitmap(image, tempX, tempY, paint);
    }

    //the card should become bigger when it is being touched
    public void grow() {
        image = Bitmap.createScaledBitmap(image, (int) (w * 1.5), (int) (h * 1.5), true);
    }
}
