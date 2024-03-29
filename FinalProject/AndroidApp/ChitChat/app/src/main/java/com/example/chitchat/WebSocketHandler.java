package com.example.chitchat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;

public class WebSocketHandler {
    private Socket socket;
    private UserListener userListener;
    private MessageListener messageListener;
    private NameListener nameListener;
    private boolean userListenerSet;
    private boolean messageListenerSet;
    private boolean nameListenerSet;
    private int activityChangeCounter;

    private final Context context;
    private String myUsername;
    private static WebSocketHandler wsh;
    public static synchronized WebSocketHandler getInstance(Context context){
        if(wsh == null){
            wsh = new WebSocketHandler(context);
        }
        return wsh;
    }
    private WebSocketHandler(Context context){
        this.myUsername = "";
        this.activityChangeCounter = 0;
        this.userListenerSet = false;
        this.messageListenerSet = false;
        this.nameListenerSet = false;
        this.context=context;
        try {
            String COMMUNICATIONSERVER_URL = context.getString(R.string.ip) + ":" + context.getString(R.string.port);
            this.socket = IO.socket(COMMUNICATIONSERVER_URL);
            setMessageListener(MessageListener.getInstance());
            socket.on(Socket.EVENT_CONNECT_ERROR, args -> Log.e("SocketConnection", "Socket connection error: " + args[0]));

        } catch(URISyntaxException e){
            e.printStackTrace();
        }
    }

    public void connect(){

        this.socket.connect();
    }

    public void disconnect(){
        if(this.activityChangeCounter <= 0){
            MessageStore.clearMessages(this.myUsername);
            this.socket.disconnect();
        }
        this.activityChangeCounter--;
    }
    public void preventDC() {
        this.activityChangeCounter++;
    }

    public boolean isConnected() {
        return this.socket.connected();
    }
    public void registerUser(String username){
        this.socket.emit("registerUser",username);
        this.myUsername = username;
    }
    public void sendMessage(String usernameDest, String messageContent,long timestamp, UUID msgID){
        JSONObject jsonMsg = new JSONObject();
        try{
            jsonMsg.put("usernameDest",usernameDest);
            jsonMsg.put("messageContent",messageContent);
            jsonMsg.put("timestamp",timestamp);
            jsonMsg.put("msgID",msgID);
        }catch(JSONException e){
            e.printStackTrace();
        }
        this.socket.emit("message",jsonMsg);
    }
    public void sendMedia(String usernameDest, String messageContent, long timestamp, UUID msgID, String base64data, String type){
        JSONObject jsonMsg = new JSONObject();
        try{
            jsonMsg.put("usernameDest",usernameDest);
            jsonMsg.put("messageContent",messageContent);
            jsonMsg.put("timestamp",timestamp);
            jsonMsg.put("msgID",msgID);
            jsonMsg.put("file", base64data);
            jsonMsg.put("type",type);
        }catch(JSONException e){
            e.printStackTrace();
        }
        this.socket.emit("media",jsonMsg);
    }
    public void deleteMsg(String usernameDest, UUID msgID,long timestamp) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("usernameDest", usernameDest);
            jsonMessage.put("msgID", msgID);
            jsonMessage.put("timestamp", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("delete", jsonMessage);
    }

