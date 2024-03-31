package com.sameetasadullah.i180479_180531;

public class Account {
    String ID, email, password, firstName, lastName, gender, bio, dp, phoneNumber, state, lastSeenTime, lastSeenDate;

    public Account(){}

    public Account(String ID, String email, String password, String firstName, String lastName,
                   String gender, String bio, String dp, String phoneNumber, String state,
                   String lastSeenTime, String lastSeenDate) {
        this.ID = ID;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.bio = bio;
        this.dp = dp;
        this.phoneNumber = phoneNumber;
        this.state = state;
        this.lastSeenTime = lastSeenTime;
        this.lastSeenDate = lastSeenDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLastSeenTime() {
        return lastSeenTime;
    }

    public void setLastSeenTime(String lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    public String getLastSeenDate() {
        return lastSeenDate;
    }

    public void setLastSeenDate(String lastSeenDate) {
        this.lastSeenDate = lastSeenDate;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}