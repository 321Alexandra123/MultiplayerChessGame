package com.example.multiplayerchess.chess;

import android.graphics.Bitmap;

public class Bishop extends Piece {
    int[] movesI = {-1, -1, 1, 1};
    int[] movesJ = {-1, 1, 1, -1};

    public Bishop(Bitmap bmp, boolean isWhite) {
        super(bmp, isWhite);
    }

    public void updatePossibleMoves(int i, int j, Piece[][] brd, int kingI, int kingJ) {
        super.possibleMoves.clear();

        int newMoveI, newMoveJ;
        for(int i1 = 0; i1 < 4; i1++) {
            newMoveI = i + movesI[i1];
            newMoveJ = j + movesJ[i1];

            while((newMoveI >= 0) && (newMoveJ >= 0) && (newMoveI < 8) && (newMoveJ < 8)) {
                if(brd[newMoveI][newMoveJ] == null) {
                    Piece[][] brd1 = new Piece[8][8];
                    for(int a = 0; a < 8; a++) {
                        for(int b = 0; b < 8; b++) {
                            brd1[a][b] = brd[a][b];
                        }
                    }
                    brd1[newMoveI][newMoveJ] = brd1[i][j];
                    brd1[i][j] = null;
                    King king = (King)brd1[kingI][kingJ];
                    if(!king.isChecked(brd1, kingI, kingJ)) {
                        Position position = new Position(newMoveI, newMoveJ);
                        super.possibleMoves.add(position);
                    }
                }
                else if(brd[newMoveI][newMoveJ].isWhite() != isWhite()) {
                    Piece[][] brd1 = new Piece[8][8];
                    for(int a = 0; a < 8; a++) {
                        for(int b = 0; b < 8; b++) {
                            brd1[a][b] = brd[a][b];
                        }
                    }
                    brd1[newMoveI][newMoveJ] = brd1[i][j];
                    brd1[i][j] = null;
                    King king = (King)brd1[kingI][kingJ];
                    if(!king.isChecked(brd1, kingI, kingJ)) {
                        Position position = new Position(newMoveI, newMoveJ);
                        super.possibleMoves.add(position);
                    }
                    newMoveI = 20;
                }
                else
                    newMoveI = 20;
                newMoveI += movesI[i1];
                newMoveJ += movesJ[i1];
            }
        }
    }
}