    public void editMsg(String usernameDest, UUID msgID, String messageContent, long timestamp) {
        JSONObject jsonMessage = new JSONObject();
        try {
            jsonMessage.put("usernameDest", usernameDest);
            jsonMessage.put("msgID", msgID);
            jsonMessage.put("messageContent", messageContent);
            jsonMessage.put("timestamp", timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("edit", jsonMessage);
    }

    public void getUsername(){
        socket.emit("getUsername");
    }

    public void setNameListener(NameListener nl,MainActivity mainActivity){
        this.nameListener = nl;
        if(!this.nameListenerSet){
            this.socket.on("getUsername", args -> {
                try{
                    JSONObject json = (JSONObject)  args[0];

                    String name = json.getString("username");
                    String action = json.getString("action");

                    this.nameListener.onEvent(name,action,mainActivity);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            });
            this.nameListenerSet = true;
        }
    }

    public void setUserListener(UserListener ul){

        this.userListener = ul;
        if(!this.userListenerSet){
            socket.on("registerUser",args -> {
                if(args.length > 0 && args[0] instanceof JSONObject){
                    try{
                        JSONObject json = (JSONObject) args[0];
                        if(json.has("data") && json.has("action")){
                            JSONArray userArray = json.getJSONArray("data");
                            List<String> users = new ArrayList<>();
                            for(int i = 0; i< userArray.length();i++){
                                users.add(userArray.getString(i));
                            }
                            userListener.onEvent(users,json.getString("action"));
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            socket.on("userList",args -> {
                if(args.length > 0 && args[0] instanceof JSONObject){
                    try{
                        JSONObject json = (JSONObject) args[0];
                        if(json.has("data") && json.has("action")){
                            userListener.onEvent(json.getString("data"),json.getString("action"));
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            this.userListenerSet = true;
        }
    }

    public void setMessageListener(MessageListener ml){

        this.messageListener = ml;
        if(!this.messageListenerSet){
            socket.on("groupMedia",args -> {
                if(args.length > 0 && args[0] instanceof JSONObject){
                    try{
                        JSONObject json = (JSONObject) args[0];
                        if(json.has("data") && json.has("action")){
                            JSONObject jsonMsg = json.getJSONObject(("data"));
                            String type = jsonMsg.getString("type");
                            String base64data = jsonMsg.getString("file");
                            Message msg = new Message(jsonMsg.getString("message"),
                                    jsonMsg.getString("usernameSource"),jsonMsg.getString("displayname"),true,jsonMsg.getLong("timestamp"), UUID.fromString(jsonMsg.getString("msgID")));
                            msg.setType(type);
                            msg.setBase64data(base64data);
                            messageListener.onMessageReceived(msg);
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            socket.on("media", args -> {
                if (args.length > 0 && args[0] instanceof JSONObject) {
                    try {
                        JSONObject json = (JSONObject) args[0];
                        if (json.has("data") && json.has("action")) {
                            if (json.getString("action").equals("media")) {
                                JSONObject jsonMsg = json.getJSONObject("data");
                                String type = jsonMsg.getString("type");
                                String base64data = jsonMsg.getString("file");
                                Message msg = new Message(
                                        jsonMsg.getString("message"),
                                        jsonMsg.getString("usernameSource"),
                                        true,
                                        jsonMsg.getLong("timestamp"),
                                        UUID.fromString(jsonMsg.getString("msgID"))
                                );
                                msg.setType(type);
                                msg.setBase64data(base64data);
                                messageListener.onMessageReceived(msg);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            socket.on("groupMessage",args -> {
                if(args.length > 0 && args[0] instanceof JSONObject){
                    try{
                        JSONObject json = (JSONObject) args[0];
                        if(json.has("data") && json.has("action")){
                            if(json.getString("action").equals("message")){
                                JSONObject jsonMsg = json.getJSONObject(("data"));
                                Message msg = new Message(jsonMsg.getString("message"),
                                        jsonMsg.getString("usernameSource"),jsonMsg.getString("displayname"),true,jsonMsg.getLong("timestamp"), UUID.fromString(jsonMsg.getString("msgID")));
                                messageListener.onMessageReceived(msg);
                            }
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });
            socket.on("message",args -> {
                if(args.length > 0 && args[0] instanceof JSONObject){
                    try{
                        JSONObject json = (JSONObject) args[0];
                        if(json.has("data") && json.has("action")){
                            if(json.getString("action").equals("message")){
                                JSONObject jsonMsg = json.getJSONObject(("data"));
                                Message msg = new Message(jsonMsg.getString("message"),
                                        jsonMsg.getString("usernameSource"),true,jsonMsg.getLong("timestamp"), UUID.fromString(jsonMsg.getString("msgID")));
                                messageListener.onMessageReceived(msg);
                            }
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            socket.on("edit",args -> {
                if(args.length > 0 && args[0] instanceof JSONObject){
                    try{
                        JSONObject json = (JSONObject) args[0];
                        if(json.has("data") && json.has("action")){
                            if(json.getString("action").equals("edit")){
                                JSONObject jsonMsg = json.getJSONObject(("data"));
                                if(messageListener!=null){
                                    messageListener.onMessageEdit(
                                            jsonMsg.getString("usernameSource"),
                                            UUID.fromString(jsonMsg.getString("msgID")),
                                            jsonMsg.getString("messageContent"),
                                            jsonMsg.getLong("timestamp")
                                    );
                                }
                            }
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            socket.on("delete",args -> {
                if(args.length > 0 && args[0] instanceof JSONObject){
                    try{
                        JSONObject json = (JSONObject) args[0];
                        if(json.has("data") && json.has("action")){
                            if(json.getString("action").equals("delete")){
                                JSONObject jsonMsg = json.getJSONObject(("data"));
                                if(messageListener!=null){
                                    messageListener.onMessageDelete(
                                            jsonMsg.getString("usernameSource"),
                                            UUID.fromString(jsonMsg.getString("msgID")),
                                            jsonMsg.getLong("timestamp"));
                                }
                            }
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });



            this.messageListenerSet = true;
        }
    }
}
