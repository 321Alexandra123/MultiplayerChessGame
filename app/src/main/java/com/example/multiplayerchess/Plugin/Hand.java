package com.example.multiplayerchess.Plugin;

import android.content.res.Resources;
import android.graphics.Canvas;

import com.example.multiplayerchess.Plugin.Card;
import com.example.multiplayerchess.Plugin.Deck;

import java.util.ArrayList;

public class Hand {
    private final ArrayList<Card> hand = new ArrayList<Card>();
    private static final int nrCardsMax = 3;
    private final int y;

    public ArrayList<Card> getHand() {
        return hand;
    }

    public Hand(Deck deck, boolean opponent) {
        //if this is the hand of the opponent we want it to print it on the upper part of the screen
        if(opponent == false) {
            y = Resources.getSystem().getDisplayMetrics().heightPixels - Card.h - 10;
        }
        //if this is the hand of the player we want it to be on the down side of the screen
        else {
            y = 10;
        }

        //we initiate the hand by drawing a card from the deck and assigning a position to that card
        drawCard(deck);
    }

    public void drawCard(Deck deck) {
        //check if the player doesn't already have the maximum number of cards in his hand
        if(hand.size() < nrCardsMax) {
            if(deck.getDeck().size() > 0) {
                //we want to draw a card to the hand from the deck
                hand.add(deck.getDeck().get(0));
                //after adding the card to the hand we want to remove it from the deck
                deck.popDeck();
            }
        }
        positionCards();
    }

    public void removeCard(int cardI) {
        hand.remove(cardI);
        if(hand.size() > 0)
            positionCards();
    }

    public void positionCards() {
        //we want to assign a position to each card from the deck
        switch(hand.size()) {
            case 1:
                hand.get(0).position((Resources.getSystem().getDisplayMetrics().widthPixels - Card.w) / 2, y);
                break;
            case 2:
                hand.get(0).position(Resources.getSystem().getDisplayMetrics().widthPixels / 2 - Card.w - 25, y);
                hand.get(1).position(Resources.getSystem().getDisplayMetrics().widthPixels / 2 + 25, y);
                break;
            case 3:
                hand.get(0).position((Resources.getSystem().getDisplayMetrics().widthPixels - Card.w) / 2 - Card.w - 25, y);
                hand.get(1).position((Resources.getSystem().getDisplayMetrics().widthPixels - Card.w) / 2, y);
                hand.get(2).position((Resources.getSystem().getDisplayMetrics().widthPixels + Card.w) / 2 + 25, y);
                break;
        }
    }

    public void draw(Canvas canvas) {
        //we draw each card from the hand
        for(int i = 0; i < hand.size(); i++) {
            hand.get(i).draw(canvas);
        }
    }
}
