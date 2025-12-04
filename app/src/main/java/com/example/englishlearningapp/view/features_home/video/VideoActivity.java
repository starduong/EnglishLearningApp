package com.example.englishlearningapp.view.features_home.video;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideoActivity extends AppCompatActivity implements VideoAdapter.OnVideoClickListener {

    private ImageButton btnBack;
    private TextInputEditText etSearch;
    private TextInputLayout searchContainer;
    private YouTubePlayerView youTubePlayerView;
    private ProgressBar progressBar;
    private RecyclerView recyclerVideos;
    private VideoAdapter videoAdapter;
    private VideoViewModel videoViewModel;
    private YouTubePlayer currentPlayer;

    private List<VideoItem> videoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupViewModel();
        setupRecyclerView();
        setupYouTubePlayer();
        setupSearch();
        setupClickListeners();

        // Load videos initially
        videoViewModel.searchVideos("");
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);
        searchContainer = findViewById(R.id.searchContainer);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        progressBar = findViewById(R.id.progress_bar);
        recyclerVideos = findViewById(R.id.recycler_videos);
    }

    private void setupViewModel() {
        // Tạo ViewModel với default factory
        videoViewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        // Khởi tạo ViewModel với context
        videoViewModel.init(getApplicationContext());

        // Observe video list
        videoViewModel.getVideoList().observe(this, videos -> {
            videoList.clear();
            videoList.addAll(videos);
            if (videoAdapter != null) {
                videoAdapter.updateVideos(videos);
            }

            // Hide player if no videos
            if (videos.isEmpty() && youTubePlayerView.getVisibility() == View.VISIBLE) {
                youTubePlayerView.setVisibility(View.GONE);
            }
        });

        // Observe loading state
        videoViewModel.getIsLoading().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Observe errors
        videoViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        if (recyclerVideos != null) {
            recyclerVideos.setLayoutManager(new LinearLayoutManager(this));
            videoAdapter = new VideoAdapter(videoList, this);
            recyclerVideos.setAdapter(videoAdapter);
        }
    }

    private void setupYouTubePlayer() {
        if (youTubePlayerView != null) {
            getLifecycle().addObserver(youTubePlayerView);
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    currentPlayer = youTubePlayer;
                }
            });
        }
    }

    private void setupSearch() {
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (videoViewModel != null) {
                        videoViewModel.filterVideos(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = Objects.requireNonNull(etSearch.getText()).toString();
                    if (!query.isEmpty() && videoViewModel != null) {
                        videoViewModel.searchVideos(query);
                    }
                    return true;
                }
                return false;
            });
        }

        if (searchContainer != null) {
            // Clear text icon handler
            searchContainer.setEndIconOnClickListener(v -> {
                if (etSearch != null) {
                    etSearch.setText("");
                }
                if (videoViewModel != null) {
                    videoViewModel.filterVideos("");
                    videoViewModel.searchVideos("");
                }
            });
        }
    }

    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    @Override
    public void onVideoClick(VideoItem video) {
        if (currentPlayer != null) {
            currentPlayer.loadVideo(video.getVideoId(), 0);
            if (youTubePlayerView != null) {
                youTubePlayerView.setVisibility(View.VISIBLE);
            }

            // Scroll to top to show player
            if (recyclerVideos != null) {
                recyclerVideos.smoothScrollToPosition(0);
            }
        } else {
            Toast.makeText(this, "Đang tải player...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (youTubePlayerView != null) {
            youTubePlayerView.release();
        }
    }
}