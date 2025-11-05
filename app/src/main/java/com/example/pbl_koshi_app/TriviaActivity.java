package com.example.pbl_koshi_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button; // ★ Buttonクラスをインポート
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

public class TriviaActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "EncyclopediaPrefs";
    public static final String KEY_DISCOVERED_TRIVIA = "discovered_trivia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        // --- UI要素の取得 ---
        TextView triviaTextView = findViewById(R.id.text_trivia_content); // レイアウトで定義したID
        // ★★★ 閉じるボタンを取得する処理を追加 ★★★
        Button closeButton = findViewById(R.id.button_close_trivia);

        // --- IntentからSpotオブジェクトを受け取る ---
        Spot spot = (Spot) getIntent().getSerializableExtra("spot");

        // --- 豆知識の表示処理 ---
        if (spot != null && spot.getTriviaList() != null && !spot.getTriviaList().isEmpty()) {
            List<String> triviaList = spot.getTriviaList();
            Random random = new Random();
            int randomIndex = random.nextInt(triviaList.size());

            String trivia = triviaList.get(randomIndex);
            triviaTextView.setText(trivia);

            // この豆知識を発見済みとして保存
            saveTriviaAsDiscovered(spot.getId(), randomIndex); // spotのIDと豆知識のインデックス(0番目)
        } else {
            // ★★★ データが受け取れなかった場合の表示を追加 ★★★
            triviaTextView.setText("豆知識が見つかりませんでした。データを確認してください。");
        }

        // ★★★ 閉じるボタンのクリック処理を追加 ★★★
        closeButton.setOnClickListener(v -> {
            finish(); // このアクティビティを終了して、前の画面（SpotDetailActivity）に戻る
        });
    }

    private void saveTriviaAsDiscovered(String spotId, int triviaIndex) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // 保存済みのSetを読み込む（なければ新規作成）
        Set<String> discovered = new HashSet<>(prefs.getStringSet(KEY_DISCOVERED_TRIVIA, new HashSet<>()));

        // "spotId_triviaIndex" という形式のユニークなキーを作成
        String uniqueTriviaId = spotId + "_" + triviaIndex;
        discovered.add(uniqueTriviaId);

        // 新しいSetを保存
        editor.putStringSet(KEY_DISCOVERED_TRIVIA, discovered);
        editor.apply();
    }
}
