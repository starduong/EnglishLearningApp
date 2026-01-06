package com.example.englishlearningapp.view.features_home.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishlearningapp.R;
import com.google.android.material.button.MaterialButton;

public class GameSettingsActivity extends AppCompatActivity {

    private SeekBar seekDifficulty;
    private ImageView ivDifficultyIcon;
    private TextView tvDifficultyDescription;
    private MaterialButton btnStartGame;

    // Difficulty enum
    public enum Difficulty {
        EASY(0, "🍃 Easy: 8 lives - Perfect for beginners", R.drawable.ic_level_easy, 8),
        MEDIUM(1, "⚡ Medium: 6 lives - Good balance of challenge and fun", R.drawable.ic_level_medium, 6),
        HARD(2, "🔥 Hard: 4 lives - For word masters only!", R.drawable.ic_level_hard, 4);

        private final int position;
        private final String description;
        private final int iconResource;
        private final int maxHearts;

        Difficulty(int position, String description, int iconResource, int maxHearts) {
            this.position = position;
            this.description = description;
            this.iconResource = iconResource;
            this.maxHearts = maxHearts;
        }

        public int getPosition() {
            return position;
        }

        public String getDescription() {
            return description;
        }

        public int getIconResource() {
            return iconResource;
        }

        public int getMaxHearts() {
            return maxHearts;
        }

        public static Difficulty fromPosition(int position) {
            for (Difficulty difficulty : values()) {
                if (difficulty.position == position) {
                    return difficulty;
                }
            }
            return MEDIUM; // Default
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        initViews();
        setupDifficultySeekBar();
        setupStartButton();
        setupBackPressed();
    }

    private void initViews() {
        seekDifficulty = findViewById(R.id.seekDifficulty);
        ivDifficultyIcon = findViewById(R.id.ivDifficultyIcon);
        tvDifficultyDescription = findViewById(R.id.tvDifficultyDescription);
        btnStartGame = findViewById(R.id.btnStartGame);
    }

    private void setupDifficultySeekBar() {
        // Set default to Medium difficulty
        seekDifficulty.setProgress(Difficulty.MEDIUM.getPosition());
        updateDifficultyDisplay(Difficulty.MEDIUM);

        seekDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Difficulty selectedDifficulty = Difficulty.fromPosition(progress);
                    updateDifficultyDisplay(selectedDifficulty);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updateDifficultyDisplay(Difficulty difficulty) {
        ivDifficultyIcon.setImageResource(difficulty.getIconResource());
        tvDifficultyDescription.setText(difficulty.getDescription());
    }

    private void setupStartButton() {
        btnStartGame.setOnClickListener(v -> startHangmanGame());
    }

    private void startHangmanGame() {
        Difficulty selectedDifficulty = Difficulty.fromPosition(seekDifficulty.getProgress());

        Intent intent = new Intent(this, HangmanActivity.class);
        intent.putExtra("DIFFICULTY_LEVEL", selectedDifficulty.name());
        intent.putExtra("MAX_HEARTS", selectedDifficulty.getMaxHearts());

        startActivity(intent);
        finish(); // Close settings activity
    }

    private void setupBackPressed() {
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Intent intent = new Intent(GameSettingsActivity.this, GameListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }

}
