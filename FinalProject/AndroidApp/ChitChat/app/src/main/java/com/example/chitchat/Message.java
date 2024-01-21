package com.example.chitchat;
import java.util.Date;

public class Message {
    private String content;
    private String sendername;
    private boolean isIncoming;

    private Date timestamp;

    public Message(String content, String sendername, boolean isIncoming,long timestamp){
        this.content = content;
        this.sendername = sendername;
        this.isIncoming = isIncoming;

        this.timestamp = new Date(timestamp);
    }

    public String getContent(){
        return this.content;
    }
    public String getSendername(){
        return this.sendername;
    }
    public boolean getIsIncoming(){
        return this.isIncoming;
    }
    public Date getTimestamp(){
        return this.timestamp;
    }
}
