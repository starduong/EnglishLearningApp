package com.example.englishlearningapp.view.features_home.listening;

import java.io.Serializable;
import java.util.List;

public class ArticleListening implements Serializable {
    public String id;
    public String title;
    public String level;
    public int duration;
    public String audio;
    public String thumbnail;
    public List<ContentSegmentListening> content;
    public List<QuestionData> questions;

    public static class QuestionData implements Serializable {
        public int no;
        public String question;
        public Options options;
        public String answer;
    }

    public static class Options implements Serializable {
        public String a;
        public String b;
        public String c;
    }

    public String getFullThumbnailPath() {
        return "listening/images/" + thumbnail;
    }
}
