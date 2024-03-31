package com.example.myapplication5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupChatroomModel {
    private String id;
    private String groupName;

    public List<String> getMemberIDs() {
        return memberIDs;
    }

    public void setMemberIDs(List<String> memberIDs) {
        this.memberIDs = memberIDs;
    }

    private List<String> memberIDs;

    private List<Map<String, String>> members;
    private List<ChatMessageModel> chats;

    boolean isGroupChat;

    public List<ChatMessageModel> getChats() {
        return chats;
    }

    public void setChats(List<ChatMessageModel> chats) {
        this.chats = chats;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    public GroupChatroomModel() {
        // Default constructor required for Firestore
    }

    public GroupChatroomModel(String id, String groupName, List<Map<String, String>> members) {
        this.id = id;
        this.groupName = groupName;
        this.members = members;
        this.chats = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Map<String, String>> getMembers() {
        return members;
    }

    public void setMembers(List<Map<String, String>> members) {
        this.members = members;
    }

    public List<ChatMessageModel> getMessages() {
        return chats;
    }

    public void setMessages(List<ChatMessageModel> messages) {
        this.chats = messages;
    }

    public void addMessage(ChatMessageModel message) {
        this.chats.add(message);
    }


}

