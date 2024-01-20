package com.example.distributedapplicationsproject.models.chat;

import com.example.distributedapplicationsproject.utils.Utils;

import java.util.UUID;

public class GroupChat extends Chat {

    private String creatorId;

    private String title;

    public GroupChat() {
        super();
        this.type = ChatType.GROUP;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
