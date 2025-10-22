package com.example.pbl_koshi_app.data; // ★パッケージ名はあなたのプロジェクトに合わせて変更してください

import java.util.List;

// 単一スポットに関する5問のクイズセット全体を保持するクラス
public class SpotQuiz {

    private String id;              // スポット固有の識別子 (KOSHI_SPOT_001など)
    private String spotName;        // スポット名 (誘導ボタンのテキストに使用)
    private List<QuizItem> quizList; // 5問分のQuizItemリスト

    // ----- GsonがJSONをオブジェクトに変換するために必要なメソッド -----

    // ★必須: データを取り出すためのGetterメソッド

    public String getId() {
        return id;
    }

    public String getSpotName() {
        return spotName;
    }

    public List<QuizItem> getQuizList() {
        return quizList;
    }
}
