package com.example.chitchatapp;

import android.content.Context;
import android.util.Log;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;


import org.json.JSONException;
import org.json.JSONObject;

public class SocketHelper {

    private final Socket socket;
    private int activityChangeCounter;
    private String username;

    private static SocketHelper instance;

    private SocketHelper(Context ctx) {
        username = "";
        activityChangeCounter = 0;
        try {
            Log.d("SocketHelper", "Connecting to server");
            String SERVER_URL = ctx.getString(R.string.ip) + ":" + ctx.getString(R.string.port);
            socket = IO.socket(SERVER_URL);
            socket.connect();

        } catch (URISyntaxException e) {
            Log.e("SocketHelper", "Error creating socket", e);
            throw new RuntimeException(e);
        }
    }

    public static synchronized SocketHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new SocketHelper(ctx);
        }
        return instance;
    }

    public void connect() {
        socket.connect();
    }

    public void preventDisconnectOnActivityChange() {
        activityChangeCounter++;
    }

    public void disconnect() {
        if (activityChangeCounter <= 0) {
            MessageStore.clearMessagesFromUser(username);
            socket.disconnect();
        }
        activityChangeCounter--;
    }

    public boolean isConnected() {
        return socket.connected();
    }

    public void registerUser(String userId) {
        socket.emit("registerUser", userId);
        username = userId;
    }

    public void sendMessage(Message message, MessageAction action) {
        try {
            JSONObject jsonMessage = message.toJSON();
            jsonMessage.put("action", action.toString());

            socket.emit("message", jsonMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void checkUsername(String username) {
        socket.emit("userCheck", username);
    }

    public Socket getSocket() {
        return socket;
    }
}
