package BroCardsNetworks;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;

/**
 * Client
 * The Socket networking class for client
 * this version however, doesn't support synchronization (e.g. do in background)
 * however, this can be wrapped later
 * Created by Chitphentom on 11/29/2016 AD.
 */

public class Client {
    private String dest;                // destination IP address
    private int PORT;                   // destination port

    public Client(String destination, int port) {
        dest = destination;
        PORT = port;
    }

    // sendData: JSONObject ->
    // Send Data to server and wait for reply
    public JSONObject sendData(String msg, boolean wait_for_reply) {
        try {
            Socket socket = new Socket(dest, PORT);

            SocketIO.send(socket, msg);

            JSONObject response = null;

            Log.d("senddata", msg);
            Log.d("senddata", "now if wait for reply");

            if (wait_for_reply) {
                String reply = SocketIO.readLine(socket);

                // convert to json
                response = new JSONObject(reply);
            }
            // close
            socket.close();

            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ListenNReply: ReplyRoutine ->
    // Read line from server, process and answer the server inquiry
    public void listenNReply(ReplyRoutine r) {
        try {
            Socket socket = new Socket(dest, PORT);

            // read Json object inquiry
            String inquiry = SocketIO.readLine(socket);

            // do something about it
            JSONObject replyJson = r.processInquiry(new JSONObject(inquiry));

            // reply
            SocketIO.send(socket, replyJson.toString());
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // justListen: ->
    // Read line from server, convert to JSON and close
    public JSONObject justListen() {
        try {

            Socket socket = new Socket(dest, PORT);
            String msg = SocketIO.readLine(socket);

            // close
            socket.close();

            return new JSONObject(msg);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // JustSend: JSONObject ->
    // Send JsonObject to server and close
    public void justSend(JSONObject jsonObject) {
        sendData(jsonObject.toString(), false);
    }
}
