package com.example.letschat.model;


import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoom {

    String chatRoomId;
    List<String> userIds;
    Timestamp lastMsgTimestamp;
    String lastMsgSenderId;
    String lastMsg;

    public ChatRoom() {
    }

    public ChatRoom(String chatRoomId, List<String> userIds, Timestamp lastMsgTimestamp, String lastMsgSenderId) {
        this.chatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lastMsgTimestamp = lastMsgTimestamp;
        this.lastMsgSenderId = lastMsgSenderId;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMsgTimestamp() {
        return lastMsgTimestamp;
    }

    public void setLastMsgTimestamp(Timestamp lastMsgTimestamp) {
        this.lastMsgTimestamp = lastMsgTimestamp;
    }

    public String getLastMsgSenderId() {
        return lastMsgSenderId;
    }

    public void setLastMsgSenderId(String lastMsgSenderId) {
        this.lastMsgSenderId = lastMsgSenderId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}
