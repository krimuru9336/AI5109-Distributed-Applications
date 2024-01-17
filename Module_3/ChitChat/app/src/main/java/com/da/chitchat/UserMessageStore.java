package com.da.chitchat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class UserMessageStore {
    private static final Map<String, List<Message>> userMessageMap = new HashMap<>();
    private UserMessageStore() {

    }

    private static List<Message> createIfUserNotExists(String username) {
        return userMessageMap.computeIfAbsent(username, k -> new ArrayList<>());
    }

    public static List<Message> getUserMessages(String username) {
        return Collections.unmodifiableList(Objects.requireNonNull(createIfUserNotExists(username)));
    }

    public static void addMessageToUser(String username, Message message) {
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
