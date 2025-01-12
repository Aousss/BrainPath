package com.example.brainpath.ui.interaction;

public class Friend {
    private String username;
    private String userId;
    private String profile;
    private String lastMessage;
    private String lastSeen;

    public Friend(String username, String userId, String profile, String lastMessage, String lastSeen) {
        this.username = username;
        this.userId = userId;
        this.profile = profile;
        this.lastMessage = lastMessage;
        this.lastSeen = lastSeen;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfile() {
        return profile;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastSeen() {
        return lastSeen;
    }
}
