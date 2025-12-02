package com.example.englishlearningapp.view.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.dao.WordDAO;
import com.example.englishlearningapp.data.model.Word;
import com.example.englishlearningapp.util.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.Locale;

public class WordDetailActivity extends AppCompatActivity {
    private static final String TAG = "WordDetailActivity";

    // UI Components
    private Toolbar toolbar;
    private TextView tvWord, tvPronunciation, tvPartOfSpeech, tvVietnameseMeaning, tvEnglishDefinition;
    private TextView tvExample, tvExampleTranslation, tvSynonyms, tvAntonyms;
    private TextView tvReviewCount, tvCorrectCount, tvWrongCount, tvSuccessRate, tvMasteryLevel, tvNextReview;
    private TextView tvNotes;
    private ProgressBar progressSuccessRate;
    private ImageButton btnPlayAudio;
    private MaterialButton btnPractice, btnEdit, btnFavorite, btnDelete;
    private View cardNotes, loadingOverlay, layoutSynonyms, layoutAntonyms;

    // Data
    private Word word;
    private WordDAO wordDAO;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        wordDAO = new WordDAO(this);
        mediaPlayer = new MediaPlayer();

        initViews();
        setupToolbar();
        setupClickListeners();
        loadWordData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);

        // Word info
        tvWord = findViewById(R.id.tvWord);
        tvPronunciation = findViewById(R.id.tvPronunciation);
        tvPartOfSpeech = findViewById(R.id.tvPartOfSpeech);
        tvVietnameseMeaning = findViewById(R.id.tvVietnameseMeaning);
        tvEnglishDefinition = findViewById(R.id.tvEnglishDefinition);
        tvExample = findViewById(R.id.tvExample);
        tvExampleTranslation = findViewById(R.id.tvExampleTranslation);
        tvSynonyms = findViewById(R.id.tvSynonyms);
        tvAntonyms = findViewById(R.id.tvAntonyms);
        btnPlayAudio = findViewById(R.id.btnPlayAudio);
        layoutSynonyms = findViewById(R.id.layoutSynonyms);
        layoutAntonyms = findViewById(R.id.layoutAntonyms);

        // Stats
        tvReviewCount = findViewById(R.id.tvReviewCount);
        tvCorrectCount = findViewById(R.id.tvCorrectCount);
        tvWrongCount = findViewById(R.id.tvWrongCount);
        tvSuccessRate = findViewById(R.id.tvSuccessRate);
        tvMasteryLevel = findViewById(R.id.tvMasteryLevel);
        tvNextReview = findViewById(R.id.tvNextReview);
        progressSuccessRate = findViewById(R.id.progressSuccessRate);

        // Actions
        btnPractice = findViewById(R.id.btnPractice);
        btnEdit = findViewById(R.id.btnEdit);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnDelete = findViewById(R.id.btnDelete);

        // Notes
        cardNotes = findViewById(R.id.cardNotes);
        tvNotes = findViewById(R.id.tvNotes);

        // Loading
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupClickListeners() {
        btnPlayAudio.setOnClickListener(v -> playPronunciation());
        btnPractice.setOnClickListener(v -> practiceWord());
        btnEdit.setOnClickListener(v -> editWord());
        btnFavorite.setOnClickListener(v -> toggleFavorite());
        btnDelete.setOnClickListener(v -> deleteWord());
    }

    private void loadWordData() {
        String wordId = getIntent().getStringExtra("word_id");
        if (TextUtils.isEmpty(wordId)) {
            Toast.makeText(this, "Không tìm thấy từ vựng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showLoading();

        new Thread(() -> {
            word = wordDAO.getWordById(wordId);

            runOnUiThread(() -> {
                hideLoading();
                if (word != null) {
                    updateUI();
                } else {
                    Toast.makeText(this, "Không tìm thấy từ vựng", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    private void updateUI() {
        if (word == null) return;

        // Word info
        tvWord.setText(word.getEnglishWord());

        if (!TextUtils.isEmpty(word.getPronunciation())) {
            tvPronunciation.setText("/" + word.getPronunciation() + "/");
            tvPronunciation.setVisibility(View.VISIBLE);
        } else {
            tvPronunciation.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(word.getPartOfSpeech())) {
            tvPartOfSpeech.setText(word.getPartOfSpeech());
            tvPartOfSpeech.setVisibility(View.VISIBLE);
        } else {
            tvPartOfSpeech.setVisibility(View.GONE);
        }

        tvVietnameseMeaning.setText(word.getVietnameseMeaning());

        if (!TextUtils.isEmpty(word.getEnglishDefinition())) {
            tvEnglishDefinition.setText(word.getEnglishDefinition());
            tvEnglishDefinition.setVisibility(View.VISIBLE);
        } else {
            tvEnglishDefinition.setVisibility(View.GONE);
        }

        // Example
        if (!TextUtils.isEmpty(word.getExampleSentence())) {
            tvExample.setText(word.getExampleSentence());
            tvExample.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(word.getExampleTranslation())) {
                tvExampleTranslation.setText(word.getExampleTranslation());
                tvExampleTranslation.setVisibility(View.VISIBLE);
            } else {
                tvExampleTranslation.setVisibility(View.GONE);
            }
        } else {
            tvExample.setVisibility(View.GONE);
            tvExampleTranslation.setVisibility(View.GONE);
        }

        // Synonyms
        if (!TextUtils.isEmpty(word.getSynonyms())) {
            tvSynonyms.setText(word.getSynonyms());
            layoutSynonyms.setVisibility(View.VISIBLE);
        } else {
            layoutSynonyms.setVisibility(View.GONE);
        }

        // Antonyms
        if (!TextUtils.isEmpty(word.getAntonyms())) {
            tvAntonyms.setText(word.getAntonyms());
            layoutAntonyms.setVisibility(View.VISIBLE);
        } else {
            layoutAntonyms.setVisibility(View.GONE);
        }

        // Audio button
        if (!TextUtils.isEmpty(word.getAudioUrl())) {
            btnPlayAudio.setVisibility(View.VISIBLE);
        } else {
            btnPlayAudio.setVisibility(View.GONE);
        }

        // Stats
        tvReviewCount.setText(String.valueOf(word.getReviewCount()));
        tvCorrectCount.setText(String.valueOf(word.getCorrectCount()));
        tvWrongCount.setText(String.valueOf(word.getWrongCount()));

        double successRate = word.getSuccessRate();
        tvSuccessRate.setText(String.format(Locale.getDefault(), "%.0f%%", successRate));
        progressSuccessRate.setProgress((int) successRate);

        tvMasteryLevel.setText(word.getMasteryLevelText());

        if (word.getNextReviewDate() > 0) {
            String nextReview = DateUtils.getRelativeDate(word.getNextReviewDate());
            tvNextReview.setText(nextReview);
        }

        // Notes
        if (!TextUtils.isEmpty(word.getNotes())) {
            tvNotes.setText(word.getNotes());
            cardNotes.setVisibility(View.VISIBLE);
        } else {
            cardNotes.setVisibility(View.GONE);
        }

        // Favorite button
        updateFavoriteButton(word.isFavorite());
    }

    private void updateFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            btnFavorite.setIconResource(R.drawable.ic_favorite_24);
            btnFavorite.setText("Bỏ yêu thích");
        } else {
            btnFavorite.setIconResource(R.drawable.ic_favorite_border_24);
            btnFavorite.setText("Yêu thích");
        }
    }

    private void playPronunciation() {
        if (word == null || TextUtils.isEmpty(word.getAudioUrl())) {
            Toast.makeText(this, "Không có file phát âm", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(word.getAudioUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> mp.start());

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi phát âm thanh", Toast.LENGTH_SHORT).show());
                return false;
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải âm thanh", Toast.LENGTH_SHORT).show();
        }
    }

    private void practiceWord() {
        Toast.makeText(this, "Bắt đầu luyện tập từ: " + word.getEnglishWord(), Toast.LENGTH_SHORT).show();
        // TODO: Open practice activity
    }

    private void editWord() {
        // TODO: Open edit activity
        Toast.makeText(this, "Chỉnh sửa từ: " + word.getEnglishWord(), Toast.LENGTH_SHORT).show();
    }

    private void toggleFavorite() {
        if (word == null) return;

        boolean newFavoriteState = !word.isFavorite();
        word.setFavorite(newFavoriteState);

        new Thread(() -> {
            int result = wordDAO.updateWord(word);
            runOnUiThread(() -> {
                if (result > 0) {
                    updateFavoriteButton(newFavoriteState);
                    Toast.makeText(this,
                            newFavoriteState ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void deleteWord() {
        if (word == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Xóa từ vựng")
                .setMessage("Bạn có chắc chắn muốn xóa từ \"" + word.getEnglishWord() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    showLoading();
                    new Thread(() -> {
                        int result = wordDAO.deleteWord(word.getId());
                        runOnUiThread(() -> {
                            hideLoading();
                            if (result > 0) {
                                Toast.makeText(this, "Đã xóa từ vựng", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (wordDAO != null) {
            wordDAO.close();
        }
    }
}