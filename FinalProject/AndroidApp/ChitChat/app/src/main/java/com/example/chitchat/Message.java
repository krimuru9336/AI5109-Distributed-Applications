package com.example.chitchat;
import java.util.Date;
import java.util.UUID;
public class Message {
    public enum State {
        DEFAULT,
        EDITED,
        DELETED
    }
    private final UUID id;
    private String content;
    private String sendername;
    private boolean isIncoming;
    private Date timestamp;

    private Date changedTimestamp;

    private State state;


    public Message(String content, String sendername, boolean isIncoming){

        this.id=UUID.randomUUID();
        this.content = content;
        this.sendername = sendername;
        this.isIncoming = isIncoming;
        this.timestamp = new Date(System.currentTimeMillis());
        this.state = State.DEFAULT;
    }

    public Message(String content, String sendername, boolean isIncoming,long timestamp, UUID id){

        this.id=id;
        this.content = content;
        this.sendername = sendername;
        this.isIncoming = isIncoming;
        this.timestamp = new Date(timestamp);
        this.state = State.DEFAULT;
    }

    public void setContent(String content) {
        this.content = content;
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

    public UUID getID(){
        return this.id;
    }
    public void setChangedTimestamp(long timestamp){
        this.changedTimestamp = new Date(timestamp);
    }

    public Date getChangedTimestamp(){
        return this.changedTimestamp;
    }
    public void setState(State state){
        this.state = state;
    }
    public State getState(){
        return this.state;
    }
}
