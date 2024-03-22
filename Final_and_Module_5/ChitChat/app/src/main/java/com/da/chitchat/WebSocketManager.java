package com.da.chitchat;

import android.content.Context;

import com.da.chitchat.activities.MainActivity;
import com.da.chitchat.interfaces.MessageListener;
import com.da.chitchat.interfaces.NameListener;
import com.da.chitchat.interfaces.UserListener;
import com.da.chitchat.singletons.UserMessageListenerSingleton;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class WebSocketManager {

    private Socket socket;
    private UserListener<String> userListListener;
    private MessageListener messageListener;
    private NameListener<Boolean, String> nameListener;
    private int activityChangeCounter;
    private String ownUsername;
    private String ownUUID;

    public WebSocketManager(Context ctx) {
        ownUsername = "";
        activityChangeCounter = 0;
        try {
            String SERVER_URL = ctx.getString(R.string.ip) + ":" + ctx.getString(R.string.port);
            socket = IO.socket(SERVER_URL);
            setMessageListener(UserMessageListenerSingleton.getInstance());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        socket.connect();
    }

    public void preventDisconnectOnActivityChange() {
        activityChangeCounter++;
    }

    public void disconnect() {
        if (activityChangeCounter <= 0) {
            UserMessageStore.clearMessagesFromUser(ownUsername);
            socket.disconnect();
        }
        activityChangeCounter--;
    }

    public boolean isConnected() {
        return socket.connected();
    }

    public void registerUser(String userName, String uuid) {

        socket.emit("registerUser", userName, uuid);
        ownUsername = userName;
        ownUUID = uuid;
    }

    public void sendMessage(String targetUserId, String message, UUID id) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("targetUserId", targetUserId);
            jsonMessage.put("message", message);
            jsonMessage.put("messageId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("message", jsonMessage);
    }

    public void deleteMessage(String targetUserId, UUID id) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("targetUserId", targetUserId);
            jsonMessage.put("messageId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("delete", jsonMessage);
    }

    public void editMessage(String targetUserId, UUID id, String input, Date editDate) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("targetUserId", targetUserId);
            jsonMessage.put("messageId", id);
            jsonMessage.put("message", input);
            jsonMessage.put("editDate", editDate.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("edit", jsonMessage);
    }

    public void checkUsername(String username) {
        socket.emit("userCheck", username);
    }

    public void checkUsername(String username, String userID) {
        socket.emit("userCheck", username, userID);
    }

    public void getOfflineMessages(String username, String userID) {
        socket.emit("getOfflineMessages", username, userID);
    }

    public void setUserNameListener(NameListener<Boolean, String> listener, MainActivity activity) {
        boolean eventListenerSet = this.nameListener != null;
        this.nameListener = listener;

        if (!eventListenerSet) {
            socket.on("userExists", args -> {
                try {
                    JSONObject jsonObject = (JSONObject) args[0];

                    Boolean exists = jsonObject.getBoolean("data");
                    String action = jsonObject.getString("action");
                    String name = jsonObject.getString("name");

                    listener.onEvent(exists, action, name, activity);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void setUserListListener(UserListener<String> listener) {
        boolean eventListenerSet = this.userListListener != null;
        this.userListListener = listener;

        if (!eventListenerSet) {
            // User connects for the first time
            socket.on("init", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            JSONArray userArray = jsonObject.getJSONArray("data");

                            List<String> userList = new ArrayList<>();
                            for (int i = 0; i < userArray.length(); i++) {
                                userList.add(userArray.getString(i));
                            }

                            if (userListListener != null) {
                                userListListener.onEvent(userList, jsonObject.getString("action"));
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // New user logs on / off
            socket.on("userList", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {

                            if (userListListener != null) {
                                userListListener.onEvent(jsonObject.getString("data"),
                                        jsonObject.getString("action"));
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    public void setMessageListener(MessageListener listener) {
        boolean eventListenerSet = this.messageListener != null;
        this.messageListener = listener;

        if (!eventListenerSet) {
            // Receive message from user
            socket.on("message", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("message")) {
                                JSONObject messageObj = jsonObject.getJSONObject("data");

                                if (messageListener != null) {
                                    Message msg = new Message(messageObj.getString("message"),
                                            messageObj.getString("senderUserId"), true,
                                            messageObj.getLong("timestamp"),
                                            UUID.fromString(messageObj.getString("messageId")));
                                    messageListener.onMessageReceived(msg);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // User deletes sent message
            socket.on("delete", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("delete")) {
                                JSONObject messageObj = jsonObject.getJSONObject("data");

                                if (messageListener != null) {
                                    String target = messageObj.getString("senderUserId");
                                    UUID messageId = UUID.fromString(messageObj.getString("messageId"));
                                    messageListener.onMessageDelete(target, messageId);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // Get sent timestamp from server
            socket.on("timestamp", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            boolean isEditTimestamp = false;
                            if (jsonObject.getString("action").equals("editTimestamp")) {
                                isEditTimestamp = true;
                            }
                            if (jsonObject.getString("action").equals("timestamp") ||
                                    jsonObject.getString("action").equals("editTimestamp")) {
                                JSONObject messageObj = jsonObject.getJSONObject("data");

                                if (messageListener != null) {
                                    UUID messageId = UUID.fromString(messageObj.getString("messageId"));
                                    long timestamp = messageObj.getLong("timestamp");
                                    messageListener.onTimestampReceived(messageId, timestamp, isEditTimestamp);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // User edits sent message
            socket.on("edit", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("edit")) {
                                JSONObject messageObj = jsonObject.getJSONObject("data");

                                if (messageListener != null) {
                                    String target = messageObj.getString("senderUserId");
                                    UUID messageId = UUID.fromString(messageObj.getString("messageId"));
                                    String message = messageObj.getString("message");
                                    Date editDate = new Date(messageObj.getLong("editDate"));
                                    messageListener.onMessageEdit(target, messageId, message, editDate);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            socket.on("offlineMessages", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("offlineMessages")) {
                                JSONArray messageArray = jsonObject.getJSONArray("data");

                                int length = messageArray.length();

                                for (int i = 0; i < length; i++) {
                                    JSONObject messageObject = messageArray.getJSONObject(i);

                                    UUID id = UUID.fromString(messageObject.getString("id"));
                                    String partnerName = messageObject.getString("partnerName");
                                    String text = messageObject.getString("messageText");
                                    boolean isIncoming = messageObject.getInt("incoming") == 1;
                                    long timestamp = messageObject.getLong("timestamp");
                                    long editTimestamp = 0;
                                    if (!messageObject.isNull("timestampEdit"))
                                        editTimestamp = messageObject.getLong("timestampEdit");
                                    boolean isDeleted = messageObject.getInt("deleted") == 1;
                                    Message.State state = isDeleted ? Message.State.DELETED :
                                            (editTimestamp > 0 ? Message.State.EDITED :
                                                    Message.State.UNMODIFIED);


                                    Message msg = new Message(text, partnerName, isIncoming,
                                            timestamp, id, state, editTimestamp);

                                    if (messageListener != null) {
                                        messageListener.onMessageReceived(msg);
                                    }
                                }

                                if (length > 0) {
                                    socket.emit("offlineReceived", ownUsername, ownUUID);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}