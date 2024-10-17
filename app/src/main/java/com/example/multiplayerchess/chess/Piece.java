package com.example.multiplayerchess.chess;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;

public class Piece {
    private final Bitmap image;
    protected static int w = 150;
    protected static int h = 150;
    private final boolean isWhite;
    boolean hasMoved;
    protected ArrayList<Position>possibleMoves = new ArrayList<Position>();

    public ArrayList<Position> getPossibleMoves() {
        return possibleMoves;
    }
    public boolean isWhite() {
        return isWhite;
    }

    public Piece(Bitmap bmp, boolean isWhite) {
        //set and resize the image
        image = Bitmap.createScaledBitmap(bmp, w, h, true);
        this.isWhite = isWhite;
        this.hasMoved = false;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public void draw(Canvas canvas, int x, int y, int l, int i, int j) {
        //draw the image, setting the position of the board
        canvas.drawBitmap(image, x + j * l + (l - w) / 2, y + i * l + (l - h) / 2, null);
    }

    public void draw(Canvas canvas, float x, float y) {
        //draw the image, setting the position of the board
        canvas.drawBitmap(image, x, y, null);
    }

    //check if the move is valid by checking if it exists in the possible moves
    public boolean isValid(int x, int y) {
        for(Position pos : possibleMoves) {
            if(pos.x == x && pos.y == y)
                return true;
        }
        return false;
    }
}
