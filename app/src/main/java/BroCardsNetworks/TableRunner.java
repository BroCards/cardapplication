package BroCardsNetworks;

import org.json.JSONObject;

import java.util.List;

/**
 * TableRunner
 * Use to run the game and communicate between player and central table
 * Implementation of Runnable class which use run() method
 * Created by Chitphentom on 12/4/2016 AD.
 */

public abstract class TableRunner implements Runnable {

    protected List<Client> clients;

    TableRunner(List<Client> participants) {
        // DANGER: the object is not actually copied

        clients = participants;
    }

    private JSONObject communicate(int playerNum, String msg, boolean waitForReply) {
        // make request
        Client c = clients.get(playerNum);

        c.sendData(msg, waitForReply);

        JSONObject toRet = null;

        if (waitForReply) {
            toRet = c.getResponseJson();
        }
        return toRet;
    }


    protected void tellPlayer(final int playerNum, final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communicate(playerNum, msg, false);
            }
        }).run();
    }

    protected JSONObject tellPlayer(int playerNum, JSONObject msg) {
        return communicate(playerNum, msg.toString(), true);
    }

}
