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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_choosing_game);

        listview = (ListView) findViewById(R.id.listview);
        description_tv = (TextView) findViewById(R.id.description_tv);

        String[] games = {"Big Two", "Cangkul"};
        final String[] descriptions = {"Author : Bivan\n\nDescription:\nCards may be played as singles or in groups of two, " +
                "three or five (var. 1 and 8), in combinations which resemble poker hands. " +
                "The leading card to a trick sets down the number of cards to be played; " +
                "all the cards of a trick must contain the same number of cards. " +
                "The highest ranking card is 2 instead of A.",

                "Author : Ivan\n\nDescription:\nPlayer takes turn in either drawing a card which has the same sign " +
                        "as the previously drawn card, or take card from the deck until he/she found the card with the " +
                        "same sign. If no card of same sign until the deck is finished, take all the cards previously " +
                        "drawn, then skip."};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, games);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                description_tv.setText(descriptions[i]);
            }
        });
    }

    public void connect_player(View v){
        Intent i = new Intent(this, ConnectToPlayer.class);
        startActivity(i);
    }
}
