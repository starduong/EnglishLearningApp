package com.example.englishlearningapp.view.features_home.vocabulary;

import java.io.Serializable;
import java.util.List;

public class TopicVocabulary implements Serializable {
    private String topic;
    private List<Vocabulary> words;

    // Getters and Setters
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<Vocabulary> getWords() {
        return words;
    }

    public void setWords(List<Vocabulary> words) {
        this.words = words;
    }
}