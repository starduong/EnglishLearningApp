package com.example.englishlearningapp.view.features_home.listening;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishlearningapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListeningActivity extends AppCompatActivity {

    private static final String TAG = "ListeningActivity";
    private ImageButton btnBack;
    private GridView gvTopics;
    private TopicListeningAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listening);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadTopicsFromAssets();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        gvTopics = findViewById(R.id.gv_topics);
    }

    /**
     * Đọc và parse JSON từ assets/listening/data_listening.json trên background thread
     */
    private void loadTopicsFromAssets() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            List<TopicListening> topics = new ArrayList<>();

            try {
                // Đọc file JSON
                InputStream inputStream = getAssets().open("listening/data_listening.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder jsonString = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }

                reader.close();
                inputStream.close();

                // Parse JSON
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonString.toString(), JsonObject.class);
                JsonArray topicsArray = jsonObject.getAsJsonArray("topics");

                if (topicsArray != null) {
                    for (JsonElement element : topicsArray) {
                        TopicListening topic = gson.fromJson(element, TopicListening.class);
                        topics.add(topic);
                    }
                }

                Log.d(TAG, "Loaded " + topics.size() + " topics from JSON");

            } catch (IOException e) {
                Log.e(TAG, "Error reading JSON file", e);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing JSON", e);
            }

            // Trả kết quả về main thread
            handler.post(() -> {
                if (!topics.isEmpty()) {
                    adapter = new TopicListeningAdapter(this, topics);
                    gvTopics.setAdapter(adapter);

                } else {
                    Log.w(TAG, "No topics loaded");
                }
            });
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
