package com.example.distributedapplicationsproject.models;

import com.example.distributedapplicationsproject.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class User {
    private String id;
    private String name;
    private String createdAt;

    public User() {
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = Utils.generateCreatedAt();
    }

    public User(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.createdAt = Utils.generateCreatedAt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
