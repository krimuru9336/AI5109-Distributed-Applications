package com.example.whatsdown.model;

public class ChatMessage {
    /*
     * Jonas Wagner - 1315578
     */
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private String timestamp;
    private String mediaType;
    private String mediaUrl;
    private User sender;

    public ChatMessage(int id, int senderId, int receiverId, String content, String timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }


    public String getTimestamp() {
        return timestamp;
    }


    public String getMediaType() {
        return mediaType;
    }


    public String getMediaUrl() {
        return mediaUrl;
    }

    public User getSender() {
        return sender;
    }

}
