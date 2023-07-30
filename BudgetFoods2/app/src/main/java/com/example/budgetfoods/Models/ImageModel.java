package com.example.budgetfoods.models;

public class ImageModel {
    String imagepath;

    public ImageModel() {
    }

    public ImageModel(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}
