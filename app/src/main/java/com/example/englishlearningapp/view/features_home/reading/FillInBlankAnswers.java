package com.example.englishlearningapp.view.features_home.reading;

import com.google.gson.annotations.SerializedName;

public class FillInBlankAnswers {
    @SerializedName("1")
    private String answer1;

    @SerializedName("2")
    private String answer2;

    @SerializedName("3")
    private String answer3;

    @SerializedName("4")
    private String answer4;

    @SerializedName("5")
    private String answer5;

    // Getters and Setters
    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getAnswer5() {
        return answer5;
    }

    public void setAnswer5(String answer5) {
        this.answer5 = answer5;
    }
}