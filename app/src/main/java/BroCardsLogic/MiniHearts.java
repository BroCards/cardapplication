package BroCardsLogic;

import android.app.Activity;

import java.util.List;

import BroCardsNetworks.Client;
import BroCardsNetworks.TableRunner;

public class MiniHearts extends TableRunner {
    
    private BroCards b;
    private int numPlayers;
    private int[] score;
    
    // init
    public MiniHearts(List<Client> players, Activity c) {
        super(players, c);
        numPlayers = clients.size();
        b = new BroCards(numPlayers);
        
        // only use as many suits as players
        for (int card = 13*(4-numPlayers); card < 52; card++)
            b.insertCard(card, 0);
        b.shuffleDeck();
        
        score = new int[numPlayers];
        for (int p = 0; p < numPlayers; p++)
            score[p] = 0;
    }
    
    // find holder of lowest card
    private int findLowest() {
        int lowestCard = 13*(4-numPlayers);
        for (int p = 0; p < numPlayers; p++) {
            for (int i = 0; i < 13; i++) {
                if (b.getHand()[p][i] == lowestCard)
                    return p;
            }
        }
        return 0;
    }
    
    // check if card matches suit
    private boolean matchSuit(int card, int suit) {
        if (suit == -1)
            return true;
        return (card / 13 == suit);
    }
    
    // draw a card from deck to hand of player
    private int drawCard(int player) {
        int drawnCard = b.getDeck()[b.getDeckLen()-1];
        b.removeCard(drawnCard, 0);
        b.insertCard(drawnCard, 10+player);
        return drawnCard;
    }
    
    // play a card: remove from hand of player, put to played area
    private void playCard(int card, int player) {
        b.removeCard(card, 10+player);
        b.insertCard(card, 20+player);
    }
    
    // discard played cards
    private void discardPlays() {
        for (int i = 0; i < numPlayers; i++) {
            int discardedCard = b.getPlayed()[i];
            b.removeCard(discardedCard, 20+i);
            b.insertCard(discardedCard, 1);
        }
    }
    
    @Override
    public void run() {
        int i, j, p;

        // 13 cards per player
        for (i = 0; i < 13; i++)
            for (p = 0; p < numPlayers; p++)
                drawCard(p);
        
        int startPlayer = findLowest();
        int currPlayer = 0;
        int leadSuit = -1;
        int allowedSuit = -1;
        boolean foundPlayableCard = true;
        int selectedCard = -1;
        int scoreOfTrick = 0;
        boolean retry = false;
        
        // main loop; the game runs 13 tricks
        for (int trick = 0; trick < 13; trick++) {
            
            leadSuit = -1;
            
            // ask each player to play a card
            for (i = 0; i < numPlayers; i++) {
                currPlayer = (startPlayer + i) % numPlayers;
                
                if ((trick == 0) && (i == 0)) {
                    
                    // special case for first card played: must play lowest card
                    retry = false;
                    do {
                        selectedCard = b.requestMove(currPlayer, retry);
                        retry = true;
                    } while (selectedCard != 13*(4-numPlayers));
                    
                } else {
                    
                    // find card of playable suit; if none, play anything
                    allowedSuit = leadSuit;
                    if (leadSuit != -1) {
                        foundPlayableCard = false;
                        for (j = 0; j < b.getHandLen()[currPlayer]; j++) {
                            if (matchSuit(b.getHand()[currPlayer][j], leadSuit)) {
                                foundPlayableCard = true;
                                break;
                            }
                        }
                        
                        if (!foundPlayableCard)
                            allowedSuit = -1;
                    }
                    
                    // request a card of allowed suit
                    retry = false;
                    do {
                        selectedCard = b.requestMove(currPlayer, retry);
                        retry = true;
                    } while (!matchSuit(selectedCard, allowedSuit));
                }
                
                // play card; also, if first player, this sets the lead suit
                playCard(selectedCard, currPlayer);
                if (i == 0)
                    leadSuit = selectedCard / 13;
            }
            
            // find score of trick (negative because they are penalty points; in Hearts you want as few penalty points as possible)
            scoreOfTrick = 0;
            for (p = 0; p < numPlayers; p++) {
                if (matchSuit(b.getPlayed()[p], 2)) // hearts
                    scoreOfTrick -= 1;
                if (b.getPlayed()[p] == 49) // queen of spades
                    scoreOfTrick -= 13;
            }
            
            // find winner of trick; they get the score and are starting player of next trick
            int winner = -1;
            int winningCard = -1;
            for (i = 0; i < numPlayers; i++) {
                if (matchSuit(b.getPlayed()[i], leadSuit) && (winningCard < b.getPlayed()[i])) {
                    winningCard = b.getPlayed()[i];
                    winner = i;
                }
            }
            score[winner] += scoreOfTrick;
            b.updateScore(winner, scoreOfTrick);
            startPlayer = winner;
            
            // discard plays
            discardPlays();
        }
        
        // end game
        b.endGame();
        
    }
    
}