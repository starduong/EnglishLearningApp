package com.example.englishlearningapp.view.features_home.reading;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ContentReading {
    @SerializedName("reading_text_en")
    private List<String> readingTextEn;

    @SerializedName("reading_text_vi")
    private List<String> readingTextVi;

    @SerializedName("vocabulary")
    private List<VocabularyReading> vocabulary;

    @SerializedName("exercises")
    private List<ExerciseReading> exercises;

    // Getters and Setters
    public List<String> getReadingTextEn() {
        return readingTextEn;
    }

    public void setReadingTextEn(List<String> readingTextEn) {
        this.readingTextEn = readingTextEn;
    }

    public List<String> getReadingTextVi() {
        return readingTextVi;
    }

    public void setReadingTextVi(List<String> readingTextVi) {
        this.readingTextVi = readingTextVi;
    }

    public List<VocabularyReading> getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(List<VocabularyReading> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public List<ExerciseReading> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExerciseReading> exercises) {
        this.exercises = exercises;
    }
}