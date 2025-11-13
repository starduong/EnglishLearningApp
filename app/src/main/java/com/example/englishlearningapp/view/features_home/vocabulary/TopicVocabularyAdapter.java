package com.example.englishlearningapp.view.features_home.vocabulary;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách các chủ đề từ vựng.
 * Sử dụng DiffUtil để cập nhật dữ liệu hiệu quả hơn thay vì notifyDataSetChanged().
 */
public class TopicVocabularyAdapter extends RecyclerView.Adapter<TopicVocabularyAdapter.TopicViewHolder> {

    private final List<TopicVocabulary> topicList = new ArrayList<>();
    private final OnTopicClickListener listener;

    /**
     * Interface callback khi người dùng click vào 1 chủ đề.
     */
    public interface OnTopicClickListener {
        void onTopicClick(TopicVocabulary topic);
    }

    public TopicVocabularyAdapter(List<TopicVocabulary> initialTopics, OnTopicClickListener listener) {
        if (initialTopics != null) {
            topicList.addAll(initialTopics);
        }
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic_vocabulary, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        TopicVocabulary topic = topicList.get(position);
        holder.bind(topic);
        holder.cardTopic.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTopicClick(topic);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    /**
     * Cập nhật danh sách chủ đề từ vựng bằng DiffUtil (hiệu năng cao hơn notifyDataSetChanged)
     */
    public void updateTopics(List<TopicVocabulary> newTopics) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return topicList.size();
            }

            @Override
            public int getNewListSize() {
                return newTopics != null ? newTopics.size() : 0;
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                // So sánh ID hoặc tên chủ đề (tùy theo model bạn có)
                String oldTopic = topicList.get(oldItemPosition).getTopic();
                String newTopic = newTopics.get(newItemPosition).getTopic();
                return oldTopic != null && oldTopic.equals(newTopic);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return topicList.get(oldItemPosition).equals(newTopics.get(newItemPosition));
            }
        });

        topicList.clear();
        if (newTopics != null) {
            topicList.addAll(newTopics);
        }
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * ViewHolder hiển thị thông tin một chủ đề từ vựng.
     */
    static class TopicViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardTopic;
        private final TextView tvTopicName;
        private final TextView tvWordCount;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTopic = itemView.findViewById(R.id.cardTopic);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            tvWordCount = itemView.findViewById(R.id.tvWordCount);
        }

        @SuppressLint("SetTextI18n")
        public void bind(TopicVocabulary topic) {
            tvTopicName.setText(topic.getTopic());
            int wordCount = (topic.getWords() != null) ? topic.getWords().size() : 0;
            tvWordCount.setText(wordCount + " từ");
        }
    }
}
