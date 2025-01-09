package com.example.brainpath.ui.progress;

public class Reminder {

    private String title;
    private String date;

    public Reminder (String title, String date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}
