package com.example.pbl_koshi_app.data; // ★あなたのプロジェクトのルートパッケージ名に変更してください

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbl_koshi_app.R;
import com.example.pbl_koshi_app.data.QuizItem; // ★データ構造クラスのパッケージをインポート
import com.example.pbl_koshi_app.data.SpotQuiz; // ★データ構造クラスのパッケージをインポート
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 合志バスナビ：クイズ画面のロジックを制御するActivity
 * 1. assetsフォルダからクイズデータを読み込む。
 * 2. ランダムなスポットのクイズ（5問）を出題する。
 * 3. 正誤判定、スコア管理、画面更新を行う。
 * 4. 5問終了後、スポット詳細画面への誘導を行う。
 */
public class QuizActivity extends AppCompatActivity {

    // UI要素の宣言
    private TextView textProgress, textQuestion, textResultStatus, textCommentary, textFinalScore;
    private ImageView imageHint;
    private LinearLayout layoutOptions, layoutResult, finalResultArea, quizArea;
    private Button buttonNext, buttonToSpotDetail;
    private Button[] optionButtons = new Button[4]; // 選択肢ボタンを配列で管理

    // クイズデータの管理
    private SpotQuiz currentQuizSet; // 現在出題中の5問セット
    private int currentQuestionIndex = 0; // 現在の問題番号 (0からスタート)
    private int score = 0; // スコア

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_quiz.xmlをこのActivityの画面として設定
        setContentView(R.layout.activity_quiz);

        // 1. UI要素の初期化と取得
        initializeViews();

