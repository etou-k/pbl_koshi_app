package com.example.pbl_koshi_app.data;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pbl_koshi_app.R;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private TextView textProgress, textQuestion, textResultStatus, textCommentary, textFinalScore;
    private LinearLayout layoutOptions, layoutResult, finalResultArea, quizArea;
    private Button buttonNext, buttonToSpotDetail;
    private Button[] optionButtons = new Button[4];
    private final String[] optionKeys = {"A", "B", "C", "D"};

    private SpotQuiz currentQuizSet;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initializeViews();

        currentQuizSet = QuizDataLoader.getRandomQuizSet(this);
        if (currentQuizSet == null || currentQuizSet.getQuizList() == null || currentQuizSet.getQuizList().isEmpty()) {
            Toast.makeText(this, "クイズデータの読み込み、または解析に失敗しました。", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        displayQuestion();
        setEventListeners();
    }

    private void initializeViews() {
        textProgress = findViewById(R.id.text_progress);
        textQuestion = findViewById(R.id.text_question);
        quizArea = findViewById(R.id.quiz_area);
        layoutOptions = findViewById(R.id.layout_options);
        optionButtons[0] = findViewById(R.id.button_option_a);
        optionButtons[1] = findViewById(R.id.button_option_b);
        optionButtons[2] = findViewById(R.id.button_option_c);
        optionButtons[3] = findViewById(R.id.button_option_d);
        layoutResult = findViewById(R.id.layout_result);
        textResultStatus = findViewById(R.id.text_result_status);
        textCommentary = findViewById(R.id.text_commentary);
        buttonNext = findViewById(R.id.button_next);
        finalResultArea = findViewById(R.id.final_result_area);
        textFinalScore = findViewById(R.id.text_final_score);
        buttonToSpotDetail = findViewById(R.id.button_to_spot_detail);

        layoutResult.setVisibility(View.GONE);
        finalResultArea.setVisibility(View.GONE);
    }

    private void setEventListeners() {
        for (int i = 0; i < optionButtons.length; i++) {
            final int index = i;
            optionButtons[i].setOnClickListener(v -> handleAnswer(optionKeys[index]));
        }
        buttonNext.setOnClickListener(v -> handleNextStep());
        buttonToSpotDetail.setOnClickListener(v -> goToSpotDetail());
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= currentQuizSet.getQuizList().size()) {
            showFinalResult();
            return;
        }
        QuizItem currentItem = currentQuizSet.getQuizList().get(currentQuestionIndex);
        layoutResult.setVisibility(View.GONE);
        for (Button button : optionButtons) {
            button.setEnabled(true);
            button.setBackgroundColor(Color.LTGRAY);
        }
        textProgress.setText(currentItem.getQuestionNumber() + " / " + currentQuizSet.getQuizList().size() + " 問目");
        textQuestion.setText(currentItem.getQuestionText());
        Map<String, String> options = currentItem.getOptions();
        for (int i = 0; i < optionKeys.length; i++) {
            optionButtons[i].setText(options.get(optionKeys[i]));
        }
    }

    private void handleAnswer(String selectedKey) {
        QuizItem currentItem = currentQuizSet.getQuizList().get(currentQuestionIndex);
        String correctKey = currentItem.getCorrectOption();
        boolean isCorrect = selectedKey.equals(correctKey);

        if (isCorrect) {
            score++;
            textResultStatus.setText("正解！");
        } else {
            textResultStatus.setText("不正解…");
        }
        textCommentary.setText(currentItem.getCommentary());

        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setEnabled(false);
            if (optionKeys[i].equals(correctKey)) {
                optionButtons[i].setBackgroundColor(Color.GREEN);
            } else if (optionKeys[i].equals(selectedKey)) {
                optionButtons[i].setBackgroundColor(Color.RED);
            }
        }
        layoutResult.setVisibility(View.VISIBLE);
    }

    private void handleNextStep() {
        currentQuestionIndex++;
        if (currentQuestionIndex < currentQuizSet.getQuizList().size()) {
            displayQuestion();
        } else {
            showFinalResult();
        }
    }

    private void showFinalResult() {        quizArea.setVisibility(View.GONE);
        finalResultArea.setVisibility(View.VISIBLE);
        textFinalScore.setText(score + " / " + currentQuizSet.getQuizList().size() + " 問正解");

        // ★★★ ここからが追加する処理 ★★★

        // 1. このクイズセットに関連するスポットのIDを取得する
        //    (get(0)で最初の問題からIDを取得する前提)
        String spotId = currentQuizSet.getQuizList().get(0).getRelatedSpotId();

        // 2. SpotMasterを使って、スポットIDからSpotオブジェクトを取得する
        //    SpotMasterとSpotクラスは、dataパッケージまたはルートパッケージにある必要があります
        com.example.pbl_koshi_app.Spot currentSpot = com.example.pbl_koshi_app.data.SpotMaster.getInstance(this).findSpotById(spotId);

        // 3. スポット情報が取得できたか確認し、ボタンのテキストを設定する
        if (currentSpot != null) {
            // strings.xmlからフォーマット文字列（"%1$s の詳細を見る"）を取得
            String buttonTextFormat = getString(R.string.go_to_spot_detail_button);

            // フォーマット文字列にスポット名を埋め込む
            String buttonText = String.format(buttonTextFormat, currentSpot.getName());

            // ボタンに生成したテキストをセット
            buttonToSpotDetail.setText(buttonText);
        } else {
            // もしスポット情報が見つからなかった場合の代替テキスト
            buttonToSpotDetail.setText("スポットの詳細を見る");
        }
        // ★★★ 追加処理はここまで ★★★
    }


    /**
     * スポット詳細画面へ遷移する（誘導）
     * 呼び出し元にスポットIDを返して終了する
     */
    private void goToSpotDetail() {
        // クイズ対象のスポットIDを取得
        String spotId = currentQuizSet.getQuizList().get(0).getRelatedSpotId();

        // 呼び出し元（HomeFragment）に返すためのIntentを作成
        Intent resultIntent = new Intent();
        // "SPOT_ID"というキーで、取得したスポットIDをセット
        resultIntent.putExtra("SPOT_ID", spotId);

        // アクティビティが成功したことと、返すデータ(resultIntent)をセット
        setResult(RESULT_OK, resultIntent);

        // このアクティビティを終了する
        finish();
    }


    // --- データローダー部分を完全に修正 ---
    private static class QuizDataFile {
        private List<SpotQuiz> quiz_spot_list;
        public List<SpotQuiz> getQuizSpotList() { return quiz_spot_list; }
    }

    public static class QuizDataLoader {
        private static final String JSON_FILE_NAME = "koshi_quiz_data.json";

        public static SpotQuiz getRandomQuizSet(Context context) {
            Gson gson = new Gson();
            try (InputStream is = context.getAssets().open(JSON_FILE_NAME);
                 Reader reader = new InputStreamReader(is)) {

                QuizDataFile dataFile = gson.fromJson(reader, QuizDataFile.class);

                if (dataFile == null || dataFile.getQuizSpotList() == null || dataFile.getQuizSpotList().isEmpty()) {
                    return null;
                }
                List<SpotQuiz> allQuizSets = dataFile.getQuizSpotList();
                Random random = new Random();
                return allQuizSets.get(random.nextInt(allQuizSets.size()));

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
