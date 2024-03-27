package com.example.module5.model;

import java.util.HashMap;

public class User {

    private String userId;
    private String userName;
    private String userType;
    private HashMap<String, Boolean> members; // Groups the user belongs to

    public User() {
        // Default constructor required for Firebase
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public HashMap<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Boolean> members) {
        this.members = members;
    }
}
