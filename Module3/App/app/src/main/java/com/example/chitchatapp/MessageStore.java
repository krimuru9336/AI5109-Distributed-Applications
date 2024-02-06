/*  Christian Jumtow
    Matr. Nr: 1166358
 */

package com.example.chitchatapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

    public static void editMessageFromUser(Message message) {
        List<Message> userMessages = userMessageMap.get(message.getSender());

        if (userMessages != null) {
            userMessages.forEach(m -> {
                if (m.getId().equals(message.getId())) {
                    m.setMessage(message.getMessage());
                    m.setState(MessageState.EDITED);
                    m.setEditTimeStamp(new Date());
                }
            });
        }
    }

    public static void deleteMessageFromUser(Message message) {
        List<Message> userMessages = userMessageMap.get(message.getSender());

        if (userMessages != null) {
            userMessages.forEach(m -> {
                if (m.getId().equals(message.getId())) {
                    m.setMessage("message deleted");
                    m.setState(MessageState.DELETED);
                }
            });
        }
    }

//    public static void replaceUserMessage(String username, int index, Message message) {
//        List<Message> userMessages = userMessageMap.get(username);
//
//        if (userMessages != null) {
//            Log.d("test", message.getMessageState().toString());
//            userMessages.set(index, message);
//        }
//    }
}
