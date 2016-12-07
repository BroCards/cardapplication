package com.example.bivanalzackyh.cardapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import BroCardsLogic.SimpleGame;
import BroCardsNetworks.TableRunner;

public class Table2Player extends Table {

    View viewDeck;
    View viewDiscard;
    View viewPlay1;
    View viewPlay2;

    /*
    viewDeck : the change that may occur is just that when the pile has all
    its cards distributed, it will just disappear (maybe just set visibility to gone)

    viewDiscard : for game Cangkul, any card drawn by any player will immediately
    go to this discard pile. as for game Mini-Hearts, the card drawn will be put
    to this pile only after the turn has finished

    viewPlay(x) : only will be used for game Mini-Hearts. Every time the player
    draws the card, it will first go here. After all 2 players play their card,
    the score will be calculated and then all those 2 cards will be going to
    the discard pile
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table2_player);

        viewDeck = (View) findViewById(R.id.viewDeck);
        viewDiscard = (View) findViewById(R.id.viewDiscard);
        viewPlay1 = (View) findViewById(R.id.viewPlay1);
        viewPlay2 = (View) findViewById(R.id.viewPlay2);

        Thread t = new Thread((TableRunner) new SimpleGame(clients, this));
        t.start();
    }
}
