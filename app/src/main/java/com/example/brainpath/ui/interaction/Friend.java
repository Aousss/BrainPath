package com.example.brainpath.ui.interaction;
public class Friend {
    private String name;
    private String userId;

    public Friend(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }
}
