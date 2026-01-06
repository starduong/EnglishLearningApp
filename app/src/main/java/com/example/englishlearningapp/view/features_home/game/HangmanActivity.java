package com.example.englishlearningapp.view.features_home.game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.englishlearningapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HangmanActivity extends AppCompatActivity {
    
    private static final int MAX_WRONG_GUESSES = 6;
    
    // UI Components
    private TextView tvWord;
    private TextView tvScore;
    private TextView tvFinalScore;
    private GridLayout gridLetters;
    private MaterialButton btnPlayAgain;
    private MaterialButton btnHelp;
    private MaterialButton btnExit;
    private FrameLayout overlayGameOver;
    private FrameLayout overlayWin;
    private TextView tvCorrectAnswer;
    private MaterialButton btnPlayAgainOverlay;
    private MaterialButton btnExitOverlay;
    private MaterialButton btnContinue;
    private MaterialButton btnExitWin;
    
    // Hearts display
    private TextView[] heartViews;
    
    // Score Manager
    private ScoreManager scoreManager;
    
    // Game data với nhiều chủ đề từ vựng đa dạng
    private String[] words = {
        "CAT", "DOG", "BIRD", "FISH", "TIGER", "LION", "BEAR",
        "APPLE", "PIZZA", "BREAD", "CAKE", "RICE",
        "RED", "BLUE", "GREEN", "BLACK",
        "BOOK", "PEN", "TREE", "HOUSE", "CAR", "PHONE", "DOOR", "CHAIR",
        "WATER", "FIRE", "EARTH", "WIND",
        "HAPPY", "LOVE", "HEART", "SMILE", "DREAM", "MUSIC", "DANCE"
    };
    
    // Categories corresponding to words array
    private String[] categories = {
        "Animals", "Animals", "Animals", "Animals", "Animals", "Animals", "Animals",
        "Food", "Food", "Food", "Food", "Food",
        "Colors", "Colors", "Colors", "Colors", 
        "Objects", "Objects", "Objects", "Objects", "Objects", "Objects", "Objects", "Objects",
        "Nature", "Nature", "Nature", "Nature",
        "Life", "Life", "Life", "Life", "Life", "Life", "Life"
    };
    private String currentWord;
    private String currentCategory;
    private StringBuilder guessedWord;
    private List<String> guessedLetters;
    private List<String> wrongLetters;
    private int score = 0;
    private int wrongCount;
    private int hearts = 6; // Số mạng ban đầu
    private boolean gameOver = false;
    private Vibrator vibrator;
    
    // Help feature
    private int helpRemaining = 2; // Default cho Medium
    private int maxHelp = 2; // Lưu số help tối đa theo difficulty
    
    // Level progression system
    private int gamesWon = 0; // Số games đã thắng
    private int gamesPerLevel = 5; // 5 games mỗi level
    private String currentDifficulty = "MEDIUM"; // Track current difficulty
    
    // Difficulty progression: EASY -> MEDIUM -> HARD
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangman);
        
        // Initialize Score Manager
        scoreManager = new ScoreManager(this);
        
        initViews();
        processDifficultyFromIntent();
        setupClickListeners();
        startNewGame();
    }
    
    private void initViews() {
        tvWord = findViewById(R.id.tvWord);
        tvScore = findViewById(R.id.tvScore);
        tvFinalScore = findViewById(R.id.tvFinalScore);
        gridLetters = findViewById(R.id.gridLetters);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnHelp = findViewById(R.id.btnHelp);
        btnExit = findViewById(R.id.btnExit);
        overlayGameOver = findViewById(R.id.overlayGameOver);
        overlayWin = findViewById(R.id.overlayWin);
        tvCorrectAnswer = findViewById(R.id.tvCorrectAnswer);
        btnPlayAgainOverlay = findViewById(R.id.btnPlayAgainOverlay);
        btnExitOverlay = findViewById(R.id.btnExitOverlay);
        btnContinue = findViewById(R.id.btnContinue);
        btnExitWin = findViewById(R.id.btnExitWin);
        
        // Debug log để check buttons
        android.util.Log.d("HangmanGame", "=== VIEW INITIALIZATION ===");
        android.util.Log.d("HangmanGame", "overlayWin found: " + (overlayWin != null));
        android.util.Log.d("HangmanGame", "btnContinue found: " + (btnContinue != null));
        android.util.Log.d("HangmanGame", "btnExitWin found: " + (btnExitWin != null));
        android.util.Log.d("HangmanGame", "tvFinalScore found: " + (tvFinalScore != null));
        
        // Initialize vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    
        // Khởi tạo hearts layout
        LinearLayout heartsLayout = findViewById(R.id.layoutHearts);
        if (heartsLayout != null) {
            heartViews = new TextView[8]; // Maximum for EASY difficulty
            for (int i = 0; i < 8; i++) {
                TextView heartView = new TextView(this);
                heartView.setText("❤️");
                heartView.setTextSize(18f);
                heartView.setPadding(4, 0, 4, 0);
                heartsLayout.addView(heartView);
                heartViews[i] = heartView;
            }
        }
    }
    
    private void processDifficultyFromIntent() {
        String difficultyStr = getIntent().getStringExtra("DIFFICULTY");
        if (difficultyStr != null) {
            currentDifficulty = difficultyStr.toUpperCase();
            try {
                switch (currentDifficulty) {
                    case "EASY":
                        hearts = Difficulty.EASY.getMaxHearts();
                        maxHelp = 3;
                        helpRemaining = 3;
                        break;
                    case "MEDIUM":
                        hearts = Difficulty.MEDIUM.getMaxHearts();
                        maxHelp = 2;
                        helpRemaining = 2;
                        break;
                    case "HARD":
                        hearts = Difficulty.HARD.getMaxHearts();
                        maxHelp = 1;
                        helpRemaining = 1;
                        break;
                    default:
                        currentDifficulty = "MEDIUM";
                        hearts = Difficulty.MEDIUM.getMaxHearts();
                        maxHelp = 2;
                        helpRemaining = 2;
                        break;
                }
            } catch (Exception e) {
                currentDifficulty = "MEDIUM";
                hearts = Difficulty.MEDIUM.getMaxHearts();
                maxHelp = 2;
                helpRemaining = 2;
            }
        } else {
            currentDifficulty = "MEDIUM";
            hearts = Difficulty.MEDIUM.getMaxHearts();
            maxHelp = 2;
            helpRemaining = 2;
        }
    }
    
    private void setupClickListeners() {
        // Help button
        if (btnHelp != null) {
            btnHelp.setOnClickListener(v -> revealRandomLetter());
            btnHelp.setText("💡 (" + helpRemaining + ")");
        }
        
        // Exit button
        if (btnExit != null) {
            btnExit.setOnClickListener(v -> exitToGameCenter());
        }
        
        // Play again button
        if (btnPlayAgain != null) {
            btnPlayAgain.setOnClickListener(v -> resetGame());
        }
        
        // Overlay buttons
        if (btnPlayAgainOverlay != null) {
            btnPlayAgainOverlay.setOnClickListener(v -> resetGame());
        }
        
        if (btnExitOverlay != null) {
            btnExitOverlay.setOnClickListener(v -> exitToGameCenter());
        }
        
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> {
                if (overlayWin != null) {
                    overlayWin.setVisibility(View.GONE);
                }
                startNewGame();
            });
        }
        
        if (btnExitWin != null) {
            btnExitWin.setOnClickListener(v -> exitToGameCenter());
        }
    }
    
    private void exitToGameCenter() {
        Intent intent = new Intent(this, GameListActivity.class);
        startActivity(intent);
        finish();
    }
    
    /**
     * Reset entire game (score, level, everything) - used by Try Again button
     */
    private void resetGame() {
        // Reset all progress
        score = 0;
        gamesWon = 0;
        currentDifficulty = getIntent().getStringExtra("DIFFICULTY");
        if (currentDifficulty == null) {
            currentDifficulty = "MEDIUM";
        }
        
        // Reapply initial difficulty settings
        processDifficultyFromIntent();
        
        android.util.Log.d("HangmanGame", "GAME RESET - Score: " + score + ", Level: " + currentDifficulty);
        
        // Start fresh game
        startNewGame();
    }
    
    private void startNewGame() {
        // Reset game state
        gameOver = false;
        wrongCount = 0;
        helpRemaining = maxHelp; // Khởi tạo lại theo difficulty
        guessedLetters = new ArrayList<>();
        wrongLetters = new ArrayList<>();
        
        // Select random word
        Random random = new Random();
        int wordIndex = random.nextInt(words.length);
        currentWord = words[wordIndex];
        currentCategory = categories[wordIndex];
        
        // Debug log để kiểm tra word selection
        android.util.Log.d("HangmanGame", "Words array length: " + words.length);
        android.util.Log.d("HangmanGame", "Selected index: " + wordIndex);
        android.util.Log.d("HangmanGame", "Selected word: " + currentWord);
        android.util.Log.d("HangmanGame", "Selected category: " + currentCategory);
        android.util.Log.d("HangmanGame", "First 5 words: " + words[0] + ", " + words[1] + ", " + words[2] + ", " + words[3] + ", " + words[4]);
        
        // Initialize guessed word with underscores
        guessedWord = new StringBuilder();
        for (int i = 0; i < currentWord.length(); i++) {
            guessedWord.append("_");
        }
        
        // Update UI
        updateWordDisplay();
        updateScoreWithLevelDisplay();
        updateCategoryDisplay();
        updateHeartsDisplay();
        createAlphabetButtons();
        hideOverlays();
        
        // Update help button
        if (btnHelp != null) {
            btnHelp.setText("💡 (" + helpRemaining + ")");
            btnHelp.setEnabled(true);
            btnHelp.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_orange_light));
        }
    }
    
    private void updateWordDisplay() {
        if (tvWord != null) {
            String displayWord = guessedWord.toString().replaceAll("", " ").trim();
            tvWord.setText(displayWord);
        }
    }
    
    private void updateScoreDisplay() {
        if (tvScore != null) {
            tvScore.setText("Score: " + score);
        }
    }
    
    private void updateCategoryDisplay() {
        TextView tvCategory = findViewById(R.id.tvCategory);
        if (tvCategory != null) {
            tvCategory.setText("💡 Category: " + currentCategory);
        }
    }
    
    /**
     * Update score display with level info
     */
    private void updateScoreWithLevelDisplay() {
        if (tvScore != null) {
            int gamesInCurrentLevel = gamesWon % gamesPerLevel;
            int nextLevelGames = gamesPerLevel - gamesInCurrentLevel;
            
            String levelInfo = "";
            if (!currentDifficulty.equals("HARD")) {
                // Shortened format: E = Easy, M = Medium, H = Hard
                String diffAbbrev = currentDifficulty.equals("EASY") ? "E" : 
                                   currentDifficulty.equals("MEDIUM") ? "M" : "H";
                levelInfo = " | " + diffAbbrev + "(" + nextLevelGames + ")";
            } else {
                levelInfo = " | H(Max)";
            }
            
            tvScore.setText("Pts: " + score + levelInfo);
        }
    }
    
    /**
     * Check and upgrade difficulty level after wins
     */
    private void checkLevelProgression() {
        gamesWon++;
        
        // Check if should upgrade level (every 5 games)
        if (gamesWon % gamesPerLevel == 0 && !currentDifficulty.equals("HARD")) {
            String newDifficulty = getNextDifficulty();
            if (!newDifficulty.equals(currentDifficulty)) {
                currentDifficulty = newDifficulty;
                applyDifficultySettings();
                
                // Show level up message briefly
                android.util.Log.d("HangmanGame", "LEVEL UP! Now playing " + currentDifficulty + " mode!");
                
                // You could add a Toast here if wanted:
                // Toast.makeText(this, "Level Up! Now playing " + currentDifficulty + " mode!", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    /**
     * Get next difficulty level
     */
    private String getNextDifficulty() {
        switch (currentDifficulty) {
            case "EASY":
                return "MEDIUM";
            case "MEDIUM":
                return "HARD";
            case "HARD":
            default:
                return "HARD"; // Max level
        }
    }
    
    /**
     * Apply difficulty settings to game
     */
    private void applyDifficultySettings() {
        switch (currentDifficulty) {
            case "EASY":
                hearts = 8;
                maxHelp = 3;
                break;
            case "MEDIUM":
                hearts = 6;
                maxHelp = 2;
                break;
            case "HARD":
                hearts = 4;
                maxHelp = 1;
                break;
        }
        helpRemaining = maxHelp;
    }
    
    private void updateHeartsDisplay() {
        if (heartViews != null) {
            int maxHeartsForDifficulty = hearts;
            int remainingHearts = hearts - wrongCount;
            
            for (int i = 0; i < heartViews.length; i++) {
                if (heartViews[i] != null) {
                    if (i < maxHeartsForDifficulty) {
                        if (i < remainingHearts) {
                            // Hearts còn nguyên
                            heartViews[i].setText("❤️");
                            heartViews[i].setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
                            heartViews[i].setVisibility(View.VISIBLE);
                        } else {
                            // Hearts đã mất - trái tim vỡ
                            heartViews[i].setText("💔");
                            heartViews[i].setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                            heartViews[i].setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Hide extra hearts for easier difficulties
                        heartViews[i].setVisibility(View.GONE);
                    }
                }
            }
        }
    }
    
    private void createAlphabetButtons() {
        if (gridLetters == null) return;
        
        gridLetters.removeAllViews();
        
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        for (int i = 0; i < alphabet.length(); i++) {
            char letter = alphabet.charAt(i);
            Button button = new Button(this);
            button.setText(String.valueOf(letter));
            button.setTextSize(14);
            button.setPadding(8, 8, 8, 8); // Giảm padding
            
            // Set button size - nhỏ gọn hơn
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(4, 4, 4, 4); // Giảm margin
            button.setLayoutParams(params);
            
            // Styling
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_selector));
            button.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            
            // Click listener
            button.setOnClickListener(v -> processGuess(letter, button));
            
            gridLetters.addView(button);
        }
    }
    
    private void processGuess(char letter, Button button) {
        if (gameOver || guessedLetters.contains(String.valueOf(letter))) {
            return;
        }
        
        guessedLetters.add(String.valueOf(letter));
        button.setEnabled(false);
        
        if (currentWord.contains(String.valueOf(letter))) {
            // Correct guess
            for (int i = 0; i < currentWord.length(); i++) {
                if (currentWord.charAt(i) == letter) {
                    guessedWord.setCharAt(i, letter);
                }
            }
            
            // Style correct button
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_correct));
            button.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            
            updateWordDisplay();
            
            // Check win condition
            android.util.Log.d("HangmanGame", "Checking win: guessedWord=" + guessedWord.toString() + ", currentWord=" + currentWord);
            if (guessedWord.toString().equals(currentWord)) {
                android.util.Log.d("HangmanGame", "WIN CONDITION MET - calling onGameWin()");
                System.out.println("WIN CONDITION MET - calling onGameWin()");
                onGameWin();
            }
        } else {
            // Incorrect guess
            wrongLetters.add(String.valueOf(letter));
            wrongCount++;
            
            // Style incorrect button
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_incorrect));
            button.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            
            updateHeartsDisplay();
            
            // Check lose condition
            if (wrongCount >= hearts) {
                onGameLose();
            }
        }
    }
    
    private void revealRandomLetter() {
        if (helpRemaining <= 0 || gameOver) return;
        
        // Find unrevealed positions
        List<Integer> unrevealedPositions = new ArrayList<>();
        for (int i = 0; i < guessedWord.length(); i++) {
            if (guessedWord.charAt(i) == '_') {
                unrevealedPositions.add(i);
            }
        }
        
        if (unrevealedPositions.isEmpty()) return;
        
        // Find letters that haven't been guessed yet
        List<Character> availableLetters = new ArrayList<>();
        for (int pos : unrevealedPositions) {
            char letter = currentWord.charAt(pos);
            if (!guessedLetters.contains(String.valueOf(letter)) && !availableLetters.contains(letter)) {
                availableLetters.add(letter);
            }
        }
        
        if (availableLetters.isEmpty()) {
            // All remaining letters have been guessed
            int randomPos = unrevealedPositions.get(new Random().nextInt(unrevealedPositions.size()));
            char letter = currentWord.charAt(randomPos);
            for (int i = 0; i < currentWord.length(); i++) {
                if (currentWord.charAt(i) == letter) {
                    guessedWord.setCharAt(i, letter);
                }
            }
        } else {
            // Choose random unrevealed letter
            char letterToReveal = availableLetters.get(new Random().nextInt(availableLetters.size()));
            for (int i = 0; i < currentWord.length(); i++) {
                if (currentWord.charAt(i) == letterToReveal) {
                    guessedWord.setCharAt(i, letterToReveal);
                }
            }
            
            // Mark as guessed and disable button
            guessedLetters.add(String.valueOf(letterToReveal));
            for (int i = 0; i < gridLetters.getChildCount(); i++) {
                Button btn = (Button) gridLetters.getChildAt(i);
                if (btn.getText().toString().equals(String.valueOf(letterToReveal))) {
                    btn.setEnabled(false);
                    btn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_correct));
                    btn.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    break;
                }
            }
        }
        
        helpRemaining--;
        
        // Update help button
        if (btnHelp != null) {
            btnHelp.setText("💡 (" + helpRemaining + ")");
            if (helpRemaining <= 0) {
                btnHelp.setEnabled(false);
                btnHelp.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
            }
        }
        
        updateWordDisplay();
        
        // Check win condition
        if (guessedWord.toString().equals(currentWord)) {
            onGameWin();
        }
    }
    
    private void onGameWin() {
        gameOver = true;
        
        // Calculate points based on difficulty
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
        
        // Save score to ScoreManager
        scoreManager.saveHangmanScore(score, true);
        
        // Check level progression (level up after 5 wins)
        checkLevelProgression();
        
        // Debug log
        android.util.Log.d("HangmanGame", "onGameWin called - pointsEarned: " + pointsEarned + ", totalScore: " + score + ", gamesWon: " + gamesWon + ", difficulty: " + currentDifficulty);
        
        // Victory vibration
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 200, 100, 200};
            vibrator.vibrate(pattern, -1);
        }
        
        // Show points earned message
        android.widget.Toast.makeText(this, "🎉 +" + pointsEarned + " points! Total: " + score, android.widget.Toast.LENGTH_SHORT).show();
        
        // Auto-continue to next game after short delay
        tvWord.postDelayed(() -> {
            android.util.Log.d("HangmanGame", "Auto-starting new game...");
            startNewGame();
        }, 1500); // 1.5 second delay to show completed word
    }
    
    private void onGameLose() {
        gameOver = true;
        
        // Save score when game ends (win = false)
        scoreManager.saveHangmanScore(score, false);
        
        // Debug log
        android.util.Log.d("HangmanGame", "onGameLose called - wrongCount: " + wrongCount + ", hearts: " + hearts + ", finalScore: " + score);
        
        if (overlayGameOver != null) {
            overlayGameOver.setVisibility(View.VISIBLE);
            android.util.Log.d("HangmanGame", "Game Over overlay set to VISIBLE");
        } else {
            android.util.Log.e("HangmanGame", "overlayGameOver is null!");
        }
        
        if (tvCorrectAnswer != null) {
            tvCorrectAnswer.setText("The word was: " + currentWord);
            android.util.Log.d("HangmanGame", "Set correct answer text: " + currentWord);
        } else {
            android.util.Log.e("HangmanGame", "tvCorrectAnswer is null!");
        }
        
        // Vibrate for game over
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500);
        }
    }
    
    private void hideOverlays() {
        if (overlayGameOver != null) {
            overlayGameOver.setVisibility(View.GONE);
        }
        if (overlayWin != null) {
            overlayWin.setVisibility(View.GONE);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
