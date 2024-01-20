package com.example.chitchatapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class MessageStore {
    private static final Map<String, List<Message>> userMessageMap = new HashMap<>();
    private MessageStore() {

    }

    private static List<Message> createIfUserNotExists(String username) {
        return userMessageMap.computeIfAbsent(username, k -> new ArrayList<>());
    }

    public static List<Message> getUserMessages(String username) {
        return Collections.unmodifiableList(Objects.requireNonNull(createIfUserNotExists(username)));
    }

    public static void addMessageToUser(String username, Message message) {
        Log.d("addMessageToUser", username + ":" + message.getMessage());
        createIfUserNotExists(username).add(message);
    }

    public static void clearMessagesFromUser(String username) {
        if (userMessageMap.containsKey(username)) {
            List<Message> messages = userMessageMap.get(username);
            if (messages != null) {
                messages.clear();
            }
        }
    }
}
