package com.example.brainpath.ui.assessment;

import java.io.Serializable;
import java.util.List;

public class QuizModel implements Serializable {
    private String id;
    private String subject; // Matches the "subject" key in JSON
    private String title;
    private String subtitle;
    private String time;
    private List<QuestionModel> questionList;

    // Default constructor required for Firebase
    public QuizModel() {}

    // Constructor for initializing all fields
    public QuizModel(String id, String subject, String title, String subtitle, String time, List<QuestionModel> questionList) {
        this.id = id;
        this.subject = subject;
        this.title = title;
        this.subtitle = subtitle;
        this.time = time;
        this.questionList = questionList;
    }

    // Getters and Setters (required for Firebase)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<QuestionModel> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<QuestionModel> questionList) {
        this.questionList = questionList;
    }
}

