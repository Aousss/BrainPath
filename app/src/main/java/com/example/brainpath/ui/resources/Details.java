package com.example.brainpath.ui.resources;

public class Details {

    private String resDetailsTitle;
    private String resDetailsDesc;
    private String resDetailsPreview;

    public Details(String resDetailsTitle, String resDetailsDesc, String resDetailsPreview) {
        this.resDetailsTitle = resDetailsTitle;
        this.resDetailsDesc = resDetailsDesc;
        this.resDetailsPreview = resDetailsPreview;
    }

    public String getResDetailsTitle() {
        return resDetailsTitle;
    }

    public String getResDetailsDesc() {
        return resDetailsDesc;
    }

    public String getResDetailsPreview() {
        return resDetailsPreview;
    }
}
