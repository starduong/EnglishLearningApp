package com.example.englishlearningapp.view.features_home.dictionary;

import com.google.gson.annotations.SerializedName;

public class License {
    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}