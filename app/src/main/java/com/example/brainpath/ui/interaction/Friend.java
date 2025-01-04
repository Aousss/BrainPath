package com.example.brainpath.ui.interaction;

public class Friend {
    private String username;
    private String userId;

    public Friend(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }
}

