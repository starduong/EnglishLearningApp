package com.example.englishlearningapp.view.features_home.reading;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishlearningapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadingActivity extends AppCompatActivity {

    private static final String TAG = "ReadingActivity";
    private ImageButton btnBack;
    private ListView listViewArticles;
    private MaterialButton btnStartReading;
    private ArticleReadingAdapter adapter;
    private List<ArticleReading> articles;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reading);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadArticlesFromJson();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        listViewArticles = findViewById(R.id.listViewArticles);
        btnStartReading = findViewById(R.id.btnStartReading);
    }

    private void loadArticlesFromJson() {
        executor.execute(() -> {
            try {
                InputStream inputStream = getAssets().open("reading/data_reading.json");
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();

                String json = new String(buffer, StandardCharsets.UTF_8);

                Log.d(TAG, "JSON loaded successfully, length: " + json.length());

                // Sử dụng Gson thông thường vì không cần BaseQuestionDeserializer
                Gson gson = new GsonBuilder().create();

                ReadingList readingList = gson.fromJson(json, ReadingList.class);

                if (readingList != null && readingList.getArticles() != null) {
                    articles = readingList.getArticles();
                    Log.d(TAG, "Found " + articles.size() + " articles");

                    // Log thông tin chi tiết về articles
                    for (ArticleReading article : articles) {
                        Log.d(TAG, "Article: " + article.getTitle() +
                                ", ID: " + article.getArticleId() +
                                ", Level: " + article.getLevel());

                        if (article.getContent() != null) {
                            Log.d(TAG, "Content has " +
                                    article.getContent().getReadingTextEn().size() + " EN paragraphs, " +
                                    article.getContent().getVocabulary().size() + " vocabulary items, " +
                                    article.getContent().getExercises().size() + " exercises");
                        }
                    }
                } else {
                    Log.e(TAG, "ReadingList or articles is null");
                }

                handler.post(() -> {
                    if (articles != null && !articles.isEmpty()) {
                        Log.d(TAG, "Successfully loaded " + articles.size() + " articles");
                        setupListView();

                        if (btnStartReading != null) {
                            btnStartReading.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e(TAG, "Articles is null or empty");
                        Toast.makeText(ReadingActivity.this,
                                "No articles found or failed to parse JSON.",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Failed to load articles", e);
                handler.post(() -> {
                    Toast.makeText(ReadingActivity.this,
                            "Failed to load articles: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void setupListView() {
        if (articles == null || articles.isEmpty()) {
            Log.e(TAG, "Cannot setup list view - articles is null or empty");
            return;
        }

        adapter = new ArticleReadingAdapter(this, articles);

        // Thiết lập listener cho adapter
        adapter.setOnArticleClickListener(new ArticleReadingAdapter.OnArticleClickListener() {
            @Override
            public void onArticleClick(ArticleReading article) {
                Log.d(TAG, "Adapter listener triggered for: " + article.getTitle());
                openArticleDetail(article);
            }
        });

        listViewArticles.setAdapter(adapter);

        // Xử lý click trực tiếp trên list item
        listViewArticles.setOnItemClickListener((parent, view, position, id) -> {
            if (position < articles.size()) {
                ArticleReading article = articles.get(position);
                Log.d(TAG, "List item clicked for: " + article.getTitle());
                openArticleDetail(article);
            }
        });
    }

    private void openArticleDetail(ArticleReading article) {
        if (article == null) {
            Log.e(TAG, "Cannot open article detail - article is null");
            Toast.makeText(this, "Article data is corrupted", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Opening article detail: " + article.getTitle());
        Intent intent = new Intent(this, ArticleReadingDetailActivity.class);

        // Chuyển đối tượng article thành JSON string
        String articleJson = new Gson().toJson(article);
        intent.putExtra("article", articleJson);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setupClickListeners() {
        // Nút back
        btnBack.setOnClickListener(v -> finish());

        // Nút Start Reading - mở article đầu tiên
        if (btnStartReading != null) {
            btnStartReading.setOnClickListener(v -> {
                Log.d(TAG, "Start Reading button clicked");
                if (articles != null && !articles.isEmpty()) {
                    ArticleReading firstArticle = articles.get(0);
                    openArticleDetail(firstArticle);
                } else {
                    Toast.makeText(this, "No articles available", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Start Reading clicked but no articles available");
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dọn dẹp executor để tránh memory leak
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}