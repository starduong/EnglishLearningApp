package com.example.englishlearningapp.data.model;

import java.io.Serializable;
import java.util.Date;

public class Word implements Serializable {
    private String id;
    private String userId;          // Khóa ngoại đến User
    private String englishWord;     // Từ tiếng Anh
    private String pronunciation;   // Phiên âm
    private String vietnameseMeaning; // Nghĩa tiếng Việt
    private String englishDefinition; // Định nghĩa tiếng Anh
    private String exampleSentence; // Ví dụ
    private String exampleTranslation; // Dịch ví dụ
    private String partOfSpeech;    // Loại từ
    private String synonyms;        // Từ đồng nghĩa
    private String antonyms;        // Từ trái nghĩa
    private String tags;           // Thẻ phân loại (dấu phẩy phân cách)
    private String imageUrl;       // URL ảnh minh họa
    private String audioUrl;       // URL phát âm
    private int difficultyLevel;   // Độ khó: 1-Dễ, 2-Trung bình, 3-Khó
    private int masteryLevel;      // Mức độ ghi nhớ: 0-Chưa học, 1-Biết sơ, 2-Nhớ, 3-Thành thạo
    private long addedDate;        // Ngày thêm vào (timestamp)
    private long lastReviewed;     // Lần ôn tập cuối (timestamp)
    private long nextReviewDate;   // Ngày ôn tiếp theo (timestamp)
    private int reviewCount;       // Số lần đã ôn
    private int correctCount;      // Số lần trả lời đúng
    private int wrongCount;        // Số lần trả lời sai
    private boolean isFavorite;    // Đánh dấu yêu thích
    private String notes;          // Ghi chú cá nhân
    private int priority;          // Độ ưu tiên học (1-5)
    private String source;         // Nguồn học (app nào, bài nào)

    // Constants
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_HARD = 3;

    public static final int MASTERY_NEW = 0;
    public static final int MASTERY_BEGINNER = 1;
    public static final int MASTERY_INTERMEDIATE = 2;
    public static final int MASTERY_MASTERED = 3;

    public Word() {
        this.addedDate = System.currentTimeMillis();
        this.difficultyLevel = DIFFICULTY_MEDIUM;
        this.masteryLevel = MASTERY_NEW;
        this.reviewCount = 0;
        this.correctCount = 0;
        this.wrongCount = 0;
        this.isFavorite = false;
        this.priority = 3;
        this.lastReviewed = 0;
        this.nextReviewDate = calculateNextReviewDate();
    }

    public Word(String id, String userId, String englishWord, String vietnameseMeaning) {
        this();
        this.id = id;
        this.userId = userId;
        this.englishWord = englishWord;
        this.vietnameseMeaning = vietnameseMeaning;
    }

