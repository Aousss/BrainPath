package com.example.brainpath.ui.interaction;

public class ForumPost {
    private String username;
    private String title;
    private String description;
    private String timestamp;
    private String imageUrl;
    private String fileUrl;
    private String fileDocId;

    // Default constructor (required for Firestore)
    public ForumPost() {
        // Empty constructor for Firestore
    }

    // Constructor
    public ForumPost(String username, String title, String description, String timestamp, String imageUrl, String fileUrl) {
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
        return fileDocId;  // Getter for fileDocId
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
}
