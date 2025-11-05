package com.example.pbl_koshi_app.data;

import java.util.List;

// JSONの構造に完全に一致する、データ読み込みのための中継クラス
public class JsonSpot {
    private String id;    private String name;
    private String description;
    private String imageName; // R.drawable... ではなく、画像のファイル名を文字列として受け取る
    private double latitude;
    private double longitude;
    private List<String> triviaList;

    // --- 各フィールドのGetter ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageName() { return imageName; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public List<String> getTriviaList() { return triviaList; }
}