    // Constructor đầy đủ
    public Word(String id, String userId, String englishWord, String pronunciation,
                String vietnameseMeaning, String englishDefinition, String exampleSentence,
                String exampleTranslation, String partOfSpeech, String synonyms,
                String antonyms, String tags, String imageUrl, String audioUrl,
                int difficultyLevel, int masteryLevel, long addedDate, long lastReviewed,
                long nextReviewDate, int reviewCount, int correctCount, int wrongCount,
                boolean isFavorite, String notes, int priority, String source) {
        this.id = id;
        this.userId = userId;
        this.englishWord = englishWord;
        this.pronunciation = pronunciation;
        this.vietnameseMeaning = vietnameseMeaning;
        this.englishDefinition = englishDefinition;
        this.exampleSentence = exampleSentence;
        this.exampleTranslation = exampleTranslation;
        this.partOfSpeech = partOfSpeech;
        this.synonyms = synonyms;
        this.antonyms = antonyms;
        this.tags = tags;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.difficultyLevel = difficultyLevel;
        this.masteryLevel = masteryLevel;
        this.addedDate = addedDate;
        this.lastReviewed = lastReviewed;
        this.nextReviewDate = nextReviewDate;
        this.reviewCount = reviewCount;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.isFavorite = isFavorite;
        this.notes = notes;
        this.priority = priority;
        this.source = source;
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public void setEnglishWord(String englishWord) {
        this.englishWord = englishWord;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getVietnameseMeaning() {
        return vietnameseMeaning;
    }

    public void setVietnameseMeaning(String vietnameseMeaning) {
        this.vietnameseMeaning = vietnameseMeaning;
    }

    public String getEnglishDefinition() {
        return englishDefinition;
    }

    public void setEnglishDefinition(String englishDefinition) {
        this.englishDefinition = englishDefinition;
    }

    public String getExampleSentence() {
        return exampleSentence;
    }

    public void setExampleSentence(String exampleSentence) {
        this.exampleSentence = exampleSentence;
    }

    public String getExampleTranslation() {
        return exampleTranslation;
    }

    public void setExampleTranslation(String exampleTranslation) {
        this.exampleTranslation = exampleTranslation;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    public String getAntonyms() {
        return antonyms;
    }

    public void setAntonyms(String antonyms) {
        this.antonyms = antonyms;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(int masteryLevel) {
        this.masteryLevel = masteryLevel;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public long getLastReviewed() {
        return lastReviewed;
    }

    public void setLastReviewed(long lastReviewed) {
        this.lastReviewed = lastReviewed;
    }

    public long getNextReviewDate() {
        return nextReviewDate;
    }

    public void setNextReviewDate(long nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    // Helper methods
    public double getSuccessRate() {
        if (reviewCount == 0) return 0.0;
        return (double) correctCount / reviewCount * 100;
    }

    public boolean needsReview() {
        return System.currentTimeMillis() >= nextReviewDate;
    }

    public int getDaysSinceAdded() {
        long diff = System.currentTimeMillis() - addedDate;
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public void incrementReview(boolean isCorrect) {
        this.reviewCount++;
        if (isCorrect) {
            this.correctCount++;
            this.masteryLevel = Math.min(MASTERY_MASTERED, this.masteryLevel + 1);
        } else {
            this.wrongCount++;
            this.masteryLevel = Math.max(MASTERY_BEGINNER, this.masteryLevel - 1);
        }
        this.lastReviewed = System.currentTimeMillis();
        this.nextReviewDate = calculateNextReviewDate();
    }

    private long calculateNextReviewDate() {
        long currentTime = System.currentTimeMillis();
        int daysToAdd = 0;

        // Spaced repetition algorithm đơn giản
        switch (masteryLevel) {
            case MASTERY_NEW:
                daysToAdd = 1; // Hôm sau ôn lại
                break;
            case MASTERY_BEGINNER:
                daysToAdd = 3; // 3 ngày sau
                break;
            case MASTERY_INTERMEDIATE:
                daysToAdd = 7; // 1 tuần sau
                break;
            case MASTERY_MASTERED:
                daysToAdd = 30; // 1 tháng sau
                break;
        }

        return currentTime + (daysToAdd * 24 * 60 * 60 * 1000L);
    }

    public String getMasteryLevelText() {
        switch (masteryLevel) {
            case MASTERY_NEW:
                return "Mới";
            case MASTERY_BEGINNER:
                return "Sơ cấp";
            case MASTERY_INTERMEDIATE:
                return "Trung cấp";
            case MASTERY_MASTERED:
                return "Thành thạo";
            default:
                return "Không xác định";
        }
    }

    public String getDifficultyText() {
        switch (difficultyLevel) {
            case DIFFICULTY_EASY:
                return "Dễ";
            case DIFFICULTY_MEDIUM:
                return "Trung bình";
            case DIFFICULTY_HARD:
                return "Khó";
            default:
                return "Không xác định";
        }
    }

    public String[] getTagArray() {
        if (tags == null || tags.isEmpty()) return new String[0];
        return tags.split(",");
    }

}