package com.example.englishlearningapp.view.features_home.reading;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExerciseReading {
    @SerializedName("exercise_id")
    private int exerciseId;

    @SerializedName("type")
    private String type;

    @SerializedName("title")
    private String title;

    @SerializedName("questions")
    private List<MultipleChoiceQuestion> questions;

    @SerializedName("text")
    private String text;

    @SerializedName("word_bank")
    private List<String> wordBank;

    @SerializedName("answers")
    private FillInBlankAnswers answers;

    // Getters and Setters
    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MultipleChoiceQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MultipleChoiceQuestion> questions) {
        this.questions = questions;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getWordBank() {
        return wordBank;
    }

    public void setWordBank(List<String> wordBank) {
        this.wordBank = wordBank;
    }

    public FillInBlankAnswers getAnswers() {
        return answers;
    }

    public void setAnswers(FillInBlankAnswers answers) {
        this.answers = answers;
    }
}