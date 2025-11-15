package com.example.englishlearningapp.view.features_home.listening;

import java.io.Serializable;
import java.util.List;

public class ArticleListening implements Serializable {
    public int id;
    public String level;
    public String title;
    public String audio;
    public String thumbnail;
    public int duration;
    public List<ContentSegmentListening> content;

    public String getFullThumbnailPath() {
        return "listening/images/" + thumbnail;
    }
}