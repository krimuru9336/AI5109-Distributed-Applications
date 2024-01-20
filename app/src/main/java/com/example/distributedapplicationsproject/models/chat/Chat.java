package com.example.distributedapplicationsproject.models.chat;

import com.example.distributedapplicationsproject.models.Message;
import com.example.distributedapplicationsproject.utils.Utils;

import java.util.List;
import java.util.UUID;

public abstract class Chat {

    public enum ChatType {
        PRIVATE,
        GROUP
    }

    protected String id;

    protected ChatType type;

    protected List<String> memberIdList;

    protected String createdAt;

    protected List<Message> messageList;

    public Chat() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Utils.generateCreatedAt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getMemberIdList() {
        return memberIdList;
    }

    public void setMemberIdList(List<String> memberIdList) {
        this.memberIdList = memberIdList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
