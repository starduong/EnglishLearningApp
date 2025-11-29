package com.example.englishlearningapp.view.features_home.reading;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReadingList {
    @SerializedName("reading_list")
    private ReadingData readingData;

    public ReadingData getReadingData() {
        return readingData;
    }

    public void setReadingData(ReadingData readingData) {
        this.readingData = readingData;
    }

    // Helper method để lấy articles trực tiếp
    public List<ArticleReading> getArticles() {
        return readingData != null ? readingData.getArticles() : null;
    }

    public static class ReadingData {
        @SerializedName("total_articles")
        private int totalArticles;

        @SerializedName("articles")
        private List<ArticleReading> articles;

        public int getTotalArticles() {
            return totalArticles;
        }

        public void setTotalArticles(int totalArticles) {
            this.totalArticles = totalArticles;
        }

        public List<ArticleReading> getArticles() {
            return articles;
        }

        public void setArticles(List<ArticleReading> articles) {
            this.articles = articles;
        }
    }
}