package com.example.englishlearningapp.view.features_home.dictionary;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DictionaryResponse {
    @SerializedName("word")
    private String word;

    @SerializedName("phonetic")
    private String phonetic;

    @SerializedName("phonetics")
    private List<Phonetic> phonetics;

    @SerializedName("meanings")
    private List<Meaning> meanings;

    @SerializedName("sourceUrls")
    private List<String> sourceUrls;

    // Getters
    public String getWord() {
        return word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public List<Phonetic> getPhonetics() {
        return phonetics;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public List<String> getSourceUrls() {
        return sourceUrls;
    }
}