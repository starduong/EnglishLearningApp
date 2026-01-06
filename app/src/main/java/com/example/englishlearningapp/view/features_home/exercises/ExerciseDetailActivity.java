package com.example.englishlearningapp.view.features_home.exercises;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ExerciseDetailActivity extends AppCompatActivity implements ExerciseAdapter.OnExerciseClickListener {

    private static final int REQUEST_CODE_EXERCISE = 1001;
    
    private RecyclerView recyclerViewExercises;
    private ExerciseAdapter exerciseAdapter;
    private ExerciseManager exerciseManager;
    private TextView tvTopicTitle;
    private TextView tvTopicDescription;
    private TextView tvProgress;
    private MaterialButton btnBack;
    
    private String topicId;
    private String topicTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        // Get data from intent
        topicId = getIntent().getStringExtra("TOPIC_ID");
        topicTitle = getIntent().getStringExtra("TOPIC_TITLE");
        
        if (topicId == null) {
            finish();
            return;
        }

        // Initialize Manager
        exerciseManager = new ExerciseManager(this);

        // Setup UI
        initViews();
        setupClickListeners();
        setupRecyclerView();
        loadTopicData();
        loadExercises();
    }

    private void initViews() {
        tvTopicTitle = findViewById(R.id.tvTopicTitle);
        tvTopicDescription = findViewById(R.id.tvTopicDescription);
        tvProgress = findViewById(R.id.tvProgress);
        recyclerViewExercises = findViewById(R.id.recyclerViewExercises);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewExercises.setLayoutManager(layoutManager);
        
        exerciseAdapter = new ExerciseAdapter(this);
        recyclerViewExercises.setAdapter(exerciseAdapter);
    }

    private void loadTopicData() {
        Topic topic = exerciseManager.getTopicById(topicId);
        if (topic != null) {
            tvTopicTitle.setText(topic.getTitle());
            tvTopicDescription.setText(topic.getDescription());
            tvProgress.setText(topic.getProgressText());
        } else {
            tvTopicTitle.setText(topicTitle);
        }
    }

    private void loadExercises() {
        List<Exercise> exercises = exerciseManager.getExercisesByTopic(topicId);
        exerciseAdapter.updateExercises(exercises);
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
        if (exercise.isCompleted()) {
            // Review mode
            // TODO: Implement review mode
            startExercise(exercise, true);
        } else {
            // Practice mode
            startExercise(exercise, false);
        }
    }

    private void startExercise(Exercise exercise, boolean reviewMode) {
        Intent intent = new Intent(this, FillBlanksActivity.class);
        intent.putExtra("EXERCISE_ID", exercise.getId());
        intent.putExtra("REVIEW_MODE", reviewMode);
        startActivityForResult(intent, REQUEST_CODE_EXERCISE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from exercises
        loadTopicData();
        loadExercises();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_EXERCISE && resultCode == RESULT_OK && data != null) {
            // Get exercise completion data
            String exerciseId = data.getStringExtra("EXERCISE_ID");
            boolean completed = data.getBooleanExtra("COMPLETED", false);
            int score = data.getIntExtra("SCORE", 0);
            int correctCount = data.getIntExtra("CORRECT_COUNT", 0);
            int totalQuestions = data.getIntExtra("TOTAL_QUESTIONS", 0);
            float accuracy = data.getFloatExtra("ACCURACY", 0f);
            
            if (completed) {
                // Show completion message
                String message = String.format("🎉 Exercise completed!\nPerfect score: %d points (%d/%d correct - %.0f%%)", 
                    score, correctCount, totalQuestions, accuracy);
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
                
                // Update exercise status in adapter
                exerciseAdapter.markExerciseCompleted(exerciseId, accuracy);
                
                // Refresh progress display
                loadTopicData();
            } else {
                // Show incomplete message
                String message = String.format("📚 Keep practicing!\nScore: %d points (%d/%d correct - %.0f%%)\n❌ Need 100%% to complete!", 
                    score, correctCount, totalQuestions, accuracy);
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFinsh() {
        super.onBackPressed();
        finish();
    }
}