        // 2. クイズデータの読み込みとセットアップ
        currentQuizSet = QuizDataLoader.getRandomQuizSet(this);
        if (currentQuizSet == null || currentQuizSet.getQuizList() == null || currentQuizSet.getQuizList().isEmpty()) {
            Toast.makeText(this, "クイズデータの読み込みに失敗しました。", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 3. 最初の問題を出題
        displayQuestion();

        // 4. イベントリスナーの設定
        setEventListeners();
    }

    /**
     * XMLレイアウトのUI要素をJavaオブジェクトに紐づける
     */
    private void initializeViews() {
        // 問題表示エリア
        textProgress = findViewById(R.id.text_progress);
        textQuestion = findViewById(R.id.text_question);
        quizArea = findViewById(R.id.quiz_area);

        // 選択肢エリア
        layoutOptions = findViewById(R.id.layout_options);
        optionButtons[0] = findViewById(R.id.button_option_a);
        optionButtons[1] = findViewById(R.id.button_option_b);
        optionButtons[2] = findViewById(R.id.button_option_c);
        optionButtons[3] = findViewById(R.id.button_option_d);

        // 結果/解説エリア
        layoutResult = findViewById(R.id.layout_result);
        textResultStatus = findViewById(R.id.text_result_status);
        textCommentary = findViewById(R.id.text_commentary);
        buttonNext = findViewById(R.id.button_next);

        // 最終結果・誘導エリア
        finalResultArea = findViewById(R.id.final_result_area);
        textFinalScore = findViewById(R.id.text_final_score);
        buttonToSpotDetail = findViewById(R.id.button_to_spot_detail);

        // 初期状態では結果エリアと最終結果エリアを非表示
        layoutResult.setVisibility(View.GONE);
        finalResultArea.setVisibility(View.GONE);
    }

    /**
     * 選択肢ボタンと「次へ」ボタンのクリックリスナーを設定
     */
    private void setEventListeners() {
        // 選択肢ボタンのリスナー設定
        for (int i = 0; i < optionButtons.length; i++) {
            final int index = i;
            optionButtons[i].setOnClickListener(v -> handleAnswer(index));
        }

        // 次へ/結果を見るボタンのリスナー
        buttonNext.setOnClickListener(v -> handleNextStep());

        // スポット詳細へ誘導ボタンのリスナー
        buttonToSpotDetail.setOnClickListener(v -> goToSpotDetail());
    }

    /**
     * 現在の問題データを画面に表示する
     */
    private void displayQuestion() {
        if (currentQuestionIndex >= currentQuizSet.getQuizList().size()) {
            // 全問終了
            showFinalResult();
            return;
        }

        QuizItem currentItem = currentQuizSet.getQuizList().get(currentQuestionIndex);

        // UIをリセットし、選択肢を有効化
        layoutResult.setVisibility(View.GONE);
        for (Button button : optionButtons) {
            button.setEnabled(true);
            button.setBackgroundResource(R.drawable.default_button_color); // ★drawableで標準色を定義
            button.setTextColor(Color.BLACK);
        }

        // 1. 進捗と問題文の設定
        textProgress.setText((currentQuestionIndex + 1) + " / " + currentQuizSet.getQuizList().size() + " 問目");
        textQuestion.setText(currentItem.getQuestionText());

        // 2. 画像ヒントの設定
        // TODO: assetsフォルダ内の画像を読み込む処理（AssetManagerを使用）を実装する
        // imageHint.setImageDrawable(loadImageFromAssets(currentItem.getHintImageURL()));

        // 3. 選択肢の設定
        String[] keys = {"A", "B", "C", "D"};
        for (int i = 0; i < optionButtons.length; i++) {
            String optionKey = keys[i];
            String optionText = currentItem.getOptions().get(optionKey);
            optionButtons[i].setText(optionKey + ". " + optionText);
        }
    }

    /**
     * ユーザーが選択肢ボタンを押したときの処理
     * @param selectedIndex 押されたボタンのインデックス
     */
    private void handleAnswer(int selectedIndex) {
        if (layoutResult.getVisibility() == View.VISIBLE) return; // 二重回答防止

        QuizItem currentItem = currentQuizSet.getQuizList().get(currentQuestionIndex);
        String selectedKey = new String[]{"A", "B", "C", "D"}[selectedIndex];
        String correctKey = currentItem.getCorrectOption();

        boolean isCorrect = selectedKey.equals(correctKey);

        // 1. スコアと結果の更新
        if (isCorrect) {
            score++;
            textResultStatus.setText(getString(R.string.result_status_placeholder)); // 正解！🎉
        } else {
            textResultStatus.setText("残念...");
        }
        textCommentary.setText(currentItem.getCommentary());

        // 2. 選択肢ボタンのフィードバック（色変更）
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setEnabled(false); // ボタンを無効化
            String key = new String[]{"A", "B", "C", "D"}[i];

            if (key.equals(correctKey)) {
                // 正解のボタンは緑色
                optionButtons[i].setBackgroundColor(Color.parseColor("#4CAF50"));
                optionButtons[i].setTextColor(Color.WHITE);
            } else if (i == selectedIndex) {
                // 選択したボタンが不正解なら赤色
                optionButtons[i].setBackgroundColor(Color.parseColor("#F44336"));
                optionButtons[i].setTextColor(Color.WHITE);
            } else {
                // 他のボタンはグレー
                optionButtons[i].setBackgroundColor(Color.parseColor("#CCCCCC"));
            }
        }

        // 3. 結果エリアの表示と「次へ」ボタンのテキスト更新
        layoutResult.setVisibility(View.VISIBLE);
        if (currentQuestionIndex == currentQuizSet.getQuizList().size() - 1) {
            buttonNext.setText("最終結果を見る");
        } else {
            buttonNext.setText(getString(R.string.next_question_button));
        }
    }

    /**
     * 「次へ」ボタンが押されたときの処理 (次の問題 or 最終結果)
     */
    private void handleNextStep() {
        currentQuestionIndex++;
        if (currentQuestionIndex < currentQuizSet.getQuizList().size()) {
            displayQuestion(); // 次の問題へ
        } else {
            showFinalResult(); // 全問終了
        }
    }

