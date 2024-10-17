package com.example.multiplayerchess.chess;

import android.graphics.Bitmap;

public class King extends Piece {
    int[] movesI = {-1, -1, -1, 0, 1, 1, 1, 0};
    int[] movesJ = {-1, 0, 1, 1, 1, 0, -1, -1};

    public King(Bitmap bmp, boolean isWhite) {
        super(bmp, isWhite);
    }

    public void addMovesKnight(int[] movesJ1, int[] movesI1) {
        int[] combinedMovesI = new int[movesIKnight.length + movesI1.length];
        int[] combinedMovesJ = new int[movesJKnight.length + movesJ1.length];

        System.arraycopy(movesIKnight, 0, combinedMovesI, 0, movesIKnight.length);
        System.arraycopy(movesI1, 0, combinedMovesI, movesIKnight.length, movesI1.length);

        System.arraycopy(movesJKnight, 0, combinedMovesJ, 0, movesJKnight.length);
        System.arraycopy(movesJ1, 0, combinedMovesJ, movesJKnight.length, movesJ1.length);

        movesIKnight = combinedMovesI;
        movesJKnight = combinedMovesJ;
    }

    public void updatePossibleMoves(int i, int j, Piece[][] brd) {
        super.possibleMoves.clear();
        int newI, newJ;
        for(int ind = 0; ind < 8; ind++) {
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
                    if(!isChecked(brd1, newI, newJ)) {
                        Position position = new Position(newI, newJ);
                        super.possibleMoves.add(position);
                    }
                }
            }
        }
        //check for king side castling
        if(!hasMoved && brd[i][j + 3] != null) {
            if(!brd[i][j + 3].hasMoved && (brd[i][j + 1] == null) && (brd[i][j + 2] == null)) {
                if (!isChecked(brd, i, j)) {
                    Piece[][] brd1 = new Piece[8][8];
                    for (int a = 0; a < 8; a++) {
                        for (int b = 0; b < 8; b++) {
                            brd1[a][b] = brd[a][b];
                        }
                    }
                    brd1[i][j + 1] = brd1[i][j];
                    brd1[i][j] = null;
                    if (!isChecked(brd1, i, j + 1)) {
                        brd1[i][j + 2] = brd1[i][j + 1];
                        brd1[i][j + 1] = null;
                        if (!isChecked(brd1, i, j + 2)) {
                            Position position = new Position(i, j + 2);
                            super.possibleMoves.add(position);
                        }
                    }
                }
            }
        }

        //check for queen side castling
        if(!hasMoved) {
            if(brd[i][j - 4] != null) {
                if (!brd[i][j - 4].hasMoved && (brd[i][j - 1] == null) && (brd[i][j - 2] == null) && (brd[i][j - 3] == null)) {
                    if (!isChecked(brd, i, j)) {
                        Piece[][] brd1 = new Piece[8][8];
                        for (int a = 0; a < 8; a++) {
                            for (int b = 0; b < 8; b++) {
                                brd1[a][b] = brd[a][b];
                            }
                        }
                        brd1[i][j - 1] = brd1[i][j];
                        brd1[i][j] = null;
                        if (!isChecked(brd1, i, j - 1)) {
                            brd1[i][j - 2] = brd1[i][j - 1];
                            brd1[i][j - 1] = null;
                            if (!isChecked(brd1, i, j - 2)) {
                                Position position = new Position(i, j - 2);
                                super.possibleMoves.add(position);
                            }
                        }
                    }
                }
            }
        }
    }

    private int[] movesJKnight = {-2, -1, 1, 2, 2, 1, -1, -2};
    private int[] movesIKnight = {-1, -2, -2, -1, 1, 2, 2, 1};
    private final int[] movesIBishop = {-1, -1, 1, 1};
    private final int[] movesJBishop = {-1, 1, 1, -1};
    private final int[] movesIRook = {0, -1, 0, 1};
    private final int[] movesJRook = {-1, 0, 1, 0};

    //check if the king is being checked
    public boolean isChecked(Piece[][] brd, int i, int j) {
        //check if it is checked by a knight
        int newI, newJ;
        for(int ind = 0; ind < movesIKnight.length; ind++) {
            newI = i + movesIKnight[ind];
            newJ = j + movesJKnight[ind];

            if((newI >= 0) &&  (newJ >= 0) && (newI < 8) && (newJ < 8)) {
                if((brd[newI][newJ] != null) && (brd[newI][newJ].isWhite() != isWhite()) && (brd[newI][newJ] instanceof Knight)) {
                    return true;
                }
            }
        }
        //check bishop moves
        for(int i1 = 0; i1 < 4; i1++) {
            newI = i + movesIBishop[i1];
            newJ = j + movesJBishop[i1];

            while((newI >= 0) && (newJ >= 0) && (newI < 8) && (newJ < 8)) {
                if(brd[newI][newJ] != null) {
                    if(brd[newI][newJ].isWhite() != isWhite() && ((brd[newI][newJ] instanceof Bishop) || (brd[newI][newJ] instanceof Queen))) {
                        return true;
                    }
                    else {
                        newI = 20;
                    }
                }
                newI += movesIBishop[i1];
                newJ += movesJBishop[i1];
            }
        }

        //check rook moves
        for(int i1 = 0; i1 < 4; i1++) {
            newI = i + movesIRook[i1];
            newJ = j + movesJRook[i1];

            while((newI >= 0) && (newJ >= 0) && (newI < 8) && (newJ < 8)) {
                if(brd[newI][newJ] != null) {
                    if(brd[newI][newJ].isWhite() != isWhite() && ((brd[newI][newJ] instanceof Rook) || (brd[newI][newJ] instanceof Queen))) {
                        return true;
                    }
                    else {
                        newI = 20;
                    }
                }
                newI += movesIRook[i1];
                newJ += movesJRook[i1];
            }
        }

        //check if it is being checked by a pawn
        if(!isWhite()) {
            if(i < 7) {
                if (j > 0) {
                    if (brd[i + 1][j - 1] != null) {
                        if (brd[i + 1][j - 1].isWhite() != isWhite() && (brd[i + 1][j - 1] instanceof Pawn)) {
                            return true;
                        }
                    }
                }
                if (j < 7) {
                    if (brd[i + 1][j + 1] != null) {
                        if (brd[i + 1][j + 1].isWhite() != isWhite() && (brd[i + 1][j + 1] instanceof Pawn)) {
                            return true;
                        }
                    }
                }
            }
        }
        else {
            if(i > 0) {
                if (j > 0) {
                    if (brd[i - 1][j - 1] != null) {
                        if (brd[i - 1][j - 1].isWhite() != isWhite() && (brd[i - 1][j - 1] instanceof Pawn)) {
                            return true;
                        }
                    }
                }
                if (j < 7) {
                    if (brd[i - 1][j + 1] != null) {
                        if (brd[i - 1][j + 1].isWhite() != isWhite() && (brd[i - 1][j + 1] instanceof Pawn)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
