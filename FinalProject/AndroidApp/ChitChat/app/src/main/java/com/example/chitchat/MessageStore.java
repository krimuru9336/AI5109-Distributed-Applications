package com.example.chitchat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
}
