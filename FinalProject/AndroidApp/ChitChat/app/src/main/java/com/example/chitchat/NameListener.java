package com.example.chitchat;

public class NameListener {
    public NameListener(){};

    public void onEvent(String name, String action, MainActivity mainActivity){
        mainActivity.showUsernameToast(name,mainActivity);
        mainActivity.nextActivity(name);
    }
}
