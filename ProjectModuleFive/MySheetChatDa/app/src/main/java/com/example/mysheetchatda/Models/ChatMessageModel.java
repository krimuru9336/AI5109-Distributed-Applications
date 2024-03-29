package com.example.mysheetchatda.Models;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 02.02.2024
*/
public class ChatMessageModel {

    String userId;
    String messageId;
    String messageText;
    Long timestamp;
    String imageUrl;
    String senderName;
    String videoUrl;

    String gifUrl;

    // empty constructor
    public ChatMessageModel() {

    }

    // constructor: name, mesage
    public ChatMessageModel(String userId, String messageText) {
        this.userId = userId;
        this.messageText = messageText;
    }

    // constructor: name, message, timestamp
    public ChatMessageModel(String userId, String messageText, Long timestamp) {
        this.userId = userId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    // constructor: name, message, timestamp, sendername
    public ChatMessageModel(String userId, String messageText, Long timestamp, String senderName) {
        this.userId = userId;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.senderName = senderName;
    }

    // constructor: name, message, timestamp, sendername, imageurl
    public ChatMessageModel(String userId, String messageText, Long timestamp, String senderName,String imageUrl) {
        this.userId = userId;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.imageUrl = imageUrl;
    }

    // constructor: name, message, timestamp, sendername, imageurl, videourl
    public ChatMessageModel(String userId, String messageText, Long timestamp, String senderName,String imageUrl, String videoUrl) {
        this.userId = userId;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }

    // constructor: name, message, timestamp, sendername, imageurl, videourl, gifurl
    public ChatMessageModel(String userId, String messageText, Long timestamp, String senderName,String imageUrl, String videoUrl, String gifUrl) {
        this.userId = userId;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.gifUrl = gifUrl;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }
}
