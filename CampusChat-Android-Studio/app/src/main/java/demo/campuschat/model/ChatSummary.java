package demo.campuschat.model;

public class ChatSummary {
    private String chatPartnerId;
    private String chatPartnerName;
    private String lastMessage;
    private long lastMessageTimestamp;

    private boolean isGroupChat;


    public ChatSummary() {}

    public ChatSummary(String chatPartnerId, String chatPartnerName, String lastMessage, long lastMessageTimestamp, boolean isGroupChat) {
        this.chatPartnerId = chatPartnerId;
        this.chatPartnerName = chatPartnerName;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.isGroupChat = isGroupChat;
    }

    public String getChatPartnerId() {
        return chatPartnerId;
    }

    public void setChatPartnerId(String chatPartnerId) {
        this.chatPartnerId = chatPartnerId;
    }

    public String getChatPartnerName() {
        return chatPartnerName;
    }

    public void setChatPartnerName(String chatPartnerName) {
        this.chatPartnerName = chatPartnerName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }
}