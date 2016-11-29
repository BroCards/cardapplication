package BroCardsNetworks;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Semaphore;

/**
 * Created by Chitphentom on 11/29/2016 AD.
 */

public class Server {
    private Semaphore dataProtector;
    private ServerSocket socket;

    /* Server: Basic Server Code */
    Server (int port) {
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataProtector = new Semaphore(1);
    }

    /* Listen: ReplyRoutine -> void
    *  Start listening for incoming connection from client
    *  However, this version of server can serve 1 and only 1 client at the time
    *  There's no need to serve more than 1 client at the time since this runs on
    *  the *PLAYER* side
    */
    public void Listen() {

    }
}
