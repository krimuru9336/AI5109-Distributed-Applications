package com.example.mysheetchatda.Models;

/*
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 02.02.2024
*/
public class User {

    String userName, eMail, password, userId, lastMessage;

    public User(){

    }

    public User(String userName, String eMail, String password, String userId, String lastMessage) {
        this.userName = userName;
        this.eMail = eMail;
        this.password = password;
        this.userId = userId;
        this.lastMessage = lastMessage;
    }

    // contructor for signup
    public User(String userName, String eMail, String password) {
        this.userName = userName;
        this.eMail = eMail;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
