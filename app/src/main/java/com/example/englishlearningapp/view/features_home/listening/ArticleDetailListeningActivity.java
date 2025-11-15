package com.example.englishlearningapp.view.features_home.listening;

import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class ArticleDetailListeningActivity extends AppCompatActivity {

    private ImageButton btnBack, btnPlayPause, btnRewind, btnForward, btnRepeat;
    private TextView tvTitle, tvCurrentTime, tvTotalTime, tvSpeed, tvDuration, tvLevel;
    private ImageView ivThumbnail;
    private SeekBar seekBar;
    private RecyclerView rvContent;

    private ArticleListening article;
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ContentSegmentListeningAdapter contentAdapter;
    private boolean isPlaying = false;
    private float[] speeds = {0.5f, 1.0f, 1.5f, 2.0f};
    private int currentSpeedIndex = 1; // Mặc định 1.0x
    private boolean isRepeatMode = false;
    private int lastHighlightedPosition = -1; // Theo dõi vị trí highlight cuối cùng

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

        // ĐẢM BẢO TRẠNG THÁI BAN ĐẦU
        resetPlayerState();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tv_article_title);
        tvDuration = findViewById(R.id.tv_article_duration);
        tvLevel = findViewById(R.id.tv_article_level);
        ivThumbnail = findViewById(R.id.iv_article_thumbnail);
        rvContent = findViewById(R.id.rv_content);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        seekBar = findViewById(R.id.seek_bar);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        btnRewind = findViewById(R.id.btn_rewind);
        btnForward = findViewById(R.id.btn_forward);
        btnRepeat = findViewById(R.id.btn_repeat);
        tvSpeed = findViewById(R.id.tv_speed);
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
            try {
                InputStream ims = getAssets().open(article.getFullThumbnailPath());
                Drawable d = Drawable.createFromStream(ims, null);
                ivThumbnail.setImageDrawable(d);
                ims.close();
            } catch (IOException e) {
                e.printStackTrace();
                // Set default thumbnail if error
                Toast.makeText(this, "Lỗi khi tải ảnh đại diện", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Lỗi khi tải ảnh đại diện", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMediaPlayer() {
        if (article == null) return;

        try {
            mediaPlayer = new MediaPlayer();

            // THÊM XỬ LÝ SỰ KIỆN ERROR
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(ArticleDetailListeningActivity.this, "Lỗi audio: " + what, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            AssetFileDescriptor afd = getAssets().openFd("listening/audio/" + article.audio);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();

            // Set initial speed
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speeds[currentSpeedIndex]));
            tvSpeed.setText(String.format(Locale.getDefault(), "%.1fx", speeds[currentSpeedIndex]));

            tvTotalTime.setText(formatTime(article.duration * 1000));
            seekBar.setMax(article.duration * 1000);

            mediaPlayer.setOnCompletionListener(mp -> {
                if (isRepeatMode) {
                    // Repeat mode: chờ 1 giây rồi phát lại
                    handler.postDelayed(() -> {
                        if (isRepeatMode && mediaPlayer != null) {
                            mediaPlayer.seekTo(0);
                            mediaPlayer.start();
                            isPlaying = true;
                            btnPlayPause.setImageResource(R.drawable.ic_pause);
                            // Clear highlight khi phát lại từ đầu
                            contentAdapter.clearAllHighlights();
                            lastHighlightedPosition = -1;
                        }
                    }, 1000);
                } else {
                    // Normal mode: dừng phát
                    isPlaying = false;
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                    seekBar.setProgress(0);
                    tvCurrentTime.setText("00:00");
                    // Clear highlight khi kết thúc
                    contentAdapter.clearAllHighlights();
                    lastHighlightedPosition = -1;
                }
            });

            // CHẮC CHẮN MEDIA PLAYER ĐANG Ở TRẠNG THÁI STOP
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
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải audio", Toast.LENGTH_SHORT).show();
        }
    }

    // THÊM PHƯƠNG THỨC RESET TRẠNG THÁI PLAYER
    private void resetPlayerState() {
        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.ic_play);
        seekBar.setProgress(0);
        tvCurrentTime.setText("00:00");
        lastHighlightedPosition = -1;

        // Đảm bảo media player không phát
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

        // Tự động scroll đến item đang active nếu có thay đổi
        if (currentActivePosition != -1 && currentActivePosition != lastHighlightedPosition) {
            scrollToPosition(currentActivePosition);
            lastHighlightedPosition = currentActivePosition;
        }
    }

    // PHƯƠNG THỨC TỰ ĐỘNG SCROLL ĐẾN VỊ TRÍ (CẬP NHẬT)
    private void scrollToPosition(int position) {
        if (rvContent == null) return;

        LinearLayoutManager layoutManager = (LinearLayoutManager) rvContent.getLayoutManager();
        if (layoutManager != null) {
            // Sử dụng smoothScrollToPositionWithOffset để scroll chính xác đến trung tâm
            int firstVisible = layoutManager.findFirstVisibleItemPosition();
            int lastVisible = layoutManager.findLastVisibleItemPosition();
            int visibleCount = lastVisible - firstVisible;

            // Tính toán offset để item ở trung tâm
            View child = layoutManager.findViewByPosition(position);
            if (child != null) {
                int itemHeight = child.getHeight();
                int recyclerViewHeight = rvContent.getHeight();
                int offset = (recyclerViewHeight - itemHeight) / 2;
                layoutManager.scrollToPositionWithOffset(position, offset);
            } else {
                // Nếu item chưa visible, scroll với offset mặc định
                layoutManager.scrollToPositionWithOffset(position, 0);
            }
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                    isPlaying = false;
                } else {
                    // ĐẢM BẢO MEDIA PLAYER Ở ĐÚNG VỊ TRÍ TRƯỚC KHI PLAY
                    if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration() - 100) {
                        mediaPlayer.seekTo(0);
                        lastHighlightedPosition = -1;
                    }
                    mediaPlayer.start();
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                    isPlaying = true;
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(formatTime(progress));
                    // Clear all highlights khi user seek manually
                    contentAdapter.clearAllHighlights();
                    lastHighlightedPosition = -1;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar sb) {
                // TẠM DỪNG KHI ĐANG SEEK
                if (isPlaying && mediaPlayer != null) {
                    mediaPlayer.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar sb) {
                // TIẾP TỤC PLAY SAU KHI SEEK NẾU ĐANG PLAY
                if (isPlaying && mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });

        btnRewind.setOnClickListener(v -> {
            seekToRelative(-10000);
            lastHighlightedPosition = -1;
        });

        btnForward.setOnClickListener(v -> {
            seekToRelative(10000);
            lastHighlightedPosition = -1;
        });

        btnRepeat.setOnClickListener(v -> {
            isRepeatMode = !isRepeatMode;
            if (isRepeatMode) {
                btnRepeat.setImageResource(R.drawable.ic_repeat_on);
            } else {
                btnRepeat.setImageResource(R.drawable.ic_repeat);
            }
        });

        tvSpeed.setOnClickListener(v -> {
            currentSpeedIndex = (currentSpeedIndex + 1) % speeds.length;
            float newSpeed = speeds[currentSpeedIndex];
            tvSpeed.setText(String.format(Locale.getDefault(), "%.1fx", newSpeed));
            if (mediaPlayer != null) {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(newSpeed));
            }
        });
    }

    private void seekToRelative(int millis) {
        if (mediaPlayer != null) {
            int newPos = Math.max(0, Math.min(mediaPlayer.getCurrentPosition() + millis, mediaPlayer.getDuration()));
            mediaPlayer.seekTo(newPos);
            seekBar.setProgress(newPos);
            tvCurrentTime.setText(formatTime(newPos));
            contentAdapter.clearAllHighlights();
        }
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // DỪNG AUDIO KHI ACTIVITY BỊ PAUSE
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setImageResource(R.drawable.ic_play);
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