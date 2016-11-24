package com.example.bivanalzackyh.cardapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PlayerJoinTable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_join_table);
    }

    public void game_start_player(View v){
        Intent i = new Intent(this, GameStartingPlayer.class);
        startActivity(i);
    }
}
