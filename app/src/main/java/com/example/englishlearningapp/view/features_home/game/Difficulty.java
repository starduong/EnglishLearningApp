package com.example.englishlearningapp.view.features_home.game;

public enum Difficulty {
    EASY(0, 8, "Easy - 8 Lives"),
    MEDIUM(1, 6, "Medium - 6 Lives"), 
    HARD(2, 4, "Hard - 4 Lives");
    
    private final int position;
    private final int maxHearts;
    private final String description;
    
    Difficulty(int position, int maxHearts, String description) {
        this.position = position;
        this.maxHearts = maxHearts;
        this.description = description;
    }
    
    public int getPosition() {
        return position;
    }
    
    public int getMaxHearts() {
        return maxHearts;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static Difficulty fromPosition(int position) {
        for (Difficulty difficulty : values()) {
            if (difficulty.position == position) {
                return difficulty;
            }
        }
        return MEDIUM; // Default fallback
    }
}
