package com.example.cchat.model;

public class SecretsModel {
    String fcmAPIKey;

    public SecretsModel() {
    }

    public SecretsModel(String fcmAPIKey) {
        this.fcmAPIKey = fcmAPIKey;
    }

    public String getFcmAPIKey() {
        return fcmAPIKey;
    }

    public void setFcmAPIKey(String fcmAPIKey) {
        this.fcmAPIKey = fcmAPIKey;
    }
}
