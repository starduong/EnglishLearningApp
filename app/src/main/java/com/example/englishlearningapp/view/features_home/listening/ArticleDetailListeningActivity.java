package com.example.englishlearningapp.view.features_home.listening;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishlearningapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArticleDetailListeningActivity extends AppCompatActivity {

    ImageButton btnBack;
    TextView tvTitle, tvDuration, tvLevel;
    ImageView ivThumbnail;
    RecyclerView rvContent, rvMultipleChoice;
    ImageButton btnPlayPause, btnRewind, btnForward, btnRepeat;
    SeekBar seekBar;
    TextView tvCurrentTime, tvTotalTime, tvSpeed;
    LinearLayout scriptHeader, exerciseHeader;
    ImageView ivScriptArrow, ivExerciseArrow;
    LinearLayout layoutQuestionButtons;
    com.google.android.material.button.MaterialButton btnSubmit, btnTryAgain;
    NestedScrollView nestedScrollView;

    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    ArticleListening article;
    ContentSegmentListeningAdapter contentAdapter;
    QuestionListeningAdapter questionAdapter;

    boolean isPlaying = false;
    boolean isRepeatMode = false;
    boolean isScriptExpanded = false;
    boolean isExerciseExpanded = false;

    float[] speeds = {1.0f, 1.2f, 1.5f, 0.8f};
    int currentSpeedIndex = 0;
    int lastHighlightedPosition = -1;

    List<QuestionListening> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_article_detail_listening);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        getArticleFromIntent();
        setupUI();
        setupMediaPlayer();
        setupClickListeners();
        setupSectionHeaders();
        loadQuestionsFromArticle();
        resetPlayerState();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tv_article_title);
        tvDuration = findViewById(R.id.tv_article_duration);
        tvLevel = findViewById(R.id.tv_article_level);
        ivThumbnail = findViewById(R.id.iv_article_thumbnail);
        rvContent = findViewById(R.id.rv_content);
        rvMultipleChoice = findViewById(R.id.rv_multiple_choice);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        seekBar = findViewById(R.id.seek_bar);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        btnRewind = findViewById(R.id.btn_rewind);
        btnForward = findViewById(R.id.btn_forward);
        btnRepeat = findViewById(R.id.btn_repeat);
        tvSpeed = findViewById(R.id.tv_speed);
        nestedScrollView = findViewById(R.id.nested_scroll_view);

        // Section headers
        scriptHeader = findViewById(R.id.script_header);
        exerciseHeader = findViewById(R.id.exercise_header);
        ivScriptArrow = findViewById(R.id.iv_script_arrow);
        ivExerciseArrow = findViewById(R.id.iv_exercise_arrow);

        // Question buttons
        layoutQuestionButtons = findViewById(R.id.layout_question_buttons);
        btnSubmit = findViewById(R.id.btn_submit);
        btnTryAgain = findViewById(R.id.btn_try_again);
    }

    private void getArticleFromIntent() {
        article = (ArticleListening) getIntent().getSerializableExtra("article");
        String topicTitle = getIntent().getStringExtra("topic_title");

        if (tvTitle != null) {
            tvTitle.setText(topicTitle != null ? topicTitle : article.title);
        }
    }

    private void setupUI() {
        if (article == null) return;

        // Title
        tvTitle.setText(article.title);

        // Duration
        int minutes = article.duration / 60;
        int seconds = article.duration % 60;
        String durationText = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        tvDuration.setText(durationText);

        // Level
        tvLevel.setText(article.level);

        // Thumbnail
        loadThumbnail();

        // Content
        List<ContentSegmentListening> segments = article.content;
        String audioPath = "listening/audio/" + article.audio;

        contentAdapter = new ContentSegmentListeningAdapter(segments, audioPath);
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(contentAdapter);
    }

    private void loadThumbnail() {
        if (article.thumbnail != null && !article.thumbnail.trim().isEmpty()) {
            String imagePath = "file:///android_asset/" + article.getFullThumbnailPath();
            Glide.with(this).load(imagePath).placeholder(R.drawable.bg_placeholder_topic_listening).error(R.drawable.bg_placeholder_topic_listening).into(ivThumbnail);
        } else {
            ivThumbnail.setImageResource(R.drawable.bg_placeholder_topic_listening);
        }
    }

    private void setupSectionHeaders() {
        // Script section
        scriptHeader.setOnClickListener(v -> toggleScriptVisibility());

        // Exercise section
        exerciseHeader.setOnClickListener(v -> toggleExerciseVisibility());
    }

    private void toggleScriptVisibility() {
        isScriptExpanded = !isScriptExpanded;

        if (isScriptExpanded) {
            rvContent.setVisibility(View.VISIBLE);
            ivScriptArrow.setRotation(180f);
        } else {
            rvContent.setVisibility(View.GONE);
            ivScriptArrow.setRotation(0f);
        }
    }

    private void toggleExerciseVisibility() {
        isExerciseExpanded = !isExerciseExpanded;

        if (isExerciseExpanded) {
            rvMultipleChoice.setVisibility(View.VISIBLE);
            layoutQuestionButtons.setVisibility(View.VISIBLE);
            ivExerciseArrow.setRotation(180f);
        } else {
            rvMultipleChoice.setVisibility(View.GONE);
            layoutQuestionButtons.setVisibility(View.GONE);
            ivExerciseArrow.setRotation(0f);
        }
    }

    private void loadQuestionsFromArticle() {
        if (article != null && article.questions != null && !article.questions.isEmpty()) {
            questions.clear();
            for (int i = 0; i < article.questions.size(); i++) {
                ArticleListening.QuestionData questionData = article.questions.get(i);
                QuestionListening question = new QuestionListening(questionData.no, questionData.question, questionData.options.a, questionData.options.b, questionData.options.c, questionData.answer);
                questions.add(question);
            }

            setupQuestionAdapter();
        }
    }

    private void setupQuestionAdapter() {
        questionAdapter = new QuestionListeningAdapter(questions);
        rvMultipleChoice.setLayoutManager(new LinearLayoutManager(this));
        rvMultipleChoice.setAdapter(questionAdapter);

        questionAdapter.setOnOptionClickListener((position, selectedOption) -> {
            QuestionListening question = questions.get(position);
            question.setSelectedOption(selectedOption);
            questionAdapter.updateQuestion(position, question);

            // Kiểm tra xem tất cả câu hỏi đã được trả lời chưa
            checkAllQuestionsAnswered();
        });

        setupQuestionButtons();
    }

    private void setupQuestionButtons() {
        btnSubmit.setOnClickListener(v -> submitAnswers());
        btnTryAgain.setOnClickListener(v -> tryAgain());

        // Set initial state
        btnSubmit.setVisibility(View.VISIBLE);
        btnTryAgain.setVisibility(View.GONE);
    }

    private void checkAllQuestionsAnswered() {
        boolean allAnswered = true;
        for (QuestionListening question : questions) {
            if (question.getSelectedOption() == null) {
                allAnswered = false;
                break;
            }
        }

        // Có thể thêm visual feedback nếu muốn
        if (allAnswered) {
            btnSubmit.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void submitAnswers() {
        // Kiểm tra xem tất cả câu hỏi đã được trả lời chưa
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getSelectedOption() == null) {
                Toast.makeText(this, "Vui lòng trả lời tất cả câu hỏi!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Hiển thị kết quả
        questionAdapter.setShowAllAnswers(true);

        // Tính điểm
        int correctCount = calculateCorrectAnswers();
        int totalQuestions = questions.size();

        // Hiển thị kết quả
        String resultMessage = String.format("Kết quả: %d/%d câu đúng", correctCount, totalQuestions);
        Toast.makeText(this, resultMessage, Toast.LENGTH_LONG).show();

        // Ẩn nút Submit, hiện nút Try Again
        btnSubmit.setVisibility(View.GONE);
        btnTryAgain.setVisibility(View.VISIBLE);
    }

    private int calculateCorrectAnswers() {
        int correctCount = 0;
        for (QuestionListening question : questions) {
            if (question.isCorrect()) {
                correctCount++;
            }
        }
        return correctCount;
    }

    private void tryAgain() {
        // Reset tất cả câu hỏi
        for (QuestionListening question : questions) {
            question.setSelectedOption(null);
            question.setShowAnswer(false);
        }

        questionAdapter.setShowAllAnswers(false);
        questionAdapter.notifyDataSetChanged();

        // Hiển thị nút Submit, ẩn nút Try Again
        btnSubmit.setVisibility(View.VISIBLE);
        btnTryAgain.setVisibility(View.GONE);
    }

    private void setupMediaPlayer() {
        if (article == null) return;

        try {
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(ArticleDetailListeningActivity.this, "Lỗi audio: " + what, Toast.LENGTH_SHORT).show();
                return true;
            });

            AssetFileDescriptor afd = getAssets().openFd("listening/audio/" + article.audio);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();

            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speeds[currentSpeedIndex]));
            tvSpeed.setText(String.format(Locale.getDefault(), "%.1fx", speeds[currentSpeedIndex]));

            tvTotalTime.setText(formatTime(article.duration * 1000));
            seekBar.setMax(article.duration * 1000);

            mediaPlayer.setOnCompletionListener(mp -> {
                if (isRepeatMode) {
                    handler.postDelayed(() -> {
                        if (isRepeatMode && mediaPlayer != null) {
                            mediaPlayer.seekTo(0);
                            mediaPlayer.start();
                            isPlaying = true;
                            btnPlayPause.setImageResource(R.drawable.ic_pause);
                            contentAdapter.clearAllHighlights();
                            lastHighlightedPosition = -1;
                        }
                    }, 1000);
                } else {
                    isPlaying = false;
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                    seekBar.setProgress(0);
                    tvCurrentTime.setText("00:00");
                    contentAdapter.clearAllHighlights();
                    lastHighlightedPosition = -1;
                }
            });

            mediaPlayer.seekTo(0);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && isPlaying) {
                        int current = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(current);
                        tvCurrentTime.setText(formatTime(current));
                        highlightCurrentSegment(current / 1000.0);
                    }
                    handler.postDelayed(this, 100);
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Lỗi khi tải audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPlayerState() {
        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.ic_play);
        seekBar.setProgress(0);
        tvCurrentTime.setText("00:00");
        lastHighlightedPosition = -1;

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }

    private void highlightCurrentSegment(double currentTime) {
        if (article == null || article.content == null) return;

        int currentActivePosition = -1;

        for (int i = 0; i < article.content.size(); i++) {
            ContentSegmentListening seg = article.content.get(i);
            boolean isActive = currentTime >= seg.start && currentTime < seg.end;

            if (isActive) {
                currentActivePosition = i;
            }

            contentAdapter.updateHighlight(i, isActive);
        }

        if (currentActivePosition != -1 && currentActivePosition != lastHighlightedPosition) {
            scrollToPosition(currentActivePosition);
            lastHighlightedPosition = currentActivePosition;
        }
    }

    private void scrollToPosition(int position) {
        if (rvContent.getLayoutManager() != null) {
            final RecyclerView.ViewHolder holder = rvContent.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                final int y = (int) (rvContent.getY() + holder.itemView.getY());
                nestedScrollView.smoothScrollTo(0, y);
            } else {
                rvContent.scrollToPosition(position);
                nestedScrollView.post(() -> {
                    final RecyclerView.ViewHolder newHolder = rvContent.findViewHolderForAdapterPosition(position);
                    if (newHolder != null) {
                        final int newY = (int) (rvContent.getY() + newHolder.itemView.getY());
                        nestedScrollView.smoothScrollTo(0, newY);
                    }
                });
            }
        }
    }

    private String formatTime(int millis) {
        int seconds = (millis / 1000) % 60;
        int minutes = (millis / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnRewind.setOnClickListener(v -> rewind());
        btnForward.setOnClickListener(v -> forward());
        btnRepeat.setOnClickListener(v -> toggleRepeatMode());
        tvSpeed.setOnClickListener(v -> changeSpeed());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    if (isPlaying) {
                        mediaPlayer.start();
                    }
                }
            }
        });
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;
        if (isPlaying) {
            mediaPlayer.pause();
            btnPlayPause.setImageResource(R.drawable.ic_play);
        } else {
            mediaPlayer.start();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
        }
        isPlaying = !isPlaying;
    }

    private void rewind() {
        if (mediaPlayer == null) return;
        int currentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.seekTo(Math.max(0, currentPosition - 10000)); // 10 seconds
    }

    private void forward() {
        if (mediaPlayer == null) return;
        int currentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.seekTo(Math.min(mediaPlayer.getDuration(), currentPosition + 10000)); // 10 seconds
    }

    private void toggleRepeatMode() {
        isRepeatMode = !isRepeatMode;
        if (isRepeatMode) {
            btnRepeat.setColorFilter(getResources().getColor(R.color.colorPrimary));
            Toast.makeText(this, "Chế độ lặp lại đã bật", Toast.LENGTH_SHORT).show();
        } else {
            btnRepeat.clearColorFilter();
            Toast.makeText(this, "Chế độ lặp lại đã tắt", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeSpeed() {
        currentSpeedIndex = (currentSpeedIndex + 1) % speeds.length;
        float newSpeed = speeds[currentSpeedIndex];

        if (mediaPlayer != null) {
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(newSpeed));
            tvSpeed.setText(String.format(Locale.getDefault(), "%.1fx", newSpeed));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}