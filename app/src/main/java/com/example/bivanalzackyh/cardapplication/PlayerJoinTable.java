package com.example.bivanalzackyh.cardapplication;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import BroCardsNetworks.Client;

/**
 * PlayerJoinTable
 * Player Lounge to access
 */
public class PlayerJoinTable extends AppCompatActivity {

    Button JoinButton;
    EditText ipField;
    EditText portField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_join_table);

        // register button
        JoinButton = (Button) findViewById(R.id.game_start_btn);
        ipField = (EditText) findViewById(R.id.ip_number);
        portField = (EditText) findViewById(R.id.port_number);

        // set function
        JoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: get destination IP address and port
                final View viewParam = view;
                final String targetIP = ipField.getText().toString();
                int x;
                try {
                    x = Integer.parseInt(portField.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return;
                }
                final int port = x;

                // Check-in
                // get wifi information
                final WifiManager wifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                final WifiInfo wifiInf = wifiObj.getConnectionInfo();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Client client = new Client(targetIP, port);

                        try {
                            JSONObject send = new JSONObject();
                            send.put("IP", wifiInf.getIpAddress());

                            // sendData
                            client.sendData(send.toString(), true);

                            // wait for data to be ready
                            JSONObject reply = client.getResponseJson();

                            if (reply != null) {
                                startPlaying(viewParam);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    void startPlaying(View v){
        Intent i = new Intent(this, PlayerHand.class);
        startActivity(i);
    }
}
