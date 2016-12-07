package BroCardsLogic;

import android.app.Activity;
import android.util.Log;

import java.util.List;

import BroCardsNetworks.Client;
import BroCardsNetworks.TableRunner;

public class SimpleGame extends TableRunner {
    
    private BroCards b;
    private int numPlayers;
    
    // init
    public SimpleGame(List<Client> players, Activity c) {
        super(players, c);

        Log.d("SimpleGame", String.valueOf(c));

        numPlayers = clients.size();
        b = new BroCards(numPlayers);
        
        // only lowest clubs, equal to number of players
        for (int card = 0; card < numPlayers; card++)
            b.insertCard(card, 0);
        b.shuffleDeck();
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
    
    // discard played card
    private void discardPlay(int player) {
        int playedCard = b.getPlayed()[player];
        b.removeCard(playedCard, 20+player);
        b.insertCard(playedCard, 1);
    }
    
    @Override
    public void run() {
        
        // give each player a card
        for (int p = 0; p < numPlayers; p++)
            drawCard(p);
        
        // ask player a card; score is equal to rank of card
        for (int p = 0; p < numPlayers; p++) {
            int selectedCard = b.requestMove(p, false);
            playCard(selectedCard, p);
            b.updateScore(p, selectedCard+2);
        }
        
        // discard all plays
        for (int p = 0; p < numPlayers; p++)
            discardPlay(p);
        
        // end the game
        b.endGame();
    }
    
}