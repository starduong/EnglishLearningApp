package com.example.englishlearningapp.view.features_home.listening;

import java.io.Serializable;
import java.util.List;

public class TopicListening implements Serializable {
    public String topic;
    public String icon;
    public int number_of_articles;
    public List<ArticleListening> articles;

    public String getFullImagePath() {
        return "listening/images/" + icon;
    }
}