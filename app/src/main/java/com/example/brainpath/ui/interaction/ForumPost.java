package com.example.brainpath.ui.interaction;

import java.util.Objects;

public class ForumPost {
    private String username;
    private String title;
    private String description;
    private String timestamp;
    private String imageUrl;
    private String fileDocId;

    // Default constructor required for Firebase/Firestore serialization
    public ForumPost() {
        // Empty constructor
    }

    // Constructor
    public ForumPost(String username, String title, String description, String timestamp, String imageUrl, String fileDocId) {
        this.username = username;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.fileDocId = fileDocId;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFileDocId() {
        return fileDocId;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setFileDocId(String fileDocId) {
        this.fileDocId = fileDocId;
    }

    // Override equals method
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Same object reference
        if (obj == null || getClass() != obj.getClass()) return false; // Null or different class

        ForumPost that = (ForumPost) obj;

        return Objects.equals(username, that.username) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(fileDocId, that.fileDocId);
    }

    // Override hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(username, title, description, timestamp, imageUrl, fileDocId);
    }
}
