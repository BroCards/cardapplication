package BroCardsNetworks;

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

    /* Server: Basic Server Code */
    Server (int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataProtector = new Semaphore(1);
    }

    /* ListenNReply: ReplyRoutine ->
    *  Start listening for incoming connection from client
    *  However, this version of server can serve 1 and only 1 client at the time
    *  There's no need to serve more than 1 client at the time since this runs on
    *  the *PLAYER* side
    */
    public void Listen(ReplyRoutine r) {
        try {
            // accept connection
            Socket socket = serverSocket.accept();

            // read inquiry
            JSONObject inquiry = new JSONObject(SocketIO.readLine(socket));

            // process
            JSONObject reply = r.processInquiry(inquiry);

            // reply
            SocketIO.send(socket, reply.toString());

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
