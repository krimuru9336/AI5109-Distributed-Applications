package com.da.chitchat;

import com.da.chitchat.singletons.AppContextSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

    public static void deleteMessageFromUser(String username, UUID id) {
        List<Message> userMessages = userMessageMap.get(username);

        if (userMessages != null) {
            userMessages.forEach(message -> {
                if (message.getID().equals(id)) {
                    String deleteMessageText = AppContextSingleton.getInstance().getString(R.string.deleteMessageText);
                    message.setText(deleteMessageText);
                    message.setState(Message.State.DELETED);
                    message.setEditTimestamp(null);
                }
            });
        }
    }

    public static void editMessageFromUser(String username, UUID id, String newInput, Date editDate) {
        List<Message> userMessages = userMessageMap.get(username);

        if (userMessages != null) {
            userMessages.forEach(message -> {
                if (message.getID().equals(id)) {
                    message.setText(newInput);
                    message.setState(Message.State.EDITED);
                    message.setEditTimestamp(editDate);
                }
            });
        }
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
