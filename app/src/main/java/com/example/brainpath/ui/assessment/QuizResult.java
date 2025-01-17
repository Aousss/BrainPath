package com.example.brainpath.ui.assessment;

public class QuizResult {
    private String subject;
    private String title;
    private String marks;
    private String status;
    private String userId;

    public QuizResult() { }

    public QuizResult(String subject, String title, String marks, String status, String userId) {
        this.subject = subject;
        this.title = title;
        this.marks = marks;
        this.status = status;
        this.userId = userId;
    }

    // Getters and setters
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
