package com.example.englishlearningapp.view.features_home.listening;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishlearningapp.R;

import java.util.List;

public class TopicListeningActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTopicTitle;
    private ListView lvArticles;
    private ArticleListeningAdapter adapter;
    private TopicListening topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_topic_listening);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        getTopicFromIntent();
        setupUI();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTopicTitle = findViewById(R.id.tv_topic_title);
        lvArticles = findViewById(R.id.lv_articles);
    }

    private void getTopicFromIntent() {
        topic = (TopicListening) getIntent().getSerializableExtra("topic");
    }

    private void setupUI() {
        if (topic != null) {
            tvTopicTitle.setText(topic.topic);

            List<ArticleListening> articles = topic.articles;
            if (articles != null && !articles.isEmpty()) {
                adapter = new ArticleListeningAdapter(this, articles, topic.topic);
                lvArticles.setAdapter(adapter);
            }
        }
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