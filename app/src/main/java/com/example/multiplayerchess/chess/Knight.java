package com.example.multiplayerchess.chess;

import android.graphics.Bitmap;

public class Knight extends Piece {
    private int[] movesJ = {-2, -1, 1, 2, 2, 1, -1, -2};
    private int[] movesI = {-1, -2, -2, -1, 1, 2, 2, 1};

    public Knight(Bitmap bmp, boolean isWhite) {
        super(bmp, isWhite);
    }

    public void addMoves(int[] movesJ1, int[] movesI1) {
        int[] combinedMovesI = new int[movesI.length + movesI1.length];
        int[] combinedMovesJ = new int[movesJ.length + movesJ1.length];

        System.arraycopy(movesI, 0, combinedMovesI, 0, movesI.length);
        System.arraycopy(movesI1, 0, combinedMovesI, movesI.length, movesI1.length);

        System.arraycopy(movesJ, 0, combinedMovesJ, 0, movesJ.length);
        System.arraycopy(movesJ1, 0, combinedMovesJ, movesJ.length, movesJ1.length);

        movesI = combinedMovesI;
        movesJ = combinedMovesJ;
    }

    public void updatePossibleMoves(int i, int j, Piece[][] brd, int kingI, int kingJ) {
        super.possibleMoves.clear();
        int newI, newJ;
        for(int ind = 0; ind < movesI.length; ind++) {
            newI = i + movesI[ind];
            newJ = j + movesJ[ind];
            if((newI >= 0) &&  (newJ >= 0) && (newI < 8) && (newJ < 8)) {
                if((brd[newI][newJ] == null) || (brd[newI][newJ].isWhite() != isWhite())) {
                    Piece[][] brd1 = new Piece[8][8];
                    for(int a = 0; a < 8; a++) {
                        for(int b = 0; b < 8; b++) {
                            brd1[a][b] = brd[a][b];
                        }
                    }
                    brd1[newI][newJ] = brd1[i][j];
                    brd1[i][j] = null;
                    King king = (King)brd1[kingI][kingJ];
                    if(!king.isChecked(brd1, kingI, kingJ)) {
                        Position position = new Position(newI, newJ);
                        super.possibleMoves.add(position);
                    }
                }
            }
        }
    }
}
