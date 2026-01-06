package com.example.englishlearningapp.view.features_home.exercises;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ExerciseListActivity extends AppCompatActivity implements TopicAdapter.OnTopicClickListener {

    private RecyclerView recyclerViewTopics;
    private TopicAdapter topicAdapter;
    private ExerciseManager exerciseManager;
    private TextView tvTotalTopics;
    private TextView tvCompletedExercises;
    private TextView tvOverallProgress;
    private MaterialButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        // Initialize Manager
        exerciseManager = new ExerciseManager(this);

        // Setup UI
        initViews();
        setupClickListeners();
        setupRecyclerView();
        loadStatistics();
        loadTopics();
    }

    private void initViews() {
        recyclerViewTopics = findViewById(R.id.recyclerViewTopics);
        tvTotalTopics = findViewById(R.id.tvTotalTopics);
        tvCompletedExercises = findViewById(R.id.tvCompletedExercises);
        tvOverallProgress = findViewById(R.id.tvOverallProgress);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        // Grid layout with 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewTopics.setLayoutManager(layoutManager);
        
        topicAdapter = new TopicAdapter(this);
        recyclerViewTopics.setAdapter(topicAdapter);
    }

    private void loadTopics() {
        List<Topic> topics = exerciseManager.getAllTopics();
        topicAdapter.updateTopics(topics);
    }
    
    private void loadStatistics() {
        List<Topic> topics = exerciseManager.getAllTopics();
        int totalTopics = topics.size();
        int completedExercises = exerciseManager.getTotalCompletedExercises();
        double overallProgress = exerciseManager.getOverallProgress();
        
        tvTotalTopics.setText(String.valueOf(totalTopics));
        tvCompletedExercises.setText(String.valueOf(completedExercises));
        tvOverallProgress.setText(String.format("%.0f%%", overallProgress));
    }

    @Override
    public void onTopicClick(Topic topic) {
        if (topic.isUnlocked()) {
            // Navigate to Exercise Detail Activity
            Intent intent = new Intent(this, ExerciseDetailActivity.class);
            intent.putExtra("TOPIC_ID", topic.getId());
            intent.putExtra("TOPIC_TITLE", topic.getTitle());
            startActivity(intent);
        } else {
            // Show locked message
            // TODO: Implement unlock mechanism
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh progress when returning from exercises
        loadStatistics();
        loadTopics();
    }

    @Override
    public void onFinsh() {
        super.onBackPressed();
        // Return to main menu
        finish();
    }
}
