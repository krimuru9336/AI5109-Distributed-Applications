package demo.campuschat.model;


public class Message {

    private String messageId;
    private String messageText;
    private long timestamp;
    private String senderId;
    private String receiverId;

//     Empty Constructor
    public Message() {}

//    All Args Constructor
    public Message(String messageId, String senderId, String receiverId, String messageText, long timestamp) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

//    Setters

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    //    Getters


    public String getMessageId() {
        return messageId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
