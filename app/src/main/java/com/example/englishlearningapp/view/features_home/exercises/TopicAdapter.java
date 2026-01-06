package com.example.englishlearningapp.view.features_home.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<Topic> topics;
    private OnTopicClickListener listener;

    public interface OnTopicClickListener {
        void onTopicClick(Topic topic);

        void onFinsh();
    }

    public TopicAdapter(OnTopicClickListener listener) {
        this.topics = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);
        holder.bind(topic);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void updateTopics(List<Topic> newTopics) {
        this.topics.clear();
        this.topics.addAll(newTopics);
        notifyDataSetChanged();
    }

    class TopicViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView ivTopicImage;
        private TextView tvTopicTitle;
        private TextView tvTopicDescription;
        private TextView tvProgress;
        private ProgressBar progressBar;
        private ImageView ivLockIcon;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewTopic);
            ivTopicImage = itemView.findViewById(R.id.ivTopicImage);
            tvTopicTitle = itemView.findViewById(R.id.tvTopicTitle);
            tvTopicDescription = itemView.findViewById(R.id.tvTopicDescription);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            progressBar = itemView.findViewById(R.id.progressBar);
            ivLockIcon = itemView.findViewById(R.id.ivLockIcon);
        }

        public void bind(Topic topic) {
            tvTopicTitle.setText(topic.getTitle());
            tvTopicDescription.setText(topic.getDescription());
            tvProgress.setText(topic.getProgressText());
            
            progressBar.setMax(topic.getTotalExercises());
            progressBar.setProgress(topic.getCompletedExercises());

            // Set topic image based on ID
            setTopicImage(topic.getId());

            // Handle lock/unlock state
            if (topic.isUnlocked()) {
                ivLockIcon.setVisibility(View.GONE);
                cardView.setAlpha(1.0f);
            } else {
                ivLockIcon.setVisibility(View.VISIBLE);
                cardView.setAlpha(0.6f);
            }

            // Click listener
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTopicClick(topic);
                }
            });
        }

        private void setTopicImage(String topicId) {
            int imageResource;
            switch (topicId) {
                case "vietnam":
                    imageResource = R.drawable.topic_vietnam;
                    break;
                case "travel":
                    imageResource = R.drawable.topic_travel;
                    break;
                case "electric_vehicle":
                    imageResource = R.drawable.topic_electric_vehicle;
                    break;
                case "nasa":
                    imageResource = R.drawable.topic_nasa;
                    break;
                case "animals":
                    imageResource = R.drawable.topic_animals;
                    break;
                case "nature":
                    imageResource = R.drawable.topic_nature;
                    break;
                default:
                    imageResource = R.drawable.ic_placeholder; // Default
                    break;
            }
            ivTopicImage.setImageResource(imageResource);
        }
    }
}
