package com.example.chatstnr.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.List;

public class GroupModel {
    public Timestamp createdTimestamp;
    private String groupId;
    private String groupName;
    @PropertyName("users")
    private List<UserModel> users;
    private List<String> userIds;

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    Timestamp lastMessageTimestamp;
    String lastMessageSenderId;
    String lastMessage;

    public GroupModel(){

    }
    public GroupModel(Timestamp createdTimestamp, String groupId, String groupName, List<UserModel> users, Timestamp lastMessageTimestamp, String lastMessageSenderId, String lastMessage, List<String> userIds) {
        this.createdTimestamp = createdTimestamp;
        this.groupId = groupId;
        this.groupName = groupName;
        this.users = users;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessage = lastMessage;
        this.userIds = userIds;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    // Handle null values for lastMessageSenderId
    public String getLastMessageSenderId() {
        return lastMessageSenderId != null ? lastMessageSenderId : "";
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    // Handle null values for lastMessage
    public String getLastMessage() {
        return lastMessage != null ? lastMessage : "";
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

}
