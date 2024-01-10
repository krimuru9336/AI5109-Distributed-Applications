package com.example.whatsdown.model;

public class UpdateMessage {
    String content;

    public UpdateMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
