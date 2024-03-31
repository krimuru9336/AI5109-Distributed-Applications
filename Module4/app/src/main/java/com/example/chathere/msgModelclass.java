package com.example.chathere;

public class msgModelclass {
    String messageKey;
    String messageId;
    String message;
    String senderid;
    long timeStamp;

    public msgModelclass() {
    }

    public msgModelclass(String messageId, String message, String senderid, long timeStamp) {
        this.messageId = messageId;
        this.message = message;
        this.senderid = senderid;
        this.timeStamp = timeStamp;
    }

    public void setMessageKey(String messageKey){
        this.messageKey = messageKey;
    }
    public String getMessageId() {
        return messageId;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessageKey(){return  messageKey;}



}
