package com.example.myapplication5;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class ChatMessageModel {
    private String message;
    private String senderId;
    private Timestamp timestamp;

    //group
//    private boolean isGroupChat;
//
//    public boolean isGroupChat() {
//        return isGroupChat;
//    }
//
//    public void setGroupChat(boolean groupChat) {
//        isGroupChat = groupChat;
//    }
//    public String getGroupName() {
//        return groupName;
//    }
//
//    public void setGroupName(String groupName) {
//        this.groupName = groupName;
//    }
//
//
//    private String groupName; // For group chats
//group

    //messageid
    private String messageId;

    // New fields for media content
    private String mediaUrl; // URL or local path of the media content
    private MessageType messageType; // Enum to indicate the type of message

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    // Add a getter and setter for the message type
    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    // Enum to indicate the type of message
    public enum MessageType {
        TEXT,
        IMAGE,
        VIDEO,
        GIF
    }

    public ChatMessageModel() {
    }
//messageid
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }



    public ChatMessageModel(String message, String senderId, Timestamp timestamp, String messageId, MessageType messageType) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messageId = messageId;
        this.messageType = messageType; // Initialize MessageType

        //group
//        this.isGroupChat = isGroupChat;
//        this.groupName = groupName;

        //group
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("senderId", senderId);
        map.put("timestamp", timestamp);
        map.put("messageId", messageId);

        map.put("messageType", messageType != null ? messageType.name() : null);
        map.put("mediaURL", mediaUrl != null ? mediaUrl : null);

        //group
//        map.put("isGroupChat", isGroupChat);
//        map.put("groupName", groupName != null ? groupName : null);
        //group

        return map;
    }
    //messageid
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
