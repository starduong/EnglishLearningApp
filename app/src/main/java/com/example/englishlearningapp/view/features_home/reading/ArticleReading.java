package com.example.englishlearningapp.view.features_home.reading;

import com.google.gson.annotations.SerializedName;

public class ArticleReading {
    @SerializedName("article_id")
    private int articleId;

    @SerializedName("title")
    private String title;

    @SerializedName("level")
    private String level;

    @SerializedName("image")
    private String image;

    @SerializedName("audio")
    private String audio;

    @SerializedName("duration")
    private String duration;

    @SerializedName("created_date")
    private String createdDate;

    @SerializedName("content")
    private ContentReading content;

    // Getters and Setters
    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public ContentReading getContent() {
        return content;
    }

    public void setContent(ContentReading content) {
        this.content = content;
    }
}