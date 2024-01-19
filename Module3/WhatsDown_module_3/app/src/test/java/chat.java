public class ChatMessage {
    private String sender;
    private String message;

    // Constructors, getters, and setters

    public ChatMessage() {
        // Default constructor required for Firebase
    }

    public ChatMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }
}