    /**
     * 5問すべて終了した後の最終結果画面を表示する
     */
    private void showFinalResult() {
        // 1. クイズエリア全体を非表示
        quizArea.setVisibility(View.GONE);

        // 2. 最終結果エリアを表示
        finalResultArea.setVisibility(View.VISIBLE);

        // 3. スコア表示
        textFinalScore.setText(getString(R.string.final_score_placeholder, score, currentQuizSet.getQuizList().size()));

        // 4. 誘導ボタンのテキストを更新
        String spotName = currentQuizSet.getSpotName();
        String buttonText = getString(R.string.go_to_spot_detail_button, spotName);
        buttonToSpotDetail.setText(buttonText);
    }

    /**
     * スポット詳細画面へ遷移する（誘導）
     */
    private void goToSpotDetail() {
        String spotId = currentQuizSet.getQuizList().get(0).getRelatedSpotId();

        // TODO: SpotDetailActivityへ遷移するためのIntentを作成する
        // Intent intent = new Intent(QuizActivity.this, SpotDetailActivity.class);
        // intent.putExtra("SPOT_ID", spotId);
        // startActivity(intent);

        Toast.makeText(this, currentQuizSet.getSpotName() + "の詳細画面へ誘導します (ID: " + spotId + ")", Toast.LENGTH_SHORT).show();
        finish();
    }

    // =========================================================================
    // 外部ファイル（JSON）の読み込みとデータ選択を行うヘルパークラス（内部クラスとして実装）
    // =========================================================================

    /**
     * assetsフォルダからクイズJSONを読み込み、ランダムなスポットのクイズセットを返す
     * このクラスは、QuizActivityから切り離して、utilityパッケージに作成することを推奨します。
     */
    public static class QuizDataLoader {

        private static final String JSON_FILE_NAME = "koshi_quiz_data.json";

        // QuizDataLoader クラス内の getRandomQuizSet メソッドを以下に置き換える

        public static SpotQuiz getRandomQuizSet(Context context) {
            String jsonString = loadJsonFromAsset(context, JSON_FILE_NAME);
            if (jsonString == null) {return null;
            }

            Gson gson = new Gson();

            // 手順1: JSONの最も外側の配列 [ ... ] を読み取る
            // 型定義: QuizSpotListContainerの配列
            Type outerListType = new TypeToken<List<QuizSpotListContainer>>() {}.getType();
            List<QuizSpotListContainer> outerList = gson.fromJson(jsonString, outerListType);

            // JSONの構造が想定外なら、即座に処理を中断
            if (outerList == null || outerList.isEmpty() || outerList.get(0) == null) {
                return null;
            }

            // 手順2: 最初の要素から "quiz_spot_list" の中身（SpotQuizのリスト）を取り出す
            List<SpotQuiz> allQuizSets = outerList.get(0).getQuizSpotList();

            if (allQuizSets == null || allQuizSets.isEmpty()) {
                return null;
            }

            // 手順3: リストからランダムに1つのSpotQuizを選択して返す
            Random random = new Random();
            return allQuizSets.get(random.nextInt(allQuizSets.size()));
        }


        /**
         * assetsフォルダから指定されたJSONファイルの内容を文字列として読み込む
         */
        private static String loadJsonFromAsset(Context context, String fileName) {
            String json;
            try {
                AssetManager assetManager = context.getAssets();
                InputStream is = assetManager.open(fileName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }
    }

    // =========================================================================
    // JSONの特殊な構造を読み解くための「入れ物」クラス
    // =========================================================================

    /**
     * JSONの "quiz_spot_list" というキーを読み取るためのクラス
     */
    class QuizSpotListContainer {
        // JSONの "quiz_spot_list" キーと名前を一致させる
        private List<SpotQuiz> quiz_spot_list;

        public List<SpotQuiz> getQuizSpotList() {
            return quiz_spot_list;
        }
    }

}
