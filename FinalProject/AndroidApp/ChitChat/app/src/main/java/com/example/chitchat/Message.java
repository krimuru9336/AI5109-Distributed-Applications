package com.example.chitchat;
import android.net.Uri;

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

    private String displayname;
    private boolean isIncoming;
    private Date timestamp;
    private Date changedTimestamp;
    private String base64data;
    private String type;

    private State state;


    public Message(String content, String sendername, boolean isIncoming){

        this.id=UUID.randomUUID();
        this.content = content;
        this.sendername = sendername;
        this.displayname = sendername;
        this.isIncoming = isIncoming;
        this.timestamp = new Date(System.currentTimeMillis());
        this.state = State.DEFAULT;
    }

    public Message(String content, String sendername, boolean isIncoming,long timestamp, UUID id){
        this.id=id;
        this.content = content;
        this.sendername = sendername;
        this.displayname = sendername;
        this.isIncoming = isIncoming;
        this.timestamp = new Date(timestamp);
        this.state = State.DEFAULT;
    }
    public Message(String content, String sendername, String displayname, boolean isIncoming, long timestamp, UUID id){
        this.id=id;
        this.content = content;
        this.sendername = sendername;
        this.displayname = displayname;
        this.isIncoming = isIncoming;
        this.timestamp = new Date(timestamp);
        this.state = State.DEFAULT;
    }

    public void setBase64data(String base64data) {this.base64data=base64data;}
    public String getBase64data(){
        return this.base64data;
    }
    public void setType(String type){
        this.type=type;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getType(){
        return this.type;
    }
    public String getContent(){
        return this.content;
    }
    public String getSendername(){
        return this.sendername;
    }
    public String getDisplayname(){
        return this.displayname;
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
