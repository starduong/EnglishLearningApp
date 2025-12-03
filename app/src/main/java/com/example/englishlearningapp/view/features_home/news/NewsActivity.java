package com.example.englishlearningapp.view.features_home.news;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.englishlearningapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {

    private static final String TAG = "NewsActivity";

    // Views từ layout mới
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Empty state views
    private LinearLayout emptyView;
    private TextView tvEmptyTitle;
    private TextView tvEmptyMessage;
    private Button btnRetry;

    // Error state views
    private LinearLayout errorView;
    private TextView tvErrorTitle;
    private TextView tvErrorMessage;
    private Button btnErrorRetry;

    // Adapter & Data
    private NewsAdapter adapter;
    private final List<NewsItem> newsList = new ArrayList<>();

    // API
    private NewsApiService apiService;

    // Current category
    private String currentCategory = "all";
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Initialize views
        initViews();

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("The Guardian News");
        }

        // Setup RecyclerView
        setupRecyclerView();

        // Initialize API service
        apiService = ApiClientNews.getApiService(this);

        // Setup click listeners
        setupClickListeners();

        // Load news
        loadNews(currentCategory, currentPage, false);
    }

    private void initViews() {
        // Main views
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Empty state
        emptyView = findViewById(R.id.emptyView);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        btnRetry = findViewById(R.id.btnRetry);

        // Error state
        errorView = findViewById(R.id.errorView);
        tvErrorTitle = findViewById(R.id.tvErrorTitle);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnErrorRetry = findViewById(R.id.btnErrorRetry);
    }

    private void setupRecyclerView() {
        adapter = new NewsAdapter(newsList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        // Load more khi scroll đến cuối
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && hasMorePages && dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= 10) {
                        currentPage++;
                        loadNews(currentCategory, currentPage, true);
                    }
                }
            }
        });
    }

    private void setupClickListeners() {
        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            hasMorePages = true;
            loadNews(currentCategory, currentPage, false);
        });

        // Retry buttons
        btnRetry.setOnClickListener(v -> {
            currentPage = 1;
            hasMorePages = true;
            loadNews(currentCategory, currentPage, false);
        });

        btnErrorRetry.setOnClickListener(v -> {
            currentPage = 1;
            hasMorePages = true;
            loadNews(currentCategory, currentPage, false);
        });
    }

    private void loadNews(String category, int page, boolean isLoadMore) {
        if (!isLoadMore && !swipeRefreshLayout.isRefreshing()) {
            showLoadingState();
        }
        isLoading = true;

        // Chuẩn bị query dựa trên category
        String query = "";
        String section = "";

        switch (category) {
            case "education":
                query = "education OR learning OR English";
                break;
            case "technology":
                section = "technology";
                break;
            case "business":
                section = "business";
                break;
            case "sport":
                section = "sport";
                break;
            case "culture":
                section = "culture";
                break;
            default:
                // Tất cả tin tức
                break;
        }

        // Fields cần lấy - CHỈ LẤY trailText (không có thumbnail)
        String fields = "trailText";

        Call<NewsResponse> call;

        if (category.equals("education")) {
            call = apiService.getEducationNews(
                    NewsApiService.API_KEY,
                    query,
                    fields,
                    20,
                    "newest"
            );
        } else if (!section.isEmpty()) {
            call = apiService.searchNews(
                    NewsApiService.API_KEY,
                    "",
                    section,
                    fields,
                    20,
                    "newest",
                    page
            );
        } else {
            call = apiService.getLatestNews(
                    NewsApiService.API_KEY,
                    fields,
                    20,
                    "newest"
            );
        }

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                isLoading = false;
                hideLoadingStates();
                swipeRefreshLayout.setRefreshing(false);

                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    NewsResponse newsResponse = response.body();

                    if (newsResponse.getResponse() != null &&
                            newsResponse.getResponse().getStatus().equals("ok")) {

                        List<NewsResponse.Result> results = newsResponse.getResponse().getResults();
                        Log.d(TAG, "Number of items: " + (results != null ? results.size() : 0));

                        if (results != null && !results.isEmpty()) {
                            if (!isLoadMore) {
                                newsList.clear();
                            }

                            for (NewsResponse.Result result : results) {
                                NewsItem newsItem = result.toNewsItem();
                                newsList.add(newsItem);
                            }

                            adapter.notifyDataSetChanged();
                            showContentState();

                            // Kiểm tra còn trang tiếp theo không
                            int totalItems = newsResponse.getResponse().getTotal();
                            hasMorePages = newsList.size() < totalItems;

                            if (!isLoadMore) {
                                Toast.makeText(NewsActivity.this,
                                        "✓ Loaded " + results.size() + " news items",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // Log first item for debugging
                            if (!newsList.isEmpty()) {
                                NewsItem first = newsList.get(0);
                                Log.d(TAG, "First item - Title: " + first.getWebTitle());
                                Log.d(TAG, "First item - Section: " + first.getSectionName());
                                Log.d(TAG, "First item - TrailText: " + first.getTrailText());
                            }
                        } else {
                            if (!isLoadMore) {
                                showEmptyState("No Articles Found",
                                        "Try changing the category or check back later");
                            } else {
                                hasMorePages = false;
                                Toast.makeText(NewsActivity.this,
                                        "No more articles", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        if (!isLoadMore) {
                            showErrorState("API Error",
                                    "Server response error. Please try again.");
                        }
                    }
                } else {
                    if (!isLoadMore) {
                        showErrorState("Loading Failed",
                                "Failed to load news: HTTP " + response.code());
                    }
                    Log.e(TAG, "API Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                isLoading = false;
                hideLoadingStates();
                swipeRefreshLayout.setRefreshing(false);

                Log.e(TAG, "Network error: " + t.getMessage(), t);

                if (!isLoadMore) {
                    showErrorState("Network Error",
                            "Check your internet connection and try again");
                }
            }
        });
    }

    // Helper methods for state management
    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    private void hideLoadingStates() {
        progressBar.setVisibility(View.GONE);
    }

    private void showContentState() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    private void showEmptyState(String title, String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        tvEmptyTitle.setText(title);
        tvEmptyMessage.setText(message);
    }

    private void showErrorState(String title, String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        tvErrorTitle.setText(title);
        tvErrorMessage.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_refresh) {
            currentPage = 1;
            hasMorePages = true;
            loadNews(currentCategory, currentPage, false);
            return true;
        } else if (id == R.id.action_level_all) {
            currentCategory = "all";
            currentPage = 1;
            hasMorePages = true;
            loadNews(currentCategory, currentPage, false);
            Toast.makeText(this, "Showing all news", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_level_1) {
            currentCategory = "education";
            currentPage = 1;
            hasMorePages = true;
            loadNews(currentCategory, currentPage, false);
            Toast.makeText(this, "Showing education news", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_level_2) {
            currentCategory = "technology";
            currentPage = 1;
            hasMorePages = true;
            loadNews(currentCategory, currentPage, false);
            Toast.makeText(this, "Showing technology news", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_level_3) {
            currentCategory = "business";
            currentPage = 1;
            hasMorePages = true;
            loadNews(currentCategory, currentPage, false);
            Toast.makeText(this, "Showing business news", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_level_sport) {
            currentCategory = "sport";
            currentPage = 1;
            hasMorePages = true;
            loadNews(currentCategory, currentPage, false);
            Toast.makeText(this, "Showing sports news", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_level_culture) {
            currentCategory = "culture";
            currentPage = 1;
            hasMorePages = true;
            loadNews(currentCategory, currentPage, false);
            Toast.makeText(this, "Showing culture news", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}