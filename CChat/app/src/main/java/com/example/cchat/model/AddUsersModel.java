package com.example.cchat.model;

import java.util.List;

public class AddUsersModel {


    String username;
    List<String> userIds;
    String phoneNumber;

    public AddUsersModel() {
    }

    public AddUsersModel(String username, List<String> userIds, String phoneNumber) {
        this.username = username;
        this.userIds = userIds;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
