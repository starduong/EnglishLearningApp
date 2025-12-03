package com.example.englishlearningapp.view.features_home.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.example.englishlearningapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsDetailActivity extends AppCompatActivity {
    private static final String TAG = "NewsDetailActivity";

    // Constants
    private static final String BASE_API_HOST = "https://content.guardianapis.com/";
    private static final String SHOW_FIELDS_PARAM = "show-fields=bodyText";
    private static final int MANUAL_TIMEOUT_MS = 10_000;

    // Models & API
    private NewsItem newsItem;
    private NewsApiService apiService;

    // Views
    private Toolbar toolbar;
    private TextView tvTitle, tvDate, tvSection, tvContent;
    private Button btnReadOriginal, btnListen;
    private ProgressBar progressBar;
    private NestedScrollView scrollView;
    private View errorView;
    private Button btnRetry;

    // TTS
    private TextToSpeech tts;
    private boolean isTtsReady = false;
    private boolean isPlaying = false;

    // Threading
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // 1. Lấy newsItem từ Intent
        retrieveIntentData();
        if (newsItem == null) {
            Toast.makeText(this, "No news data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Init views, toolbar, TTS, API client
        initViews();
        setupToolbar();
        displayBasicInfo();
        setupClickListeners();
        initTts();

        apiService = ApiClientNews.getApiService(this);

        // 3. Bắt đầu load full article
        loadFullArticle();
    }

    // -------------------------
    // Initialization & UI
    // -------------------------
    private void retrieveIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("news_item")) {
            try {
                newsItem = (NewsItem) intent.getSerializableExtra("news_item");
                Log.d(TAG, "Received news item: id=" + newsItem.getId() + " apiUrl=" + newsItem.getApiUrl());
            } catch (Exception e) {
                Log.e(TAG, "Error reading news_item from intent", e);
            }
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        tvSection = findViewById(R.id.tvSection);
        tvContent = findViewById(R.id.tvContent);
        btnReadOriginal = findViewById(R.id.btnReadOriginal);
        btnListen = findViewById(R.id.btnListen);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);
        errorView = findViewById(R.id.errorView);
        btnRetry = findViewById(R.id.btnRetry);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Article Detail");
        }
    }

    private void displayBasicInfo() {
        showLoadingState();
        tvTitle.setText(newsItem.getWebTitle());
        tvDate.setText("Published: " + newsItem.getWebPublicationDate());
        tvSection.setText(newsItem.getSectionName());
        try {
            tvSection.setBackgroundColor(android.graphics.Color.parseColor(newsItem.getSectionColor()));
        } catch (Exception e) {
            // ignore color parse error
        }
        String initialContent = newsItem.getTrailText() != null && !newsItem.getTrailText().isEmpty() ? newsItem.getTrailText() : "Loading article content...";
        tvContent.setText(initialContent);
        btnListen.setVisibility(View.GONE);
    }

    private void showLoadingState() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        if (errorView != null) errorView.setVisibility(View.GONE);
    }

    // -------------------------
    // Click handlers
    // -------------------------
    private void setupClickListeners() {
        btnReadOriginal.setOnClickListener(v -> openInBrowser(newsItem.getWebUrl()));
        btnListen.setOnClickListener(v -> toggleTts());
        btnRetry.setOnClickListener(v -> {
            showLoadingState();
            loadFullArticle();
        });
    }

    private void openInBrowser(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open browser", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error opening browser", e);
        }
    }

    // -------------------------
    // TTS
    // -------------------------
    private void initTts() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int res = tts.setLanguage(Locale.UK);
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language not supported", Toast.LENGTH_SHORT).show();
                } else {
                    isTtsReady = true;
                    Log.d(TAG, "TTS initialized");
                }
            } else {
                Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "TTS init failed, status=" + status);
            }
        });
    }

    private void toggleTts() {
        if (!isTtsReady) {
            Toast.makeText(this, "TTS not ready", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isPlaying) {
            stopTts();
            btnListen.setText("Listen");
            isPlaying = false;
        } else {
            speakArticle();
            btnListen.setText("Stop");
            isPlaying = true;
        }
    }

    private void speakArticle() {
        String text = (newsItem.getWebTitle() != null ? newsItem.getWebTitle() + ". " : "") + (newsItem.getBodyText() != null ? newsItem.getBodyText() : "");
        if (text.length() > 3999) {
            text = text.substring(0, 3999) + "...";
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "article_tts");
        Log.d(TAG, "TTS speaking len=" + text.length());
    }

    private void stopTts() {
        if (tts != null) {
            tts.stop();
            Log.d(TAG, "TTS stopped");
        }
    }

    // -------------------------
    // Load full article
    // -------------------------
    private void loadFullArticle() {
        Log.d(TAG, "=== loadFullArticle ===");
        String apiUrl = safeString(newsItem.getApiUrl());
        if (apiUrl.isEmpty()) {
            showError("No API URL available");
            return;
        }

        // normalize and build final URL for logging only (we will still call via Retrofit @Url)
        String normalized = normalizeApiUrl(apiUrl);
        String finalUrlForLogging = buildApiUrlWithParams(normalized, NewsApiService.API_KEY, SHOW_FIELDS_PARAM);
        Log.d(TAG, "Final URL (for logging): " + finalUrlForLogging);

        // Primary approach: Retrofit call with @Url (full URL)
        Call<NewsResponse> call = apiService.getArticleByUrl(normalized + (normalized.contains("?") ? "&" : "?") + "api-key=" + NewsApiService.API_KEY + "&" + SHOW_FIELDS_PARAM, NewsApiService.API_KEY, "bodyText");
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Primary response code=" + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    if (handleResponseBody(response.body())) return;
                }
                // nếu thất bại -> thử relative path approach
                tryAlternativeApproach();
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Primary call failure: " + t.getMessage(), t);
                tryAlternativeApproach();
            }
        });
    }

    // -------------------------
    // Alternative approach: call API with relative path via Retrofit
    // -------------------------
    private void tryAlternativeApproach() {
        Log.d(TAG, "=== tryAlternativeApproach ===");
        String relative = getRelativePath(newsItem.getApiUrl());
        if (relative == null || relative.isEmpty()) {
            showError("Failed to parse API relative path");
            return;
        }
        Log.d(TAG, "Relative path: " + relative);
        Call<NewsResponse> call = apiService.getArticleDetail(relative, NewsApiService.API_KEY, "bodyText");
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                Log.d(TAG, "Alternative response code=" + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    if (handleResponseBody(response.body())) return;
                }
                // Nếu vẫn không có nội dung -> thử final manual request
                tryFinalApproach();
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e(TAG, "Alternative call failed: " + t.getMessage(), t);
                tryFinalApproach();
            }
        });
    }

    // -------------------------
    // Final fallback: Manual HttpURLConnection request (background thread)
    // -------------------------
    private void tryFinalApproach() {
        Log.d(TAG, "=== tryFinalApproach (manual HTTP) ===");
        String finalFullUrl = buildApiUrlWithParams(newsItem.getApiUrl(), NewsApiService.API_KEY, SHOW_FIELDS_PARAM);

        executor.submit(() -> {
            try {
                java.net.URL url = new java.net.URL(finalFullUrl);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "EnglishLearningApp/1.0");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(MANUAL_TIMEOUT_MS);
                conn.setReadTimeout(MANUAL_TIMEOUT_MS);

                int code = conn.getResponseCode();
                Log.d(TAG, "Manual request response code: " + code);
                if (code == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    in.close();

                    JSONObject root = new JSONObject(sb.toString());
                    JSONObject resp = root.optJSONObject("response");
                    if (resp != null && "ok".equals(resp.optString("status"))) {
                        JSONObject contentObj = resp.optJSONObject("content");
                        JSONObject fields = contentObj != null ? contentObj.optJSONObject("fields") : null;
                        final String bodyText = fields != null ? fields.optString("bodyText", "") : "";

                        runOnUiThread(() -> {
                            if (bodyText != null && !bodyText.isEmpty()) {
                                newsItem.setBodyText(bodyText);
                                displayFullContentAndShow();
                                Toast.makeText(NewsDetailActivity.this, "Article loaded successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                showError("No content found in article (manual request)");
                            }
                        });
                        return;
                    }
                }

                runOnUiThread(() -> showError("Failed to load article (manual request)"));
            } catch (Exception e) {
                Log.e(TAG, "Manual request error", e);
                runOnUiThread(() -> showError("Error: " + e.getMessage()));
            }
        });
    }

    // -------------------------
    // Helpers: parse/normalize URL, build param URL, get relative path
    // -------------------------
    private String normalizeApiUrl(String url) {
        if (url == null) return "";
        String s = url.trim();
        // remove trailing slash
        if (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        return s;
    }

    private String buildApiUrlWithParams(String base, String apiKey, String fieldsParam) {
        if (base == null) return "";
        String b = normalizeApiUrl(base);
        if (b.contains("?")) {
            return b + "&api-key=" + apiKey + "&" + fieldsParam;
        } else {
            return b + "?api-key=" + apiKey + "&" + fieldsParam;
        }
    }

    private String getRelativePath(String fullUrl) {
        try {
            if (fullUrl == null) return "";
            String withoutQuery = fullUrl.contains("?") ? fullUrl.substring(0, fullUrl.indexOf("?")) : fullUrl;
            if (withoutQuery.endsWith("/"))
                withoutQuery = withoutQuery.substring(0, withoutQuery.length() - 1);
            if (withoutQuery.startsWith(BASE_API_HOST)) {
                return withoutQuery.substring(BASE_API_HOST.length());
            }
            return "";
        } catch (Exception e) {
            Log.e(TAG, "Error getting relative path", e);
            return "";
        }
    }

    // -------------------------
    // Handle Retrofit response body: update newsItem and UI
    // -------------------------
    private boolean handleResponseBody(NewsResponse newsResponse) {
        if (newsResponse == null || newsResponse.getResponse() == null) {
            Log.e(TAG, "Invalid response body");
            return false;
        }
        if (!"ok".equals(newsResponse.getResponse().getStatus())) {
            Log.e(TAG, "API status not ok: " + newsResponse.getResponse().getStatus());
            return false;
        }
        if (newsResponse.getResponse().getContent() == null) {
            Log.e(TAG, "No content in response");
            return false;
        }

        // Convert content -> NewsItem (giữ logic cũ)
        NewsItem updated = newsResponse.getResponse().getContent().toNewsItem();
        // preserve original metadata
        updated.setSectionName(newsItem.getSectionName());
        updated.setWebUrl(newsItem.getWebUrl());
        updated.setWebPublicationDate(newsItem.getWebPublicationDate());
        newsItem = updated;

        // Update UI
        runOnUiThread(() -> {
            displayFullContentAndShow();
            btnListen.setVisibility(View.VISIBLE);
            Toast.makeText(NewsDetailActivity.this, "Article loaded successfully", Toast.LENGTH_SHORT).show();
        });
        return true;
    }

    private void displayFullContentAndShow() {
        displayFullContent();
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        if (errorView != null) errorView.setVisibility(View.GONE);
    }

    private void displayFullContent() {
        String content = newsItem.getBodyText();
        if (content != null && !content.trim().isEmpty()) {
            // clean and format a bit
            content = content.replaceAll("\\s+", " ").trim();
            content = content.replace(". ", ".\n\n");
            tvContent.setText(content);
        } else if (newsItem.getTrailText() != null && !newsItem.getTrailText().isEmpty()) {
            tvContent.setText(newsItem.getTrailText());
        } else {
            tvContent.setText("No content available for this article.");
        }
    }

    // -------------------------
    // Error UI
    // -------------------------
    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
        if (errorView != null) {
            errorView.setVisibility(View.VISIBLE);
            TextView tvError = errorView.findViewById(R.id.tvErrorMessage);
            if (tvError != null) tvError.setText(message);
        }
        Toast.makeText(this, "Failed to load article", Toast.LENGTH_SHORT).show();
        Log.e(TAG, message);
    }

    private String safeString(String s) {
        return s == null ? "" : s;
    }

    // -------------------------
    // Menu / share / save
    // -------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            shareArticle();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            saveArticle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareArticle() {
        String snippet = (newsItem.getBodyText() != null && newsItem.getBodyText().length() > 200) ? newsItem.getBodyText().substring(0, 200) + "..." : (newsItem.getBodyText() != null ? newsItem.getBodyText() : newsItem.getWebTitle());
        String shareText = newsItem.getWebTitle() + "\n\n" + snippet + "\n\nRead more: " + newsItem.getWebUrl();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, newsItem.getWebTitle());
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void saveArticle() {
        Toast.makeText(this, "Article saved to favorites", Toast.LENGTH_SHORT).show();
        // TODO: Implement save to database (same as original)
    }

    // -------------------------
    // Lifecycle
    // -------------------------
    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            Log.d(TAG, "TTS destroyed");
        }
        executor.shutdownNow();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
