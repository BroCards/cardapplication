package com.example.bivanalzackyh.cardapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import BroCardsNetworks.Client;

/**
 * Table Class
 * Super Class on Table Activities
 *
 * Created by Chitphentom on 12/4/2016 AD.
 */

class Table extends AppCompatActivity {
    protected List<Client> clients = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the clients
        String jsonArrayStr = savedInstanceState.getString("Participants");

        try {
            // TODO: get game class

            //

            JSONArray participants = new JSONArray(jsonArrayStr);

            for (int i = 0; i < participants.length(); i++) {
                JSONObject jsonObject = participants.getJSONObject(i);

                // get IP address
                int IP = jsonObject.getInt("IP");

                clients.add(new Client(String.format(Locale.ENGLISH, "%d.%d.%d.%d", (IP & 0xff),
                        (IP >> 8 & 0xff),(IP >> 16 & 0xff),(IP >> 24 & 0xff)), 8888));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TODO: use super.onCreate on subclass of this class to do this work and carry on from here

        /*
        the game interaction has to be done on the new thread for simplicity
        that is it has to be run in the manner which separated from the main thread

        use either:

            class Blackjacks extends TableRunner {
                ...

                public BlackJacks(int numParticipants) {
                    ... [initialize] ...
                }

                ...

                @Override
                public void run() {
                   ..[start you game routine]..

                   ...

                   // when to tell player

                }
            }

        and in the game activity onCreate() method

            Thread gameThread = new Thread(new Blackjacks(totalParticipants));
         */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
