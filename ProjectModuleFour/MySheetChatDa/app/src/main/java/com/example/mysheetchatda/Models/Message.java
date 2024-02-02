package com.example.mysheetchatda.Models;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 02.02.2024
*/
public class Message {

    String userId;
    String messageId;
    String messageText;
    Long timestamp;

    public Message(String userId, String messageText, Long timestamp) {
        this.userId = userId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public Message(String userId, String messageText) {
        this.userId = userId;
        this.messageText = messageText;
    }

    public Message(){

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


}
