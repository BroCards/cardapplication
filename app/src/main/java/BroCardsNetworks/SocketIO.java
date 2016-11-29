package BroCardsNetworks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * SocketIO
 * Socket IO interface class
 * Created by Chitphentom on 11/30/2016 AD.
 */

class SocketIO {
    // Send: Socket, String ->
    // send string message to a given socket
    static void send(Socket socket, String msg) throws IOException {
        // out data
        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
        out.write(msg);

        // message has been sent, over.
        out.write("\r\n");
    }

    // readLine: Socket -> String
    // read incoming line from given socket
    static String readLine(Socket socket) throws IOException {
        // input data
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // read line
        return in.readLine();
    }
}
