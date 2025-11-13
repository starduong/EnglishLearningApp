package com.example.englishlearningapp.view.features_home.vocabulary;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;

public class VocabularyDetailActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTitle;
    private RecyclerView rvVocabulary;
    private VocabularyAdapter vocabularyAdapter;
    private TopicVocabulary topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vocabulary_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        getIntentData();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        rvVocabulary = findViewById(R.id.rvVocabulary);
    }

    private void getIntentData() {
        topic = (TopicVocabulary) getIntent().getSerializableExtra("topic");
        if (topic != null) {
            tvTitle.setText(topic.getTopic());
        }
    }

    private void setupRecyclerView() {
        if (topic != null && topic.getWords() != null) {
            vocabularyAdapter = new VocabularyAdapter(topic.getWords());
            rvVocabulary.setLayoutManager(new LinearLayoutManager(this));
            rvVocabulary.setAdapter(vocabularyAdapter);
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