package com.example.distributedapplicationsproject.utils;

import com.example.distributedapplicationsproject.db.MediaCachingService;
import com.example.distributedapplicationsproject.models.User;
import com.example.distributedapplicationsproject.models.chat.ChatInfo;

import java.util.List;

public class DataShare {
    // Private constructor to prevent instantiation outside this class
    private DataShare() {
        // Constructor implementation, if needed
    }

    private static DataShare instance;

    // Method to get the singleton instance
    public static DataShare getInstance() {
        if (instance == null) {
            // Create the instance if it doesn't exist
            instance = new DataShare();
        }
        return instance;
    }

    private MediaCachingService mediaCachingService;

    private User currentUser;

    private List<User> userList;

    private ChatInfo currentChatInfo;

    public MediaCachingService getMediaCachingService() {
        return mediaCachingService;
    }

    public void setMediaCachingService(MediaCachingService mediaCachingService) {
        this.mediaCachingService = mediaCachingService;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public ChatInfo getCurrentChatInfo() {
        return currentChatInfo;
    }

    public void setCurrentChatInfo(ChatInfo currentChatInfo) {
        this.currentChatInfo = currentChatInfo;
    }
}
