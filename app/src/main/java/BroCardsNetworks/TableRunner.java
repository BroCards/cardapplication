package BroCardsNetworks;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bivanalzackyh.cardapplication.R;
import com.example.bivanalzackyh.cardapplication.Table2Player;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * TableRunner
 * Use to run the game and communicate between player and central table
 * Implementation of Runnable class which use run() method
 * Created by Chitphentom on 12/4/2016 AD.
 */

public abstract class TableRunner implements Runnable {

    protected List<Client> clients;

    public TableRunner(List<Client> participants) {
        // DANGER: the object is not actually copied
        clients = participants;
    }

    private JSONObject communicate(int playerNum, String msg, boolean waitForReply) {
        // make request
        Client c = clients.get(playerNum);

        c.sendData(msg, waitForReply);

        JSONObject toRet = null;

        if (waitForReply) {
            toRet = c.getResponseJson();
        }
        return toRet;
    }


    protected void tellPlayer(final int playerNum, final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communicate(playerNum, msg, false);
            }
        }).start();
    }

    protected JSONObject tellPlayer(int playerNum, JSONObject msg) throws JSONException {
        // check if need reply
        boolean needReply = msg.getBoolean("needReply");

        if (needReply) {
            tellPlayer(playerNum, msg.toString());
            return null;
        }

        return communicate(playerNum, msg.toString(), true);
    }

    ///////////////////////////////////////////////////////
    // BroCard Class                                     //
    ///////////////////////////////////////////////////////

    /*
    card number: rank + 13 * suit
    rank from 0 to 12: 2/3/4/5/6/7/8/9/10/J/Q/K/A
    suit from 0 to 3: C/D/H/S
    e.g. 2C = 0, 3C = 1, 4C = 2, ..., AC = 12, 2D = 13, 3D = 14, ..., AD = 25, ..., AS = 51

    area number:
    0: deck (face down)
    1: discards (face up)
    10/11/12/13: hand of player 0/1/2/3 (face up on each player)
    20/21/22/23: play area of player 0/1/2/3 (face up on table)

    only works for games where:
    - there is one pack of cards
    - only using areas described above
    - each move asks one player, asks for one card from hand
    */

    protected class BroCards {

        private int numPlayers;
        private int[] deck;
        private int deckLen;
        private int[] discard;
        private int discardLen;
        private int[][] hand;
        private int[] handLen;
        private int[] played;
        private int[] score;

        public int[] getDeck() {return deck;}
        public int getDeckLen() {return deckLen;}
        public int[] getDiscard() {return discard;}
        public int getDiscardLen() {return discardLen;}
        public int[][] getHand() {return hand;}
        public int[] getHandLen() {return handLen;}
        public int[] getPlayed() {return played;}
        public int[] getScore() {return score;}

        private int findInArray(int elem, int[] array, int arrayLen) {
            for (int i = 0; i < arrayLen; i++) {
                if (array[i] == elem)
                    return i;
            }
            return -1;
        }

        private int[] removeFromArray(int elem, int[] array, int arrayLen) {
            for (int i = 0; i < arrayLen; i++) {
                if (array[i] == elem) {
                    array[i] = -1;
                    break;
                }
            }
            for (int i = 0; i < arrayLen-1; i++) {
                if (array[i] == -1) {
                    array[i] = array[i+1];
                    array[i+1] = -1;
                }
            }
            return array;
        }

        public BroCards(int numOfPlayers) {
            numPlayers = numOfPlayers;
            deck = new int[52];
            deckLen = 0;
            discard = new int[52];
            discardLen = 0;
            hand = new int[numPlayers][52];
            handLen = new int[numPlayers];
            played = new int[numPlayers];

            for (int i = 0; i < 52; i++) {
                deck[i] = -1;
                discard[i] = -1;
            }
            for (int p = 0; p < numPlayers; p++) {
                for (int i = 0; i < 52; i++) {
                    hand[p][i] = -1;
                }
                handLen[p] = 0;
                played[p] = -1;
                score[p] = 0;
            }
        }

        private int getAreaViewId(int area) {
            switch (area) {
                case 0: return R.id.viewDeck;
                case 1: return R.id.viewDiscard;
                case 20: return R.id.viewPlay1;
                case 21: return R.id.viewPlay2;
                case 22: return R.id.viewPlay3;
                case 23: return R.id.viewPlay4;
            }
            return 0;
        }

        private int getCardImageId(int card) {
            switch (card) {
                case 0: return R.drawable.clubs_2;
                case 1: return R.drawable.clubs_3;
                case 2: return R.drawable.clubs_4;
                case 3: return R.drawable.clubs_5;
                case 4: return R.drawable.clubs_6;
                case 5: return R.drawable.clubs_7;
                case 6: return R.drawable.clubs_8;
                case 7: return R.drawable.clubs_9;
                case 8: return R.drawable.clubs_10;
                case 9: return R.drawable.clubs_j;
                case 10: return R.drawable.clubs_q;
                case 11: return R.drawable.clubs_k;
                case 12: return R.drawable.clubs_a;
                case 13: return R.drawable.diamonds_2;
                case 14: return R.drawable.diamonds_3;
                case 15: return R.drawable.diamonds_4;
                case 16: return R.drawable.diamonds_5;
                case 17: return R.drawable.diamonds_6;
                case 18: return R.drawable.diamonds_7;
                case 19: return R.drawable.diamonds_8;
                case 20: return R.drawable.diamonds_9;
                case 21: return R.drawable.diamonds_10;
                case 22: return R.drawable.diamonds_j;
                case 23: return R.drawable.diamonds_q;
                case 24: return R.drawable.diamonds_k;
                case 25: return R.drawable.diamonds_a;
                case 26: return R.drawable.hearts_2;
                case 27: return R.drawable.hearts_3;
                case 28: return R.drawable.hearts_4;
                case 29: return R.drawable.hearts_5;
                case 30: return R.drawable.hearts_6;
                case 31: return R.drawable.hearts_7;
                case 32: return R.drawable.hearts_8;
                case 33: return R.drawable.hearts_9;
                case 34: return R.drawable.hearts_10;
                case 35: return R.drawable.hearts_j;
                case 36: return R.drawable.hearts_q;
                case 37: return R.drawable.hearts_k;
                case 38: return R.drawable.hearts_a;
                case 39: return R.drawable.spades_2;
                case 40: return R.drawable.spades_3;
                case 41: return R.drawable.spades_4;
                case 42: return R.drawable.spades_5;
                case 43: return R.drawable.spades_6;
                case 44: return R.drawable.spades_7;
                case 45: return R.drawable.spades_8;
                case 46: return R.drawable.spades_9;
                case 47: return R.drawable.spades_10;
                case 48: return R.drawable.spades_j;
                case 49: return R.drawable.spades_q;
                case 50: return R.drawable.spades_k;
                case 51: return R.drawable.spades_a;
                case 100: return R.drawable.cardback;
            }
            return 0;
        }

        // not sure
        private void setImage(int area, int card) {
            int areaId = getAreaViewId(area);
            int cardId = getCardImageId(card);
            switch (numPlayers) {
                case 2:
                    setImage2P(areaId, cardId);
                    // i want to call setImage2P in cardapplication/Table2Player.java, how
            }
        }

        public void insertCard(int card, int area) {
            // add card to area
            // 1. find the area
            // 2. append the card to area

            int p;
            switch (area) {
                case 0:
                    // case for deck
                    deck[deckLen] = card;
                    deckLen++;
                    setImage(0, 100);
                    break;
                case 1:
                    // case for discard
                    discard[discardLen] = card;
                    discardLen++;
                    setImage(1, card);
                    break;
                case 10: case 11: case 12: case 13:
                    // case for hand
                    p = area - 10;
                    hand[p][handLen[p]] = card;
                    handLen[p]++;
                    try {
                        JSONObject msg = new JSONObject();
                        msg.put("requestResponse", false);
                        msg.put("type", "insert");
                        msg.put("card", card);
                        tellPlayer(p, msg);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 20: case 21: case 22: case 23:
                    // case for play area
                    p = area - 20;
                    played[p] = card;
                    setImage(area, card);
                    break;
            }
        }

        public void removeCard(int card, int area) {
            // remove card from area
            // 1. find the area
            // 2. find the card from area
            // 3. remove it
            // 4. consolidate the rest into a contiguous array

            int p;
            switch (area) {
                case 0:
                    // case for deck
                    deck = removeFromArray(card, deck, deckLen);
                    deckLen--;
                    if (deckLen == 0) {
                        setImage(0, -1);
                    } else {
                        setImage(0, 100);
                    }
                    break;
                case 1:
                    // case for discard
                    discard = removeFromArray(card, discard, discardLen);
                    discardLen--;
                    if (discardLen == 0) {
                        setImage(1, -1);
                    } else {
                        int lastCard = discard[discardLen-1];
                        setImage(1, lastCard);
                    }
                    break;
                case 10: case 11: case 12: case 13:
                    // case for hand
                    p = area - 10;
                    hand[p] = removeFromArray(card, hand[p], handLen[p]);
                    handLen[p]--;
                    try {
                        JSONObject msg = new JSONObject();
                        msg.put("requestResponse", false);
                        msg.put("type", "remove");
                        msg.put("card", card);
                        tellPlayer(p, msg);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 20: case 21: case 22: case 23:
                    // case for play
                    p = area - 20;
                    played[p] = -1;
                    setImage(area, -1);
                    break;
            }
        }

        public int requestMove(int player) {
            // request move from player
            // 1. find the player
            // 2. prompt for a move
            // 3. record the selection
            // 4. return to table

            try {
                JSONObject msg = new JSONObject();
                msg.put("requestResponse", true);
                msg.put("type", "request");
                JSONObject resp = tellPlayer(player, msg);
                return resp.getInt("card");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public void updateScore(int player, int score) {
            // update score of player
            // 1. find the player
            // 2. find the score display
            // 3. update to the score

            try {
                JSONObject msg = new JSONObject();
                msg.put("requestResponse", false);
                msg.put("type", "score");
                msg.put("score", score);
                tellPlayer(player, msg);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public void endGame() {
            // end game; must be called before quitting the game
            // 1. compute winner(s) (based on score)
            // 2. broadcast whether they win or not to everyone

            int maxscore = score[0];
            boolean[] isWinner = new boolean[numPlayers];
            for (int p = 0; p < numPlayers; p++) isWinner[p] = false;
            isWinner[0] = true;

            for (int i = 1; i < numPlayers; i++) {
                if (score[i] > maxscore) {
                    maxscore = score[i];
                    for (int p = 0; p < numPlayers; p++) isWinner[p] = false;
                    isWinner[i] = true;
                } else if (score[i] == maxscore) {
                    isWinner[i] = true;
                }
            }

            for (int p = 0; p < numPlayers; p++) {
                try {
                    JSONObject msg = new JSONObject();
                    msg.put("requestResponse", false);
                    msg.put("type", "end");
                    msg.put("win", isWinner[p]);
                    tellPlayer(p, msg);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void shuffleDeck() {
            // shuffle the deck
            // Fisher-Yates algorithm

            for (int i = deckLen - 1; i > 0; i--) {
                int j = (int) (Math.random() * (i+1));
                int temp = deck[j];
                deck[j] = deck[i];
                deck[i] = temp;
            }
        }

    }
}
