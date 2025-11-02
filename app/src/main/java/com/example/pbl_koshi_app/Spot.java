package com.example.pbl_koshi_app;

import java.io.Serializable;
import java.util.List;

public class Spot implements Serializable {
    private String name;
    private String description;
    private int imageResourceId;

    private double latitude;
    private double longitude;

    private String id;
    private List<String> triviaList;

    public Spot(String name, String description, int imageResourceId, double latitude, double longitude, String id, List<String> triviaList) {
        this.name = name;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.triviaList = triviaList;
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

    public double getLatitude() { return latitude; }   // ★ Getterを追加
    public double getLongitude() { return longitude; }

    public String getId() { return id; }

    public List<String> getTriviaList() { return triviaList; }
}
