package com.example.pbl_koshi_app.data;

import android.content.Context;
import com.example.pbl_koshi_app.R; // Rクラスをインポート
import com.example.pbl_koshi_app.Spot;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SpotDataLoader {

    private static final String SPOT_DATA_FILE = "koshi_spot_data.json";

    public static List<Spot> loadSpots(Context context) {
        Gson gson = new Gson();
        Type jsonSpotListType = new TypeToken<List<JsonSpot>>(){}.getType();
        List<JsonSpot> jsonSpots;

        try (InputStream is = context.getAssets().open(SPOT_DATA_FILE);
             Reader reader = new InputStreamReader(is)) {
            jsonSpots = gson.fromJson(reader, jsonSpotListType);
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        if (jsonSpots == null) {
            return new ArrayList<>();
        }

        List<Spot> spotList = new ArrayList<>();
        for (JsonSpot jsonSpot : jsonSpots) {

            // ★★★ ここからが本当の修正 ★★★

            int imageResId = 0; // いったん0で初期化
            String imageName = jsonSpot.getImageName();

            // imageNameがnullではなく、空文字列でもないことを確認する
            if (imageName != null && !imageName.isEmpty()) {
                imageResId = context.getResources().getIdentifier(
                        imageName, "drawable", context.getPackageName());
            }

            // もし画像が見つからなかった場合(imageResIdが0のまま)でも、
            // クラッシュさせず、代替画像を指定する
            if (imageResId == 0) {
                imageResId = R.drawable.ic_launcher_background; // ← 代替の画像
            }

            // ★★★ ここまで ★★★

            Spot spot = new Spot(
                    jsonSpot.getName(),
                    jsonSpot.getDescription(),
                    imageResId, // 安全なIDを渡す
                    jsonSpot.getLatitude(),
                    jsonSpot.getLongitude(),
                    jsonSpot.getId(),
                    jsonSpot.getTriviaList()
            );
            spotList.add(spot);
        }

        return spotList;
    }
}