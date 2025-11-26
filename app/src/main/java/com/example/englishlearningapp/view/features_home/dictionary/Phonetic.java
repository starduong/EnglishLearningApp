package com.example.englishlearningapp.view.features_home.dictionary;

import com.google.gson.annotations.SerializedName;

public class Phonetic {
    @SerializedName("text")
    private String text;

    @SerializedName("audio")
    private String audio;

    @SerializedName("sourceUrl")
    private String sourceUrl;

    @SerializedName("license")
    private License license;

    public String getText() {
        return text;
    }

    public String getAudio() {
        return audio;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public License getLicense() {
        return license;
    }
}