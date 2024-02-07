package de.lorenz.da_exam_project.models;

import java.io.Serializable;
import java.util.List;

import de.lorenz.da_exam_project.Constants;

public class ChatRoom implements Serializable {

    private String id;
    private List<String> userIds;
    private long lastMessageTimestamp;
    private String lastMessageSenderId;
    private String lastMessage;
    private String lastMessageId;
    private String title;

    private ChatRoom() {
        // need empty constructor for firestore
    }

    public ChatRoom(String id, List<String> userIds, long lastMessageTimestamp, String lastMessageSenderId, String lastMessage, String lastMessageId, String title) {
        this.id = id;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessage = lastMessage;
        this.lastMessageId = lastMessageId;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public boolean isGroup() {
        return userIds.size() >= Constants.MIN_GROUP_SIZE;
    }

    public String getTitle() {
        return title;
    }
}
