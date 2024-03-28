package com.da.chitchat;

import android.net.Uri;

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
    private static final Map<String, List<Message>> groupMessageMap = new HashMap<>();

    private UserMessageStore() {

    }

    private static List<Message> createIfNotExists(Map<String, List<Message>> messageMap, String key) {
        return messageMap.computeIfAbsent(key, k -> new ArrayList<>());
    }

    public static List<Message> getMessages(Map<String, List<Message>> messageMap, String key) {
        return Collections.unmodifiableList(Objects.requireNonNull(createIfNotExists(messageMap, key)));
    }

    public static void addMessage(Map<String, List<Message>> messageMap, String key, Message message) {
        if (message.isDeleted()) {
            message.setText(AppContextSingleton.getInstance().getString(R.string.deleteMessageText));
        }
        createIfNotExists(messageMap, key).add(message);
    }

    public static void deleteMessage(Map<String, List<Message>> messageMap, String key, UUID id) {
        List<Message> messages = messageMap.get(key);

        if (messages != null) {
            for (Message message : messages) {
                if (message.getID().equals(id)) {
                    String deleteMessageText = AppContextSingleton
                            .getInstance().getString(R.string.deleteMessageText);
                    message.setText(deleteMessageText);
                    message.setState(Message.State.DELETED);
                    message.setEditTimestamp(null);
                    break;
                }
            }
        }
    }

    public static void editMessage(Map<String, List<Message>> messageMap, String key, UUID id,
                                   String newInput, Date editDate) {
        List<Message> messages = messageMap.get(key);

        if (messages != null) {
            for (Message message : messages) {
                if (message.getID().equals(id)) {
                    message.setText(newInput);
                    message.setState(Message.State.EDITED);
                    message.setEditTimestamp(editDate);
                    break;
                }
            }
        }
    }

    public static void editMedia(Map<String, List<Message>> messageMap, String key, UUID id,
                                 Uri mediaUri) {
        List<Message> messages = messageMap.get(key);

        if (messages != null) {
            for (Message message : messages) {
                if (message.getID().equals(id)) {
                    message.setMediaUri(mediaUri);
                    break;
                }
            }
        }
    }

    public static void clearMessages(Map<String, List<Message>> messageMap, String key) {
        if (messageMap.containsKey(key)) {
            List<Message> messages = messageMap.get(key);
            if (messages != null) {
                messages.clear();
            }
        }
    }

    public static List<Message> getUserMessages(String username) {
        return getMessages(userMessageMap, username);
    }

    public static List<Message> getGroupMessages(String groupName) {
        return getMessages(groupMessageMap, groupName);
    }

    public static void addMessageToUser(String username, Message message) {
        addMessage(userMessageMap, username, message);
    }

    public static void addMessageToGroup(String groupName, Message message) {
        addMessage(groupMessageMap, groupName, message);
    }

    public static void deleteMessageFromUser(String username, UUID id) {
        deleteMessage(userMessageMap, username, id);
    }

    public static void deleteMessageFromGroup(String groupName, UUID id) {
        deleteMessage(groupMessageMap, groupName, id);
    }

    public static void editMessageFromUser(String username, UUID id, String newInput, Date editDate) {
        editMessage(userMessageMap, username, id, newInput, editDate);
    }

    public static void editMessageFromGroup(String groupName, UUID id, String newInput, Date editDate) {
        editMessage(groupMessageMap, groupName, id, newInput, editDate);
    }

    public static void editMediaFromUser(String username, UUID id, Uri mediaUri) {
        editMedia(userMessageMap, username, id, mediaUri);
    }

    public static void editMediaFromGroup(String groupName, UUID id, Uri mediaUri) {
        editMedia(groupMessageMap, groupName, id, mediaUri);
    }

    public static void clearMessagesFromUser(String username) {
        clearMessages(userMessageMap, username);
    }
}
