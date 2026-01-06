package com.example.englishlearningapp.view.features_home.exercises;

public class Topic {
    private String id;
    private String title;
    private String description;
    private String imageResource;
    private int totalExercises;
    private int completedExercises;
    private boolean isUnlocked;
    
    public Topic() {}
    
    public Topic(String id, String title, String description, String imageResource, int totalExercises) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
        this.totalExercises = totalExercises;
        this.completedExercises = 0;
        this.isUnlocked = true; // First topic is always unlocked
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImageResource() { return imageResource; }
    public void setImageResource(String imageResource) { this.imageResource = imageResource; }
    
    public int getTotalExercises() { return totalExercises; }
    public void setTotalExercises(int totalExercises) { this.totalExercises = totalExercises; }
    
    public int getCompletedExercises() { return completedExercises; }
    public void setCompletedExercises(int completedExercises) { this.completedExercises = completedExercises; }
    
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
    
    // Helper methods
    public int getProgress() {
        if (totalExercises == 0) return 0;
        return (completedExercises * 100) / totalExercises;
    }
    
    public String getProgressText() {
        return completedExercises + "/" + totalExercises;
    }
    
    public boolean isCompleted() {
        return completedExercises >= totalExercises;
    }
}
