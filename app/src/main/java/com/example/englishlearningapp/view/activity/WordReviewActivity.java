package com.example.englishlearningapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.dao.WordDAO;
import com.example.englishlearningapp.data.model.Word;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WordReviewActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack;
    private TextView tvProgress, tvVietnameseMeaning, tvPronunciation, tvPartOfSpeech;
    private TextView tvExample, tvResultMessage, tvCorrectAnswer, tvCharCount;
    private LinearProgressIndicator progressBar;
    private MaterialCardView cardReview;
    private TextInputLayout inputLayoutAnswer;
    private TextInputEditText etAnswer;
    private MaterialButton btnSkip, btnCheck, btnAgain;

    // Data
    private List<Word> reviewWords = new ArrayList<>();
    private Word currentWord;
    private WordDAO wordDAO;
    private String userId;

    // Review state
    private int currentIndex = 0;
    private int totalWords = 0;
    private int correctCount = 0;
    private int wrongCount = 0;
    private boolean isAnswered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_review);
        setupWindowInsets();

        // Initialize DAO first, as it's needed in getIntentData()
        wordDAO = new WordDAO(this);

        // Get data from intent
        getIntentData();

        // Initialize UI
        initViews();
        setupClickListeners();
        setupInputListeners();

        // Start review
        if (reviewWords.isEmpty()) {
            showNoWordsMessage();
        } else {
            loadNextWord();
        }
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getIntentData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");

        // Get review words from database
        if (!TextUtils.isEmpty(userId)) {
            reviewWords = wordDAO.getWordsNeedReview(userId);
            // Shuffle words for better learning
            Collections.shuffle(reviewWords);
            totalWords = reviewWords.size();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvProgress = findViewById(R.id.tvProgress);
        progressBar = findViewById(R.id.progressBar);
        cardReview = findViewById(R.id.cardReview);
        tvVietnameseMeaning = findViewById(R.id.tvVietnameseMeaning);
        tvPronunciation = findViewById(R.id.tvPronunciation);
        tvPartOfSpeech = findViewById(R.id.tvPartOfSpeech);
        tvExample = findViewById(R.id.tvExample);
        tvResultMessage = findViewById(R.id.tvResultMessage);
        tvCorrectAnswer = findViewById(R.id.tvCorrectAnswer);
        inputLayoutAnswer = findViewById(R.id.inputLayoutAnswer);
        etAnswer = findViewById(R.id.etAnswer);
        tvCharCount = findViewById(R.id.tvCharCount);
        btnSkip = findViewById(R.id.btnSkip);
        btnCheck = findViewById(R.id.btnCheck);
        btnAgain = findViewById(R.id.btnAgain);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> {
            if (currentIndex > 0) {
                showExitConfirmationDialog();
            } else {
                finish();
            }
        });

        // Skip button
        btnSkip.setOnClickListener(v -> {
            if (!isAnswered) {
                skipWord();
            }
        });

        // Check answer button
        btnCheck.setOnClickListener(v -> {
            if (!isAnswered) {
                checkAnswer();
            }
        });

        // Again button (after answer)
        btnAgain.setOnClickListener(v -> {
            if (isAnswered) {
                nextWord();
            }
        });
    }

    private void setupInputListeners() {
        // Character count
        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                tvCharCount.setText(length + "/50");

                // Enable/disable check button
                btnCheck.setEnabled(length > 0);
            }
        });

        // Handle Enter key
        etAnswer.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                if (!isAnswered && etAnswer.getText().length() > 0) {
                    checkAnswer();
                    return true;
                }
            }
            return false;
        });
    }

    private void loadNextWord() {
        if (currentIndex >= totalWords) {
            showCompletionScreen();
            return;
        }

        // Reset state
        isAnswered = false;
        currentWord = reviewWords.get(currentIndex);

        // Update UI
        updateProgress();
        clearAnswerFields();

        // Display word information
        tvVietnameseMeaning.setText(currentWord.getVietnameseMeaning());

        // Pronunciation
        if (!TextUtils.isEmpty(currentWord.getPronunciation())) {
            tvPronunciation.setText("/" + currentWord.getPronunciation() + "/");
            tvPronunciation.setVisibility(View.VISIBLE);
        } else {
            tvPronunciation.setVisibility(View.GONE);
        }

        // Part of speech
        if (!TextUtils.isEmpty(currentWord.getPartOfSpeech())) {
            tvPartOfSpeech.setText(currentWord.getPartOfSpeech());
            tvPartOfSpeech.setVisibility(View.VISIBLE);
        } else {
            tvPartOfSpeech.setVisibility(View.GONE);
        }

        // Example sentence (optional hint)
        if (!TextUtils.isEmpty(currentWord.getExampleSentence())) {
            tvExample.setText("\"" + currentWord.getExampleSentence() + "\"");
            tvExample.setVisibility(View.VISIBLE);
        } else {
            tvExample.setVisibility(View.GONE);
        }

        // Hide result fields
        tvResultMessage.setVisibility(View.GONE);
        tvCorrectAnswer.setVisibility(View.GONE);

        // Show input, hide again button
        inputLayoutAnswer.setVisibility(View.VISIBLE);
        btnSkip.setVisibility(View.VISIBLE);
        btnCheck.setVisibility(View.VISIBLE);
        btnAgain.setVisibility(View.GONE);

        // Focus on input field
        etAnswer.requestFocus();

        // Enable buttons
        btnSkip.setEnabled(true);
        btnCheck.setEnabled(false); // Disabled until user types something
    }

    private void updateProgress() {
        int progress = currentIndex + 1;
        tvProgress.setText(progress + "/" + totalWords);

        float progressPercentage = (float) progress / totalWords * 100;
        progressBar.setProgressCompat((int) progressPercentage, true);
    }

    private void clearAnswerFields() {
        etAnswer.setText("");
        inputLayoutAnswer.setError(null);
        tvCharCount.setText("0/50");
    }

    private void checkAnswer() {
        String userAnswer = etAnswer.getText().toString().trim();
        String correctAnswer = currentWord.getEnglishWord().trim();

        if (TextUtils.isEmpty(userAnswer)) {
            inputLayoutAnswer.setError("Vui lòng nhập câu trả lời");
            return;
        }

        // Normalize answers for comparison
        String normalizedUserAnswer = userAnswer.toLowerCase(Locale.getDefault());
        String normalizedCorrectAnswer = correctAnswer.toLowerCase(Locale.getDefault());

        boolean isCorrect = normalizedUserAnswer.equals(normalizedCorrectAnswer);

        // Update word stats
        updateWordStats(isCorrect);

        // Show result
        showResult(isCorrect, correctAnswer);

        isAnswered = true;

        // Update UI state
        inputLayoutAnswer.setEnabled(false);
        btnSkip.setVisibility(View.GONE);
        btnCheck.setVisibility(View.GONE);
        btnAgain.setVisibility(View.VISIBLE);

        // Auto-focus on again button after delay
        etAnswer.postDelayed(() -> btnAgain.requestFocus(), 500);
    }

    private void skipWord() {
        // Mark as wrong when skipped
        updateWordStats(false);

        // Show correct answer
        tvCorrectAnswer.setText("Đáp án: " + currentWord.getEnglishWord());
        tvCorrectAnswer.setTextColor(getResources().getColor(R.color.warning_orange));
        tvCorrectAnswer.setVisibility(View.VISIBLE);

        // Show skipped message
        tvResultMessage.setText("Đã bỏ qua");
        tvResultMessage.setTextColor(getResources().getColor(R.color.warning_orange));
        tvResultMessage.setVisibility(View.VISIBLE);

        isAnswered = true;

        // Update UI state
        inputLayoutAnswer.setEnabled(false);
        btnSkip.setVisibility(View.GONE);
        btnCheck.setVisibility(View.GONE);
        btnAgain.setVisibility(View.VISIBLE);
    }

    private void showResult(boolean isCorrect, String correctAnswer) {
        if (isCorrect) {
            tvResultMessage.setText("Chính xác! 🎉");
            tvResultMessage.setTextColor(getResources().getColor(R.color.success_green));
            correctCount++;
        } else {
            tvResultMessage.setText("Sai rồi 😢");
            tvResultMessage.setTextColor(getResources().getColor(R.color.error_red));

            tvCorrectAnswer.setText("Đáp án: " + correctAnswer);
            tvCorrectAnswer.setVisibility(View.VISIBLE);
            wrongCount++;
        }
        tvResultMessage.setVisibility(View.VISIBLE);
    }

    private void updateWordStats(boolean isCorrect) {
        if (currentWord == null) return;

        // Update word statistics
        currentWord.setReviewCount(currentWord.getReviewCount() + 1);

        if (isCorrect) {
            currentWord.setCorrectCount(currentWord.getCorrectCount() + 1);
            currentWord.setWrongCount(Math.max(0, currentWord.getWrongCount() - 1)); // Giảm sai khi đúng

            // Increase mastery level based on consecutive correct answers
            if (currentWord.getCorrectCount() >= getRequiredCorrectCountForLevel(currentWord.getMasteryLevel())) {
                if (currentWord.getMasteryLevel() < 3) { // Max level là 3
                    currentWord.setMasteryLevel(currentWord.getMasteryLevel() + 1);
                    currentWord.setCorrectCount(0); // Reset cho level mới
                    Toast.makeText(this, "Từ đã lên level " + currentWord.getMasteryLevel(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            currentWord.setWrongCount(currentWord.getWrongCount() + 1);
            currentWord.setCorrectCount(Math.max(0, currentWord.getCorrectCount() - 1)); // Giảm đúng khi sai

            // Decrease mastery level if too many wrong answers
            if (currentWord.getWrongCount() >= 3 && currentWord.getMasteryLevel() > 0) {
                currentWord.setMasteryLevel(currentWord.getMasteryLevel() - 1);
                currentWord.setWrongCount(0); // Reset cho level mới
                Toast.makeText(this, "Từ bị giảm xuống level " + currentWord.getMasteryLevel(), Toast.LENGTH_SHORT).show();
            }
        }

        // Calculate next review date based on mastery level (Spaced Repetition)
        long nextReviewInterval = calculateNextReviewInterval(currentWord.getMasteryLevel());
        currentWord.setNextReviewDate(System.currentTimeMillis() + nextReviewInterval);
        currentWord.setLastReviewed(System.currentTimeMillis());

        // Update success rate
        int totalAttempts = currentWord.getCorrectCount() + currentWord.getWrongCount();
        if (totalAttempts > 0) {
            float successRate = (float) currentWord.getCorrectCount() / totalAttempts * 100;
            // Có thể lưu success rate nếu cần
        }

        // Update in database
        wordDAO.updateWord(currentWord);

        // DEBUG
        Log.d("WordReview", "Updated word: " + currentWord.getEnglishWord() +
                ", Level: " + currentWord.getMasteryLevel() +
                ", Next review: " + new Date(currentWord.getNextReviewDate()));
    }

    private int getRequiredCorrectCountForLevel(int level) {
        // Số lần đúng cần thiết để lên level
        switch (level) {
            case 0:
                return 2; // Level 0 → 1: 2 lần đúng
            case 1:
                return 3; // Level 1 → 2: 3 lần đúng
            case 2:
                return 4; // Level 2 → 3: 4 lần đúng
            default:
                return 5;
        }
    }

    private long calculateNextReviewInterval(int masteryLevel) {
        // Spaced repetition intervals
        switch (masteryLevel) {
            case 0: // New word: review in 1 day
                return 24 * 60 * 60 * 1000L;
            case 1: // Basic: review in 3 days
                return 3 * 24 * 60 * 60 * 1000L;
            case 2: // Intermediate: review in 7 days
                return 7 * 24 * 60 * 60 * 1000L;
            case 3: // Mastered: review in 30 days
                return 30 * 24 * 60 * 60 * 1000L;
            default:
                return 24 * 60 * 60 * 1000L;
        }
    }

    private void nextWord() {
        currentIndex++;
        loadNextWord();
    }

    private void showCompletionScreen() {
        // Hide input and show completion message
        inputLayoutAnswer.setVisibility(View.GONE);
        btnSkip.setVisibility(View.GONE);
        btnCheck.setVisibility(View.GONE);
        btnAgain.setVisibility(View.VISIBLE);
        btnAgain.setText("HOÀN THÀNH");

        // Show results
        tvVietnameseMeaning.setText("Hoàn thành ôn tập!");
        tvPronunciation.setVisibility(View.GONE);
        tvPartOfSpeech.setVisibility(View.GONE);
        tvExample.setVisibility(View.GONE);

        String resultText = String.format(Locale.getDefault(), "Kết quả:\n✅ Đúng: %d\n❌ Sai: %d\n📊 Độ chính xác: %.0f%%", correctCount, wrongCount, totalWords > 0 ? (float) correctCount / totalWords * 100 : 0);

        tvResultMessage.setText(resultText);
        tvResultMessage.setTextColor(getResources().getColor(R.color.primary_blue));
        tvResultMessage.setVisibility(View.VISIBLE);

        // Update progress to 100%
        progressBar.setProgressCompat(100, true);
        tvProgress.setText(totalWords + "/" + totalWords);

        // Change button action
        btnAgain.setOnClickListener(v -> finish());
    }

    private void showNoWordsMessage() {
        cardReview.setVisibility(View.GONE);
        inputLayoutAnswer.setVisibility(View.GONE);
        btnSkip.setVisibility(View.GONE);
        btnCheck.setVisibility(View.GONE);

        tvResultMessage.setText("Không có từ nào cần ôn tập hôm nay! 🎉");
        tvResultMessage.setTextColor(getResources().getColor(R.color.success_green));
        tvResultMessage.setVisibility(View.VISIBLE);

        btnAgain.setVisibility(View.VISIBLE);
        btnAgain.setText("QUAY LẠI");
        btnAgain.setOnClickListener(v -> finish());
    }

    private void showExitConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Thoát ôn tập").setMessage("Bạn có chắc chắn muốn thoát? Tiến trình sẽ được lưu.").setPositiveButton("Thoát", (dialog, which) -> finish()).setNegativeButton("Tiếp tục", null).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wordDAO != null) {
            wordDAO.close();
        }
    }
}
