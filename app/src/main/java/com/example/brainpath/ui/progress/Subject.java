package com.example.brainpath.ui.progress;

public class Subject {
    private String name;
    private int progress;
    private String studyTime;
    private int studyTimeInMinutes;

    public Subject(String name, int progress, String studyTime, int studyTimeInMinutes) {
        this.name = name;
        this.progress = progress;
        this.studyTime = studyTime;
        this.studyTimeInMinutes = studyTimeInMinutes;
    }

    public String getName() {
        return name;
    }

    public int getProgress() {
        return progress;
    }

    public String getFormattedStudyTime() {
        return studyTime;
    }

    public int getStudyTimeInMinutes() {
        return studyTimeInMinutes;
    }
}
