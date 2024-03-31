package Models;



public class Message {
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public enum MessageType{
        Image,
        Video,
        GIF,
        Text
    }
    String text;
    String time;
    String name;
    String key;
    String reciever_id;

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", reciever_id='" + reciever_id + '\'' +
                ", type=" + type +
                '}';
    }

    public String getReciever_id() {
        return reciever_id;
    }

    public void setReciever_id(String reciever_id) {
        this.reciever_id = reciever_id;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    MessageType type;
    String media_url;
    public  Message( ){}

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public Message(String text, String time, String name, String reciever_id, MessageType type) {
        this.text = text;
        this.time = time;
        this.name = name;

        this.reciever_id = reciever_id;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
