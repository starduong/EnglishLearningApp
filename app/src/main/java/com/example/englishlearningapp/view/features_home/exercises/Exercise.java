package com.example.englishlearningapp.view.features_home.exercises;

import java.util.List;

public class Exercise {
    private String id;
    private String topicId;
    private String title;
    private ExerciseType type;
    private Difficulty difficulty;
    private String passage; // Đoạn văn gốc
    private String passageWithBlanks; // Đoạn văn có chỗ trống
    private List<String> options; // Danh sách từ để chọn
    private List<String> correctAnswers; // Đáp án đúng theo thứ tự
    private List<String> hints; // Gợi ý cho từng chỗ trống
    private String source; // Nguồn bài viết (báo, sách...)
    private int points; // Điểm thưởng khi hoàn thành
    private boolean isCompleted;
    private float progress; // Tiến độ hoàn thành (0-100%)
    
    public enum ExerciseType {
        FILL_IN_BLANKS("Fill in the blanks"),
        SENTENCE_ARRANGEMENT("Arrange the sentence");
        
        private final String displayName;
        
        ExerciseType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    public enum Difficulty {
        EASY(1, "🍃 Easy", 5),
        MEDIUM(2, "⚡ Medium", 10),
        HARD(3, "🔥 Hard", 15);
        
        private final int level;
        private final String displayName;
        private final int points;
        
        Difficulty(int level, String displayName, int points) {
            this.level = level;
            this.displayName = displayName;
            this.points = points;
        }
        
        public int getLevel() { return level; }
        public String getDisplayName() { return displayName; }
        public int getPoints() { return points; }
    }
    
    // Constructors
    public Exercise() {}
    
    public Exercise(String id, String topicId, String title, ExerciseType type, Difficulty difficulty) {
        this.id = id;
        this.topicId = topicId;
        this.title = title;
        this.type = type;
        this.difficulty = difficulty;
        this.points = difficulty.getPoints();
        this.isCompleted = false;
        this.progress = 0f;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTopicId() { return topicId; }
    public void setTopicId(String topicId) { this.topicId = topicId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public ExerciseType getType() { return type; }
    public void setType(ExerciseType type) { this.type = type; }
    
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    
    public String getPassage() { return passage; }
    public void setPassage(String passage) { this.passage = passage; }
    
    public String getPassageWithBlanks() { return passageWithBlanks; }
    public void setPassageWithBlanks(String passageWithBlanks) { this.passageWithBlanks = passageWithBlanks; }
    
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    
    public List<String> getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(List<String> correctAnswers) { this.correctAnswers = correctAnswers; }
    
    public List<String> getHints() { return hints; }
    public void setHints(List<String> hints) { this.hints = hints; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    
    public float getProgress() { return progress; }
    public void setProgress(float progress) { this.progress = progress; }
    
    // Helper methods
    public int getBlankCount() {
        if (passageWithBlanks == null) return 0;
        return passageWithBlanks.split("____").length - 1;
    }
}
