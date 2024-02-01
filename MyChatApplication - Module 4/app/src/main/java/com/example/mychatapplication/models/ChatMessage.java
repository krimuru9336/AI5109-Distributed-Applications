package com.example.mychatapplication.models;

import java.util.Date;

public class ChatMessage {
    public String senderId, recieverId, message, dateTime, messageId;
    public Date dateObjects;
    public String conversionId, conversionName, conversionImage;

    public void setMessage(String editedMessage) {
        this.message = editedMessage;
    }

    public Object getMessage() {
        return message;
    }

}
