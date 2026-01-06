package com.example.englishlearningapp.view.features_home.exercises;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishlearningapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class FillBlanksActivity extends AppCompatActivity {

    // Views
    private MaterialButton btnBack;
    private TextView tvExerciseTitle, tvTopicName, tvScore, tvProgress;
    private ProgressBar progressBar;
    private TextView tvSentence;
    private MaterialButton btnOption1, btnOption2, btnOption3, btnOption4;
    private MaterialCardView feedbackCard;
    private ImageView ivFeedbackIcon;
    private TextView tvFeedbackTitle, tvFeedbackMessage;
    private MaterialButton btnPrevious, btnNext;

    // Data
    private String exerciseId;
    private boolean reviewMode;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int totalScore = 0;
    private boolean[] answeredCorrectly;
    private MaterialButton selectedButton = null;
    private boolean isAnswerSelected = false;
    private boolean isQuestionAnswered = false;

    // Question data structure
    private static class Question {
        String sentence;
        String[] options;
        int correctAnswerIndex;
        String explanation;

        Question(String sentence, String[] options, int correctAnswerIndex, String explanation) {
            this.sentence = sentence;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
            this.explanation = explanation;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_fill_blanks);

            // Get data from intent
            exerciseId = getIntent().getStringExtra("EXERCISE_ID");
            reviewMode = getIntent().getBooleanExtra("REVIEW_MODE", false);

            if (exerciseId == null) {
                finish();
                return;
            }

            // Initialize views
            initViews();
            setupClickListeners();
            
            // Load questions based on exercise
            loadQuestions();
            
            // Initialize answered array
            answeredCorrectly = new boolean[questions.size()];
            
            // Display first question
            displayCurrentQuestion();
            updateUI();
            
        } catch (Exception e) {
            // Handle any initialization errors
            e.printStackTrace();
            Toast.makeText(this, "Error loading exercise. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvExerciseTitle = findViewById(R.id.tvExerciseTitle);
        tvTopicName = findViewById(R.id.tvTopicName);
        tvScore = findViewById(R.id.tvScore);
        tvProgress = findViewById(R.id.tvProgress);
        progressBar = findViewById(R.id.progressBar);
        tvSentence = findViewById(R.id.tvSentence);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        feedbackCard = findViewById(R.id.feedbackCard);
        ivFeedbackIcon = findViewById(R.id.ivFeedbackIcon);
        tvFeedbackTitle = findViewById(R.id.tvFeedbackTitle);
        tvFeedbackMessage = findViewById(R.id.tvFeedbackMessage);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnOption1.setOnClickListener(v -> selectAnswer(btnOption1, 0));
        btnOption2.setOnClickListener(v -> selectAnswer(btnOption2, 1));
        btnOption3.setOnClickListener(v -> selectAnswer(btnOption3, 2));
        btnOption4.setOnClickListener(v -> selectAnswer(btnOption4, 3));
        
        btnPrevious.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayCurrentQuestion();
                updateUI();
            }
        });
        
        btnNext.setOnClickListener(v -> {
            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                displayCurrentQuestion();
                updateUI();
            } else {
                // End of exercise
                showFinalResults();
            }
        });
    }

    private void loadQuestions() {
        questions = new ArrayList<>();
        
        // Sample questions for Vietnam topic
        if ("vn_001".equals(exerciseId)) {
            tvExerciseTitle.setText("Ha Long Bay Wonder");
            tvTopicName.setText("Vietnam • Easy Level");
            
            questions.add(new Question(
                "Ha Long Bay is one of the most _____ tourist destinations in Vietnam.",
                new String[]{"beautiful", "popularity", "popular", "beautifully"},
                2,
                "Great job! 'Popular' is the correct adjective to describe tourist destinations."
            ));
            
            questions.add(new Question(
                "The bay _____ about 1,600 limestone islands and islets.",
                new String[]{"contain", "contains", "containing", "to contain"},
                1,
                "Correct! 'Contains' is the proper present tense form for third person singular."
            ));
            
            questions.add(new Question(
                "Many tourists _____ to Ha Long Bay every year.",
                new String[]{"travels", "travel", "travelling", "traveled"},
                1,
                "Excellent! 'Travel' is correct because 'tourists' is plural."
            ));
            
            questions.add(new Question(
                "The limestone formations were _____ over millions of years.",
                new String[]{"create", "created", "creating", "creation"},
                1,
                "Perfect! 'Created' is the correct past participle in passive voice."
            ));
            
            questions.add(new Question(
                "Ha Long Bay is _____ UNESCO World Heritage Site.",
                new String[]{"a", "an", "the", "no article"},
                0,
                "Well done! We use 'a' before consonant sounds like 'UNESCO'."
            ));
            
        } else if ("vn_002".equals(exerciseId)) {
            tvExerciseTitle.setText("Vietnamese Cuisine");
            tvTopicName.setText("Vietnam • Medium Level");
            
            questions.add(new Question(
                "Pho is _____ famous Vietnamese noodle soup.",
                new String[]{"a", "an", "the", "no article"},
                0,
                "Correct! We use 'a' when introducing something for the first time."
            ));
            
            questions.add(new Question(
                "Vietnamese food _____ fresh herbs and vegetables.",
                new String[]{"use", "uses", "using", "used"},
                1,
                "Excellent! 'Food' is uncountable, so we use 'uses'."
            ));
            
            questions.add(new Question(
                "Spring rolls can be _____ fresh or fried.",
                new String[]{"serve", "served", "serving", "serves"},
                1,
                "Great! 'Served' is the correct past participle in passive voice."
            ));
            
        } else if ("vn_003".equals(exerciseId)) {
            tvExerciseTitle.setText("Saigon Economic Hub");
            tvTopicName.setText("Vietnam • Hard Level");
            
            questions.add(new Question(
                "Ho Chi Minh City is Vietnam's largest _____ area and economic powerhouse.",
                new String[]{"urban", "metropolitan", "rural", "suburban"},
                1,
                "Excellent! 'Metropolitan' refers to a large city area with surrounding suburbs."
            ));
            
            questions.add(new Question(
                "The city has undergone rapid _____ and modernization since the 1990s.",
                new String[]{"urbanization", "destruction", "isolation", "stagnation"},
                0,
                "Perfect! 'Urbanization' means the process of becoming more city-like."
            ));
            
            questions.add(new Question(
                "Today, it contributes approximately 23% of Vietnam's _____ and houses numerous corporations.",
                new String[]{"population", "GDP", "area", "history"},
                1,
                "Correct! GDP stands for Gross Domestic Product, measuring economic output."
            ));
            
        } else {
            // Default Ho Chi Minh City exercise
            tvExerciseTitle.setText("Ho Chi Minh City");
            tvTopicName.setText("Vietnam • Hard Level");
            
            questions.add(new Question(
                "Ho Chi Minh City, _____ was formerly called Saigon, is Vietnam's largest city.",
                new String[]{"that", "which", "who", "where"},
                1,
                "Perfect! 'Which' is correct for non-defining relative clauses about things."
            ));
            
            questions.add(new Question(
                "The city _____ rapidly since the economic reforms in the 1980s.",
                new String[]{"developed", "has developed", "develops", "is developing"},
                1,
                "Excellent! Present perfect shows action continuing from past to present."
            ));
        }
    }

    private void displayCurrentQuestion() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            Question current = questions.get(currentQuestionIndex);
            
            // Reset question state
            isAnswerSelected = false;
            isQuestionAnswered = false;
            selectedButton = null;
            
            // Add fade animation for smoother transition
            tvSentence.animate().alpha(0f).setDuration(150).withEndAction(() -> {
                tvSentence.setText(current.sentence);
                btnOption1.setText(current.options[0]);
                btnOption2.setText(current.options[1]);
                btnOption3.setText(current.options[2]);
                btnOption4.setText(current.options[3]);
                
                // Reset button styles
                resetButtonStyles();
                
                // Hide feedback
                hideFeedback();
                
                // Fade back in
                tvSentence.animate().alpha(1f).setDuration(150);
            });
        }
    }

    private void selectAnswer(MaterialButton button, int selectedIndex) {
        // Prevent multiple selections for the same question
        if (isQuestionAnswered) {
            return;
        }
        
        // Reset previous selection
        resetButtonStyles();
        
        // Check if answer is correct
        Question current = questions.get(currentQuestionIndex);
        boolean isCorrect = selectedIndex == current.correctAnswerIndex;
        
        // Mark current selection with appropriate color
        selectedButton = button;
        isAnswerSelected = true;
        isQuestionAnswered = true;
        
        try {
            if (isCorrect) {
                button.setBackgroundColor(getResources().getColor(R.color.green_light, null));
                button.setTextColor(getResources().getColor(R.color.success_green, null));
            } else {
                button.setBackgroundColor(getResources().getColor(R.color.orange_light, null));
                button.setTextColor(getResources().getColor(R.color.error_red, null));
                
                // Also highlight the correct answer
                MaterialButton correctButton = getButtonByIndex(current.correctAnswerIndex);
                if (correctButton != null) {
                    correctButton.setBackgroundColor(getResources().getColor(R.color.green_light, null));
                    correctButton.setTextColor(getResources().getColor(R.color.success_green, null));
                }
            }
        } catch (Exception e) {
            // Fallback if color resources fail
            if (isCorrect) {
                button.setBackgroundColor(0xFF4CAF50); // Green
            } else {
                button.setBackgroundColor(0xFFFFB74D); // Orange
            }
        }
        
        // Show feedback
        showFeedback(isCorrect, current.explanation);
        
        // Update score if correct and not already answered correctly
        if (isCorrect && !answeredCorrectly[currentQuestionIndex]) {
            answeredCorrectly[currentQuestionIndex] = true;
            totalScore += getPointsForQuestion();
            updateScoreDisplay();
        }
        
        // Auto advance to next question after 1 second (faster)
        btnNext.setEnabled(true);
        btnNext.postDelayed(() -> {
            if (currentQuestionIndex == questions.size() - 1) {
                showFinalResults();
            } else {
                goToNextQuestion();
            }
        }, 1000); // Reduced from 2000ms to 1000ms
    }
    
    private void goToNextQuestion() {
        currentQuestionIndex++;
        displayCurrentQuestion();
        updateUI();
    }
    
    private MaterialButton getButtonByIndex(int index) {
        switch (index) {
            case 0: return btnOption1;
            case 1: return btnOption2;
            case 2: return btnOption3;
            case 3: return btnOption4;
            default: return null;
        }
    }

    private void resetButtonStyles() {
        // Use a more compatible color approach
        try {
            btnOption1.setBackgroundColor(0); // Transparent
            btnOption1.setTextColor(getResources().getColor(android.R.color.black, null));
            btnOption2.setBackgroundColor(0);
            btnOption2.setTextColor(getResources().getColor(android.R.color.black, null));
            btnOption3.setBackgroundColor(0);
            btnOption3.setTextColor(getResources().getColor(android.R.color.black, null));
            btnOption4.setBackgroundColor(0);
            btnOption4.setTextColor(getResources().getColor(android.R.color.black, null));
        } catch (Exception e) {
            // Fallback if color resources fail
            btnOption1.setBackgroundColor(0);
            btnOption2.setBackgroundColor(0);
            btnOption3.setBackgroundColor(0);
            btnOption4.setBackgroundColor(0);
        }
    }

    private void showFeedback(boolean correct, String explanation) {
        feedbackCard.setVisibility(View.VISIBLE);
        
        try {
            if (correct) {
                ivFeedbackIcon.setImageResource(R.drawable.ic_check_circle);
                ivFeedbackIcon.setColorFilter(getResources().getColor(R.color.success_green, null));
                tvFeedbackTitle.setText("Correct!");
                tvFeedbackTitle.setTextColor(getResources().getColor(R.color.success_green, null));
                feedbackCard.setCardBackgroundColor(getResources().getColor(R.color.green_light, null));
            } else {
                ivFeedbackIcon.setImageResource(R.drawable.ic_error);
                ivFeedbackIcon.setColorFilter(getResources().getColor(R.color.error_red, null));
                tvFeedbackTitle.setText("Incorrect");
                tvFeedbackTitle.setTextColor(getResources().getColor(R.color.error_red, null));
                feedbackCard.setCardBackgroundColor(getResources().getColor(R.color.orange_light, null));
                
                // Show correct answer
                Question current = questions.get(currentQuestionIndex);
                explanation = "Correct answer: " + current.options[current.correctAnswerIndex] + ". " + explanation;
            }
        } catch (Exception e) {
            // Fallback colors if resources fail
            if (correct) {
                ivFeedbackIcon.setImageResource(R.drawable.ic_check_circle);
                tvFeedbackTitle.setText("Correct!");
                feedbackCard.setCardBackgroundColor(0xFF81C784); // Light green
            } else {
                ivFeedbackIcon.setImageResource(R.drawable.ic_error);
                tvFeedbackTitle.setText("Incorrect");
                feedbackCard.setCardBackgroundColor(0xFFFFB74D); // Light orange
            }
        }
        
        tvFeedbackMessage.setText(explanation);
    }
    
    private void hideFeedback() {
        feedbackCard.setVisibility(View.GONE);
    }

    private void updateUI() {
        // Update progress
        tvProgress.setText((currentQuestionIndex + 1) + "/" + questions.size());
        int progressPercentage = (int) (((float) (currentQuestionIndex + 1) / questions.size()) * 100);
        progressBar.setProgress(progressPercentage);
        
        // Disable previous button (no going back allowed)
        btnPrevious.setEnabled(false);
        btnPrevious.setVisibility(View.GONE); // Hide it completely
        
        if (currentQuestionIndex == questions.size() - 1) {
            btnNext.setText("Finish");
        } else {
            btnNext.setText("Next");
        }
        
        btnNext.setEnabled(false); // Will be enabled when answer is selected
    }

    private void updateScoreDisplay() {
        tvScore.setText(String.valueOf(totalScore));
    }

    private int getPointsForQuestion() {
        // Points based on exercise difficulty
        if (tvTopicName.getText().toString().contains("Easy")) return 5;
        if (tvTopicName.getText().toString().contains("Medium")) return 10;
        return 15; // Hard
    }

    private void showFinalResults() {
        int correctCount = getCorrectAnswersCount();
        int totalQuestions = questions.size();
        float accuracyPercentage = (float) correctCount / totalQuestions * 100;
        boolean isPerfect = accuracyPercentage == 100f;
        
        String resultMessage;
        String emoji;
        String encouragement = "";
        
        // Determine result message and emoji based on accuracy
        if (isPerfect) {
            resultMessage = "🎉 Perfect! Outstanding performance!";
            emoji = "🏆";
            encouragement = "You're a star! Exercise completed successfully!";
        } else if (accuracyPercentage >= 80) {
            resultMessage = "👏 Great job! Almost there!";
            emoji = "⭐";
            encouragement = "So close! Try again to get 100%!";
        } else if (accuracyPercentage >= 60) {
            resultMessage = "👍 Good effort! Keep practicing!";
            emoji = "💪";
            encouragement = "You need 100% to complete this exercise!";
        } else if (accuracyPercentage >= 40) {
            resultMessage = "📚 Keep studying! You're improving!";
            emoji = "🔥";
            encouragement = "Don't give up! Practice more to reach 100%!";
        } else {
            resultMessage = "💪 Don't worry! Learning takes time!";
            emoji = "🌟";
            encouragement = "Keep going! You need all answers correct to complete!";
        }
        
        // Save exercise completion only if perfect score
        if (isPerfect) {
            saveExerciseCompletion(correctCount, totalQuestions, totalScore, accuracyPercentage);
        }
        
        // Show results popup
        showResultsPopup(emoji, resultMessage, encouragement, correctCount, totalQuestions, totalScore, accuracyPercentage, isPerfect);
    }
    
    private void showResultsPopup(String emoji, String title, String encouragement, 
                                  int correctCount, int totalQuestions, int score, 
                                  float accuracy, boolean isCompleted) {
        
        // Create overlay view to dim background
        android.view.View overlay = new android.view.View(this);
        overlay.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        ));
        overlay.setBackgroundColor(android.graphics.Color.argb(180, 0, 0, 0)); // Semi-transparent black
        
        // Create popup card
        com.google.android.material.card.MaterialCardView popupCard = new com.google.android.material.card.MaterialCardView(this);
        android.widget.FrameLayout.LayoutParams cardParams = new android.widget.FrameLayout.LayoutParams(
            (int) (getResources().getDisplayMetrics().widthPixels * 0.85f), // 85% of screen width
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.gravity = android.view.Gravity.CENTER;
        popupCard.setLayoutParams(cardParams);
        popupCard.setRadius(24f);
        popupCard.setCardElevation(16f);
        popupCard.setCardBackgroundColor(android.graphics.Color.WHITE);
        
        // Create content layout
        android.widget.LinearLayout contentLayout = new android.widget.LinearLayout(this);
        contentLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        contentLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        contentLayout.setPadding(48, 48, 48, 48);
        
        // Emoji and title
        android.widget.TextView tvEmoji = new android.widget.TextView(this);
        tvEmoji.setText(emoji);
        tvEmoji.setTextSize(48f);
        tvEmoji.setGravity(android.view.Gravity.CENTER);
        tvEmoji.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        android.widget.TextView tvTitle = new android.widget.TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(18f);
        tvTitle.setTextColor(android.graphics.Color.BLACK);
        tvTitle.setTypeface(tvTitle.getTypeface(), android.graphics.Typeface.BOLD);
        tvTitle.setGravity(android.view.Gravity.CENTER);
        android.widget.LinearLayout.LayoutParams titleParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.topMargin = 16;
        tvTitle.setLayoutParams(titleParams);
        
        // Stats
        android.widget.TextView tvStats = new android.widget.TextView(this);
        String stats = String.format("📊 Results:\n• Score: %d points\n• Correct: %d/%d\n• Accuracy: %.0f%%", 
                                    score, correctCount, totalQuestions, accuracy);
        tvStats.setText(stats);
        tvStats.setTextSize(14f);
        tvStats.setTextColor(android.graphics.Color.DKGRAY);
        tvStats.setGravity(android.view.Gravity.CENTER);
        android.widget.LinearLayout.LayoutParams statsParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        statsParams.topMargin = 24;
        tvStats.setLayoutParams(statsParams);
        
        // Encouragement
        android.widget.TextView tvEncouragement = new android.widget.TextView(this);
        tvEncouragement.setText("💬 " + encouragement);
        tvEncouragement.setTextSize(14f);
        tvEncouragement.setTextColor(android.graphics.Color.DKGRAY);
        tvEncouragement.setGravity(android.view.Gravity.CENTER);
        android.widget.LinearLayout.LayoutParams encParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        encParams.topMargin = 16;
        tvEncouragement.setLayoutParams(encParams);
        
        // Completion status
        if (!isCompleted) {
            android.widget.TextView tvIncomplete = new android.widget.TextView(this);
            tvIncomplete.setText("❌ Exercise not completed\nYou need 100% to finish!");
            tvIncomplete.setTextSize(13f);
            tvIncomplete.setTextColor(getResources().getColor(R.color.error_red, null));
            tvIncomplete.setGravity(android.view.Gravity.CENTER);
            tvIncomplete.setTypeface(tvIncomplete.getTypeface(), android.graphics.Typeface.BOLD);
            android.widget.LinearLayout.LayoutParams incParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            incParams.topMargin = 16;
            tvIncomplete.setLayoutParams(incParams);
            contentLayout.addView(tvIncomplete);
        } else {
            android.widget.TextView tvComplete = new android.widget.TextView(this);
            tvComplete.setText("✅ Exercise Completed!");
            tvComplete.setTextSize(13f);
            tvComplete.setTextColor(getResources().getColor(R.color.success_green, null));
            tvComplete.setGravity(android.view.Gravity.CENTER);
            tvComplete.setTypeface(tvComplete.getTypeface(), android.graphics.Typeface.BOLD);
            android.widget.LinearLayout.LayoutParams compParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            compParams.topMargin = 16;
            tvComplete.setLayoutParams(compParams);
            contentLayout.addView(tvComplete);
        }
        
        // Close button
        com.google.android.material.button.MaterialButton btnClose = new com.google.android.material.button.MaterialButton(this);
        btnClose.setText("Continue");
        btnClose.setTextSize(16f);
        android.widget.LinearLayout.LayoutParams btnParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.topMargin = 32;
        btnClose.setLayoutParams(btnParams);
        
        // Add all views
        contentLayout.addView(tvEmoji);
        contentLayout.addView(tvTitle);
        contentLayout.addView(tvStats);
        contentLayout.addView(tvEncouragement);
        contentLayout.addView(btnClose);
        
        popupCard.addView(contentLayout);
        
        // Add to main layout
        android.widget.FrameLayout mainContainer = new android.widget.FrameLayout(this);
        mainContainer.addView(overlay);
        mainContainer.addView(popupCard);
        
        // Add to activity
        android.view.ViewGroup rootView = (android.view.ViewGroup) findViewById(android.R.id.content);
        rootView.addView(mainContainer);
        
        // Handle close button
        btnClose.setOnClickListener(v -> {
            rootView.removeView(mainContainer);
            
            // Set result to return to ExerciseDetailActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("EXERCISE_ID", exerciseId);
            resultIntent.putExtra("COMPLETED", isCompleted); // Only true if 100%
            resultIntent.putExtra("SCORE", score);
            resultIntent.putExtra("CORRECT_COUNT", correctCount);
            resultIntent.putExtra("TOTAL_QUESTIONS", totalQuestions);
            resultIntent.putExtra("ACCURACY", accuracy);
            setResult(RESULT_OK, resultIntent);
            
            finish();
        });
        
        // Auto close after 10 seconds if user doesn't interact
        btnClose.postDelayed(() -> {
            try {
                if (mainContainer.getParent() != null) {
                    rootView.removeView(mainContainer);
                    
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("EXERCISE_ID", exerciseId);
                    resultIntent.putExtra("COMPLETED", isCompleted);
                    resultIntent.putExtra("SCORE", score);
                    resultIntent.putExtra("CORRECT_COUNT", correctCount);
                    resultIntent.putExtra("TOTAL_QUESTIONS", totalQuestions);
                    resultIntent.putExtra("ACCURACY", accuracy);
                    setResult(RESULT_OK, resultIntent);
                    
                    finish();
                }
            } catch (Exception e) {
                finish();
            }
        }, 10000);
    }
    
    private void saveExerciseCompletion(int correctCount, int totalQuestions, int score, float accuracy) {
        // Only save as completed if accuracy is 100%
        if (accuracy != 100f) {
            return; // Don't save incomplete exercises
        }
        
        try {
            android.content.SharedPreferences prefs = getSharedPreferences("exercise_prefs", MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            
            // Save completion status and progress (only for 100% completion)
            editor.putBoolean("exercise_" + exerciseId + "_completed", true);
            editor.putFloat("exercise_" + exerciseId + "_progress", 100f); // Always 100% when completed
            editor.putInt("exercise_" + exerciseId + "_score", score);
            editor.putInt("exercise_" + exerciseId + "_correct", correctCount);
            editor.putInt("exercise_" + exerciseId + "_total", totalQuestions);
            editor.putLong("exercise_" + exerciseId + "_timestamp", System.currentTimeMillis());
            
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCorrectAnswersCount() {
        int count = 0;
        for (boolean answered : answeredCorrectly) {
            if (answered) count++;
        }
        return count;
    }
}
