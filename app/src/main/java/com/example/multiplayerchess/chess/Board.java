package com.example.multiplayerchess.chess;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private final Bitmap image;
    private final Piece[][] board;
    private int selectedPieceX, selectedPieceY;
    public static int w, h, x, y;
    private float tempXSelected, tempYSelected;
    private boolean sameSelectedPiece;
    private final Paint paintHighlightDarkSquare = new Paint();
    private final Paint paintHighlightLightSquareLastPiece = new Paint();
    private final Paint paintHighlightDarkSquareLastPiece = new Paint();
    private final Paint paintHighlightLightSquare = new Paint();
    private final Paint paintHighlightDarkSquarePiece = new Paint();
    private final Paint paintHighlightLightSquarePiece = new Paint();
    private int whiteKingI, whiteKingJ, blackKingI, blackKingJ;
    private int nrPossibleMovesWhite, nrPossibleMovesBlack;
    private int lastPieceI = -1, lastPieceJ = -1, lastPieceMovedFromI = -1, lastPieceMovedFromJ = -1;
    private final PawnPromotion whitePawnPromotion;
    private final PawnPromotion blackPawnPromotion;
    private final Bitmap bmpWhiteQueen;
    private final Bitmap bmpBlackQueen;
    private final Bitmap bmpWhiteRook;
    private final Bitmap bmpBlackRook;
    private final Bitmap bmpWhiteBishop;
    private final Bitmap bmpBlackBishop;
    private final Bitmap bmpWhiteKnight;
    private final Bitmap bmpBlackKnight;
    private boolean playerIsWhite;
    private int[] possibleMovesKnightJWhite, possibleMovesKnightIWhite;
    private int[] possibleMovesKnightJBlack, possibleMovesKnightIBlack;

    public int getH() {
        return h;
    }

    public Board(Bitmap bmp, Bitmap bmpWhitePawn, Bitmap bmpBlackPawn, Bitmap bmpWhiteKnight, Bitmap bmpBlackKnight, Bitmap bmpWhiteBishop, Bitmap bmpBlackBishop, Bitmap bmpWhiteRook, Bitmap bmpBlackRook, Bitmap bmpWhiteQueen, Bitmap bmpBlackQueen, Bitmap bmpWhiteKing, Bitmap bmpBlackKing, boolean playerIsWhite) {
        this.bmpWhiteQueen = bmpWhiteQueen;
        this.bmpBlackQueen = bmpBlackQueen;
        this.bmpWhiteRook = bmpWhiteRook;
        this.bmpBlackRook = bmpBlackRook;
        this.bmpWhiteBishop = bmpWhiteBishop;
        this.bmpBlackBishop = bmpBlackBishop;
        this.bmpWhiteKnight = bmpWhiteKnight;
        this.bmpBlackKnight = bmpBlackKnight;
        this.playerIsWhite = playerIsWhite;

        //rescale chess board so that it is centered and uses full screen width
        w = Resources.getSystem().getDisplayMetrics().widthPixels;
        h = Resources.getSystem().getDisplayMetrics().widthPixels;

        x = 0;
        y = (Resources.getSystem().getDisplayMetrics().heightPixels - Resources.getSystem().getDisplayMetrics().widthPixels) / 2;
        image = Bitmap.createScaledBitmap(bmp, w, h, true);

        whitePawnPromotion = new PawnPromotion(bmpWhiteQueen, bmpWhiteRook, bmpWhiteBishop, bmpWhiteKnight, playerIsWhite, w, h, x, y);
        blackPawnPromotion = new PawnPromotion(bmpBlackQueen, bmpBlackRook, bmpBlackBishop, bmpBlackKnight, !playerIsWhite, w, h, x, y);

        int whitePiecesRow, blackPiecesRow, whitePawnRow, blackPawnRow;
        int kingColumn, queenColumn;
        //if(playerIsWhite) {
            whitePiecesRow = 7;
            blackPiecesRow = 0;
            whitePawnRow = 6;
            blackPawnRow = 1;

            kingColumn = 4;
            queenColumn = 3;

        //initialize all of the pieces
        board = new Piece[8][8];
        board[blackPiecesRow][0] = new Rook(bmpBlackRook, false);
        board[blackPiecesRow][1] = new Knight(bmpBlackKnight, false);
        board[blackPiecesRow][2] = new Bishop(bmpBlackBishop, false);
        board[blackPiecesRow][queenColumn] = new Queen(bmpBlackQueen, false);
        board[blackPiecesRow][kingColumn] = new King(bmpBlackKing, false);
        board[blackPiecesRow][5] = new Bishop(bmpBlackBishop, false);
        board[blackPiecesRow][6] = new Knight(bmpBlackKnight, false);
        board[blackPiecesRow][7] = new Rook(bmpBlackRook, false);

        board[blackPawnRow][0] = new Pawn(bmpBlackPawn, false);
        board[blackPawnRow][1] = new Pawn(bmpBlackPawn, false);
        board[blackPawnRow][2] = new Pawn(bmpBlackPawn, false);
        board[blackPawnRow][3] = new Pawn(bmpBlackPawn, false);
        board[blackPawnRow][4] = new Pawn(bmpBlackPawn, false);
        board[blackPawnRow][5] = new Pawn(bmpBlackPawn, false);
        board[blackPawnRow][6] = new Pawn(bmpBlackPawn, false);
        board[blackPawnRow][7] = new Pawn(bmpBlackPawn, false);

        board[whitePawnRow][0] = new Pawn(bmpWhitePawn, true);
        board[whitePawnRow][1] = new Pawn(bmpWhitePawn, true);
        board[whitePawnRow][2] = new Pawn(bmpWhitePawn, true);
        board[whitePawnRow][3] = new Pawn(bmpWhitePawn, true);
        board[whitePawnRow][4] = new Pawn(bmpWhitePawn, true);
        board[whitePawnRow][5] = new Pawn(bmpWhitePawn, true);
        board[whitePawnRow][6] = new Pawn(bmpWhitePawn, true);
        board[whitePawnRow][7] = new Pawn(bmpWhitePawn, true);

        board[whitePiecesRow][0] = new Rook(bmpWhiteRook, true);
        board[whitePiecesRow][1] = new Knight(bmpWhiteKnight, true);
        board[whitePiecesRow][2] = new Bishop(bmpWhiteBishop, true);
        board[whitePiecesRow][queenColumn] = new Queen(bmpWhiteQueen, true);
        board[whitePiecesRow][kingColumn] = new King(bmpWhiteKing, true);
        board[whitePiecesRow][5] = new Bishop(bmpWhiteBishop, true);
        board[whitePiecesRow][6] = new Knight(bmpWhiteKnight, true);
        board[whitePiecesRow][7] = new Rook(bmpWhiteRook, true);

        for(int i = 2; i < 6; i++)
            for(int j = 0; j < 8; j++) {
                board[i][j] = null;
            }

        whiteKingJ = kingColumn;
        whiteKingI = whitePiecesRow;
        blackKingJ = kingColumn;
        blackKingI = blackPiecesRow;

        selectedPieceX = -1;
        selectedPieceY = -1;

        paintHighlightLightSquare.setColor(Color.rgb(201,201,201));
        paintHighlightDarkSquare.setColor(Color.rgb(66,66,66));
        paintHighlightLightSquarePiece.setColor(Color.rgb(201,201,201));
        paintHighlightDarkSquarePiece.setColor(Color.rgb(66,66,66));

        paintHighlightDarkSquarePiece.setStyle(Paint.Style.STROKE);
        paintHighlightDarkSquarePiece.setStrokeWidth(20f);
        paintHighlightLightSquarePiece.setStyle(Paint.Style.STROKE);
        paintHighlightLightSquarePiece.setStrokeWidth(20f);

        paintHighlightLightSquareLastPiece.setColor(Color.rgb(242, 244, 203));
        paintHighlightDarkSquareLastPiece.setColor(Color.rgb(53, 53, 53));

        possibleMovesKnightJWhite = null;
        possibleMovesKnightIWhite = null;

        sameSelectedPiece = false;

        updatePossibleMoves(true);
    }

    public void addNewMovesKnight(int[] movesJ, int[] movesI, boolean turnWhite, boolean piecesWhite) {
        if(piecesWhite) {
            possibleMovesKnightJWhite = movesJ;
            possibleMovesKnightIWhite = movesI;
        }
        else {
            possibleMovesKnightJBlack = movesJ;
            possibleMovesKnightIBlack = movesI;
        }

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board[i][j] != null) {
                    if (board[i][j] instanceof Knight && board[i][j].isWhite() == piecesWhite) {
                        ((Knight) board[i][j]).addMoves(movesJ, movesI);
                    } else if (board[i][j] instanceof King && !(board[i][j].isWhite() == piecesWhite)) {
                        ((King) board[i][j]).addMovesKnight(movesJ, movesI);
                    }
                }
            }
        }
        updatePossibleMoves(turnWhite);
    }

    //check if there are any knight or bishops of the same color as the player left
    public boolean checkKnightBishopAlive(boolean playerIsWhite) {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board[i][j] != null) {
                    if(board[i][j].isWhite() == playerIsWhite)
                        if(board[i][j] instanceof Knight || board[i][j] instanceof Bishop)
                            return true;
                }
            }
        }
        return false;
    }

    //check if there are any pawns of the same color as the player left
    public boolean checkPawnAlive(boolean playerIsWhite) {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board[i][j] != null) {
                    if(board[i][j].isWhite() == playerIsWhite)
                        if(board[i][j] instanceof Pawn)
                            return true;
                }
            }
        }
        return false;
    }

    public PawnPromotion getWhitePawnPromotion() {
        return whitePawnPromotion;
    }

    public PawnPromotion getBlackPawnPromotion() {
        return blackPawnPromotion;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public Piece[][] getBoard() {
        return board;
    }

    private Piece getPieceAtPosition(int i, int j) {
        if (!playerIsWhite) {
            return board[7 - i][7 - j];
        } else {
            return board[i][j];
        }
    }

    private int getAdjustedCoord(int i) {
        if(!playerIsWhite) {
            return 7 - i;
        }
        return i;
    }

    public void draw(Canvas canvas) {
        //draw the image, setting the position of the board
        canvas.drawBitmap(image, x, y, null);

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board[i][j] != null)
                    if(!(getAdjustedCoord(i) == selectedPieceX && getAdjustedCoord(j) == selectedPieceY) && !(getAdjustedCoord(i) == getAdjustedCoord(lastPieceI) && getAdjustedCoord(j) == getAdjustedCoord(lastPieceJ)) && !(getAdjustedCoord(i) == getAdjustedCoord(lastPieceMovedFromI) && getAdjustedCoord(j) == getAdjustedCoord(lastPieceMovedFromJ)))
                        board[i][j].draw(canvas, 0, (Resources.getSystem().getDisplayMetrics().heightPixels - Resources.getSystem().getDisplayMetrics().widthPixels) / 2, Resources.getSystem().getDisplayMetrics().widthPixels / 8, getAdjustedCoord(i), getAdjustedCoord(j));
            }
        }

        if(selectedPieceX != -1)
            if (getPieceAtPosition(selectedPieceX, selectedPieceY) != null) {
                //the highlighter has to be different based on the color of the chess square
                if ((selectedPieceX % 2) == (selectedPieceY % 2)) {
                    canvas.drawRect(x + selectedPieceY * (w / 8), y + selectedPieceX * (h / 8), x + selectedPieceY * (w / 8) + w / 8, y + selectedPieceX * (h / 8) + h / 8, paintHighlightLightSquare);
                } else {
                    canvas.drawRect(x + selectedPieceY * (w / 8), y + selectedPieceX * (h / 8), x + selectedPieceY * (w / 8) + w / 8, y + selectedPieceX * (h / 8) + h / 8, paintHighlightDarkSquare);
                }
                drawPossibleMovesSelectedPiece(canvas);
                board[getAdjustedCoord(selectedPieceX)][getAdjustedCoord(selectedPieceY)].draw(canvas, tempXSelected, tempYSelected);
            }

        if(lastPieceI != -1) {
            if (board[lastPieceI][lastPieceJ] != null) {
                if ((getAdjustedCoord(lastPieceI) % 2) == (getAdjustedCoord(lastPieceJ) % 2)) {
                    canvas.drawRect(x + getAdjustedCoord(lastPieceJ) * (w / 8), y + getAdjustedCoord(lastPieceI) * (h / 8), x + getAdjustedCoord(lastPieceJ) * (w / 8) + w / 8, y + getAdjustedCoord(lastPieceI) * (h / 8) + h / 8, paintHighlightLightSquareLastPiece);
                } else {
                    canvas.drawRect(x + getAdjustedCoord(lastPieceJ) * (w / 8), y + getAdjustedCoord(lastPieceI) * (h / 8), x + getAdjustedCoord(lastPieceJ) * (w / 8) + w / 8, y + getAdjustedCoord(lastPieceI) * (h / 8) + h / 8, paintHighlightDarkSquareLastPiece);
                }
                board[lastPieceI][lastPieceJ].draw(canvas, 0, (Resources.getSystem().getDisplayMetrics().heightPixels - Resources.getSystem().getDisplayMetrics().widthPixels) / 2, Resources.getSystem().getDisplayMetrics().widthPixels / 8, getAdjustedCoord(lastPieceI), getAdjustedCoord(lastPieceJ));
            }
        }

        if(lastPieceMovedFromI != -1) {
            if ((getAdjustedCoord(lastPieceMovedFromI) % 2) == (getAdjustedCoord(lastPieceMovedFromJ) % 2)) {
                canvas.drawRect(x + getAdjustedCoord(lastPieceMovedFromJ) * (w / 8), y + getAdjustedCoord(lastPieceMovedFromI) * (h / 8), x + getAdjustedCoord(lastPieceMovedFromJ) * (w / 8) + w / 8, y + getAdjustedCoord(lastPieceMovedFromI) * (h / 8) + h / 8, paintHighlightLightSquareLastPiece);
            } else {
                canvas.drawRect(x + getAdjustedCoord(lastPieceMovedFromJ) * (w / 8), y + getAdjustedCoord(lastPieceMovedFromI) * (h / 8), x + getAdjustedCoord(lastPieceMovedFromJ) * (w / 8) + w / 8, y + getAdjustedCoord(lastPieceMovedFromI) * (h / 8) + h / 8, paintHighlightDarkSquareLastPiece);
            }
        }

        if(whitePawnPromotion.getIsActive()) {
            whitePawnPromotion.draw(canvas);
        }
        if(blackPawnPromotion.getIsActive()) {
            blackPawnPromotion.draw(canvas);
        }
    }

    //if the user clicks a knight we want to change it into a bishop. if he clicks a bishop we want to change it into a knight
    public boolean changeKnightBishop(int x1, int y1, boolean playerIsWhite) {
        if (board[x1][y1] != null) {
            if (board[x1][y1].isWhite() == playerIsWhite) {
                if (board[x1][y1] instanceof Knight) {
                    if(playerIsWhite)
                        board[x1][y1] = new Bishop(bmpWhiteBishop, true);
                    else
                        board[x1][y1] = new Bishop(bmpBlackBishop, false);
                    return true;
                }
                else if (board[x1][y1] instanceof Bishop) {
                    if(playerIsWhite) {
                        board[x1][y1] = new Knight(bmpWhiteKnight, true);
                        if(!(possibleMovesKnightJWhite == null)) {
                            ((Knight) board[x1][y1]).addMoves(possibleMovesKnightJWhite, possibleMovesKnightIWhite);
                        }
                    }
                    else {
                        board[x1][y1] = new Knight(bmpBlackKnight, false);
                        if(!(possibleMovesKnightJBlack == null)) {
                            ((Knight) board[x1][y1]).addMoves(possibleMovesKnightJBlack, possibleMovesKnightIBlack);
                        }
                    }
                    updatePossibleMoves(!playerIsWhite);
                    return true;
                }
            }
        }
        return false;
    }

    //if the user clicks a pawn we want to change it into a knight
    public boolean changePawnKnight(int x1, int y1, boolean playerIsWhite) {
        if (board[x1][y1] != null) {
            if (board[x1][y1].isWhite() == playerIsWhite) {
                if (board[x1][y1] instanceof Pawn) {
                    if(playerIsWhite) {
                        board[x1][y1] = new Knight(bmpWhiteKnight, true);
                        if(!(possibleMovesKnightJWhite == null)) {
                            ((Knight) board[x1][y1]).addMoves(possibleMovesKnightJWhite, possibleMovesKnightIWhite);
                        }
                    }
                    else {
                        board[x1][y1] = new Knight(bmpBlackKnight, false);
                        if(!(possibleMovesKnightJBlack == null)) {
                            ((Knight) board[x1][y1]).addMoves(possibleMovesKnightJBlack, possibleMovesKnightIBlack);
                        }
                    }
                    updatePossibleMoves(!playerIsWhite);
                    return true;
                }
            }
        }
        return false;
    }

    //if the user clicks a pawn we want to change it into a bishop
    public boolean changePawnBishop(int x1, int y1, boolean playerIsWhite) {
        if (board[x1][y1] != null) {
            if (board[x1][y1].isWhite() == playerIsWhite) {
                if (board[x1][y1] instanceof Pawn) {
                    if(playerIsWhite) {
                        board[x1][y1] = new Bishop(bmpWhiteBishop, true);
                    }
                    else {
                        board[x1][y1] = new Bishop(bmpBlackBishop, false);
                    }
                    updatePossibleMoves(!playerIsWhite);
                    return true;
                }
            }
        }
        return false;
    }

    //if there is a piece in the place that was selected by the player select the piece so that it can be moved accordingly in the future
    public int selectPiece(float x, float y, boolean turnWhite, String connectionUniqueId, DatabaseReference reference, int moveNr) {
        if(turnWhite == playerIsWhite) {
            int selectedPieceX1 = selectedPieceX;
            int selectedPieceY1 = selectedPieceY;
            selectedPieceY = (int) ((x - Board.x) / (w / 8));
            selectedPieceX = (int) ((y - Board.y) / (h / 8));

            sameSelectedPiece = selectedPieceX1 == selectedPieceX && selectedPieceY1 == selectedPieceY;

            if (getPieceAtPosition(selectedPieceX, selectedPieceY) != null) {
                if (getPieceAtPosition(selectedPieceX, selectedPieceY).isWhite() == turnWhite) {
                    tempXSelected = Board.x + selectedPieceY * (w / 8) + ((w / 8) - Piece.w) / 2;
                    tempYSelected = Board.y + selectedPieceX * (w / 8) + ((w / 8) - Piece.h) / 2;
                    return 1;
                } else if (selectedPieceX1 != -1) {
                    if (getPieceAtPosition(selectedPieceX1, selectedPieceY1) != null && getPieceAtPosition(selectedPieceX1, selectedPieceY1).isWhite() == turnWhite)
                        if (isValid(selectedPieceX1, selectedPieceY1, selectedPieceX, selectedPieceY)) {
                            int d = move(selectedPieceX1, selectedPieceY1, selectedPieceX, selectedPieceY, connectionUniqueId, reference, moveNr);
                            selectedPieceX = -1;
                            selectedPieceY = -1;
                            updatePossibleMoves(turnWhite);
                            return d;
                        }
                }
            } else if (selectedPieceX1 != -1) {
                if (getPieceAtPosition(selectedPieceX1, selectedPieceY1) != null)
                    if (isValid(selectedPieceX1, selectedPieceY1, selectedPieceX, selectedPieceY)) {
                        int d = move(selectedPieceX1, selectedPieceY1, selectedPieceX, selectedPieceY, connectionUniqueId, reference, moveNr);
                        selectedPieceX = -1;
                        selectedPieceY = -1;
                        updatePossibleMoves(turnWhite);
                        return d;
                    }
            }

            selectedPieceY = -1;
            selectedPieceX = -1;
        }
        return 0;
    }

    //we want to either move the selected piece at the new position if it is valid
    //or to move it back to its initial position and to deselect it
    public int resetSelectedPiece(float x, float y, boolean turnWhite, String connectionUniqueId, DatabaseReference reference, int moveNr) {
        if(turnWhite == playerIsWhite) {
            int newX, newY;
            newY = (int) ((x - Board.x) / (w / 8));
            newX = (int) ((y - Board.y) / (h / 8));

            if (isValid(selectedPieceX, selectedPieceY, newX, newY)) {
                int d = move(selectedPieceX, selectedPieceY, newX, newY, connectionUniqueId, reference, moveNr);
                selectedPieceX = -1;
                selectedPieceY = -1;
                updatePossibleMoves(turnWhite);
                return d;
            }
            //if the piece hasn't moved we want to realign it properly in its square
            tempXSelected = Board.x + selectedPieceY * (w / 8) + ((w / 8) - Piece.w) / 2;
            tempYSelected = Board.y + selectedPieceX * (h / 8) + ((h / 8) - Piece.h) / 2;

            if (sameSelectedPiece) {
                selectedPieceY = -1;
                selectedPieceX = -1;
            }
        }
        return 0;
    }

    //check if the piece can be moved to the new position
    public boolean isValid(int x, int y, int newX, int newY) {
        //if it's the same position
        if(x == newX && y == newY)
            return false;
        //if the new position is not on the board the move is not valid
        if(newX < 0 || newY < 0 || newX > 7 || newY > 7)
            return false;

        newX = getAdjustedCoord(newX);
        newY = getAdjustedCoord(newY);

        return board[getAdjustedCoord(x)][getAdjustedCoord(y)].isValid(newX, newY);
    }

    public void pawnPromotionWhite(int i, String connectionUniqueId, DatabaseReference reference, int moveNr) {
        if(i > 3) {
            i = i - 4;
        }
        String piece = "";
        switch(i) {
            case 0:
                piece = "Queen";
                board[0][getAdjustedCoord(whitePawnPromotion.getJ())] = new Queen(bmpWhiteQueen, true);
                break;
            case 1:
                piece = "Rook";
                board[0][getAdjustedCoord(whitePawnPromotion.getJ())] = new Rook(bmpWhiteRook, true);
                break;
            case 2:
                piece = "Bishop";
                board[0][getAdjustedCoord(whitePawnPromotion.getJ())] = new Bishop(bmpWhiteBishop, true);
                break;
            case 3:
                piece = "Knight";
                board[0][getAdjustedCoord(whitePawnPromotion.getJ())] = new Knight(bmpWhiteKnight, true);
                if(!(possibleMovesKnightJWhite == null)) {
                    ((Knight) board[0][getAdjustedCoord(whitePawnPromotion.getJ())]).addMoves(possibleMovesKnightJWhite, possibleMovesKnightIWhite);
                }
                break;
        }
        board[1][whitePawnPromotion.getPawnY()] = null;
        lastPieceI = 0;
        lastPieceJ = getAdjustedCoord(whitePawnPromotion.getJ());
        lastPieceMovedFromI = 1;
        lastPieceMovedFromJ = whitePawnPromotion.getPawnY();
        updatePossibleMoves(false);

        addMoveDatabase(1, whitePawnPromotion.getPawnY(), 0, getAdjustedCoord(whitePawnPromotion.getJ()), connectionUniqueId, reference, moveNr, piece);
    }

    public void pawnPromotionBlack(int i, String connectionUniqueId, DatabaseReference reference, int moveNr) {
        if(i < 4) {
            i = i + 4;
        }
        String piece = "";
        switch(i) {
            case 4:
                piece = "Queen";
                board[7][getAdjustedCoord(blackPawnPromotion.getJ())] = new Queen(bmpBlackQueen, false);
                break;
            case 5:
                piece = "Rook";
                board[7][getAdjustedCoord(blackPawnPromotion.getJ())] = new Rook(bmpBlackRook, false);
                break;
            case 6:
                piece = "Bishop";
                board[7][getAdjustedCoord(blackPawnPromotion.getJ())] = new Bishop(bmpBlackBishop, false);
                break;
            case 7:
                piece = "Knight";
                board[7][getAdjustedCoord(blackPawnPromotion.getJ())] = new Knight(bmpBlackKnight, false);
                if(!(possibleMovesKnightJBlack == null)) {
                    ((Knight) board[7][getAdjustedCoord(blackPawnPromotion.getJ())]).addMoves(possibleMovesKnightJBlack, possibleMovesKnightIBlack);
                }
                break;
        }
        board[6][blackPawnPromotion.getPawnY()] = null;
        lastPieceI = 7;
        lastPieceJ = getAdjustedCoord(blackPawnPromotion.getJ());
        lastPieceMovedFromI = 6;
        lastPieceMovedFromJ = blackPawnPromotion.getPawnY();
        updatePossibleMoves(true);

        addMoveDatabase(6,blackPawnPromotion.getPawnY(), 7, getAdjustedCoord(blackPawnPromotion.getJ()), connectionUniqueId, reference, moveNr, piece);
    }

    //move one piece from one position to another
    public int move(int x, int y, int newX, int newY, String connectionUniqueId, DatabaseReference reference, int moveNr) {
        //check pawn promotion

        if (board[getAdjustedCoord(x)][getAdjustedCoord(y)] instanceof Pawn) {
            if (board[getAdjustedCoord(x)][getAdjustedCoord(y)].isWhite() && (getAdjustedCoord(newX) == 0)) {
                whitePawnPromotion.setIsActive(true, newY, getAdjustedCoord(y));
                return 0;
            } else if (!board[getAdjustedCoord(x)][getAdjustedCoord(y)].isWhite() && (getAdjustedCoord(newX) == 7)) {
                blackPawnPromotion.setIsActive(true, newY, getAdjustedCoord(y));
                return 0;
            }
        }

        if(!whitePawnPromotion.getIsActive() && !blackPawnPromotion.getIsActive()) {
            boolean castle = false;

            //check en passant
            if ((board[getAdjustedCoord(x)][getAdjustedCoord(y)] instanceof Pawn) && (board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] == null) && (y != newY)) {
                board[getAdjustedCoord(x)][getAdjustedCoord(newY)] = null;
            }
            //check castling
            if (board[getAdjustedCoord(x)][getAdjustedCoord(y)].isWhite()) {
                if (board[getAdjustedCoord(x)][getAdjustedCoord(y)] instanceof King) {
                    if ((getAdjustedCoord(x) == 7) && (getAdjustedCoord(y) == 4)) {
                        //white king side castle
                        if ((getAdjustedCoord(newX) == 7) && (getAdjustedCoord(newY) == 6)) {
                            castle = true;
                            board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] = board[getAdjustedCoord(x)][getAdjustedCoord(y)];
                            board[getAdjustedCoord(x)][getAdjustedCoord(y)] = null;
                            board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].setHasMoved(true);
                            board[7][5] = board[7][7];
                            board[7][7] = null;
                            board[7][5].setHasMoved(true);

                            whiteKingI = getAdjustedCoord(newX);
                            whiteKingJ = getAdjustedCoord(newY);

                            addMoveDatabase(getAdjustedCoord(x), getAdjustedCoord(y), getAdjustedCoord(newX), getAdjustedCoord(newY), connectionUniqueId, reference, moveNr, "");
                        }
                        //white queen side castle
                        else if ((getAdjustedCoord(newX) == 7) && (getAdjustedCoord(newY) == 2)) {
                            castle = true;
                            board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] = board[getAdjustedCoord(x)][getAdjustedCoord(y)];
                            board[getAdjustedCoord(x)][getAdjustedCoord(y)] = null;
                            board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].setHasMoved(true);
                            board[7][3] = board[7][0];
                            board[7][0] = null;
                            board[7][3].setHasMoved(true);

                            whiteKingI = getAdjustedCoord(newX);
                            whiteKingJ = getAdjustedCoord(newY);

                            addMoveDatabase(getAdjustedCoord(x), getAdjustedCoord(y), getAdjustedCoord(newX), getAdjustedCoord(newY), connectionUniqueId, reference, moveNr, "");
                        }
                    }
                }
            } else {
                if (board[getAdjustedCoord(x)][getAdjustedCoord(y)] instanceof King) {
                    if ((getAdjustedCoord(x) == 0) && (getAdjustedCoord(y) == 4)) {
                        //black king side castle
                        if ((getAdjustedCoord(newX) == 0) && (getAdjustedCoord(newY) == 6)) {
                            castle = true;
                            board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] = board[getAdjustedCoord(x)][getAdjustedCoord(y)];
                            board[getAdjustedCoord(x)][getAdjustedCoord(y)] = null;
                            board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].setHasMoved(true);
                            board[0][5] = board[0][7];
                            board[0][7] = null;
                            board[0][5].setHasMoved(true);

                            blackKingI = getAdjustedCoord(newX);
                            blackKingJ = getAdjustedCoord(newY);

                            addMoveDatabase(getAdjustedCoord(x), getAdjustedCoord(y), getAdjustedCoord(newX), getAdjustedCoord(newY), connectionUniqueId, reference, moveNr, "");
                        }
                        //black queen side castle
                        else if ((getAdjustedCoord(newX) == 0) && (getAdjustedCoord(newY) == 2)) {
                            castle = true;
                            board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] = board[getAdjustedCoord(x)][getAdjustedCoord(y)];
                            board[getAdjustedCoord(x)][getAdjustedCoord(y)] = null;
                            board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].setHasMoved(true);
                            board[0][3] = board[0][0];
                            board[0][0] = null;
                            board[0][3].setHasMoved(true);

                            blackKingI = getAdjustedCoord(newX);
                            blackKingJ = getAdjustedCoord(newY);

                            addMoveDatabase(getAdjustedCoord(x), getAdjustedCoord(y), getAdjustedCoord(newX), getAdjustedCoord(newY), connectionUniqueId, reference, moveNr, "");
                        }
                    }
                }
            }

            if (!castle) {
                board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] = board[getAdjustedCoord(x)][getAdjustedCoord(y)];
                board[getAdjustedCoord(x)][getAdjustedCoord(y)] = null;
                board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].setHasMoved(true);
                if (board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] instanceof King) {
                    if (board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].isWhite()) {
                        whiteKingI = getAdjustedCoord(newX);
                        whiteKingJ = getAdjustedCoord(newY);
                    } else {
                        blackKingI = getAdjustedCoord(newX);
                        blackKingJ = getAdjustedCoord(newY);
                    }
                }

                addMoveDatabase(getAdjustedCoord(x), getAdjustedCoord(y), getAdjustedCoord(newX), getAdjustedCoord(newY), connectionUniqueId, reference, moveNr, "");
            }

            lastPieceI = getAdjustedCoord(newX);
            lastPieceJ = getAdjustedCoord(newY);
            lastPieceMovedFromI = getAdjustedCoord(x);
            lastPieceMovedFromJ = getAdjustedCoord(y);
        }
        return -1;
    }

    //if the other player has promoted a pawn we want the pawn to change to the selected piece
    public void movePawnPromoNoDatabaseWhite(String piece, int y) {
        piece = piece.trim();
        if(piece.equals("Queen")) {
            board[0][y] = new Queen(bmpWhiteQueen, true);
        }
        else if(piece.equals("Rook")) {
            board[0][y] = new Rook(bmpWhiteRook, true);
        }
        else if(piece.equals("Bishop")) {
            board[0][y] = new Bishop(bmpWhiteBishop, true);
        }
        else if(piece.equals("Knight")) {
            board[0][y] = new Knight(bmpWhiteKnight, true);
            if(!(possibleMovesKnightJWhite == null)) {
                ((Knight) board[0][y]).addMoves(possibleMovesKnightJWhite, possibleMovesKnightIWhite);
            }
        }
    }

    public void movePawnPromoNoDatabaseBlack(String piece, int y) {
        piece = piece.trim();
        if(piece.equals("Queen")) {
            board[7][y] = new Queen(bmpBlackQueen, false);
        }
        else if(piece.equals("Rook")) {
            board[7][y] = new Rook(bmpBlackRook, false);
        }
        else if(piece.equals("Bishop")) {
            board[7][y] = new Bishop(bmpBlackBishop, false);
        }
        else if(piece.equals("Knight")) {
            board[7][y] = new Knight(bmpBlackKnight, false);
            if(!(possibleMovesKnightJBlack == null)) {
                ((Knight) board[7][y]).addMoves(possibleMovesKnightJBlack, possibleMovesKnightIBlack);
            }
        }
    }

    public int moveNoDatabase(int x, int y, int newX, int newY, boolean turnWhite, String piece) {
        x = getAdjustedCoord(x);
        y = getAdjustedCoord(y);
        newX = getAdjustedCoord(newX);
        newY = getAdjustedCoord(newY);

        boolean pawnPromo = false;

        //check pawn promotion
        if (board[getAdjustedCoord(x)][getAdjustedCoord(y)] instanceof Pawn) {
            if (board[getAdjustedCoord(x)][getAdjustedCoord(y)].isWhite() && (getAdjustedCoord(newX) == 0)) {
                pawnPromo = true;
                movePawnPromoNoDatabaseWhite(piece, getAdjustedCoord(newY));
            } else if (!board[getAdjustedCoord(x)][getAdjustedCoord(y)].isWhite() && (getAdjustedCoord(newX) == 7)) {
                pawnPromo = true;
                movePawnPromoNoDatabaseBlack(piece, getAdjustedCoord(newY));
            }
        }

        boolean castle = false;

        //check en passant
        if ((board[getAdjustedCoord(x)][getAdjustedCoord(y)] instanceof Pawn) && (board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] == null) && (y != newY)) {
            board[getAdjustedCoord(x)][getAdjustedCoord(newY)] = null;
        }
        //check castling
        if (board[getAdjustedCoord(x)][getAdjustedCoord(y)].isWhite()) {
            if (board[getAdjustedCoord(x)][getAdjustedCoord(y)] instanceof King) {
                if ((getAdjustedCoord(x) == 7) && (getAdjustedCoord(y) == 4)) {
                    //white king side castle
                    if ((getAdjustedCoord(newX) == 7) && (getAdjustedCoord(newY) == 6)) {
                        castle = true;
                        board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] = board[getAdjustedCoord(x)][getAdjustedCoord(y)];
                        board[getAdjustedCoord(x)][getAdjustedCoord(y)] = null;
                        board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].setHasMoved(true);
                        board[7][5] = board[7][7];
                        board[7][7] = null;
                        board[7][5].setHasMoved(true);

                        whiteKingI = getAdjustedCoord(newX);
                        whiteKingJ = getAdjustedCoord(newY);
                    }
                    //white queen side castle
                    else if ((getAdjustedCoord(newX) == 7) && (getAdjustedCoord(newY) == 2)) {
                        castle = true;
                        board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] = board[getAdjustedCoord(x)][getAdjustedCoord(y)];
                        board[getAdjustedCoord(x)][getAdjustedCoord(y)] = null;
                        board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].setHasMoved(true);
                        board[7][3] = board[7][0];
                        board[7][0] = null;
                        board[7][3].setHasMoved(true);

                        whiteKingI = getAdjustedCoord(newX);
                        whiteKingJ = getAdjustedCoord(newY);
                    }
                }
            }
        } else {
            if (board[getAdjustedCoord(x)][getAdjustedCoord(y)] instanceof King) {
                if ((getAdjustedCoord(x) == 0) && (getAdjustedCoord(y) == 4)) {
                    //black king side castle
                    if ((getAdjustedCoord(newX) == 0) && (getAdjustedCoord(newY) == 6)) {
                        castle = true;
                        board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] = board[getAdjustedCoord(x)][getAdjustedCoord(y)];
                        board[getAdjustedCoord(x)][getAdjustedCoord(y)] = null;
                        board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].setHasMoved(true);
                        board[0][5] = board[0][7];
                        board[0][7] = null;
                        board[0][5].setHasMoved(true);

                        blackKingI = getAdjustedCoord(newX);
                        blackKingJ = getAdjustedCoord(newY);
                    }
                    //black queen side castle
                    else if ((getAdjustedCoord(newX) == 0) && (getAdjustedCoord(newY) == 2)) {
                        castle = true;
                        board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] = board[getAdjustedCoord(x)][getAdjustedCoord(y)];
                        board[getAdjustedCoord(x)][getAdjustedCoord(y)] = null;
                        board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].setHasMoved(true);
                        board[0][3] = board[0][0];
                        board[0][0] = null;
                        board[0][3].setHasMoved(true);

                        blackKingI = getAdjustedCoord(newX);
                        blackKingJ = getAdjustedCoord(newY);
                    }
                }
            }
        }

        if (!castle) {
            if(!pawnPromo) {
                board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] = board[getAdjustedCoord(x)][getAdjustedCoord(y)];
            }
            board[getAdjustedCoord(x)][getAdjustedCoord(y)] = null;
            board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].setHasMoved(true);
            if (board[getAdjustedCoord(newX)][getAdjustedCoord(newY)] instanceof King) {
                if (board[getAdjustedCoord(newX)][getAdjustedCoord(newY)].isWhite()) {
                    whiteKingI = getAdjustedCoord(newX);
                    whiteKingJ = getAdjustedCoord(newY);
                } else {
                    blackKingI = getAdjustedCoord(newX);
                    blackKingJ = getAdjustedCoord(newY);
                }
            }
        }

        lastPieceI = getAdjustedCoord(newX);
        lastPieceJ = getAdjustedCoord(newY);
        lastPieceMovedFromI = getAdjustedCoord(x);
        lastPieceMovedFromJ = getAdjustedCoord(y);
        updatePossibleMoves(turnWhite);
        return -1;
    }

    public void addMoveDatabase(int x, int y, int newX, int newY, String connectionUniqueId, DatabaseReference reference, int moveNr, String piece) {

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if(snapshot.child("moves").exists()) {
                        int moveCount = (int) snapshot.getChildrenCount() / 5;
                        String newMoveName = "move" + (moveNr - 1);
                        DatabaseReference moveRef = snapshot.child("moves").child(newMoveName).getRef();

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("pawnPromo", piece);
                        updateMap.put("x", x);
                        updateMap.put("y", y);
                        updateMap.put("newX", newX);
                        updateMap.put("newY", newY);

                        moveRef.updateChildren(updateMap);
                    }
                    else {
                        DatabaseReference moveRef = snapshot.child("moves").child("move0").getRef();

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("pawnPromo", piece);
                        updateMap.put("x", x);
                        updateMap.put("y", y);
                        updateMap.put("newX", newX);
                        updateMap.put("newY", newY);

                        moveRef.updateChildren(updateMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
            }
        };

        reference.child(connectionUniqueId).addListenerForSingleValueEvent(listener);
    }

    //move the card by x, y
    //function used in order to move the piece when it is being dragged by the user
    public void moveTempPos(float x, float y) {
        tempXSelected += x;
        tempYSelected += y;
        if(tempXSelected < 0)
            tempXSelected = 0;
        if(tempYSelected < 0)
            tempYSelected = 0;
        if(tempXSelected + Piece.w > (Board.x + w))
            tempXSelected = w - Board.x - Piece.w - 1;
        if(tempYSelected + Piece.h > (Board.y * 2 + h))
            tempYSelected = Board.y * 2 + h - Piece.h - 1;
    }

    public void drawPossibleMovesSelectedPiece(Canvas canvas) {
        for(Position pos : board[getAdjustedCoord(selectedPieceX)][getAdjustedCoord(selectedPieceY)].getPossibleMoves()) {
            if((getAdjustedCoord(pos.x) % 2) == (getAdjustedCoord(pos.y) % 2)) {
                if(board[pos.x][pos.y] == null)
                    canvas.drawCircle(x + getAdjustedCoord(pos.y) * (w / 8) + w / 16, y + getAdjustedCoord(pos.x) * (h / 8) + h / 16, 25, paintHighlightLightSquare);
                else {
                    RectF oval = new RectF();
                    oval.set(x + getAdjustedCoord(pos.y) * (w / 8) + 20, y + getAdjustedCoord(pos.x) * (h / 8) + 20, x + getAdjustedCoord(pos.y) * (w / 8) + w / 8 - 20, y + getAdjustedCoord(pos.x) * (h / 8) + h / 8 - 20);
                    canvas.drawArc(oval, 0F, 360F, false, paintHighlightLightSquarePiece);
                }
            }
            else {
                if(board[pos.x][pos.y] == null)
                    canvas.drawCircle(x + getAdjustedCoord(pos.y) * (w / 8) + w / 16, y + getAdjustedCoord(pos.x) * (h / 8) + h / 16, 25, paintHighlightDarkSquare);
                else {
                    RectF oval = new RectF();
                    oval.set(x + getAdjustedCoord(pos.y) * (w / 8) + 20, y + getAdjustedCoord(pos.x) * (h / 8) + 20, x + getAdjustedCoord(pos.y) * (w / 8) + w / 8 - 20, y + getAdjustedCoord(pos.x) * (h / 8) + h / 8 - 20);
                    canvas.drawArc(oval, 0F, 360F, false, paintHighlightDarkSquarePiece);
                }
            }
        }
    }

    //update the array that contains all of the possible moves of the pieces
    public void updatePossibleMoves(boolean turnWhite) {
        nrPossibleMovesWhite = 0;
        nrPossibleMovesBlack = 0;

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board[i][j] != null) {
                    if(board[i][j] instanceof Knight) {
                        Knight knight = (Knight) board[i][j];
                        if(knight.isWhite()) {
                            knight.updatePossibleMoves(i, j, board, whiteKingI, whiteKingJ);
                            if(knight.possibleMoves.size() > 0)
                                nrPossibleMovesWhite++;
                        }
                        else {
                            knight.updatePossibleMoves(i, j, board, blackKingI, blackKingJ);
                            if(knight.possibleMoves.size() > 0)
                                nrPossibleMovesBlack++;
                        }
                    }
                    else if(board[i][j] instanceof King) {
                        King king = (King) board[i][j];
                        king.updatePossibleMoves(i, j, board);
                        if(king.isWhite()) {
                            if(king.possibleMoves.size() > 0)
                                nrPossibleMovesWhite++;
                        }
                        else {
                            if(king.possibleMoves.size() > 0)
                                nrPossibleMovesBlack++;
                        }
                    }
                    else if(board[i][j] instanceof Rook) {
                        Rook rook = (Rook) board[i][j];
                        if(rook.isWhite()) {
                            rook.updatePossibleMoves(i, j, board, whiteKingI, whiteKingJ);
                            if (rook.possibleMoves.size() > 0)
                                nrPossibleMovesWhite++;
                        }
                        else {
                            rook.updatePossibleMoves(i, j, board, blackKingI, blackKingJ);
                            if(rook.possibleMoves.size() > 0)
                                nrPossibleMovesBlack++;
                        }
                    }
                    else if(board[i][j] instanceof Bishop) {
                        Bishop bishop = (Bishop) board[i][j];
                        if(bishop.isWhite()) {
                            bishop.updatePossibleMoves(i, j, board, whiteKingI, whiteKingJ);
                            if(bishop.possibleMoves.size() > 0)
                                nrPossibleMovesWhite++;
                        }
                        else {
                            bishop.updatePossibleMoves(i, j, board, blackKingI, blackKingJ);
                            if (bishop.possibleMoves.size() > 0)
                                nrPossibleMovesBlack++;
                        }
                    }
                    else if(board[i][j] instanceof Queen) {
                        Queen queen = (Queen) board[i][j];
                        if(queen.isWhite()) {
                            queen.updatePossibleMoves(i, j, board, whiteKingI, whiteKingJ);
                            if (queen.possibleMoves.size() > 0)
                                nrPossibleMovesWhite++;
                        }
                        else {
                            queen.updatePossibleMoves(i, j, board, blackKingI, blackKingJ);
                            if(queen.possibleMoves.size() > 0)
                                nrPossibleMovesBlack++;
                        }
                    }
                    else if(board[i][j] instanceof Pawn) {
                        Pawn pawn = (Pawn) board[i][j];
                        if(pawn.isWhite()) {
                            pawn.updatePossibleMoves(i, j, board, whiteKingI, whiteKingJ, lastPieceI, lastPieceJ, lastPieceMovedFromI);
                            if (pawn.possibleMoves.size() > 0)
                                nrPossibleMovesWhite++;
                        }
                        else {
                            pawn.updatePossibleMoves(i, j, board, blackKingI, blackKingJ, lastPieceI, lastPieceJ, lastPieceMovedFromI);
                            if (pawn.possibleMoves.size() > 0)
                                nrPossibleMovesBlack++;
                        }
                    }
                }
            }
        }
    }

    //this function is going to return a String that represents the current state of the board
    public String getMovesNotation() {
        String notation = "";
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(!(board[i][j] == null)) {
                    if(board[i][j].isWhite())
                        notation = notation + "w";
                    else
                        notation = notation + "w";
                }
                if(board[i][j] instanceof Pawn) {
                    notation = notation + "P";
                }
                else if(board[i][j] instanceof Knight) {
                    notation = notation + "N";
                }
                else if(board[i][j] instanceof Bishop) {
                    notation = notation + "B";
                }
                else if(board[i][j] instanceof Rook) {
                    notation = notation + "R";
                }
                else if(board[i][j] instanceof Queen) {
                    notation = notation + "Q";
                }
                else if(board[i][j] instanceof King) {
                    notation = notation + "K";
                }
                else {
                    notation = notation + "-";
                }
            }
        }

        return notation;
    }

    public boolean checkInsufficientMaterial() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 9; j++) {
                if((board[i][j] instanceof Queen) || (board[i][j] instanceof Rook) || (board[i][j] instanceof Bishop) || (board[i][j] instanceof Knight) || (board[i][j] instanceof Pawn))
                    return false;
            }
        }
        return true;
    }

    public int getNrPossibleMovesWhite() {
        return nrPossibleMovesWhite;
    }

    public int getNrPossibleMovesBlack() {
        return nrPossibleMovesBlack;
    }

    public int getWhiteKingI() {
        return whiteKingI;
    }

    public int getBlackKingI() {
        return blackKingI;
    }

    public int getWhiteKingJ() {
        return whiteKingJ;
    }

    public int getBlackKingJ() {
        return blackKingJ;
    }
}
