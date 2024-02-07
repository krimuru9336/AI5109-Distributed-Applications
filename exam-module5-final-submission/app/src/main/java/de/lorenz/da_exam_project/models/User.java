package de.lorenz.da_exam_project.models;

import java.io.Serializable;

public class User implements Serializable {

    private String userId;
    private String username;

    private long registrationTimestamp;

    public User() {
        // need empty constructor for firestore
    }

    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getRegistrationDate() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(long registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }
}
