package BroCardsNetworks;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

/**
 * Server
 * Socket networking class for server
 * this version however, doesn't support synchronization (e.g. do in background)
 * nor the handling of multiple client at once
 * however, this can be wrapped and improve later
 *
 * IMPORTANT:
 * call this.exit() before destroying any activities -- this will close the port
 * Created by Chitphentom on 11/29/2016 AD.
 */

public class Server {
    private Semaphore dataProtector;
    private ServerSocket serverSocket;
    private boolean status = false;

    /* Server: Basic Server Code */
    public Server (int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            dataProtector = new Semaphore(1);
            status = true;
        }

    }

    public boolean isRunning() {
        return status;
    }

    /* ListenNReply: ReplyRoutine -> JSONObject
    *  Start listening for incoming connection from client
    *  However, this version of server can serve 1 and only 1 client at the time
    *  There's no need to serve more than 1 client at the time since this runs on
    *  the *PLAYER* side
    */
    public JSONObject listen(ReplyRoutine r) {
        try {
            // accept connection
            Socket socket = serverSocket.accept();

            // read inquiry
            JSONObject inquiry = new JSONObject(SocketIO.readLine(socket));

            Log.d("listen", inquiry.toString());

            // process
            JSONObject reply = r.processInquiry(inquiry);

            // reply
            SocketIO.send(socket, reply.toString());

            socket.close();

            return inquiry;
        } catch (IOException e) {
            e.printStackTrace();
            status = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject listen(JSONObject reply) {
        final JSONObject to_reply = reply;
        return listen(new ReplyRoutine() {
            @Override
            public JSONObject processInquiry(JSONObject json) {
                return to_reply;
            }
        });
    }

    // Talk: String ->
    // Listen and reply with certain predefined message
    public void talk(String replyMsg) {
        try {
            // accept connection
            Socket socket = serverSocket.accept();

            // talk
            SocketIO.send(socket, replyMsg);

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
            status = false;
        }
    }

    // Talk: JSONObject ->
    // Listen and reply with predefined json object
    public void talk(JSONObject json) {
        talk(json.toString());
    }

    /**
     * Important: call this before exit activity
     * exit: ->
     * close socket connection
     */
    public void exit() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
