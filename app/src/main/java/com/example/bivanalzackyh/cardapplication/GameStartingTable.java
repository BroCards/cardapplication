package com.example.bivanalzackyh.cardapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import BroCardsNetworks.ReplyRoutine;
import BroCardsNetworks.Server;

public class GameStartingTable extends AppCompatActivity {

    private int IP;
    private String SSID;

    // layout instances
    TextView gameInfo;
    TextView IPField;
    TextView SSIDField;
    TextView numParticipant;
    Button StartBtn;

    Server server;

    Thread serverThread;

    private JSONArray participants = new JSONArray();

    String gameClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_starting_table);

        // getting game name
        gameClass = getIntent().getExtras().getString("GAME");
        Log.d("Game name", gameClass);

        // layout instances
        IPField = (TextView) findViewById(R.id.Table_ip);
        SSIDField = (TextView) findViewById(R.id.Table_SSID);
        StartBtn = (Button) findViewById(R.id.table_start_btn);
        numParticipant = (TextView) findViewById(R.id.num_participant);
        gameInfo = (TextView) findViewById(R.id.game_name);

        // grey out start button
        StartBtn.setBackgroundColor(0xbdc3c7);

        // view the game name
        gameInfo.setText(gameClass);

        // get wifi information
        WifiManager wifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiObj.getConnectionInfo();

        IP = wifiInf.getIpAddress();
        if (IP != 0) {
            IPField.setText(String.format(Locale.ENGLISH, "%d.%d.%d.%d", (IP & 0xff),
                    (IP >> 8 & 0xff),(IP >> 16 & 0xff),(IP >> 24 & 0xff)));
            SSID = wifiInf.getSSID();
            SSIDField.setText(SSID);
        } else {
            Toast.makeText(this, "No internet connection - back and try again", Toast.LENGTH_LONG).show();
            return;
        }

        // listen for participant
        // new thread
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // open the port
                int i = 8887;
                do {
                    i++;
                    server = new Server(i);
                } while (!server.isRunning());

                // set text
                IPField.setText(String.format(Locale.ENGLISH, "%s\n%d", IPField.getText().toString(), i));

                while (participants.length() < 4) {
                    JSONObject playerInfo = server.listen(new TableLobbyReply());
                    participants.put(playerInfo);

                    // if server is not running anymore, break
                    if (!server.isRunning()) break;
                    // set text on number of participant
                    final int x = participants.length();
                    runOnUiThread(new Runnable() {
                        public void run(){
                            numParticipant.setText(String.valueOf(x));
                        }
                    });
                    Log.d("Server", String.format(Locale.ENGLISH, "Total connect %d", participants.length()));
                }

                if (!server.isRunning()) {
                    return;
                }

                activateBtn();
            }
        });
        serverThread.start();

        StartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (participants.length() < 2) {
                    return;
                }
                // start new activity here
                moveOn(view, participants);
            }
        });
    }

    // activateBtn: ->
    // change color of button
    void activateBtn() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StartBtn.setBackgroundColor(0xe67e22);
            }
        });
    }

    void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        serverThread.interrupt();

        if (server != null) {
            server.exit();
        }
    }

    void moveOn(View v, JSONArray participants) {
        Class x;
        switch (participants.length()) {
            case 2:
                x = Table2Player.class;
                break;
            case 3:
                x = Table3Player.class;
                break;
            case 4:
                x = Table4Player.class;
                break;
            default:
                Log.e("Server", "Move on with undesirable players");
                x = this.getClass();
        }

        Intent i = new Intent(this, x);
        i.putExtra("Participants", participants.toString());
        i.putExtra("GAME", gameClass);
        startActivity(i);
    }


    /**
     *  the participants JSON object is {"IP": [IP address in integer form]}
     */
    class TableLobbyReply implements ReplyRoutine {
        @Override
        public JSONObject processInquiry(JSONObject json) {
            // add to participants list
            try {
                // reply
                JSONObject reply = new JSONObject();

                Log.d("ReplyRoutine", json.toString());

                // check json
                if (json.length() != 1 && json.has("IP")) {
                    reply.put("Success", false);
                    return reply;
                } else {
                    reply.put("Success", true);
                }

                // put player name
                String playerName = String.format(Locale.ENGLISH, "Player %d", participants.length() + 1);
                reply.put("Name", playerName);

                return reply;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

