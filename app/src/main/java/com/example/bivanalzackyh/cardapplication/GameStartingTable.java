package com.example.bivanalzackyh.cardapplication;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

public class GameStartingTable extends AppCompatActivity {

    private int IP;
    private String SSID;

    // layout instances
    TextView gameInfo;
    TextView IPField;
    TextView SSIDField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_starting_table);

        // layout instances
        IPField = (TextView) findViewById(R.id.Table_ip);
        SSIDField = (TextView) findViewById(R.id.Table_SSID);

        // get wifi information
        WifiManager wifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiObj.getConnectionInfo();

        IP = wifiInf.getIpAddress();
        if (IP != 0) {
            IPField.setText(String.format(Locale.ENGLISH, "%d.%d.%d.%d", (IP & 0xff),
                    (IP >> 8 & 0xff),(IP >> 16 & 0xff),(IP >> 24 & 0xff)));
            SSID = wifiInf.getSSID();
            SSIDField.setText(SSID);
        }

        // starting the server


    }

    public void start_game(View v){
        //this function is intended to be branch
        //that it will be dependent of the number of players in the board
        Intent i = new Intent(this, Table2Player.class);
        Intent j = new Intent(this, Table3Player.class);
        Intent k = new Intent(this, Table4Player.class);
        startActivity(i);
    }


}
