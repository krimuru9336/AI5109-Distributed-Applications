package com.example.myapplication5;


public class HelperClass {

    String username;
    String email;

    private String userId;
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public HelperClass() {
    }

    public HelperClass(String username, String email, String userId) {
        this.username = username;
        this.email = email;
        this.userId = userId;
    }


}