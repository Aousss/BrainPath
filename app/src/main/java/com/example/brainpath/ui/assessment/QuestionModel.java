package com.example.brainpath.ui.assessment;

import java.util.List;

class QuestionModel {
    private String question;
    private List<String> options;
    private String correct;

    // Default constructor required for Firebase
    public QuestionModel() {}

    // Constructor for initializing all fields
    public QuestionModel(String question, List<String> options, String correct) {
        this.question = question;
        this.options = options;
        this.correct = correct;
    }

    // Getters and Setters (required for Firebase)
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }
}
