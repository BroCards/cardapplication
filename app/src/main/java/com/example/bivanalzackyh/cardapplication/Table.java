package com.example.bivanalzackyh.cardapplication;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import BroCardsNetworks.Client;
import BroCardsNetworks.TableRunner;

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

        try {
            // get information
            Bundle extra = getIntent().getExtras();

            String gameClass = extra.getString("GAME");
            String jsonArrayStr = savedInstanceState.getString("Participants");

            JSONArray participants = new JSONArray(jsonArrayStr);

            for (int i = 0; i < participants.length(); i++) {
                JSONObject jsonObject = participants.getJSONObject(i);

                // get IP address
                int IP = jsonObject.getInt("IP");

                clients.add(new Client(String.format(Locale.ENGLISH, "%d.%d.%d.%d", (IP & 0xff),
                        (IP >> 8 & 0xff),(IP >> 16 & 0xff),(IP >> 24 & 0xff)), 8888));

            }

            // game object
            Class<?> game = Class.forName(gameClass);

            Object gameObj = game
                    .getConstructor(List.class)
                    .newInstance(clients);

            // start
            Thread t = new Thread((TableRunner) gameObj);
            t.start();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong with JSON", Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "GAME CLASS NOT FOUND!", Toast.LENGTH_LONG).show();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Toast.makeText(this, "Class is not properly defined", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: use super.onCreate on subclass of this class to do this work and carry on from here

        /*
        the game interaction has to be done on the new thread for simplicity
        that is it has to be run in the manner which separated from the main thread

        use either:

            class Blackjacks extends TableRunner {
                ...

                public BlackJacks(List<Client> participants, ...) {
                    super(participants);
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
         */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
