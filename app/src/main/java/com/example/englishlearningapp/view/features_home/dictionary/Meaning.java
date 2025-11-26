package com.example.englishlearningapp.view.features_home.dictionary;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Meaning {
    @SerializedName("partOfSpeech")
    private String partOfSpeech;

    @SerializedName("definitions")
    private List<Definition> definitions;

    @SerializedName("synonyms")
    private List<String> synonyms;

    @SerializedName("antonyms")
    private List<String> antonyms;

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public List<Definition> getDefinitions() {
        return definitions;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public List<String> getAntonyms() {
        return antonyms;
    }
}