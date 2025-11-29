package com.example.englishlearningapp.view.features_home.reading;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;

import java.util.List;

public class VocabularyReadingAdapter extends RecyclerView.Adapter<VocabularyReadingAdapter.ViewHolder> {

    private static final String TAG = "VocabularyAdapter";
    private final List<VocabularyReading> vocabularyList;

    public VocabularyReadingAdapter(List<VocabularyReading> vocabularyList) {
        // Đảm bảo list không null
        this.vocabularyList = vocabularyList != null ? vocabularyList : java.util.Collections.emptyList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vocabulary_reading, parent, false); // SỬA TÊN LAYOUT
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (vocabularyList == null || position < 0 || position >= vocabularyList.size()) {
                Log.e(TAG, "Invalid position: " + position);
                return;
            }

            VocabularyReading vocabulary = vocabularyList.get(position);

            // Kiểm tra null trước khi set text
            if (holder.tvWord != null) {
                holder.tvWord.setText(vocabulary.getWord() != null ? vocabulary.getWord() : "N/A");
            } else {
                Log.e(TAG, "tvWord is null at position: " + position);
            }

            if (holder.tvDefinition != null) {
                holder.tvDefinition.setText(vocabulary.getDefinition() != null ? vocabulary.getDefinition() : "No definition");
            } else {
                Log.e(TAG, "tvDefinition is null at position: " + position);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onBindViewHolder at position: " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return vocabularyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord;
        TextView tvDefinition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                tvWord = itemView.findViewById(R.id.tvWord);
                tvDefinition = itemView.findViewById(R.id.tvDefinition);

                // Log để debug
                if (tvWord == null) {
                    Log.e(TAG, "tvWord not found in layout");
                }
                if (tvDefinition == null) {
                    Log.e(TAG, "tvDefinition not found in layout");
                }

            } catch (Exception e) {
                Log.e(TAG, "Error in ViewHolder constructor", e);
            }
        }
    }
}