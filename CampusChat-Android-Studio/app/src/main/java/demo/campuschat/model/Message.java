package demo.campuschat.model;


import androidx.annotation.Nullable;

public class Message {

    private String messageId;
    private String messageText;
    private long timestamp;
    private String senderId;
    private String receiverId; // Can be null for group messages

    private String groupId;// New field for group messages

    private String mediaURL; // URL for image, video, or GIF

    private String thumbnailURL;
    private MediaType mediaType; // Enum for media type

    // Enum for media types
    public enum MediaType {
        IMAGE, VIDEO, GIF
    }

//     Empty Constructor
    public Message() {}

//    All Args Constructor
    public Message(String messageId, String senderId, String receiverId, @Nullable String groupId, String messageText, long timestamp, @Nullable String mediaURL, @Nullable String thumbnailURL, @Nullable MediaType mediaType) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.mediaURL = mediaURL;
        this.mediaType = mediaType;
        this.thumbnailURL = thumbnailURL;
        this.groupId = groupId;
    }
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

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setMediaURL(String imageURL) {
        this.mediaURL = imageURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    //    Getters


    public String getMessageId() {
        return messageId;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public String getSenderId() {
        return senderId;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getGroupId() { return groupId;}


}
