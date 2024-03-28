// Sven Schickentanz - fdai7287
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

/**
 * The UserMessageStore class represents a store for user and group messages.
 * It provides methods to add, delete, edit, and retrieve messages for both users and groups.
 * The messages are stored in separate maps for users and groups.
 * This way, messages can also be received when the Message Activity is not active.
 */
public final class UserMessageStore {
    private static final Map<String, List<Message>> userMessageMap = new HashMap<>();
    private static final Map<String, List<Message>> groupMessageMap = new HashMap<>();

    private UserMessageStore() {

    }

    /**
     * Creates a new list of messages if it does not already exist in the message map.
     *
     * @param messageMap The message map to check for the list of messages.
     * @param key        The key to check for the list of messages.
     * @return The list of messages for the specified key.
     */
    private static List<Message> createIfNotExists(Map<String, List<Message>> messageMap, String key) {
        return messageMap.computeIfAbsent(key, k -> new ArrayList<>());
    }

    /**
     * Returns the list of messages for the specified key.
     *
     * @param messageMap The message map to retrieve the list of messages from.
     * @param key        The key to retrieve the list of messages for.
     * @return The list of messages for the specified key.
     */
    public static List<Message> getMessages(Map<String, List<Message>> messageMap, String key) {
        return Collections.unmodifiableList(Objects.requireNonNull(createIfNotExists(messageMap, key)));
    }

    /**
     * Adds a message to the specified key in the message map.
     *
     * @param messageMap The message map to add the message to.
     * @param key        The key to add the message to.
     * @param message    The message to add.
     */
    public static void addMessage(Map<String, List<Message>> messageMap, String key, Message message) {
        if (message.isDeleted()) {
            message.setText(AppContextSingleton.getInstance().getString(R.string.deleteMessageText));
        }
        createIfNotExists(messageMap, key).add(message);
    }

    /**
     * Deletes a message with the specified ID from the specified key in the message map.
     *
     * @param messageMap The message map to delete the message from.
     * @param key        The key to delete the message from.
     * @param id         The ID of the message to delete.
     */
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

    /**
     * Edits a message with the specified ID in the specified key in the message map.
     *
     * @param messageMap The message map to edit the message in.
     * @param key        The key to edit the message in.
     * @param id         The ID of the message to edit.
     * @param newInput   The new input for the message.
     * @param editDate   The date of the edit.
     */
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

    /**
     * Edits the media of a message with the specified ID in the specified key in the message map.
     *
     * @param messageMap The message map to edit the media in.
     * @param key        The key to edit the media in.
     * @param id         The ID of the message to edit.
     * @param mediaUri   The new media URI.
     * @param isVideo    Whether the media is a video.
     */
    public static void editMedia(Map<String, List<Message>> messageMap, String key, UUID id,
                                 Uri mediaUri, boolean isVideo) {
        List<Message> messages = messageMap.get(key);

        if (messages != null) {
            for (Message message : messages) {
                if (message.getID().equals(id)) {
                    message.setMediaUri(mediaUri);
                    message.setIsVideo(isVideo);
                    break;
                }
            }
        }
    }

    /**
     * Clears the messages for the specified key in the message map.
     *
     * @param messageMap The message map to clear the messages from.
     * @param key        The key to clear the messages for.
     */
    public static void clearMessages(Map<String, List<Message>> messageMap, String key) {
        if (messageMap.containsKey(key)) {
            List<Message> messages = messageMap.get(key);
            if (messages != null) {
                messages.clear();
            }
        }
    }

    /**
     * Returns the list of messages for the specified user.
     * 
     * @param username The username to retrieve the messages for.
     * @return The list of messages for the specified user.
     */
    public static List<Message> getUserMessages(String username) {
        return getMessages(userMessageMap, username);
    }

    /**
     * Returns the list of messages for the specified group.
     * 
     * @param groupName The group name to retrieve the messages for.
     * @return The list of messages for the specified group.
     */
    public static List<Message> getGroupMessages(String groupName) {
        return getMessages(groupMessageMap, groupName);
    }

    /**
     * Adds a message to the specified user.
     * 
     * @param username The username to add the message to.
     * @param message The message to add.
     */
    public static void addMessageToUser(String username, Message message) {
        addMessage(userMessageMap, username, message);
    }

    /**
     * Adds a message to the specified group.
     * 
     * @param groupName The group name to add the message to.
     * @param message The message to add.
     */
    public static void addMessageToGroup(String groupName, Message message) {
        addMessage(groupMessageMap, groupName, message);
    }

    /**
     * Deletes a message with the specified ID from the specified user.
     * 
     * @param username The username to delete the message from.
     * @param id The ID of the message to delete.
     */
    public static void deleteMessageFromUser(String username, UUID id) {
        deleteMessage(userMessageMap, username, id);
    }

    /**
     * Deletes a message with the specified ID from the specified group.
     * 
     * @param groupName The group name to delete the message from.
     * @param id The ID of the message to delete.
     */
    public static void deleteMessageFromGroup(String groupName, UUID id) {
        deleteMessage(groupMessageMap, groupName, id);
    }

    /**
     * Edits a message with the specified ID in the specified user.
     * 
     * @param username The username to edit the message in.
     * @param id The ID of the message to edit.
     * @param newInput The new input for the message.
     * @param editDate The date of the edit.
     */
    public static void editMessageFromUser(String username, UUID id, String newInput, Date editDate) {
        editMessage(userMessageMap, username, id, newInput, editDate);
    }

    /**
     * Edits a message with the specified ID in the specified group.
     * 
     * @param groupName The group name to edit the message in.
     * @param id The ID of the message to edit.
     * @param newInput The new input for the message.
     * @param editDate The date of the edit.
     */
    public static void editMessageFromGroup(String groupName, UUID id, String newInput, Date editDate) {
        editMessage(groupMessageMap, groupName, id, newInput, editDate);
    }

    /**
     * Edits the media of a message with the specified ID in the specified user.
     * 
     * @param username The username to edit the media in.
     * @param id The ID of the message to edit.
     * @param mediaUri The new media URI.
     * @param isVideo Whether the media is a video.
     */
    public static void editMediaFromUser(String username, UUID id, Uri mediaUri, boolean isVideo) {
        editMedia(userMessageMap, username, id, mediaUri, isVideo);
    }

    /**
     * Edits the media of a message with the specified ID in the specified group.
     * 
     * @param groupName The group name to edit the media in.
     * @param id The ID of the message to edit.
     * @param mediaUri The new media URI.
     * @param isVideo Whether the media is a video.
     */
    public static void editMediaFromGroup(String groupName, UUID id, Uri mediaUri, boolean isVideo) {
        editMedia(groupMessageMap, groupName, id, mediaUri, isVideo);
    }

    /**
     * Clears the messages for the specified user.
     * 
     * @param username The username to clear the messages for.
     */
    public static void clearMessagesFromUser(String username) {
        clearMessages(userMessageMap, username);
    }
}
