package com.example.englishlearningapp.view.features_home.reading;

import com.google.gson.annotations.SerializedName;

public class VocabularyReading {
    @SerializedName("word")
    private String word;

    @SerializedName("definition")
    private String definition;

    // Getters and Setters
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}