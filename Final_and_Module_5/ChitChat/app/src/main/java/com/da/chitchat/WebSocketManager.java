package com.da.chitchat;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.da.chitchat.activities.ChatOverviewActivity;
import com.da.chitchat.activities.MainActivity;
import com.da.chitchat.activities.MessageActivity;
import com.da.chitchat.interfaces.GroupListener;
import com.da.chitchat.interfaces.MessageListener;
import com.da.chitchat.interfaces.NameListener;
import com.da.chitchat.interfaces.UserListener;
import com.da.chitchat.services.MediaConverter;
import com.da.chitchat.singletons.MediaConverterSingleton;
import com.da.chitchat.singletons.UserMessageListenerSingleton;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WebSocketManager {
    private final int CHUNKSIZE = 1024;

    private Socket socket;
    private UserListener<String> userListListener;
    private MessageListener messageListener;
    private GroupListener groupListener;
    private NameListener<Boolean, String> nameListener;
    private int activityChangeCounter;
    private String ownUsername;
    private String ownUUID;
    private MessageActivity curMessageActivity;
    private final MediaConverter mc;
    private final Context ctx;

    public WebSocketManager(Context ctx) {
        this.ctx = ctx;
        mc = MediaConverterSingleton.getInstance();
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

    public void setCurMessageActivity(MessageActivity messageActivity) {
        curMessageActivity = messageActivity;
    }

    public void sendMessage(String targetUserId, String message, UUID id, boolean isGroup) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("targetUserId", targetUserId);
            jsonMessage.put("message", message);
            jsonMessage.put("messageId", id);
            jsonMessage.put("isGroup", isGroup);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("message", jsonMessage);
    }

    public void sendMedia(String targetUserId, String message, Uri mediaUri, UUID id, String mimeType,
                          boolean isGroup) {
        String base64Media = mc.convertBitmapToBase64(ctx, mediaUri);
        int chunkCount = 0;
        JSONObject jsonMessage, jsonStartAndEndMessage;

        jsonStartAndEndMessage = new JSONObject();

        try {
            jsonStartAndEndMessage.put("targetUserId", targetUserId);
            jsonStartAndEndMessage.put("message", message);
            jsonStartAndEndMessage.put("messageId", id);
            jsonStartAndEndMessage.put("isGroup", isGroup);

            socket.emit("mediaStart", jsonStartAndEndMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int offset = 0; offset < base64Media.length(); offset += CHUNKSIZE) {
            jsonMessage = new JSONObject();
            int end = Math.min(offset + CHUNKSIZE, base64Media.length());
            String chunk = base64Media.substring(offset, end);

            try {
                jsonMessage.put("targetUserId", targetUserId);
                jsonMessage.put("messageId", id);
                jsonMessage.put("chunk", chunk);
                jsonMessage.put("offset", offset);
                jsonMessage.put("isGroup", isGroup);

                socket.emit("mediaChunk", jsonMessage);
                chunkCount++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            jsonStartAndEndMessage.remove("message");
            jsonStartAndEndMessage.put("chunkCount", chunkCount);
            jsonStartAndEndMessage.put("mimeType", mimeType);

            socket.emit("mediaEnd", jsonStartAndEndMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(String targetUserId, UUID id, boolean isGroup) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("targetUserId", targetUserId);
            jsonMessage.put("messageId", id);
            jsonMessage.put("isGroup", isGroup);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("delete", jsonMessage);
    }

    public void editMessage(String targetUserId, UUID id, String input, Date editDate, boolean isGroup) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("targetUserId", targetUserId);
            jsonMessage.put("messageId", id);
            jsonMessage.put("message", input);
            jsonMessage.put("editDate", editDate.getTime());
            jsonMessage.put("isGroup", isGroup);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("edit", jsonMessage);
    }

    public void createChatGroup(String groupName) {
        socket.emit("createChatGroup", groupName, ownUsername);
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

    public void getGroups(String groupName) {
        socket.emit("getGroups", groupName);
    }

    public void getGroupUsers(String groupName) {
        socket.emit("usersInGroup", groupName);
    }

    public void changeGroupUsers(String groupName, String[] selectedUsers) {
        JsonArray jsonArray = new JsonArray();
        for (String user : selectedUsers) {
            jsonArray.add(user);
        }
        socket.emit("updateChatGroup", groupName, jsonArray);
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
    
    public void setGroupListener(GroupListener listener, ChatOverviewActivity activity) {
        boolean eventListenerSet = this.groupListener != null;
        this.groupListener = listener;

        if (!eventListenerSet) {
            socket.on("groups", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("groups")) {
                                JSONArray messageArray = jsonObject.getJSONArray("data");

                                int length = messageArray.length();

                                for (int i = 0; i < length; i++) {
                                    groupListener.onGroupAdded(messageArray.getString(i));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            socket.on("groupCreated", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("groupCreated")) {
                                groupListener.onGroupAdded(jsonObject.getString("data"));
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            socket.on("groupExists", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("groupExists")) {
                                groupListener.onShowToast(jsonObject.getString("data"), activity);
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            socket.on("usersInGroup", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("usersInGroup")) {
                                JSONArray messageArray = jsonObject.getJSONArray("data");

                                int length = messageArray.length();

                                ArrayList<String> users = new ArrayList<>();
                                for (int i = 0; i < length; i++) {
                                    users.add(messageArray.getString(i));
                                }

                                if (curMessageActivity != null) {
                                    curMessageActivity.openDialog(users.toArray(new String[0]));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            socket.on("joinChatGroup", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("joinChatGroup")) {
                                groupListener.onGroupAdded(jsonObject.getString("data"));
                                socket.emit("socketJoinGroup", jsonObject.getString("data"));
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            socket.on("leaveChatGroup", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("leaveChatGroup")) {
                                groupListener.onGroupRemoved(jsonObject.getString("data"));
                                socket.emit("socketLeaveGroup", jsonObject.getString("data"));
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            socket.on("unauthorizedGroup", args -> {
                if (curMessageActivity != null) {
                    curMessageActivity.openUnauthorizedDialog();
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

                            List<Pair<String, Boolean>> userList = new ArrayList<>();

                            for (int i = 0; i < userArray.length(); i++) {
                                JSONObject userObject = userArray.getJSONObject(i);
                                String userName = userObject.getString("userName");
                                boolean isOnline = userObject.getBoolean("isOnline");
                                userList.add(new Pair<>(userName, isOnline));
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
        mc.setMessageListener(this.messageListener);

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
                                    if (messageObj.has("chatGroup")) {
                                        msg.setChatGroup(messageObj.getString("chatGroup"));
                                    }
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
                                    boolean isGroup = messageObj.has("chatGroup");
                                    String target;
                                    if (isGroup) {
                                        target = messageObj.getString("chatGroup");
                                    } else {
                                        target = messageObj.getString("senderUserId");
                                    }
                                    UUID messageId = UUID.fromString(messageObj.getString("messageId"));
                                    messageListener.onMessageDelete(target, messageId, isGroup);
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
                            boolean isEditTimestamp = jsonObject.getString("action").equals("editTimestamp");
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
                                    boolean isGroup = messageObj.has("chatGroup");
                                    String target;
                                    if (isGroup) {
                                        target = messageObj.getString("chatGroup");
                                    } else {
                                        target = messageObj.getString("senderUserId");
                                    }
                                    UUID messageId = UUID.fromString(messageObj.getString("messageId"));
                                    String message = messageObj.getString("message");
                                    Date editDate = new Date(messageObj.getLong("editDate"));
                                    messageListener.onMessageEdit(target, messageId, message, editDate, isGroup);
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

                                    if (messageObject.has("chatGroup")) {
                                        msg.setChatGroup(messageObject.getString("chatGroup"));
                                    }

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

            socket.on("mediaStart", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("mediaEnd")) {
                                JSONObject messageObj = jsonObject.getJSONObject("data");

                                if (messageListener != null) {
                                    UUID messageId = UUID.fromString(messageObj.getString("messageId"));

                                    Message msg = new Message(messageObj.getString("messageText"),
                                            messageObj.getString("senderUserId"), true,
                                            messageObj.getLong("timestamp"),
                                            messageId);

                                    if (messageObj.has("chatGroup")) {
                                        msg.setChatGroup(messageObj.getString("chatGroup"));
                                    }

                                    messageListener.onMessageReceived(msg);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            socket.on("mediaChunk", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("mediaChunk")) {
                                JSONObject messageObj = jsonObject.getJSONObject("data");

                                if (messageListener != null) {
                                    UUID messageId = UUID.fromString(messageObj.getString("messageId"));
                                    String chunk = messageObj.getString("chunk");
                                    int offset = messageObj.getInt("offset");
                                    mc.addChunk(ctx, chunk, messageId, offset);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            socket.on("mediaEnd", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject jsonObject = (JSONObject) args[0];

                        if (jsonObject.has("data") && jsonObject.has("action")) {
                            if (jsonObject.getString("action").equals("mediaEnd")) {
                                JSONObject messageObj = jsonObject.getJSONObject("data");

                                if (messageListener != null) {
                                    UUID messageId = UUID.fromString(messageObj.getString("messageId"));

                                    boolean isGroup = messageObj.has("chatGroup");
                                    String target;
                                    if (isGroup) {
                                        target = messageObj.getString("chatGroup");
                                    } else {
                                        target = messageObj.getString("senderUserId");
                                    }

                                    String mimeType = messageObj.getString("mimeType");
                                    int chunkCount = messageObj.getInt("chunkCount");
                                    mc.saveMedia(ctx, target, messageId, chunkCount, mimeType, isGroup);
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