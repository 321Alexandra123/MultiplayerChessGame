package com.example.multiplayerchess.chess;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PawnPromotion {
    Bitmap[] images = new Bitmap[4]; //Queen, Rook, Bishop, Knight;
    int wBoard, hBoard, xBoard, yBoard;
    int wRect, hRect, xRect, yRect;
    private final Paint paint = new Paint();
    private boolean isActive;
    private int pawnY;

    public PawnPromotion(Bitmap bmpQueen, Bitmap bmpRook, Bitmap bmpBishop, Bitmap bmpKnight, boolean isUp, int w, int h, int x, int y) {
        //set and resize the image
        images[0] = Bitmap.createScaledBitmap(bmpQueen, Piece.w, Piece.h, true);
        images[1] = Bitmap.createScaledBitmap(bmpRook, Piece.w, Piece.h, true);
        images[2] = Bitmap.createScaledBitmap(bmpBishop, Piece.w, Piece.h, true);
        images[3] = Bitmap.createScaledBitmap(bmpKnight, Piece.w, Piece.h, true);
        wRect = w / 8;
        hRect = h / 2;
        if(isUp)
            yRect = y;
        else
            yRect = y + h / 2;

        wBoard = w;
        hBoard = h;
        xBoard = x;
        yBoard = y;

        paint.setColor(Color.rgb(201,201,201));
        paint.setShadowLayer(10, 0, 0, Color.BLACK);
        isActive = false;
    }

    public int getW() {
        return wRect;
    }

    public int getH() {
        return hRect;
    }

    public int getX() {
        return xRect;
    }

    public int getY() {
        return yRect;
    }

    public int getJ() {
        return (xRect - xBoard) / (wBoard / 8);
    }

    public int getPawnY() {
        return pawnY;
    }

    public void setIsActive(boolean isActive, int j, int pawnY) {
        this.isActive = isActive;
        this.pawnY = pawnY;
        xRect = xBoard + wBoard / 8 * j;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void draw(Canvas canvas) {
        //draw the image, setting the position of the board
        canvas.drawRect(xRect, yRect, xRect + wRect, yRect + hRect, paint);
        for(int i = 0; i < 4; i++)
            canvas.drawBitmap(images[i], xRect + (wBoard / 8 - Piece.w) / 2, yRect + hBoard / 8 * i + (hBoard / 8 - Piece.h) / 2, null);
    }
}
