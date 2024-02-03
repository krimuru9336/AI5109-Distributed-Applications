package com.example.chitchat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

import java.util.UUID;
public class MessageStore {
    private static final Map<String,List<Message>> messageMap = new HashMap<>();

    private MessageStore(){}

    private static List<Message> createListIfNewChat(String username){
        return messageMap.computeIfAbsent(username,k -> new ArrayList<>());
    }

    public static List<Message> getMessages(String username){
        return Collections.unmodifiableList(Objects.requireNonNull(createListIfNewChat(username)));
    }
    public static void addMessage(String username, Message msg){
        createListIfNewChat(username).add(msg);
    }
    public static void clearMessages(String username){
        if(messageMap.containsKey(username)){
            List<Message> msgs = messageMap.get(username);
            if(msgs != null){
                msgs.clear();
            }
        }
    }

    public static void editMsg(String username, UUID id, String newContent, long newTimestamp) {
        List<Message> msgs = messageMap.get(username);

        if (msgs != null) {
            msgs.forEach(msg -> {
                if (msg.getID().equals(id)) {
                    msg.setContent(newContent);
                    msg.setState(Message.State.EDITED);
                    msg.setChangedTimestamp(newTimestamp);
                }
            });
        }
    }

    public static void deleteMsg(String username, UUID id, long newTimestamp) {
        List<Message> msgs = messageMap.get(username);

        if (msgs != null) {
            msgs.forEach(msg -> {
                if (msg.getID().equals(id)) {
                    String deleteMessageText = "Deleted";
                    msg.setContent(deleteMessageText);
                    msg.setState(Message.State.DELETED);
                    msg.setChangedTimestamp(newTimestamp);
                }
            });
        }
    }
}
