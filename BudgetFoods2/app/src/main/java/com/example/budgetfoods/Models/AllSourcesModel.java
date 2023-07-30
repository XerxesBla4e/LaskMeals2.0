package com.example.budgetfoods.models;

public class AllSourcesModel {
    private int sourceimage;
    private String sourcename;

    public AllSourcesModel() {
    }

    public AllSourcesModel(int sourceimage, String sourcename) {
        this.sourceimage = sourceimage;
        this.sourcename = sourcename;
    }

    public int getSourceimage() {
        return sourceimage;
    }

    public void setSourceimage(int sourceimage) {
        this.sourceimage = sourceimage;
    }

    public String getSourcename() {
        return sourcename;
    }

    public void setSourcename(String sourcename) {
        this.sourcename = sourcename;
    }
}
