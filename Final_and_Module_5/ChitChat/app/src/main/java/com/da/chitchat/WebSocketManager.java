// Sven Schickentanz - fdai7287
package com.da.chitchat;

import android.content.Context;
import android.net.Uri;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * The WebSocketManager class provides methods to manage the WebSocket connection and send and
 * receive messages. Events are sent to the server via the socket object, emitting events for
 * Socket.IO. The class also listens for incoming events from the server and calls the appropriate
 * listener methods.
 */
public class WebSocketManager {
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

    /**
     * Constructor for the WebSocketManager class.
     *
     * @param ctx The context of the application.
     */
    public WebSocketManager(Context ctx) {
        this.ctx = ctx;
        mc = MediaConverterSingleton.getInstance();
        ownUsername = "";
        activityChangeCounter = 0;
        try {
            // Get the server URL from the resources
            String SERVER_URL = ctx.getString(R.string.ip) + ":" + ctx.getString(R.string.port);
            socket = IO.socket(SERVER_URL);
            setMessageListener(UserMessageListenerSingleton.getInstance());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to the WebSocket server.
     */
    public void connect() {
        socket.connect();
    }

    /*
     * Prevents the WebSocket connection from being disconnected when the activity changes.
     */
    public void preventDisconnectOnActivityChange() {
        activityChangeCounter++;
    }

    /**
     * Disconnects from the WebSocket server.
     */
    public void disconnect() {
        if (activityChangeCounter <= 0) {
            UserMessageStore.clearMessagesFromUser(ownUsername);
            socket.disconnect();
        }
        activityChangeCounter--;
    }

    /**
     * Checks if the WebSocket connection is established.
     *
     * @return True if the connection is established, false otherwise.
     */
    public boolean isConnected() {
        return socket.connected();
    }

    /**
     * Registers a user with the WebSocket server.
     *
     * @param userName The username of the user.
     * @param uuid The UUID of the user.
     */
    public void registerUser(String userName, String uuid) {
        socket.emit("registerUser", userName, uuid);
        ownUsername = userName;
        ownUUID = uuid;
    }

    /**
     * Sets the current MessageActivity.
     */
    public void setCurMessageActivity(MessageActivity messageActivity) {
        curMessageActivity = messageActivity;
    }

    /**
     * Sends a message to a user.
     *
     * @param targetUserId The ID of the target user.
     * @param message The message to be sent.
     * @param id The ID of the message.
     * @param isGroup True if the message is sent to a group, false otherwise.
     */
    public void sendMessage(String targetUserId, String message, UUID id, boolean isGroup) {
        // Create JSON object with message data
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

    /**
     * Sends a media message to a user.
     *
     * @param targetUserId The ID of the target user.
     * @param message The message to be sent.
     * @param mediaUri The URI of the media file.
     * @param id The ID of the message.
     * @param mimeType The MIME type of the media file.
     * @param isGroup True if the message is sent to a group, false otherwise.
     * @param isVideo True if the media file is a video, false otherwise.
     */
    public void sendMedia(String targetUserId, String message, Uri mediaUri, UUID id, String mimeType,
                          boolean isGroup, boolean isVideo) {
        // Convert media file to base64 string
        String base64Media;
        if (isVideo) {
            base64Media = mc.convertVideoToBase64(ctx, mediaUri);
        } else {
            base64Media = mc.convertBitmapToBase64(ctx, mediaUri);
        }
        // Count the number of chunks to be sent
        int chunkCount = 0;
        JSONObject jsonMessage, jsonStartAndEndMessage;

        jsonStartAndEndMessage = new JSONObject();

        try {
            // Create JSON object with message data for message start
            jsonStartAndEndMessage.put("targetUserId", targetUserId);
            jsonStartAndEndMessage.put("message", message);
            jsonStartAndEndMessage.put("messageId", id);
            jsonStartAndEndMessage.put("isGroup", isGroup);

            // Will sent a message when transmission starts
            socket.emit("mediaStart", jsonStartAndEndMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send media chunks
        int CHUNKSIZE = 1024;
        for (int offset = 0; offset < base64Media.length(); offset += CHUNKSIZE) {
            jsonMessage = new JSONObject();
            int end = Math.min(offset + CHUNKSIZE, base64Media.length());
            String chunk = base64Media.substring(offset, end);

            try {
                // Create JSON object with message data for media chunk
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
            // Recycle JSON object with message data for message end
            // Will sent a message when transmission ends including the MIME type and the number of chunks
            jsonStartAndEndMessage.remove("message");
            jsonStartAndEndMessage.put("chunkCount", chunkCount);
            jsonStartAndEndMessage.put("mimeType", mimeType);

            socket.emit("mediaEnd", jsonStartAndEndMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a message.
     *
     * @param targetUserId The ID of the target user.
     * @param id The ID of the message.
     * @param isGroup True if the message is sent to a group, false otherwise.
     */
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

    /**
     * Edits a message.
     *
     * @param targetUserId The ID of the target user.
     * @param id The ID of the message.
     * @param input The new message.
     * @param editDate The date of the edit.
     * @param isGroup True if the message is sent to a group, false otherwise.
     */
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

    /**
     * Creates a chat group.
     * 
     * @param groupName The name of the chat group.
     */
    public void createChatGroup(String groupName) {
        socket.emit("createChatGroup", groupName, ownUsername);
    }

    /**
     * Checks if a username is already taken.
     * 
     * @param username The username to be checked.
     */
    public void checkUsername(String username) {
        socket.emit("userCheck", username);
    }

    /**
     * Checks if a username is already taken.
     * 
     * @param username The username to be checked.
     * @param userID The ID of the user.
     */
    public void checkUsername(String username, String userID) {
        socket.emit("userCheck", username, userID);
    }

    /**
     * Gets message from server database when user logs in.
     * 
     * @param username The username of the user.
     * @param userID The ID of the user.
     */
    public void getOfflineMessages(String username, String userID) {
        socket.emit("getOfflineMessages", username, userID);
    }

    /**
     * Gets the groups of a user.
     * 
     * @param userName The username of the user.
     */
    public void getGroups(String userName) {
        socket.emit("getGroups", userName);
    }

    /**
     * Gets the users in a chat group.
     * 
     * @param groupName The name of the chat group.
     */
    public void getGroupUsers(String groupName) {
        socket.emit("usersInGroup", groupName);
    }

    /**
     * Changes the users in a chat group.
     * 
     * @param groupName The name of the chat group.
     * @param selectedUsers The users to be in the chat group.
     */
    public void changeGroupUsers(String groupName, String[] selectedUsers) {
        JsonArray jsonArray = new JsonArray();
        for (String user : selectedUsers) {
            jsonArray.add(user);
        }
        socket.emit("updateChatGroup", groupName, jsonArray);
    }

    
    /**
     * Sets the listener for receiving the user name events.
     *
     * @param listener The listener to be set.
     * @param activity The MainActivity instance.
     */
    public void setUserNameListener(NameListener<Boolean, String> listener, MainActivity activity) {
        boolean eventListenerSet = this.nameListener != null;
        this.nameListener = listener;

        if (!eventListenerSet) {
            // On user name check
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
    
    /**
     * Sets the listener for receiving the group events.
     *
     * @param listener The listener to be set.
     * @param activity The ChatOverviewActivity instance.
     */
    public void setGroupListener(GroupListener listener, ChatOverviewActivity activity) {
        boolean eventListenerSet = this.groupListener != null;
        this.groupListener = listener;

        if (!eventListenerSet) {
            // Get groups of user
            socket.on("groups", args -> handleEventData(args, "groups", data -> {
                JSONArray messageArray = data.getJSONArray("data");

                int length = messageArray.length();

                for (int i = 0; i < length; i++) {
                    groupListener.onGroupAdded(messageArray.getString(i));
                }
            }));

            // Group created
            socket.on("groupCreated", args -> handleEventData(args, "groupCreated",
                    data -> groupListener.onGroupAdded(data.getString("data"))));

            // Check if group exists
            socket.on("groupExists", args -> handleEventData(args, "groupExists",
                    data -> groupListener.onShowToast(data.getString("data"), activity)));

            // Get users in group
            socket.on("usersInGroup", args -> handleEventData(args, "usersInGroup", data -> {
                JSONArray messageArray = data.getJSONArray("data");

                int length = messageArray.length();

                ArrayList<String> users = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    users.add(messageArray.getString(i));
                }

                if (curMessageActivity != null) {
                    // Open dialog to show users in group as multi-choice to select users
                    curMessageActivity.openDialog(users.toArray(new String[0]));
                }
            }));

            // Join a chat group
            socket.on("joinChatGroup", args -> handleEventData(args, "joinChatGroup", data -> {
                groupListener.onGroupAdded(data.getString("data"));
                socket.emit("socketJoinGroup", data.getString("data"));
            }));

            // Leave a chat group
            socket.on("leaveChatGroup", args -> handleEventData(args, "leaveChatGroup", data -> {
                groupListener.onGroupRemoved(data.getString("data"));
                socket.emit("socketLeaveGroup", data.getString("data"));
            }));

            // Unauthorized group
            socket.on("unauthorizedGroup", args -> {
                if (curMessageActivity != null) {
                    curMessageActivity.openUnauthorizedDialog();
                }
            });
        }
    }

    /**
     * Sets the listener for receiving the user list events.
     *
     * @param listener The listener to be set.
     */
    public void setUserListListener(UserListener<String> listener) {
        boolean eventListenerSet = this.userListListener != null;
        this.userListListener = listener;

        if (!eventListenerSet) {
            // User connects for the first time
            socket.on("init", args -> handleEventData(args, "init", data -> {
                JSONArray userArray = data.getJSONArray("data");

                List<Pair<String, Boolean>> userList = new ArrayList<>();

                // Get all users and their online status
                for (int i = 0; i < userArray.length(); i++) {
                    JSONObject userObject = userArray.getJSONObject(i);
                    String userName = userObject.getString("userName");
                    boolean isOnline = userObject.getBoolean("isOnline");
                    userList.add(new Pair<>(userName, isOnline));
                }

                if (userListListener != null) {
                    userListListener.onEvent(userList, data.getString("action"));
                }
            }));

            // Get changes in user list
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

    /**
     * Sets the listener for receiving message events.
     *
     * @param listener The listener to be set.
     */
    public void setMessageListener(MessageListener listener) {
        boolean eventListenerSet = this.messageListener != null;
        this.messageListener = listener;
        mc.setMessageListener(this.messageListener);

        if (!eventListenerSet) {
            // Receive message from user or group
            socket.on("message", args -> handleEventData(args, "message", data -> {
                JSONObject messageObj = data.getJSONObject("data");

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
            }));

            // User or group deletes sent message
            socket.on("delete", args -> handleEventData(args, "delete", data -> {
                JSONObject messageObj = data.getJSONObject("data");

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
            }));

            // Get accurate timestamp of message from server
            // Either the message timestamp or the edit timestamp
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

            // User or group edits sent message
            socket.on("edit", args -> handleEventData(args, "edit", data -> {
                JSONObject messageObj = data.getJSONObject("data");

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
            }));

            // Get offline messages from server database
            socket.on("offlineMessages", args -> handleEventData(args, "offlineMessages", data -> {
                JSONArray messageArray = data.getJSONArray("data");

                int length = messageArray.length();

                for (int i = 0; i < length; i++) {
                    JSONObject messageObject = messageArray.getJSONObject(i);

                    // Get message data from message Object
                    UUID id = UUID.fromString(messageObject.getString("id"));
                    String partnerName = messageObject.getString("partnerName");
                    String text = messageObject.getString("messageText");
                    boolean isIncoming = messageObject.getInt("incoming") == 1;
                    long timestamp = messageObject.getLong("timestamp");
                    long editTimestamp = 0;
                    if (!messageObject.isNull("timestampEdit"))
                        editTimestamp = messageObject.getLong("timestampEdit");
                    boolean isDeleted = messageObject.getInt("deleted") == 1;
                    // Set message state based on message data
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
                    // Clear offline messages from database when received
                    socket.emit("offlineReceived", ownUsername, ownUUID);
                }
            }));

            // Get media chunks
            socket.on("mediaChunk", args -> handleEventData(args, "mediaChunk", data -> {
                JSONObject messageObj = data.getJSONObject("data");

                if (messageListener != null) {
                    UUID messageId = UUID.fromString(messageObj.getString("messageId"));
                    String chunk = messageObj.getString("chunk");
                    int offset = messageObj.getInt("offset");
                    // Add media chunk to media converter to be merged when all chunks are received
                    mc.addChunk(ctx, chunk, messageId, offset);
                }
            }));

            // Triggered when media transmission completes
            socket.on("mediaEnd", args -> handleEventData(args, "mediaEnd", data -> {
                JSONObject messageObj = data.getJSONObject("data");

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
                    // Save media to storage and merges chunks
                    mc.saveMedia(ctx, target, messageId, chunkCount, mimeType, isGroup);
                }
            }));
        }
    }

    // Consumer that allows lambda function to pass through Exception
    interface CheckedConsumer<T> {
        void accept(T t) throws JSONException;
    }

    /**
     * Handles the event data received from the server.
     * 
     * @param args The arguments / data received from the server.
     * @param eventName The name of the event.
     * @param action The action to be performed.
     */
    private void handleEventData(Object[] args, String eventName, CheckedConsumer<JSONObject> action) {
        if (args.length > 0 && args[0] instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) args[0];
            try {
                if (jsonObject.has("data") && jsonObject.has("action") &&
                        jsonObject.getString("action").equals(eventName)) {
                    action.accept(jsonObject);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}