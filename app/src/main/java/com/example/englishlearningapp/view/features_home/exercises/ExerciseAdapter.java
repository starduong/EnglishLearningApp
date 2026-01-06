package com.example.englishlearningapp.view.features_home.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final List<Exercise> exercises;
    private final OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);

        void onFinsh();
    }

    public ExerciseAdapter(OnExerciseClickListener listener) {
        this.exercises = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void updateExercises(List<Exercise> newExercises) {
        this.exercises.clear();
        this.exercises.addAll(newExercises);
        notifyDataSetChanged();
    }
    
    public void markExerciseCompleted(String exerciseId, float progress) {
        for (int i = 0; i < exercises.size(); i++) {
            Exercise exercise = exercises.get(i);
            if (exercise.getId().equals(exerciseId)) {
                exercise.setCompleted(true);
                exercise.setProgress(progress);
                notifyItemChanged(i);
                break;
            }
        }
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private TextView tvTitle;
        private Chip chipDifficulty;
        private Chip chipType;
        private Chip chipPoints;
        private ImageView ivCompleted;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewExercise);
            tvTitle = itemView.findViewById(R.id.tvExerciseTitle);
            chipDifficulty = itemView.findViewById(R.id.chipDifficulty);
            chipType = itemView.findViewById(R.id.chipType);
            chipPoints = itemView.findViewById(R.id.chipPoints);
            ivCompleted = itemView.findViewById(R.id.ivCompleted);
        }

        public void bind(Exercise exercise) {
            tvTitle.setText(exercise.getTitle());
            chipDifficulty.setText(exercise.getDifficulty().getDisplayName());
            chipType.setText(exercise.getType().getDisplayName());
            chipPoints.setText(exercise.getPoints() + " pts");

            // Show completion status
            if (exercise.isCompleted()) {
                ivCompleted.setVisibility(View.VISIBLE);
                ivCompleted.setImageResource(R.drawable.ic_check_circle);
                cardView.setAlpha(0.8f);
            } else {
                ivCompleted.setVisibility(View.GONE);
                cardView.setAlpha(1.0f);
            }

            // Set difficulty color (for Chips, we use background color)
            switch (exercise.getDifficulty()) {
                case EASY:
                    chipDifficulty.setChipBackgroundColorResource(R.color.green_light);
                    chipDifficulty.setText("🍃 " + exercise.getDifficulty().getDisplayName());
                    break;
                case MEDIUM:
                    chipDifficulty.setChipBackgroundColorResource(R.color.orange_light);
                    chipDifficulty.setText("⚡ " + exercise.getDifficulty().getDisplayName());
                    break;
                case HARD:
                    chipDifficulty.setChipBackgroundColorResource(R.color.purple_light);
                    chipDifficulty.setText("🔥 " + exercise.getDifficulty().getDisplayName());
                    break;
                default:
                    chipDifficulty.setChipBackgroundColorResource(R.color.gray_50);
                    chipDifficulty.setText(exercise.getDifficulty().getDisplayName());
                    break;
            }

            // Click listener
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExerciseClick(exercise);
                }
            });
        }
    }
}
