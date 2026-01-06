package com.example.englishlearningapp.view.features_home.game;

import android.content.Context;
import android.content.SharedPreferences;

public class ScoreManager {
    
    private static final String PREF_NAME = "game_scores";
    private static final String KEY_HANGMAN_BEST_SCORE = "hangman_best_score";
    private static final String KEY_WORD_SCRAMBLE_BEST_SCORE = "word_scramble_best_score";
    private static final String KEY_HANGMAN_TOTAL_GAMES = "hangman_total_games";
    private static final String KEY_WORD_SCRAMBLE_TOTAL_GAMES = "word_scramble_total_games";
    private static final String KEY_HANGMAN_TOTAL_WINS = "hangman_total_wins";
    private static final String KEY_WORD_SCRAMBLE_TOTAL_WINS = "word_scramble_total_wins";
    
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    
    public ScoreManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }
    
    // Hangman Score Management
    public void saveHangmanScore(int score, boolean isWin) {
        // Update best score
        int currentBest = preferences.getInt(KEY_HANGMAN_BEST_SCORE, 0);
        if (score > currentBest) {
            editor.putInt(KEY_HANGMAN_BEST_SCORE, score);
        }
        
        // Update total games and wins
        int totalGames = preferences.getInt(KEY_HANGMAN_TOTAL_GAMES, 0);
        int totalWins = preferences.getInt(KEY_HANGMAN_TOTAL_WINS, 0);
        
        editor.putInt(KEY_HANGMAN_TOTAL_GAMES, totalGames + 1);
        if (isWin) {
            editor.putInt(KEY_HANGMAN_TOTAL_WINS, totalWins + 1);
        }
        
        editor.apply();
    }
    
    public int getHangmanBestScore() {
        return preferences.getInt(KEY_HANGMAN_BEST_SCORE, 0);
    }
    
    public int getHangmanTotalGames() {
        return preferences.getInt(KEY_HANGMAN_TOTAL_GAMES, 0);
    }
    
    public int getHangmanTotalWins() {
        return preferences.getInt(KEY_HANGMAN_TOTAL_WINS, 0);
    }
    
    // Word Scramble Score Management
    public void saveWordScrambleScore(int score, boolean isWin) {
        // Update best score
        int currentBest = preferences.getInt(KEY_WORD_SCRAMBLE_BEST_SCORE, 0);
        if (score > currentBest) {
            editor.putInt(KEY_WORD_SCRAMBLE_BEST_SCORE, score);
        }
        
        // Update total games and wins
        int totalGames = preferences.getInt(KEY_WORD_SCRAMBLE_TOTAL_GAMES, 0);
        int totalWins = preferences.getInt(KEY_WORD_SCRAMBLE_TOTAL_WINS, 0);
        
        editor.putInt(KEY_WORD_SCRAMBLE_TOTAL_GAMES, totalGames + 1);
        if (isWin) {
            editor.putInt(KEY_WORD_SCRAMBLE_TOTAL_WINS, totalWins + 1);
        }
        
        editor.apply();
    }
    
    public int getWordScrambleBestScore() {
        return preferences.getInt(KEY_WORD_SCRAMBLE_BEST_SCORE, 0);
    }
    
    public int getWordScrambleTotalGames() {
        return preferences.getInt(KEY_WORD_SCRAMBLE_TOTAL_GAMES, 0);
    }
    
    public int getWordScrambleTotalWins() {
        return preferences.getInt(KEY_WORD_SCRAMBLE_TOTAL_WINS, 0);
    }
    
    // Utility Methods
    public double getHangmanWinRate() {
        int totalGames = getHangmanTotalGames();
        if (totalGames == 0) return 0.0;
        return (double) getHangmanTotalWins() / totalGames * 100;
    }
    
    public double getWordScrambleWinRate() {
        int totalGames = getWordScrambleTotalGames();
        if (totalGames == 0) return 0.0;
        return (double) getWordScrambleTotalWins() / totalGames * 100;
    }
    
    // Clear all data (for testing)
    public void clearAllData() {
        editor.clear();
        editor.apply();
    }
}
