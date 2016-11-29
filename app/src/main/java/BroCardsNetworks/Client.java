package BroCardsNetworks;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

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
    private Semaphore dataReady;        // semaphore in case of this class has been wrap in other thread
    private JSONObject responseJson;    // response JSON

    Client(String destination, int port) {
        dest = destination;
        PORT = port;
        dataReady = new Semaphore(0);
        responseJson = null;
    }

    // sendData: JSONObject ->
    // Send Data to server and wait for reply
    public void sendData(JSONObject jsonObject, boolean wait_for_reply) {
        try {
            Socket socket = new Socket(dest, PORT);

            SocketIO.send(socket, jsonObject.toString());

            dataReady.tryAcquire();

            if (wait_for_reply) {
                String reply = SocketIO.readLine(socket);

                // convert to json
                responseJson = new JSONObject(reply);
            }
            // close
            socket.close();

            dataReady.release();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // justListen: ->
    // Read line from server, convert to JSON and close
    public void justListen() {
        try {
            Socket socket = new Socket(dest, PORT);
            String msg = SocketIO.readLine(socket);

            dataReady.tryAcquire();

            responseJson = new JSONObject(msg);

            // close
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // JustSend: JSONObject ->
    // Send JsonObject to server and close
    public void justSend(JSONObject jsonObject) {
        sendData(jsonObject, false);
    }

    // getResponseJson: -> JSONObject
    // when call
    public JSONObject getResponseJson() {
        try {
            dataReady.acquire();
            return responseJson;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
