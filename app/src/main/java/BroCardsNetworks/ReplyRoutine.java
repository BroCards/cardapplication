package BroCardsNetworks;

import org.json.JSONObject;

import java.net.Socket;

/**
 * ReplyRoutine
 * Interface use to implement reply method which process incoming
 * JSONObject message from other devices and return anther JSONObject as a reply to
 * that device.
 * Created by Chitphentom on 11/29/2016 AD.
 */

public interface ReplyRoutine {

    // process routine: Json Object -> Json Object
    // use when called from the client reply routine
    public JSONObject processInquiry(JSONObject json);

}
