package demo.campuschat.model;

public class User {
    private String userId;
    private String userName;
    private String userEmail;
    // Other fields like profile picture URL, status, etc.


    public User() {
    }

    public User(String userId) {
        this.userId = userId;
    }

    public User(String userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}