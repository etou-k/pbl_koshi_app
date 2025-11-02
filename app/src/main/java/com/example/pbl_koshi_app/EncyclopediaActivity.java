package com.example.pbl_koshi_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EncyclopediaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia);

        Button closeButton = findViewById(R.id.button_close_encyclopedia);
        closeButton.setOnClickListener(v -> {
            finish(); // このアクティビティを終了して、前の画面（SpotDetailActivity）に戻る
        });

        // 1. SharedPreferencesから発見済みの豆知識IDのSetを取得する
        SharedPreferences prefs = getSharedPreferences(TriviaActivity.PREFS_NAME, MODE_PRIVATE);
        Set<String> discoveredTriviaIds = prefs.getStringSet(TriviaActivity.KEY_DISCOVERED_TRIVIA, new HashSet<>());

        // 2. すべてのスポットデータを取得する
        List<Spot> allSpots = SpotData.getSpots();

        // 3. 発見済みの豆知識のテキストだけをリストにまとめる
        List<EncyclopediaAdapter.TriviaData> discoveredTriviaData = new ArrayList<>();
        for (String uniqueId : discoveredTriviaIds) {
            String[] parts = uniqueId.split("_", 2); // 念のため2つにだけ分割
            if (parts.length < 2) continue;

            String spotId = parts[0];
            int triviaIndex = Integer.parseInt(parts[1]);

            // SpotIDを元に該当するSpotオブジェクトを探し、豆知識テキストを取得する
            for (Spot spot : allSpots) {
                if (spot.getId().equals(spotId)) {
                    if (triviaIndex >= 0 && triviaIndex < spot.getTriviaList().size()) {
                        String spotName = spot.getName();
                        String triviaText = spot.getTriviaList().get(triviaIndex);
                        discoveredTriviaData.add(new EncyclopediaAdapter.TriviaData(spotName, triviaText));
                        break; // 次のuniqueIdのループへ
                    }
                }
            }
        }

        // 4. RecyclerViewとAdapterを使って、リストを表示する
        RecyclerView recyclerView = findViewById(R.id.recycler_view_encyclopedia);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EncyclopediaAdapter adapter = new EncyclopediaAdapter(discoveredTriviaData);
        recyclerView.setAdapter(adapter);
    }
}

