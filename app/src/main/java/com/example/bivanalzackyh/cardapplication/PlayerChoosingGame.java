package com.example.bivanalzackyh.cardapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PlayerChoosingGame extends AppCompatActivity {
    private ListView listview;
    private TextView description_tv;
    private String selected_game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_choosing_game);

        listview = (ListView) findViewById(R.id.listview);
        description_tv = (TextView) findViewById(R.id.description_tv);

        final String[] games = {"Cangkul", "Mini-Hearts"};

        // game_class contains the class name of those games
        final String[] games_class = {"BroCardsLogic.CangKul", "BroCardsLogic.Mini_Hearts"};

        final String[] descriptions = {
            "Author: Ivan\n\n" +
            "Description:\n" +
            "Players play a card onto a trick, with the highest card leading the next trick. " +
            "However, if a player cannot follow suit, they must draw from the stock until they find a matching card, " +
            "or otherwise take all played cards into their hand. First player to empty their hand wins.",
            
            "Author: Ivan\n\n" +
            "Description:\n" +
            "Players play a card onto a trick. Highest card of the leading suit takes the cards played. " +
            "Hearts are worth 1 penalty point each, and Queen of Spades is worth 13. " +
            "Player that takes the least amount of penalty points wins."
        };
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, games);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                description_tv.setText(descriptions[i]);
                selected_game = games_class[i];
            }
        });
    }

    public void connect_player(View v){
        Intent i = new Intent(this, GameStartingTable.class);
        i.putExtra("GAME", selected_game);
        startActivity(i);
    }
}
