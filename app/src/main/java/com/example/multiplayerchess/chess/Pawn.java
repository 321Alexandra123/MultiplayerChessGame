package com.example.multiplayerchess.chess;

import android.graphics.Bitmap;

public class Pawn extends Piece {
    public Pawn(Bitmap bmp, boolean isWhite) {
        super(bmp, isWhite);
    }

    public void updatePossibleMoves(int i, int j, Piece[][] brd, int kingI, int kingJ, int lastPieceI, int lastPieceJ, int lastPieceMovedFromI) {
        super.possibleMoves.clear();
        if(i == 3 || i == 4) {
        }

        if(isWhite()) {
            //check en passant
            if(i == 3) {
                if(j > 0) {
                    if(brd[i][j - 1] instanceof Pawn) {
                        if((lastPieceI == i) && (lastPieceJ == (j - 1)) && (lastPieceMovedFromI == (i - 2))) {
                            Position position = new Position(i - 1, j - 1);
                            super.possibleMoves.add(position);
                        }
                    }
                }
                if(j < 7) {
                    if(brd[i][j + 1] instanceof Pawn) {
                        if((lastPieceI == i) && (lastPieceJ == (j + 1)) && (lastPieceMovedFromI == (i - 2))) {
                            Position position = new Position(i - 1, j + 1);
                            super.possibleMoves.add(position);
                        }
                    }
                }
            }
            if(i > 0) {
                if (brd[i - 1][j] == null) {
                    Piece[][] brd1 = new Piece[8][8];
                    for(int a = 0; a < 8; a++) {
                        for(int b = 0; b < 8; b++) {
                            brd1[a][b] = brd[a][b];
                        }
                    }
                    brd1[i - 1][j] = brd1[i][j];
                    brd1[i][j] = null;
                    King king = (King)brd1[kingI][kingJ];
                    if(!king.isChecked(brd1, kingI, kingJ)) {
                        Position position = new Position(i - 1, j);
                        super.possibleMoves.add(position);
                    }
                    if(!super.hasMoved) {
                        if (brd[i - 2][j] == null) {
                            for(int a = 0; a < 8; a++) {
                                for(int b = 0; b < 8; b++) {
                                    brd1[a][b] = brd[a][b];
                                }
                            }
                            brd1[i - 2][j] = brd1[i][j];
                            brd1[i][j] = null;
                            king  = (King)brd1[kingI][kingJ];
                            if(!king.isChecked(brd1, kingI, kingJ)) {
                                Position position = new Position(i - 2, j);
                                super.possibleMoves.add(position);
                            }
                        }
                    }
                }
                if(j > 0)
                    if (brd[i - 1][j - 1] != null && brd[i - 1][j - 1].isWhite() != isWhite()) {
                        Piece[][] brd1 = new Piece[8][8];
                        for(int a = 0; a < 8; a++) {
                            for(int b = 0; b < 8; b++) {
                                brd1[a][b] = brd[a][b];
                            }
                        }
                        brd1[i - 1][j - 1] = brd1[i][j];
                        brd1[i][j] = null;
                        King king = (King)brd1[kingI][kingJ];
                        if(!king.isChecked(brd1, kingI, kingJ)) {
                            Position position = new Position(i - 1, j - 1);
                            super.possibleMoves.add(position);
                        }
                    }
                if(j < 7)
                    if (brd[i - 1][j + 1] != null && brd[i - 1][j + 1].isWhite() != isWhite()) {
                        Piece[][] brd1 = new Piece[8][8];
                        for(int a = 0; a < 8; a++) {
                            for(int b = 0; b < 8; b++) {
                                brd1[a][b] = brd[a][b];
                            }
                        }
                        brd1[i - 1][j + 1] = brd1[i][j];
                        brd1[i][j] = null;
                        King king = (King)brd1[kingI][kingJ];
                        if(!king.isChecked(brd1, kingI, kingJ)) {
                            Position position = new Position(i - 1, j + 1);
                            super.possibleMoves.add(position);
                        }
                    }
            }
        }
        else {
            //check en passant
            if(i == 4) {
                if(j > 0) {
                    if(brd[i][j - 1] instanceof Pawn) {
                        if((lastPieceI == i) && (lastPieceJ == (j - 1)) && (lastPieceMovedFromI == (i + 2))) {
                            Position position = new Position(i + 1, j - 1);
                            super.possibleMoves.add(position);
                        }
                    }
                }
                if(j < 7) {
                    if(brd[i][j + 1] instanceof Pawn) {
                        if((lastPieceI == i) && (lastPieceJ == (j + 1)) && (lastPieceMovedFromI == (i + 2))) {
                            Position position = new Position(i + 1, j + 1);
                            super.possibleMoves.add(position);
                        }
                    }
                }
            }
            if(i < 7) {
                if (brd[i + 1][j] == null) {
                    Piece[][] brd1 = new Piece[8][8];
                    for(int a = 0; a < 8; a++) {
                        for(int b = 0; b < 8; b++) {
                            brd1[a][b] = brd[a][b];
                        }
                    }
                    brd1[i + 1][j] = brd1[i][j];
                    brd1[i][j] = null;
                    King king = (King)brd1[kingI][kingJ];
                    if(!king.isChecked(brd1, kingI, kingJ)) {
                        Position position = new Position(i + 1, j);
                        super.possibleMoves.add(position);
                    }
                    if(!super.hasMoved) {
                        if (brd[i + 2][j] == null) {
                            for(int a = 0; a < 8; a++) {
                                for(int b = 0; b < 8; b++) {
                                    brd1[a][b] = brd[a][b];
                                }
                            }
                            brd1[i + 2][j] = brd1[i][j];
                            brd1[i][j] = null;
                            king = (King)brd1[kingI][kingJ];
                            if(!king.isChecked(brd1, kingI, kingJ)) {
                                Position position = new Position(i + 2, j);
                                super.possibleMoves.add(position);
                            }
                        }
                    }
                }
                if(j > 0)
                    if (brd[i + 1][j - 1] != null && brd[i + 1][j - 1].isWhite() != isWhite()) {
                        Piece[][] brd1 = new Piece[8][8];
                        for(int a = 0; a < 8; a++) {
                            for(int b = 0; b < 8; b++) {
                                brd1[a][b] = brd[a][b];
                            }
                        }
                        brd1[i + 1][j - 1] = brd1[i][j];
                        brd1[i][j] = null;
                        King king = (King)brd1[kingI][kingJ];
                        if(!king.isChecked(brd1, kingI, kingJ)) {
                            Position position = new Position(i + 1, j - 1);
                            super.possibleMoves.add(position);
                        }
                    }
                if(j < 7)
                    if (brd[i + 1][j + 1] != null && brd[i + 1][j + 1].isWhite() != isWhite()) {
                        Piece[][] brd1 = new Piece[8][8];
                        for(int a = 0; a < 8; a++) {
                            for(int b = 0; b < 8; b++) {
                                brd1[a][b] = brd[a][b];
                            }
                        }
                        brd1[i + 1][j + 1] = brd1[i][j];
                        brd1[i][j] = null;
                        King king = (King)brd1[kingI][kingJ];
                        if(!king.isChecked(brd1, kingI, kingJ)) {
                            Position position = new Position(i + 1, j + 1);
                            super.possibleMoves.add(position);
                        }
                    }
            }
        }
    }
}
