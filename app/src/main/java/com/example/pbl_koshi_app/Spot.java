package com.example.pbl_koshi_app;

import java.io.Serializable;

public class Spot implements Serializable {
    private String name;
    private String description;
    private int imageResourceId;

    public Spot(String name, String description, int imageResourceId) {
        this.name = name;
        this.description = description;
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}
