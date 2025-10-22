package com.example.pbl_koshi_app.data;

import java.util.Map;

//JSONの一問分の構造に対応するモデルクラス
public class QuizItem{

    private int questionNumber;
    private String questionText;
    private Map<String, String> options; //選択肢{"A":"テキスト", ...}
    private String correctOption;
    private String commentary;
    private String relatedSpotId;

    //GsonがJSONをオブジェクトに変換するために必要なメソッド
    //データを取り出すためのゲッターメソッド(JSON変換後に使用)

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionText(){
        return questionText;
    }

    public Map<String, String> getOptions(){
        return options;
    }

    public String getCorrectOption(){
        return correctOption;
    }

    public String getCommentary(){
        return commentary;
    }

    public String getRelatedSpotId(){
        return relatedSpotId;
    }

}