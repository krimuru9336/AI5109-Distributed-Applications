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

    public void sendMessage(String targetUserId, Message message) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("targetUserId", targetUserId);
            jsonMessage.put("message", message.getMessage());
            jsonMessage.put("id", message.getId().toString());
            jsonMessage.put("type", message.getType());
            jsonMessage.put("sender", message.getSender());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("message", jsonMessage);
    }

    public void checkUsername(String username) {
        socket.emit("userCheck", username);
    }

//    public void setUserHelper(UserHelper userHelper, MainActivity activity) {
//        boolean listenerSet = this.userHelper != null;
//        this.userHelper = userHelper;
//
//        if (!listenerSet) {
//            // Initializing the userList on first connection
//            socket.on("init", args -> {
//                if (args.length > 0 && args[0] instanceof JSONObject) {
//                    try {
//                        JSONObject jsonObject = (JSONObject) args[0];
//
//                        if (jsonObject.has("data") && jsonObject.has("action")) {
//                            JSONArray userArray = jsonObject.getJSONArray("data");
//
//                            List<String> userList = new ArrayList<>();
//                            for (int i = 0; i < userArray.length(); i++) {
//                                userList.add(userArray.getString(i));
//                            }
//
//                            if (userHelper != null) {
//                                userHelper.initializeUserList(userList, jsonObject.getString("action"));
//                            }
//                        }
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            });
//
//            // Listening to "user" actions
//            socket.on("user", args -> {
//                if (args.length > 0 && args[0] instanceof JSONObject) {
//                    try {
//                        JSONObject jsonObject = (JSONObject) args[0];
//
//                        if (jsonObject.has("data") && jsonObject.has("action")) {
//
//                            if (userHelper != null) {
//                                userHelper.onUser(jsonObject.getString("data"), jsonObject.getString("action"));
//                            }
//                        }
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            });
//        }
//    }
//
//    public void setMessageHelper(MessageHelper messageHelper) {
//        boolean eventListenerSet = this.messageHelper != null;
//        this.messageHelper = messageHelper;
//
//        if (!eventListenerSet) {
//            // User gets a message
//            socket.on("message", args -> {
//                if (args.length > 0 && args[0] instanceof JSONObject) {
//                    try {
//                        JSONObject jsonObject = (JSONObject) args[0];
//
//                        if (jsonObject.has("data") && jsonObject.has("action")) {
//                            JSONObject messageObj = jsonObject.getJSONObject("data");
//
//                            if (this.messageHelper != null) {
//                                Message msg = new Message(messageObj.getString("message"),
//                                        messageObj.getString("senderUserId"), true, MessageType.TEXT);
//                                this.messageHelper.onMessageReceived(msg, jsonObject.getString("action"));
//                            }
//                        }
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            });
//        }
//    }

    public Socket getSocket() {
        return socket;
    }
}
