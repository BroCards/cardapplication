package com.example.bivanalzackyh.cardapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void create_table(View v){
        Intent i = new Intent(this, PlayerChoosingGame.class);
        startActivity(i);
    }

    public void join_table(View v){
        Intent i = new Intent(this, PlayerJoinTable.class);
        startActivity(i);
    }

    public void game_manager(View v){
        Intent i = new Intent(this, PlayerManageGame.class);
        startActivity(i);
    }
}
