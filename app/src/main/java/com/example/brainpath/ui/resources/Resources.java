package com.example.brainpath.ui.resources;

public class Resources {

    private String resTitle;
    private String resDesc;
    private String fullDesc;
    private String resPreview;
    private int resID;

    public Resources(String resTitle, String fullDesc, String resDesc, String resPreview, int resID) {
        this.resTitle = resTitle;
        this.resDesc = resDesc;
        this.fullDesc = fullDesc;
        this.resPreview = resPreview;
        this.resID = resID;
    }

    public String getResTitle() {
        return resTitle;
    }

    public String getResDesc() {
        return resDesc;
    }

    public String getFullDesc() {
        return fullDesc;
    }

    public String getResPreview() {
        return resPreview;
    }

    public int getResID() {
        return resID;
    }
}
