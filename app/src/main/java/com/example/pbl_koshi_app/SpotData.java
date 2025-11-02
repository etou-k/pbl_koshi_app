package com.example.pbl_koshi_app;

import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;

public class SpotData {

    public static List<Spot> getSpots() {
        List<Spot> spotList = new ArrayList<>();
        // ここに観光地のデータを追加
        spotList.add(new Spot(
                "観光地1",
                "観光地1の説明です。",
                R.drawable.ic_launcher_background,
                37.4219,
                -122.0840,
                "ID1",
                Arrays.asList(
                        "豆知識1",
                        "豆知識２"
                )
        ));

        spotList.add(new Spot(
                "観光地2",
                "観光地2の説明です。",
                R.drawable.ic_launcher_background,
                37.4219,
                -122.0840,
                "ID2",
                Arrays.asList(
                        "豆知識1",
                        "豆知識２"
                )
        ));

        spotList.add(new Spot(
                "観光地3",
                "観光地3の説明です。",
                R.drawable.ic_launcher_background,
                37.4219,
                -122.0840,
                "ID3",
                Arrays.asList(
                        "豆知識1",
                        "豆知識２"
                )
        ));
        return spotList;
    }
}
