package com.example.pbl_koshi_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pbl_koshi_app.data.SpotDataLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EncyclopediaActivity extends AppCompatActivity {

    private EncyclopediaAdapter adapter;
    private List<EncyclopediaAdapter.TriviaData> discoveredTriviaData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia);
        setTitle("発見した豆知識図鑑");

        RecyclerView recyclerView = findViewById(R.id.recycler_view_encyclopedia);
        Button closeButton = findViewById(R.id.button_close_encyclopedia);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EncyclopediaAdapter(discoveredTriviaData);
        recyclerView.setAdapter(adapter);

        loadDiscoveredTrivia();

        closeButton.setOnClickListener(v -> finish());
    }

    private void loadDiscoveredTrivia() {
        discoveredTriviaData.clear();
        SharedPreferences prefs = getSharedPreferences(TriviaActivity.PREFS_NAME, MODE_PRIVATE);
        Set<String> discoveredIds = prefs.getStringSet(TriviaActivity.KEY_DISCOVERED_TRIVIA, new HashSet<>());
        List<Spot> allSpots = SpotDataLoader.loadSpots(this);

        if (allSpots.isEmpty()) {
            adapter.notifyDataSetChanged();
            return;
        }

        for (String uniqueId : discoveredIds) {
            try {
                // ★★★ ここからが本当の修正 ★★★
                int lastUnderscoreIndex = uniqueId.lastIndexOf('_');
                if (lastUnderscoreIndex == -1 || lastUnderscoreIndex == 0) {
                    continue; // フォーマットが不正なIDは無視
                }

                // 最後の'_'を基準に、spotIdとtriviaIndexを正しく分割する
                String spotId = uniqueId.substring(0, lastUnderscoreIndex);
                String indexString = uniqueId.substring(lastUnderscoreIndex + 1);
                int triviaIndex = Integer.parseInt(indexString);
                // ★★★ ここまで ★★★

                for (Spot spot : allSpots) {
                    if (spot.getId().equals(spotId)) {
                        if (triviaIndex >= 0 && triviaIndex < spot.getTriviaList().size()) {
                            String spotName = spot.getName();
                            String triviaText = spot.getTriviaList().get(triviaIndex);
                            discoveredTriviaData.add(new EncyclopediaAdapter.TriviaData(spotName, triviaText));
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                // NumberFormatExceptionなど、予期せぬエラーが発生してもアプリを落とさない
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }
}