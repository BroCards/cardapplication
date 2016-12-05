package BroCardsLogic;

import java.lang.Math;
import java.util.List;

import BroCardsNetworks.Client;
import BroCardsNetworks.TableRunner;

/*
call GameFileCangkul with number of players as sole argument
then call the method gameCangkul() to actually start the game
e.g.
gamefile = new GameFileCangkul(2); // play with 2 players
gamefile.gameCangkul();
*/

public class Cangkul extends TableRunner {
    
    private BroCards b;
    private int numPlayers;
    
    // init
    public Cangkul(List<Client> players) {
        super(players);
        numPlayers = clients.size();
        b = new BroCards(numPlayers);
        for (int card = 0; card < 52; card++)
            b.insertCard(card, 0);
        b.shuffleDeck();
    }
    
    // check if card matches suit
    private boolean matchSuit(int card, int suit) {
        if (suit == -1)
            return true;
        return (card / 13 == suit);
    }
    
    // draw a card from deck to hand of player
    private int drawCard(int player) {
        int drawnCard = b.getDeck()[deckLen-1];
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
    
    // add played cards to player's hand
    private void penalizePlayer(int player) {
        for (int i = 0; i < numPlayers; i++) {
            int discardedCard = b.getPlayed()[i];
            if (discardedCard == -1) continue;
            b.removeCard(discardedCard, 20+i);
            b.insertCard(discardedCard, 10+player);
        }
    }
    
    // TODO: fix below this to use b object

    @Override
    public void run() {
        
        // first draw, 7 cards per player        
        for (int i = 0; i < 7; i++)
            for (int j = 0; j < numPlayers; j++)
                drawCard(j);
        
        int startPlayer = 0;
        int currPlayer = 0;
        int leadSuit = -1;
        boolean foundPlayableCard = true;
        int selectedCard = -1;
        
        // main loop
        mainLoop:
        while (true) {
            
            leadSuit = -1;
            
            // ask each player to play a card
            for (i = 0; i < numPlayers; i++) {
                currPlayer = (startPlayer + i) % numPlayers;
                
                // find card of playable suit
                foundPlayableCard = false;
                for (int j = 0; j < handLen[currPlayer]; j++) {
                    if (matchSuit(hand[currPlayer][j], leadSuit)) {
                        foundPlayableCard = true;
                        break;
                    }
                }
                
                // if no playable card, draw until there's one
                if (!foundPlayableCard) {
                    while (deckLen > 0) {
                        int drawnCard = drawCard(currPlayer);
                        if (matchSuit(drawnCard, leadSuit)) {
                            foundPlayableCard = true;
                            break;
                        }
                    }
                }
                
                // if deck runs out with no playable card, trick ends
                if ((deckLen == 0) && (!foundPlayableCard)) break;
                
                // if there's playable card, ask to play a card (trick not ended yet)
                if (foundPlayableCard) {
                    do {
                        selectedCard = requestMove(currPlayer);
                    } while (!matchSuit(selectedCard, leadSuit));
                    playCard(selectedCard, currPlayer);
                }
                
                // if first player, this sets the lead suit
                if (i == 0)
                    leadSuit = selectedCard / 13;
                
                // if run out of cards, end game
                if (handLen[currPlayer] == 0)
                    break mainLoop;
            }
            
            // find winner of trick; they are starting player of next trick
            int winner = -1;
            int winningCard = -1;
            for (i = 0; i < numPlayers; i++) {
                if (winningCard < played[i]) {
                    winningCard = played[i];
                    winner = i;
                }
            }
            startingPlayer = winner;
            
            // if current player has no played card, that means the trick ends prematurely; penalize current player
            // otherwise, all can play, discard the cards
            if (played[currPlayer] == -1)
                penalizePlayer(currPlayer);
            else
                discardPlays();
        }
        
        // end game; current player must be the one that runs out of cards, they win
        endGame(currPlayer);
        
    }
    
}