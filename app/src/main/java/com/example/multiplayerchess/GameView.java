package com.example.multiplayerchess;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.multiplayerchess.Plugin.Card;
import com.example.multiplayerchess.Plugin.Card1;
import com.example.multiplayerchess.Plugin.Card2;
import com.example.multiplayerchess.Plugin.Card3;
import com.example.multiplayerchess.Plugin.Card4;
import com.example.multiplayerchess.Plugin.Deck;
import com.example.multiplayerchess.Plugin.Hand;
import com.example.multiplayerchess.chess.Board;
import com.example.multiplayerchess.chess.GameOver;
import com.example.multiplayerchess.chess.King;
import com.example.multiplayerchess.chess.PawnPromotion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Integer.parseInt;
import static java.lang.Math.max;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;
    private Deck deckPlayer, deckOpponent;
    private Hand handPlayer, handOpponent;
    private Board board;
    private int touchDownHand = -1;
    private boolean touchDownBoard = false;
    private float initTouchX, initTouchY;
    private boolean gameOver;
    private boolean turnWhite;
    private final Context context;
    int cardPlayed;

    FirebaseDatabase database;
    DatabaseReference reference;
    private String playerName = "player_name";
    private String opponentName = "player_name";
    ProgressDialog progressDialog;

    private Paint paintPlayerNames;
    private boolean isWhitePlayer = true;
    private ValueEventListener eventListener;
    private String connectionUniqueId = "";
    private AppCompatActivity hostActivity;
    private ValueEventListener movesEventListener;
    private int moveNr;
    private int mana, manaOpponent;
    private int maxMana = 10;
    Bitmap coinBitmap;
    private int turnDrawCard;
    private int turnDrawCardOpponent;
    private int[] possibleMovesKnightJ;
    private int[] possibleMovesKnightI;
    Random random;
    String lastCardPlayedOpponent;
    boolean lastCardPlayedPlayer;
    ArrayList<String> listMoves;
    private int coinHeight;

    private static final String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int roomCodeLength = 6;
    private String roomCode;
    private boolean drawByRepetition;

    public static String generateRoomCode() {
        StringBuilder codeBuilder = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < roomCodeLength; i++) {
            int ind = random.nextInt(allowedCharacters.length());
            char c = allowedCharacters.charAt(ind);
            codeBuilder.append(c);
        }

        return codeBuilder.toString();
    }

    public GameView(Context context, AppCompatActivity hostActivity, boolean isHost) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
        this.hostActivity = hostActivity;

        thread = new MainThread(getHolder(), this);
        setFocusable(true);

        gameOver = false;
        turnWhite = true;

        random = new Random();
        //create an array containing images of the backs of the cards which we can display in order to represent the cards of the opponents
        ArrayList<Bitmap> bmpCardsOpponent = new ArrayList<Bitmap>();

        //create a bitmap with the card back which we will keep adding to the array
        Bitmap bmpOpponent = BitmapFactory.decodeResource(getResources(),R.drawable.cardback);

        //create an array containing all possible cards
        ArrayList<Bitmap> bmpAllCards = new ArrayList<Bitmap>();
        bmpAllCards.add(BitmapFactory.decodeResource(getResources(), R.drawable.card1));
        bmpAllCards.add(BitmapFactory.decodeResource(getResources(), R.drawable.card3));
        bmpAllCards.add(BitmapFactory.decodeResource(getResources(), R.drawable.card4));

        //create an array containing all the bitmaps of the cards of the player
        ArrayList<Card> bmpCards = new ArrayList<Card>();

        //add cards that can only be played once
        bmpCards.add(new Card2(BitmapFactory.decodeResource(getResources(), R.drawable.card2)));

        //randomly fill the deck with cards and the opponent's deck with the bitmap cardback
        for (int i = 0; i < Deck.nrCardsMax; i++) {
            if(i >= bmpCards.size()) {
                int randomIndex = random.nextInt(bmpAllCards.size());
                Bitmap randomBmp = bmpAllCards.get(randomIndex);

                switch (randomIndex) {
                    case 0:
                        bmpCards.add(new Card1(randomBmp));
                        break;
                    case 1:
                        bmpCards.add(new Card3(randomBmp));
                        break;
                    case 2:
                        bmpCards.add(new Card4(randomBmp));
                        break;
                }
            }

            bmpCardsOpponent.add(bmpOpponent);
        }

        mana = 0;
        manaOpponent = 0;

        //create the objects which we are going to draw
        board = new Board(BitmapFactory.decodeResource(getResources(),R.drawable.board),
                BitmapFactory.decodeResource(getResources(),R.drawable.whitepawn),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackpawn),
                BitmapFactory.decodeResource(getResources(),R.drawable.whiteknight),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackknight),
                BitmapFactory.decodeResource(getResources(),R.drawable.whitebishop),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackbishop),
                BitmapFactory.decodeResource(getResources(),R.drawable.whiterook),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackrook),
                BitmapFactory.decodeResource(getResources(),R.drawable.whitequeen),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackqueen),
                BitmapFactory.decodeResource(getResources(),R.drawable.whiteking),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackking),
                true);

        deckPlayer = new Deck(BitmapFactory.decodeResource(getResources(), R.drawable.cardback), bmpCards);
        deckOpponent = new Deck(BitmapFactory.decodeResource(getResources(),R.drawable.cardback), bmpCardsOpponent, false);

        handPlayer = new Hand(deckPlayer, false);
        handOpponent = new Hand(deckOpponent, true);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        if(isHost) {
            roomCode = generateRoomCode();
            progressDialog.setMessage("Waiting for Opponent. Room code is " + roomCode);
        }
        else
            progressDialog.setMessage("Waiting for Opponent");
        progressDialog.show();
        final View view = this;

        //When the progress dialog gets cancelled we want the connection to be deleted and we want to return to the game menu
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(eventListener != null)
                    reference.removeEventListener(eventListener);
                if(connectionUniqueId != "") {
                    reference.child(connectionUniqueId).removeValue();
                }
                Intent intent = new Intent(context, GameMenu.class);
                context.startActivity(intent);

                if (hostActivity != null) {
                    hostActivity.finish();
                }
                pause();
            }
        });

        database = FirebaseDatabase.getInstance();
        if(isHost)
            reference = database.getReference().child("ConnectionsFriends");
        else
            reference = database.getReference().child("Connections");

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        playerName = sharedPreferences.getString("username", null);

        if(!isHost) {
            findAvailableConnection(new ConnectionCallback() {
                @Override
                public void onConnectionFound(boolean isConnected) {
                    if (isConnected) {
                        //Connection found
                    } else {
                        //Connection not found
                        createNewConnection(false);
                    }
                }
            });
        }
        else {
            createNewConnection(true);
        }

        paintPlayerNames = new Paint();
        paintPlayerNames.setColor(Color.WHITE);
        paintPlayerNames.setTextSize(80);

        createCoinBitmap();
    }

    public GameView(Context context, AppCompatActivity hostActivity, String connectionUniqueId) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
        this.hostActivity = hostActivity;

        thread = new MainThread(getHolder(), this);
        setFocusable(true);

        gameOver = false;
        turnWhite = true;

        random = new Random();
        //create an array containing images of the backs of the cards which we can display in order to represent the cards of the opponents
        ArrayList<Bitmap> bmpCardsOpponent = new ArrayList<Bitmap>();

        //create a bitmap with the card back which we will keep adding to the array
        Bitmap bmpOpponent = BitmapFactory.decodeResource(getResources(),R.drawable.cardback);

        //create an array containing all possible cards
        ArrayList<Bitmap> bmpAllCards = new ArrayList<Bitmap>();
        bmpAllCards.add(BitmapFactory.decodeResource(getResources(), R.drawable.card1));
        bmpAllCards.add(BitmapFactory.decodeResource(getResources(), R.drawable.card3));
        bmpAllCards.add(BitmapFactory.decodeResource(getResources(), R.drawable.card4));

        //create an array containing all the bitmaps of the cards of the player
        ArrayList<Card> bmpCards = new ArrayList<Card>();

        //add cards that can only be played once
        bmpCards.add(new Card2(BitmapFactory.decodeResource(getResources(), R.drawable.card2)));

        //randomly fill the deck with cards and the opponent's deck with the bitmap cardback
        for (int i = 0; i < Deck.nrCardsMax; i++) {
            if(i >= bmpCards.size()) {
                int randomIndex = random.nextInt(bmpAllCards.size());
                Bitmap randomBmp = bmpAllCards.get(randomIndex);

                switch (randomIndex) {
                    case 0:
                        bmpCards.add(new Card1(randomBmp));
                        break;
                    case 1:
                        bmpCards.add(new Card3(randomBmp));
                        break;
                    case 2:
                        bmpCards.add(new Card4(randomBmp));
                        break;
                }
            }

            bmpCardsOpponent.add(bmpOpponent);
        }

        mana = 0;
        manaOpponent = 0;

        //create the objects which we are going to draw
        board = new Board(BitmapFactory.decodeResource(getResources(),R.drawable.board),
                BitmapFactory.decodeResource(getResources(),R.drawable.whitepawn),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackpawn),
                BitmapFactory.decodeResource(getResources(),R.drawable.whiteknight),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackknight),
                BitmapFactory.decodeResource(getResources(),R.drawable.whitebishop),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackbishop),
                BitmapFactory.decodeResource(getResources(),R.drawable.whiterook),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackrook),
                BitmapFactory.decodeResource(getResources(),R.drawable.whitequeen),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackqueen),
                BitmapFactory.decodeResource(getResources(),R.drawable.whiteking),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackking),
                true);

        deckPlayer = new Deck(BitmapFactory.decodeResource(getResources(), R.drawable.cardback), bmpCards);
        deckOpponent = new Deck(BitmapFactory.decodeResource(getResources(),R.drawable.cardback), bmpCardsOpponent, false);

        handPlayer = new Hand(deckPlayer, false);
        handOpponent = new Hand(deckOpponent, true);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Trying to join room");
        progressDialog.show();
        final View view = this;

        //When the progress dialog gets cancelled we want the connection to be deleted and we want to return to the game menu
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(eventListener != null)
                    reference.removeEventListener(eventListener);
                if(connectionUniqueId != "") {
                    reference.child(connectionUniqueId).removeValue();
                }
                Intent intent = new Intent(context, GameMenu.class);
                context.startActivity(intent);

                if (hostActivity != null) {
                    hostActivity.finish();
                }
                pause();
            }
        });

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("ConnectionsFriends");

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        playerName = sharedPreferences.getString("username", null);

        findAvailableConnection(connectionUniqueId);

        paintPlayerNames = new Paint();
        paintPlayerNames.setColor(Color.WHITE);
        paintPlayerNames.setTextSize(80);

        createCoinBitmap();
    }

    public interface ConnectionCallback {
        void onConnectionFound(boolean isConnected);
    }

    public void createCoinBitmap() {
        Bitmap coinBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.coin1);

        String text = "0/10";
        Rect textBounds = new Rect();
        paintPlayerNames.getTextBounds(text, 0, text.length(), textBounds);
        coinHeight = textBounds.height();

        coinBitmap = Bitmap.createScaledBitmap(coinBitmap1, coinHeight, coinHeight, true);
    }

    //find available connection in Online mode
    public void findAvailableConnection(ConnectionCallback callback) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean foundAvailableConnection = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot.child("status").getValue(String.class).equals("waiting")) {
                        opponentName = String.valueOf(childSnapshot.child("player1").getValue());

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("player2", playerName);
                        updateMap.put("status", "playing");
                        updateMap.put("lastCardPlayed", "");

                        foundAvailableConnection = true;

                        Random random = new Random();
                        int randomNumber = random.nextInt(2) + 1;
                        if(randomNumber == 1) {
                            isWhitePlayer = true;
                            updateMap.put("playerWhite", playerName);
                        }
                        else {
                            isWhitePlayer = false;
                            updateMap.put("playerWhite", opponentName);
                        }

                        childSnapshot.getRef().updateChildren(updateMap);

                        connectionUniqueId = childSnapshot.getKey();

                        initGame();
                        break;
                    }
                }
                callback.onConnectionFound(foundAvailableConnection);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onConnectionFound(false);
            }
        });
    }

    //find available connection in Friends mode
    public void findAvailableConnection(final String connectionUniqueId) {
        this.connectionUniqueId = connectionUniqueId;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean foundAvailableConnection = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot.getKey().equals(connectionUniqueId)) {
                        opponentName = String.valueOf(childSnapshot.child("player1").getValue());

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("player2", playerName);
                        updateMap.put("status", "playing");
                        updateMap.put("lastCardPlayed", "");

                        foundAvailableConnection = true;

                        Random random = new Random();
                        int randomNumber = random.nextInt(2) + 1;
                        if(randomNumber == 1) {
                            isWhitePlayer = true;
                            updateMap.put("playerWhite", playerName);
                        }
                        else {
                            isWhitePlayer = false;
                            updateMap.put("playerWhite", opponentName);
                        }

                        childSnapshot.getRef().updateChildren(updateMap);
                        initGame();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //create a new connection in the database
    public void createNewConnection(boolean isHost) {
        connectionUniqueId = String.valueOf(System.currentTimeMillis());

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("status", "waiting");
        updateMap.put("player1", playerName);

        if(isHost) {
            updateMap.put("roomCode", roomCode);
        }

        reference.child(connectionUniqueId).updateChildren(updateMap);

        final boolean[] dataFound = {false};

        eventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reference.child(connectionUniqueId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && !dataFound[0]) {
                            if(dataSnapshot.child("playerWhite").exists() && dataSnapshot.child("player2").exists() && dataSnapshot.child("status").exists()) {
                                if(dataSnapshot.child("status").getValue(String.class).equals("playing")) {
                                    if(dataSnapshot.child("playerWhite").getValue(String.class) != "") {
                                        if(dataSnapshot.child("playerWhite").getValue(String.class).equals(playerName)) {
                                            isWhitePlayer = true;
                                        }
                                        else {
                                            isWhitePlayer = false;
                                        }

                                        if(dataSnapshot.child("player2").getValue(String.class) != "") {
                                            opponentName = dataSnapshot.child("player2").getValue(String.class);

                                            initGame();

                                            dataFound[0] = true;
                                            reference.child(connectionUniqueId).removeEventListener(this);
                                            reference.removeEventListener(eventListener);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createStateListener() {
        reference.child(connectionUniqueId).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if(!value.equals("playing") && !gameOver)
                    startGameOver(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createMovesListener() {
        DatabaseReference movesRef = reference.child(connectionUniqueId).child("moves");

        reference.child(connectionUniqueId).child("moves").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int nrMoveLastChild = 0;

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        nrMoveLastChild = max(nrMoveLastChild, parseInt(childSnapshot.getKey().substring("move".length())));
                    }
                    DataSnapshot lastChildSnapshot = snapshot.child("move" + nrMoveLastChild);

                    if (lastChildSnapshot != null && lastChildSnapshot.child("x").exists()
                            && lastChildSnapshot.child("y").exists()
                            && lastChildSnapshot.child("newX").exists()
                            && lastChildSnapshot.child("newY").exists() && lastChildSnapshot.child("pawnPromo").exists() && nrMoveLastChild >= moveNr) {

                        Integer x = lastChildSnapshot.child("x").getValue(Integer.class);
                        Integer y = lastChildSnapshot.child("y").getValue(Integer.class);
                        Integer newX = lastChildSnapshot.child("newX").getValue(Integer.class);
                        Integer newY = lastChildSnapshot.child("newY").getValue(Integer.class);

                        if (x != null && y != null && newX != null && newY != null) {
                            String pawnPromoPiece = lastChildSnapshot.child("pawnPromo").getValue(String.class);

                            if (!pawnPromoPiece.equals("")) {
                                board.moveNoDatabase(x, y, newX, newY, turnWhite, pawnPromoPiece);
                            } else {
                                board.moveNoDatabase(x, y, newX, newY, turnWhite, "");
                            }
                            //Toast.makeText(context, "It's your turn!", Toast.LENGTH_SHORT).show();
                            nextTurnOpponent();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void nextTurnOpponent() {
        //if a piece was moved we want the player to hear the sound of the piece moving
        MediaPlayer music = MediaPlayer.create(context, R.raw.pawn_move);
        music.start();

        addManaOpponent();

        if(turnDrawCardOpponent == 4) {
            turnDrawCardOpponent = 0;
            handOpponent.drawCard(deckOpponent);
        }
        else {
            turnDrawCardOpponent++;
        }
        turnWhite = !turnWhite;
        moveNr += 2;
        checkGameOver();

        String newMove = board.getMovesNotation();
        if(!checkDrawByRepetition(newMove))
            listMoves.add(newMove);
    }

    public boolean checkDrawByRepetition(String newMove) {
        int k = 0;
        for (String move : listMoves) {
            if(move.equals(newMove))
                k++;
        }
        if(k >= 2) {
            drawByRepetition = true;
            return true;
        }
        return false;
    }

    public void initGame() {
        board = new Board(BitmapFactory.decodeResource(getResources(),R.drawable.board),
                BitmapFactory.decodeResource(getResources(),R.drawable.whitepawn),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackpawn),
                BitmapFactory.decodeResource(getResources(),R.drawable.whiteknight),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackknight),
                BitmapFactory.decodeResource(getResources(),R.drawable.whitebishop),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackbishop),
                BitmapFactory.decodeResource(getResources(),R.drawable.whiterook),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackrook),
                BitmapFactory.decodeResource(getResources(),R.drawable.whitequeen),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackqueen),
                BitmapFactory.decodeResource(getResources(),R.drawable.whiteking),
                BitmapFactory.decodeResource(getResources(),R.drawable.blackking),
                isWhitePlayer);

        turnDrawCard = 0;
        turnDrawCardOpponent = 0;
        createStateListener();
        createMovesListener();

        if(!isWhitePlayer) {
            moveNr = 0;
        }
        else {
            moveNr = 1;
        }

        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        lastCardPlayedOpponent = "The opponent has played no card yet.";

        lastCardPlayedPlayer = false;
        cardPlayed = 0;
        listMoves = new ArrayList<>();

        drawByRepetition = false;

        handleLastCardPlayed();
    }

    public void handleLastCardPlayed() {
        reference.child(connectionUniqueId).child("lastCardPlayed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if(!snapshot.getValue().equals("")) {
                        if(lastCardPlayedPlayer == false) {
                            Long snapshotValue = snapshot.getValue(Long.class);
                            String val = snapshotValue.toString();

                            switch(val) {
                                case "1":
                                    reference.child(connectionUniqueId).child("cardChanged").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (snapshot.exists()) {
                                                if (dataSnapshot != null && dataSnapshot.child("x").exists()
                                                        && dataSnapshot.child("y").exists()) {
                                                    Integer x = dataSnapshot.child("x").getValue(Integer.class);
                                                    Integer y = dataSnapshot.child("y").getValue(Integer.class);

                                                    if (x != null && y != null) {
                                                        manaOpponent -= Card1.manaCard;
                                                        lastCardPlayedOpponent = "Opponent played: " + Card1.abilityCard;
                                                        Toast.makeText(context, "Opponent played: " + Card1.abilityCard, Toast.LENGTH_SHORT).show();
                                                        board.changeKnightBishop(x, y, !isWhitePlayer);
                                                        addManaOpponent();
                                                        String newMove = board.getMovesNotation();
                                                        if(!checkDrawByRepetition(newMove))
                                                            listMoves.add(newMove);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                    break;
                                case "2":
                                    manaOpponent -= Card2.manaCard;
                                    lastCardPlayedOpponent = "Opponent played: " + Card2.abilityCard;
                                    Toast.makeText(context, "Opponent played: " + Card2.abilityCard, Toast.LENGTH_SHORT).show();
                                    board.addNewMovesKnight(new int[]{-3, -1, 1, 3, 3, 1, -1, -3},
                                            new int[]{-1, -3, -3, -1, 1, 3, 3, 1}, turnWhite, !isWhitePlayer);
                                    addManaOpponent();
                                    break;
                                case "3":
                                    reference.child(connectionUniqueId).child("cardChanged").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (snapshot.exists()) {
                                                if (dataSnapshot != null && dataSnapshot.child("x").exists()
                                                        && dataSnapshot.child("y").exists()) {
                                                    Integer x = dataSnapshot.child("x").getValue(Integer.class);
                                                    Integer y = dataSnapshot.child("y").getValue(Integer.class);

                                                    if (x != null && y != null) {
                                                        manaOpponent -= Card3.manaCard;
                                                        lastCardPlayedOpponent = "Opponent played: " + Card3.abilityCard;
                                                        Toast.makeText(context, "Opponent played: " + Card3.abilityCard, Toast.LENGTH_SHORT).show();
                                                        board.changePawnKnight(x, y, !isWhitePlayer);
                                                        addManaOpponent();
                                                        String newMove = board.getMovesNotation();
                                                        if(!checkDrawByRepetition(newMove))
                                                            listMoves.add(newMove);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                    break;
                                case "4":
                                    reference.child(connectionUniqueId).child("cardChanged").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (snapshot.exists()) {
                                                if (dataSnapshot != null && dataSnapshot.child("x").exists()
                                                        && dataSnapshot.child("y").exists()) {
                                                    Integer x = dataSnapshot.child("x").getValue(Integer.class);
                                                    Integer y = dataSnapshot.child("y").getValue(Integer.class);

                                                    if (x != null && y != null) {
                                                        manaOpponent -= Card4.manaCard;
                                                        lastCardPlayedOpponent = "Opponent played: " + Card4.abilityCard;
                                                        Toast.makeText(context, "Opponent played: " + Card4.abilityCard, Toast.LENGTH_SHORT).show();
                                                        board.changePawnBishop(x, y, !isWhitePlayer);
                                                        addManaOpponent();

                                                        String newMove = board.getMovesNotation();
                                                        if(!checkDrawByRepetition(newMove))
                                                            listMoves.add(newMove);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                    break;
                                default:
                                    Toast.makeText(context, val, Toast.LENGTH_SHORT).show();
                                    handOpponent.drawCard(deckOpponent);
                                    break;
                            }
                            if(turnDrawCardOpponent == 4) {
                                turnDrawCardOpponent = 0;
                                handOpponent.drawCard(deckOpponent);
                            }
                            else {
                                turnDrawCardOpponent++;
                            }
                            turnWhite = !turnWhite;
                            moveNr++;
                            handOpponent.removeCard(0);
                        }
                        lastCardPlayedPlayer = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void pause() {
        thread.setRunning(false);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        resume();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(canvas != null) {
            //draw the background color
            canvas.drawARGB(255, 75, 74, 76);
            //draw the chess board
            board.draw(canvas);

            //draw the 2 decks
            deckPlayer.draw(canvas);
            deckOpponent.draw(canvas);

            //draw the hand of the player and of the opponent
            handOpponent.draw(canvas);
            handPlayer.draw(canvas);

            //draw the names of the players
            canvas.drawText(opponentName, 20, board.getY() - 50, paintPlayerNames);
            canvas.drawText(playerName, 20, board.getH() + board.getY() + 100, paintPlayerNames);

            //draw the amount of mana the players have
            String manaPlayer1 = manaOpponent + "/" + maxMana;
            String manaPlayer2 = mana + "/" + maxMana;
            float x1 = board.getW() - paintPlayerNames.measureText(manaPlayer1) - 120;
            float y1 = board.getY() - 50;

            float x2 = board.getW() - paintPlayerNames.measureText(manaPlayer2) - 120;
            float y2 = board.getH() + board.getY() + 100;

            canvas.drawText(manaPlayer1, x1, y1, paintPlayerNames);
            canvas.drawText(manaPlayer2, x2, y2, paintPlayerNames);

            canvas.drawBitmap(coinBitmap, x1 + paintPlayerNames.measureText(manaPlayer1) + 10, y1 - coinHeight + 3, null);
            canvas.drawBitmap(coinBitmap, x2 + paintPlayerNames.measureText(manaPlayer2) + 10, y2 - coinHeight + 3, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!gameOver) {
                    initTouchX = event.getX();
                    initTouchY = event.getY();

                    if(cardPlayed == 0) {
                        //if there is a collision between the place where the user has touched the screen and the player's deck of cards
                        //we want to display information
                        if (checkCollision(deckPlayer, initTouchX, initTouchY)) {
                            String text;
                            if (turnDrawCard == 4)
                                text = "There is 1 turn left until you draw a card.";
                            else
                                text = "There are " + (5 - turnDrawCard) + " turns left until you draw a card.";
                            if (deckPlayer.getDeck().size() == 1)
                                text = text + " There is 1 more card left in the deck.";
                            else
                                text = text + " There are " + deckPlayer.getDeck().size() + " more cards left in the deck.";

                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                        }

                        //if there is a collision between the place where the user has touched the screen and the opponent's deck of cards
                        //we want to display information
                        if (checkCollision(deckOpponent, initTouchX, initTouchY)) {
                            String text;
                            if (turnDrawCardOpponent == 4)
                                text = "There is 1 turn left until your opponent draws a card.";
                            else
                                text = "There are " + (5 - turnDrawCardOpponent) + " turns left until your opponent draws a card.";
                            if (deckOpponent.getDeck().size() == 1)
                                text = text + " There is 1 more card left in the deck.";
                            else
                                text = text + " There are " + deckOpponent.getDeck().size() + " more cards left in the deck.";
                            text = text + " " + lastCardPlayedOpponent;

                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                        }

                        if (touchDownHand == -1) {
                            //if the user has touched the screen we want to check if he touched any of his cards
                            touchDownHand = checkCollision(handPlayer, initTouchX, initTouchY);
                            if(touchDownHand != -1)
                                handPlayer.getHand().get(touchDownHand).grow();
                        }
                        if (!touchDownBoard && touchDownHand == -1) {
                            //if the user has touched the screen we want to check if he touched any of the pieces
                            touchDownBoard = checkCollision(board, initTouchX, initTouchY);
                        }
                    }
                    else {
                        if(cardPlayed == 1) {
                            if(changeKnightBishop(board, initTouchX, initTouchY))
                                cardPlayed = 0;
                            else
                                Toast.makeText(context, "You need to select one of your knights or bishops.", Toast.LENGTH_SHORT).show();
                        }
                        else if(cardPlayed == 3) {
                            if(changePawnKnight(board, initTouchX, initTouchY))
                                cardPlayed = 0;
                            else
                                Toast.makeText(context, "You need to select one of your pawns.", Toast.LENGTH_SHORT).show();
                        }
                        else if(cardPlayed == 4) {
                            if(changePawnBishop(board, initTouchX, initTouchY))
                                cardPlayed = 0;
                            else
                                Toast.makeText(context, "You need to select one of your pawns.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            //if the user stopped touching the screen we want to reset the touchDown variable
            case MotionEvent.ACTION_UP:
                if(!gameOver) {
                    if (touchDownHand != -1) {
                        //if the card hasn't been played
                        //we reset the position of the card we might have moved to the initial position
                        if (!cardPlayed())
                            handPlayer.getHand().get(touchDownHand).resetTempPos();

                        if(cardPlayed == 1) {
                            if(!board.checkKnightBishopAlive(isWhitePlayer)) {
                                cardPlayed = 0;
                                lastCardPlayedPlayer = true;
                                reference.child(connectionUniqueId).child("lastCardPlayed").setValue("");
                                reference.child(connectionUniqueId).child("lastCardPlayed").setValue("Transform knight into bishop or bishop into knight card couldn't be played because the opponent has no knights and bishops left.");
                                handPlayer.drawCard(deckPlayer);
                                Toast.makeText(context, "Card couldn't be played because you have no knights and bishops left.", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(context, "Select a knight or a bishop to change", Toast.LENGTH_SHORT).show();
                        }
                        else if(cardPlayed == 3) {
                            if(!board.checkPawnAlive(isWhitePlayer)) {
                                cardPlayed = 0;
                                lastCardPlayedPlayer = true;
                                reference.child(connectionUniqueId).child("lastCardPlayed").setValue("");
                                reference.child(connectionUniqueId).child("lastCardPlayed").setValue("Transform pawn into knight card couldn't be played because the opponent has no pawns left.");
                                handPlayer.drawCard(deckPlayer);
                                Toast.makeText(context, "Card couldn't be played because you have no pawns left.", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(context, "Select a pawn to change", Toast.LENGTH_SHORT).show();
                        }
                        else if(cardPlayed == 4) {
                            if(!board.checkPawnAlive(isWhitePlayer)) {
                                cardPlayed = 0;
                                lastCardPlayedPlayer = true;
                                reference.child(connectionUniqueId).child("lastCardPlayed").setValue("");
                                reference.child(connectionUniqueId).child("lastCardPlayed").setValue("Transform pawn into bishop card couldn't be played because the opponent has no pawns left.");
                                handPlayer.drawCard(deckPlayer);
                                Toast.makeText(context, "Card couldn't be played because you have no pawns left.", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(context, "Select a pawn to change", Toast.LENGTH_SHORT).show();
                        }

                        touchDownHand = -1;
                    } else if (touchDownBoard) {
                        //we reset the position of the piece we might have moved to the initial position
                        //or to the new position if it is valid
                        int state = board.resetSelectedPiece(event.getX(), event.getY(), turnWhite, connectionUniqueId, reference, moveNr);

                        if (state == -1) {
                            nextTurn(true);
                        }
                        touchDownBoard = false;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!gameOver) {
                    //if the user is moving his finger and he has touched a card in the beginning
                    //we want the card to move based on the movement of his finger
                    if (touchDownHand != -1) {
                        handPlayer.getHand().get(touchDownHand).moveTempPos(event.getX() - initTouchX, event.getY() - initTouchY);
                    }
                    //if the user is moving his finger and he has touched a piece in the beginning
                    //we want the piece to move based on the movement of his finger
                    else if(touchDownBoard) {
                        board.moveTempPos(event.getX() - initTouchX, event.getY() - initTouchY);
                    }
                    initTouchX = event.getX();
                    initTouchY = event.getY();
                }
                break;
        }
        return true;
    }

    //checks collision between all cards in the hand and a coordinate
    //if a collision is found it returns the index of the card that collides with the coordinate
    public int checkCollision(Hand hand, float x, float y) {
        for(int i = 0; i < hand.getHand().size(); i++) {
            if(checkCollision(hand.getHand().get(i), x, y)) {
                //tell the user what the ability of the card that was touched is and the amount of mana it costs
                //Toast.makeText(context, hand.getHand().get(i).getAbility() + " Mana: " + hand.getHand().get(i).getMana(), Toast.LENGTH_SHORT).show();
                return i;
            }
        }
        return -1;
    }

    //checks collision between a card and a coordinate
    public boolean checkCollision(Card card, float x, float y) {
        return (!(x < card.getX())) && (!(y < card.getY())) && (!((card.getX() + Card.w) < x)) && (!((card.getY() + Card.h) < y));
    }

    public boolean changeKnightBishop(Board brd, float x, float y) {
        if((x < Board.x) || (y < Board.y) || ((Board.x + Board.w) < x) || ((Board.y + Board.h) < y))
            return false;

        int y1 = (int) ((x - Board.x) / (Board.w / 8));
        int x1 = (int) ((y - Board.y) / (Board.h / 8));
        if(!isWhitePlayer) {
            y1 = 7 - y1;
            x1 = 7 - x1;
        }

        if(brd.changeKnightBishop(x1, y1, isWhitePlayer)) {
            DatabaseReference moveRef = reference.child(connectionUniqueId).getRef();
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("cardChanged/x", x1);
            updateMap.put("cardChanged/y", y1);
            updateMap.put("lastCardPlayed", "");
            updateMap.put("lastCardPlayed", 1);

            moveRef.updateChildren(updateMap);

            mana -= Card1.manaCard;
            moveNr--;
            nextTurn(false);
            return true;
        }

        return false;
    }

    public boolean changePawnKnight(Board brd, float x, float y) {
        if((x < Board.x) || (y < Board.y) || ((Board.x + Board.w) < x) || ((Board.y + Board.h) < y))
            return false;

        int y1 = (int) ((x - Board.x) / (Board.w / 8));
        int x1 = (int) ((y - Board.y) / (Board.h / 8));
        if(!isWhitePlayer) {
            y1 = 7 - y1;
            x1 = 7 - x1;
        }

        if(brd.changePawnKnight(x1, y1, isWhitePlayer)) {
            DatabaseReference moveRef = reference.child(connectionUniqueId).getRef();
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("cardChanged/x", x1);
            updateMap.put("cardChanged/y", y1);
            updateMap.put("lastCardPlayed", "");
            updateMap.put("lastCardPlayed", 3);

            moveRef.updateChildren(updateMap);
            mana -= Card3.manaCard;
            moveNr--;
            nextTurn(false);
            return true;
        }

        return false;
    }

    public boolean changePawnBishop(Board brd, float x, float y) {
        if((x < Board.x) || (y < Board.y) || ((Board.x + Board.w) < x) || ((Board.y + Board.h) < y))
            return false;

        int y1 = (int) ((x - Board.x) / (Board.w / 8));
        int x1 = (int) ((y - Board.y) / (Board.h / 8));
        if(!isWhitePlayer) {
            y1 = 7 - y1;
            x1 = 7 - x1;
        }

        if(brd.changePawnBishop(x1, y1, isWhitePlayer)) {
            DatabaseReference moveRef = reference.child(connectionUniqueId).getRef();
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("cardChanged/x", x1);
            updateMap.put("cardChanged/y", y1);
            updateMap.put("lastCardPlayed", "");
            updateMap.put("lastCardPlayed", 4);

            moveRef.updateChildren(updateMap);
            mana -= Card4.manaCard;
            moveNr--;
            nextTurn(false);
            return true;
        }

        return false;
    }

    //checks collision between the board and a coordinate
    public boolean checkCollision(Board brd, float x, float y) {
        if((x < Board.x) || (y < Board.y) || ((Board.x + Board.w) < x) || ((Board.y + Board.h) < y))
            return false;
        if(brd.getWhitePawnPromotion().getIsActive()) {
            if(!checkCollision(brd.getWhitePawnPromotion(), x, y)) {
                brd.getWhitePawnPromotion().setIsActive(false, 0, 0);
            }
            else {
                int i = (int) ((y - brd.getY()) / (brd.getW() / 8));
                brd.pawnPromotionWhite(i, connectionUniqueId, reference, moveNr);

                brd.getWhitePawnPromotion().setIsActive(false, 0, 0);
                nextTurn(true);
            }
            return false;
        }
        else if(brd.getBlackPawnPromotion().getIsActive()) {
            if(!checkCollision(brd.getBlackPawnPromotion(), x, y)) {
                brd.getBlackPawnPromotion().setIsActive(false, 0, 0);
            }
            else {
                int i = (int) ((y - brd.getY()) / (brd.getW() / 8));
                brd.pawnPromotionBlack(i, connectionUniqueId, reference, moveNr);

                brd.getBlackPawnPromotion().setIsActive(false, 0, 0);
                nextTurn(true);
            }
            return false;
        }
        else {
            int state = board.selectPiece(x, y, turnWhite, connectionUniqueId, reference, moveNr);

            // The highlighted piece has moved to a new position
            if (state == -1) {
                nextTurn(true);
                return false;
            }
            //there is no piece at the place
            return state != 0;
        }
    }

    public void nextTurn(boolean pieceMoved) {
        //if a piece was moved we want the player to hear the sound of the piece moving
        if(pieceMoved) {
            MediaPlayer music = MediaPlayer.create(context, R.raw.pawn_move);
            music.start();
        }

        turnWhite = !turnWhite;
        if(turnDrawCard == 4) {
            turnDrawCard = 0;
            handPlayer.drawCard(deckPlayer);
        }
        else {
            turnDrawCard++;
        }
        addManaPlayer();
        checkGameOver();
        String newMove = board.getMovesNotation();
        if(!checkDrawByRepetition(newMove))
            listMoves.add(newMove);
    }

    //check collision between the pawn promotion panel and the mouse click
    public boolean checkCollision(PawnPromotion pawnPromotion, float x, float y) {
        return (!(x < pawnPromotion.getX())) && (!(y < pawnPromotion.getY())) && (!((pawnPromotion.getX() + pawnPromotion.getW()) < x)) && (!((pawnPromotion.getY() + pawnPromotion.getH()) < y));
    }

    //check collision between the deck of cards and the mouse click
    public boolean checkCollision(Deck deck, float x, float y) {
        return (!(x < deck.getX())) && (!(y < deck.getY())) && (!((deck.getX() + deck.getW()) < x)) && (!((deck.getY() + deck.getH()) < y));
    }

    //the turn hasn't been changed in the main code yet so the actual turn is !turnWhite
    //we want to check if the player whose turn it is has any valid moves
    public void checkGameOver() {
        if(turnWhite) {
            if(board.getNrPossibleMovesWhite() == 0) {
                King whiteKing = (King) board.getBoard()[board.getWhiteKingI()][board.getWhiteKingJ()];
                if(whiteKing.isChecked(board.getBoard(), board.getWhiteKingI(), board.getWhiteKingJ())) {
                    if(isWhitePlayer) {
                        //The white player has lost
                        startGameOver("You lost!");
                        reference.child(connectionUniqueId).child("status").setValue("You won!");
                    }
                    else {
                        startGameOver("You won!");
                        reference.child(connectionUniqueId).child("status").setValue("You lost!");
                    }
                }
                else {
                    startGameOver("It's a draw!");
                    reference.child(connectionUniqueId).child("status").setValue("It's a draw!");
                }
                gameOver = true;
            }
        }
        else {
            if(board.getNrPossibleMovesBlack() == 0) {
                King blackKing = (King) board.getBoard()[board.getBlackKingI()][board.getBlackKingJ()];
                if(blackKing.isChecked(board.getBoard(), board.getBlackKingI(), board.getBlackKingJ())) {
                    //The black player has lost
                    if(isWhitePlayer) {
                        startGameOver("You won!");
                        reference.child(connectionUniqueId).child("status").setValue("You lost!");
                    }
                    else {
                        startGameOver("You lost!");
                        reference.child(connectionUniqueId).child("status").setValue("You won!");
                    }
                }
                else {
                    startGameOver("It's a draw!");
                    reference.child(connectionUniqueId).child("status").setValue("It's a draw!");
                }
                gameOver = true;
            }
        }

        //check for draws
        if(!gameOver) {
            if(drawByRepetition) {
                startGameOver("It's a draw by repetition!");
                reference.child(connectionUniqueId).child("status").setValue("It's a draw by repetition!");
                gameOver = true;
            } //check if only the kings remain
            else if(board.checkInsufficientMaterial()) {
                startGameOver("It's a draw by insufficient material!");
                reference.child(connectionUniqueId).child("status").setValue("It's a draw by insufficient material!");
                gameOver = true;
            }
        }
    }

    public void addManaPlayer() {
        if(mana < maxMana)
            mana++;
    }

    public void addManaOpponent() {
        if(manaOpponent < maxMana)
            manaOpponent++;
    }

    public boolean cardPlayed() {
        if(handPlayer.getHand().get(touchDownHand).cardPlayed()) {
            int mana1 = handPlayer.getHand().get(touchDownHand).getMana();
            if(mana >= mana1) {
                if(turnWhite == isWhitePlayer) {
                    if (handPlayer.getHand().get(touchDownHand) instanceof Card2) {
                        board.addNewMovesKnight(new int[]{-3, -1, 1, 3, 3, 1, -1, -3},
                                new int[]{-1, -3, -3, -1, 1, 3, 3, 1}, turnWhite, isWhitePlayer);
                    }
                    else if(handPlayer.getHand().get(touchDownHand) instanceof Card1) {
                        cardPlayed = 1;
                        lastCardPlayedPlayer = true;
                        handPlayer.removeCard(touchDownHand);
                        return true;
                    }
                    else if(handPlayer.getHand().get(touchDownHand) instanceof Card3) {
                        cardPlayed = 3;
                        lastCardPlayedPlayer = true;
                        handPlayer.removeCard(touchDownHand);
                        return true;
                    }
                    else if(handPlayer.getHand().get(touchDownHand) instanceof Card4) {
                        cardPlayed = 4;
                        lastCardPlayedPlayer = true;
                        handPlayer.removeCard(touchDownHand);
                        return true;
                    }
                    moveNr--;
                    mana -= mana1;
                    nextTurn(false);
                    reference.child(connectionUniqueId).child("lastCardPlayed").setValue("");
                    reference.child(connectionUniqueId).child("lastCardPlayed").setValue(handPlayer.getHand().get(touchDownHand).getCardIndex());
                    lastCardPlayedPlayer = true;
                    handPlayer.removeCard(touchDownHand);
                    return true;
                }
                else {
                    Toast.makeText(context, "It is not your turn.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(context, "You don't have enough mana.", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    public void startGameOver(String state) {
        Intent intent = new Intent(context, GameOver.class);
        intent.putExtra("State", state);
        context.startActivity(intent);

        if (hostActivity != null) {
            hostActivity.finish();
        }
        pause();
    }
}