package Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Models.Message;

public class MessageRes{
    public Map<String,ArrayList<Message>> messages = new HashMap<>();

    public Map<String,ArrayList<Message>> getMessages() {
        return messages;
    }

    public void setMessages(Map<String,ArrayList<Message>> messages) {
        this.messages = messages;
    }

    public MessageRes() {
    }
}