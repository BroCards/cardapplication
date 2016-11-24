package com.example.bivanalzackyh.cardapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ConnectToPlayer extends AppCompatActivity {

    private TextView instruction_tv;
    private EditText ip_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_player);

        instruction_tv = (TextView) findViewById(R.id.instruction_tv);
    }

    public void game_start_player(View v){
        Intent i = new Intent(this, GameStartingPlayer.class);
        startActivity(i);
    }
}
