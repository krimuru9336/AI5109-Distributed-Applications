package com.example.letschat.model;

import com.google.firebase.Timestamp;

public class User {

    private String userId;

    private String phone;
    private String username;
    private Timestamp createdTimestamp;

    public User() {
    }

    public User(String phone, String username, Timestamp createdTimestamp, String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
