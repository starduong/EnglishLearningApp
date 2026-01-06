package com.example.englishlearningapp.view.features_home.game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.englishlearningapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WordScrambleActivity extends AppCompatActivity {

    // UI Components
    private TextView tvScore;
    private TextView tvTargetWord;
    private TextView tvCategory;
    private LinearLayout scrambledLettersContainer;
    private LinearLayout answerContainer;
    private MaterialButton btnHelp;
    private MaterialButton btnExit;
    private MaterialButton btnShuffle;
    private MaterialButton btnClear;
    private TextView tvLevel;
    
    // Game State
    private int score = 0;
    private int gamesWon = 0;
    private String currentWord = "";
    private String currentCategory = "";
    private String currentDifficulty = "MEDIUM";
    private List<Character> scrambledLetters = new ArrayList<>();
    private List<Character> answerLetters = new ArrayList<>();
    private int helpUsesLeft = 2;
    private int gamesPerLevel = 5;
    
    // Vibrator for feedback
    private Vibrator vibrator;
    
    // Score Manager
    private ScoreManager scoreManager;
    
    // Word database with categories (reuse from Hangman)
    private String[] words = {
        "CAT", "DOG", "BIRD", "FISH", "TIGER", "LION", "BEAR",           // Animals
        "APPLE", "BREAD", "WATER", "MILK", "CAKE",                        // Food
        "RED", "BLUE", "GREEN", "YELLOW",                                 // Colors
        "BOOK", "PHONE", "CAR", "HOUSE", "TREE", "CHAIR", "TABLE", "PEN", // Objects
        "SUN", "MOON", "STAR", "CLOUD", "RAIN", "WIND", "FIRE", "EARTH", "LOVE", "HOPE", "DREAM" // Nature/Life
    };
    
    private String[] categories = {
        "Animals", "Animals", "Animals", "Animals", "Animals", "Animals", "Animals",
        "Food", "Food", "Food", "Food", "Food",
        "Colors", "Colors", "Colors", "Colors",
        "Objects", "Objects", "Objects", "Objects", "Objects", "Objects", "Objects", "Objects",
        "Nature/Life", "Nature/Life", "Nature/Life", "Nature/Life", "Nature/Life", "Nature/Life", "Nature/Life", "Nature/Life", "Nature/Life", "Nature/Life", "Nature/Life"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_scramble);
        
        // Initialize vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        // Initialize Score Manager
        scoreManager = new ScoreManager(this);
        
        // Get difficulty from intent
        currentDifficulty = getIntent().getStringExtra("DIFFICULTY");
        if (currentDifficulty == null) {
            currentDifficulty = "MEDIUM";
        }
        
        initViews();
        setupClickListeners();
        processDifficultyFromIntent();
        startNewGame();
        
        Log.d("WordScrambleGame", "Game started with difficulty: " + currentDifficulty);
    }
    
    private void initViews() {
        // Header components
        tvScore = findViewById(R.id.tvScore);
        tvLevel = findViewById(R.id.tvLevel);
        btnHelp = findViewById(R.id.btnHelp);
        btnExit = findViewById(R.id.btnExit);
        
        // Game components
        tvTargetWord = findViewById(R.id.tvTargetWord);
        tvCategory = findViewById(R.id.tvCategory);
        scrambledLettersContainer = findViewById(R.id.scrambledLettersContainer);
        answerContainer = findViewById(R.id.answerContainer);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnClear = findViewById(R.id.btnClear);
        
        Log.d("WordScrambleGame", "Views initialized");
    }
    
    private void setupClickListeners() {
        btnExit.setOnClickListener(v -> finish());
        
        btnHelp.setOnClickListener(v -> {
            if (helpUsesLeft > 0) {
                useHelp();
            } else {
                showToast("No more helps available!");
            }
        });
        
        btnShuffle.setOnClickListener(v -> shuffleScrambledLetters());
        
        btnClear.setOnClickListener(v -> clearAnswer());
    }
    
    private void processDifficultyFromIntent() {
        switch (currentDifficulty) {
            case "EASY":
                helpUsesLeft = 3;
                break;
            case "MEDIUM":
                helpUsesLeft = 2;
                break;
            case "HARD":
                helpUsesLeft = 1;
                break;
        }
        updateHelpButton();
    }
    
    private void startNewGame() {
        // Select random word
        Random random = new Random();
        int wordIndex = random.nextInt(words.length);
        currentWord = words[wordIndex];
        currentCategory = categories[wordIndex];
        
        // Reset game state
        scrambledLetters.clear();
        answerLetters.clear();
        
        // Create scrambled letters
        for (char c : currentWord.toCharArray()) {
            scrambledLetters.add(c);
        }
        Collections.shuffle(scrambledLetters);
        
        // Initialize answer with empty slots
        for (int i = 0; i < currentWord.length(); i++) {
            answerLetters.add(' ');
        }
        
        updateUI();
        updateCategoryDisplay();
        
        Log.d("WordScrambleGame", "New game started - Word: " + currentWord + ", Category: " + currentCategory);
    }
    
    private void updateUI() {
        updateScoreWithLevelDisplay();
        updateTargetWordDisplay();
        createScrambledLetterButtons();
        createAnswerSlots();
    }
    
    private void updateScoreWithLevelDisplay() {
        if (tvScore != null) {
            int gamesInCurrentLevel = gamesWon % gamesPerLevel;
            int nextLevelGames = gamesPerLevel - gamesInCurrentLevel;
            
            String levelInfo = "";
            if (!currentDifficulty.equals("HARD")) {
                String diffAbbrev = currentDifficulty.equals("EASY") ? "E" : 
                                   currentDifficulty.equals("MEDIUM") ? "M" : "H";
                levelInfo = " | " + diffAbbrev + "(" + nextLevelGames + ")";
            } else {
                levelInfo = " | H(Max)";
            }
            
            tvScore.setText("Pts: " + score + levelInfo);
        }
        
        if (tvLevel != null) {
            tvLevel.setText("Level: " + currentDifficulty);
        }
    }
    
    private void updateCategoryDisplay() {
        if (tvCategory != null) {
            tvCategory.setText("💡 Category: " + currentCategory);
        }
    }
    
    private void updateTargetWordDisplay() {
        if (tvTargetWord != null) {
            StringBuilder display = new StringBuilder();
            for (char c : answerLetters) {
                if (c == ' ') {
                    display.append("_ ");
                } else {
                    display.append(c).append(" ");
                }
            }
            tvTargetWord.setText(display.toString().trim());
        }
    }
    
    private void createScrambledLetterButtons() {
        scrambledLettersContainer.removeAllViews();
        
        for (int i = 0; i < scrambledLetters.size(); i++) {
            char letter = scrambledLetters.get(i);
            MaterialButton letterButton = createLetterButton(letter, i, true);
            scrambledLettersContainer.addView(letterButton);
        }
    }
    
    private void createAnswerSlots() {
        answerContainer.removeAllViews();
        
        for (int i = 0; i < answerLetters.size(); i++) {
            char letter = answerLetters.get(i);
            MaterialButton slotButton = createLetterButton(letter == ' ' ? ' ' : letter, i, false);
            answerContainer.addView(slotButton);
        }
    }
    
    private MaterialButton createLetterButton(char letter, int index, boolean isScrambled) {
        MaterialButton button = new MaterialButton(this);
        
        // Set button properties
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, 120);
        params.setMargins(8, 8, 8, 8);
        button.setLayoutParams(params);
        
        if (letter == ' ') {
            button.setText("");
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            button.setStrokeColorResource(R.color.gray);
            button.setStrokeWidth(4);
        } else {
            button.setText(String.valueOf(letter));
            button.setBackgroundColor(isScrambled ? 
                ContextCompat.getColor(this, R.color.blue) : 
                ContextCompat.getColor(this, R.color.green));
        }
        
        button.setTextColor(Color.WHITE);
        button.setTextSize(18);
        button.setCornerRadius(12);
        
        // Set click listener
        if (isScrambled) {
            button.setOnClickListener(v -> moveLetterToAnswer(index));
        } else {
            button.setOnClickListener(v -> moveLetterBackToScrambled(index));
        }
        
        return button;
    }
    
    private void moveLetterToAnswer(int scrambledIndex) {
        if (scrambledIndex >= scrambledLetters.size()) return;
        
        char letter = scrambledLetters.get(scrambledIndex);
        
        // Find first empty slot in answer
        for (int i = 0; i < answerLetters.size(); i++) {
            if (answerLetters.get(i) == ' ') {
                answerLetters.set(i, letter);
                scrambledLetters.set(scrambledIndex, ' '); // Mark as used
                
                // Add animation
                animateButton((MaterialButton) scrambledLettersContainer.getChildAt(scrambledIndex));
                
                updateUI();
                checkWin();
                return;
            }
        }
        
        showToast("No empty slots available!");
    }
    
    private void moveLetterBackToScrambled(int answerIndex) {
        if (answerIndex >= answerLetters.size()) return;
        
        char letter = answerLetters.get(answerIndex);
        if (letter == ' ') return;
        
        // Find original position in scrambled letters
        for (int i = 0; i < scrambledLetters.size(); i++) {
            if (scrambledLetters.get(i) == ' ') {
                scrambledLetters.set(i, letter);
                answerLetters.set(answerIndex, ' ');
                
                updateUI();
                return;
            }
        }
    }
    
    private void shuffleScrambledLetters() {
        // Only shuffle non-used letters
        List<Character> availableLetters = new ArrayList<>();
        List<Integer> emptyIndices = new ArrayList<>();
        
        for (int i = 0; i < scrambledLetters.size(); i++) {
            if (scrambledLetters.get(i) != ' ') {
                availableLetters.add(scrambledLetters.get(i));
                emptyIndices.add(i);
            }
        }
        
        Collections.shuffle(availableLetters);
        
        for (int i = 0; i < emptyIndices.size(); i++) {
            scrambledLetters.set(emptyIndices.get(i), availableLetters.get(i));
        }
        
        updateUI();
        
        // Vibration feedback
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(100);
        }
    }
    
    private void clearAnswer() {
        // Move all letters back to scrambled
        for (int i = 0; i < answerLetters.size(); i++) {
            char letter = answerLetters.get(i);
            if (letter != ' ') {
                // Find empty slot in scrambled
                for (int j = 0; j < scrambledLetters.size(); j++) {
                    if (scrambledLetters.get(j) == ' ') {
                        scrambledLetters.set(j, letter);
                        break;
                    }
                }
                answerLetters.set(i, ' ');
            }
        }
        
        updateUI();
        
        // Vibration feedback
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(150);
        }
    }
    
    private void useHelp() {
        if (helpUsesLeft <= 0) return;
        
        // Find first wrong or empty position
        for (int i = 0; i < answerLetters.size(); i++) {
            char correctLetter = currentWord.charAt(i);
            char currentLetter = answerLetters.get(i);
            
            if (currentLetter != correctLetter) {
                // Move wrong letter back if any
                if (currentLetter != ' ') {
                    for (int j = 0; j < scrambledLetters.size(); j++) {
                        if (scrambledLetters.get(j) == ' ') {
                            scrambledLetters.set(j, currentLetter);
                            break;
                        }
                    }
                }
                
                // Place correct letter
                answerLetters.set(i, correctLetter);
                
                // Remove correct letter from scrambled
                for (int j = 0; j < scrambledLetters.size(); j++) {
                    if (scrambledLetters.get(j) == correctLetter) {
                        scrambledLetters.set(j, ' ');
                        break;
                    }
                }
                
                helpUsesLeft--;
                updateHelpButton();
                updateUI();
                checkWin();
                
                showToast("Hint used! " + helpUsesLeft + " left");
                return;
            }
        }
    }
    
    private void updateHelpButton() {
        if (btnHelp != null) {
            btnHelp.setText("💡 (" + helpUsesLeft + ")");
            btnHelp.setEnabled(helpUsesLeft > 0);
        }
    }
    
    private void checkWin() {
        // Check if word is complete
        boolean isComplete = true;
        for (char c : answerLetters) {
            if (c == ' ') {
                isComplete = false;
                break;
            }
        }
        
        if (isComplete) {
            String playerAnswer = getPlayerAnswer();
            if (playerAnswer.equals(currentWord)) {
                onGameWin();
            } else {
                showToast("Not quite right! Try again.");
                // Vibration for wrong answer
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(new long[]{0, 200, 100, 200}, -1);
                }
            }
        }
    }
    
    private String getPlayerAnswer() {
        StringBuilder answer = new StringBuilder();
        for (char c : answerLetters) {
            answer.append(c);
        }
        return answer.toString();
    }
    
    private void onGameWin() {
        // Calculate points based on difficulty (new scoring system)
        int pointsEarned = 0;
        switch (currentDifficulty) {
            case "EASY":
                pointsEarned = 1;
                break;
            case "MEDIUM":
                pointsEarned = 5;
                break;
            case "HARD":
                pointsEarned = 10;
                break;
            default:
                pointsEarned = 5; // Default to medium
        }
        
        score += pointsEarned;
        gamesWon++;
        
        // Save score to ScoreManager
        scoreManager.saveWordScrambleScore(score, true);
        
        // Vibration for win
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{0, 100, 50, 100, 50, 200}, -1);
        }
        
        showToast("🎉 Correct! +" + pointsEarned + " points! Total: " + score);
        
        // Check level progression
        checkLevelProgression();
        
        Log.d("WordScrambleGame", "GAME WON - pointsEarned: " + pointsEarned + ", totalScore: " + score + ", gamesWon: " + gamesWon + ", difficulty: " + currentDifficulty);
        
        // Auto-continue after 1.5 seconds
        tvTargetWord.postDelayed(() -> startNewGame(), 1500);
    }
    
    private void checkLevelProgression() {
        if (gamesWon > 0 && gamesWon % gamesPerLevel == 0) {
            if (currentDifficulty.equals("EASY")) {
                currentDifficulty = "MEDIUM";
                helpUsesLeft = 2;
                showToast("🆙 Level Up! MEDIUM difficulty");
            } else if (currentDifficulty.equals("MEDIUM")) {
                currentDifficulty = "HARD";
                helpUsesLeft = 1;
                showToast("🔥 Level Up! HARD difficulty");
            } else {
                showToast("🏆 Max level reached! Keep playing!");
            }
            updateHelpButton();
        }
    }
    
    private void animateButton(MaterialButton button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1.0f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1.0f, 1.2f, 1.0f);
        scaleX.setDuration(200);
        scaleY.setDuration(200);
        scaleX.start();
        scaleY.start();
    }
    
    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
