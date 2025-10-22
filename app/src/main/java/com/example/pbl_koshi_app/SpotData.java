package com.example.pbl_koshi_app;

import java.util.ArrayList;
import java.util.List;

public class SpotData {

    public static List<Spot> getSpots() {
        List<Spot> spotList = new ArrayList<>();
        // ここに観光地のデータを追加
        spotList.add(new Spot("観光地1", "観光地1の説明です。", R.drawable.ic_launcher_background));
        spotList.add(new Spot("観光地2", "観光地2の説明です。", R.drawable.ic_launcher_background));
        spotList.add(new Spot("観光地3", "観光地3の説明です。", R.drawable.ic_launcher_background));
        spotList.add(new Spot("観光地4", "観光地4の説明です。", R.drawable.ic_launcher_background)); // 観光地4を追加
        return spotList;
    }
}
