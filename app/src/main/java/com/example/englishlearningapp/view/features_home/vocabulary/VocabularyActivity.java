package com.example.englishlearningapp.view.features_home.vocabulary;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VocabularyActivity extends AppCompatActivity implements TopicVocabularyAdapter.OnTopicClickListener {

    private static final String TAG = "VocabularyActivity";
    private static final String VOCABULARY_FILE_PATH = "vocabulary/vocabulary.json";

    private ImageButton btnBack;
    private RecyclerView rvTopics;
    private TopicVocabularyAdapter topicAdapter;
    private final List<TopicVocabulary> topicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vocabulary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        loadVocabularyData();
        setupClickListeners();
    }

    /**
     * Ánh xạ view
     */
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvTopics = findViewById(R.id.rvTopics);
    }

    /**
     * Thiết lập RecyclerView
     */
    private void setupRecyclerView() {
        topicAdapter = new TopicVocabularyAdapter(topicList, this);
        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        rvTopics.setAdapter(topicAdapter);
    }

    /**
     * Tải dữ liệu từ file JSON trong assets
     */
    private void loadVocabularyData() {
        try {
            String jsonData = loadJSONFromAsset(VOCABULARY_FILE_PATH);
            if (jsonData == null || jsonData.isEmpty()) {
                Toast.makeText(this, "Không thể tải dữ liệu từ vựng", Toast.LENGTH_SHORT).show();
                return;
            }

            Gson gson = new Gson();
            Type topicListType = new TypeToken<List<TopicVocabulary>>() {
            }.getType();
            List<TopicVocabulary> loadedTopics = gson.fromJson(jsonData, topicListType);

            if (loadedTopics == null || loadedTopics.isEmpty()) {
                Toast.makeText(this, "Không có dữ liệu từ vựng khả dụng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gán base path cho các từ vựng trong từng chủ đề
            for (TopicVocabulary topic : loadedTopics) {
                if (topic.getWords() != null) {
                    for (Vocabulary word : topic.getWords()) {
                        word.setAssetsBasePath("vocabulary/");
                    }
                }
            }

            topicList.clear();
            topicList.addAll(loadedTopics);
            topicAdapter.updateTopics(loadedTopics);

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi tải dữ liệu từ vựng", e);
            Toast.makeText(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Đọc nội dung file JSON từ thư mục assets
     */
    private String loadJSONFromAsset(String fileName) {
        StringBuilder jsonBuilder = new StringBuilder();
        AssetManager assetManager = getAssets();

        try (InputStream is = assetManager.open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

        } catch (IOException e) {
            Log.e(TAG, "Lỗi đọc file JSON: " + fileName, e);
            return null;
        }

        return jsonBuilder.toString();
    }

    /**
     * Thiết lập các sự kiện click
     */
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Khi người dùng chọn 1 chủ đề từ vựng
     */
    @Override
    public void onTopicClick(TopicVocabulary topic) {
        Intent intent = new Intent(this, VocabularyDetailActivity.class);
        intent.putExtra("topic", topic);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
    }

    /**
     * Hiệu ứng khi quay lại
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}