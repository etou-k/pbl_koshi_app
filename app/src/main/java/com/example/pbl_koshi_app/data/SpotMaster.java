package com.example.pbl_koshi_app.data;

import android.content.Context;
import com.example.pbl_koshi_app.Spot; // 作成したSpotクラスをインポート
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpotMaster {

    private static SpotMaster instance;
    private final Map<String, Spot> spotMap;

    // コンストラクタをprivateにし、SpotDataLoaderを使ってデータを一度だけ読み込む
    private SpotMaster(Context context) {
        List<Spot> spotList = SpotDataLoader.loadSpots(context);
        // 読み込んだリストを、IDで高速に検索できるようMapに変換
        this.spotMap = spotList.stream()
                .collect(Collectors.toMap(Spot::getId, spot -> spot));
    }

    // シングルトンインスタンスを取得するメソッド
    public static synchronized SpotMaster getInstance(Context context) {
        if (instance == null) {
            instance = new SpotMaster(context.getApplicationContext());
        }
        return instance;
    }

    // IDを指定してSpotオブジェクトを検索するメソッド
    public Spot findSpotById(String spotId) {
        if (spotId == null) {
            return null;
        }
        return spotMap.get(spotId);
    }
}
