package com.example.englishlearningapp.view.features_home.vocabulary;

import java.io.Serializable;
import java.util.List;

public class Vocabulary implements Serializable {
    private String word;
    private String cefr;
    private String pronunciation_uk;
    private String pronunciation_us;
    private String audio_uk;
    private String audio_us;
    private String image;
    private List<Meaning> meaning;
    private String assetsBasePath;

    // Getters and Setters
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getCefr() {
        return cefr;
    }

    public void setCefr(String cefr) {
        this.cefr = cefr;
    }

    public String getPronunciation_uk() {
        return pronunciation_uk;
    }

    public void setPronunciation_uk(String pronunciation_uk) {
        this.pronunciation_uk = pronunciation_uk;
    }

    public String getPronunciation_us() {
        return pronunciation_us;
    }

    public void setPronunciation_us(String pronunciation_us) {
        this.pronunciation_us = pronunciation_us;
    }

    public String getAudio_uk() {
        return audio_uk;
    }

    public void setAudio_uk(String audio_uk) {
        this.audio_uk = audio_uk;
    }

    public String getAudio_us() {
        return audio_us;
    }

    public void setAudio_us(String audio_us) {
        this.audio_us = audio_us;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Meaning> getMeaning() {
        return meaning;
    }

    public void setMeaning(List<Meaning> meaning) {
        this.meaning = meaning;
    }

    public String getAssetsBasePath() {
        return assetsBasePath;
    }

    public void setAssetsBasePath(String assetsBasePath) {
        this.assetsBasePath = assetsBasePath;
    }

    // Helper methods to get full asset paths
    public String getFullImagePath() {
        if (assetsBasePath != null && image != null) {
            return assetsBasePath + "images/" + image;
        }
        return null;
    }

    public String getFullAudioUkPath() {
        if (assetsBasePath != null && audio_uk != null) {
            return assetsBasePath + "audio/" + audio_uk;
        }
        return null;
    }

    public String getFullAudioUsPath() {
        if (assetsBasePath != null && audio_us != null) {
            return assetsBasePath + "audio/" + audio_us;
        }
        return null;
    }

    public static class Meaning implements Serializable {
        private String en;
        private String vi;
        private String example_en;
        private String example_vi;

        // Getters and Setters
        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }

        public String getVi() {
            return vi;
        }

        public void setVi(String vi) {
            this.vi = vi;
        }

        public String getExample_en() {
            return example_en;
        }

        public void setExample_en(String example_en) {
            this.example_en = example_en;
        }

        public String getExample_vi() {
            return example_vi;
        }

        public void setExample_vi(String example_vi) {
            this.example_vi = example_vi;
        }
    }
}