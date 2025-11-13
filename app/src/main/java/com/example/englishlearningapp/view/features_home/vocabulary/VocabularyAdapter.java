package com.example.englishlearningapp.view.features_home.vocabulary;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder> {

    private static final String TAG = "VocabularyAdapter";
    private final List<Vocabulary> vocabularyList;
    private Context context;
    private MediaPlayer mediaPlayer;

    public VocabularyAdapter(List<Vocabulary> vocabularyList) {
        this.vocabularyList = vocabularyList;
    }

    @NonNull
    @Override
    public VocabularyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_vocabulary, parent, false);
        return new VocabularyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyViewHolder holder, int position) {
        Vocabulary vocabulary = vocabularyList.get(position);
        holder.bind(vocabulary);
    }

    @Override
    public int getItemCount() {
        return vocabularyList.size();
    }

    class VocabularyViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardVocabulary;
        private final TextView tvWord;
        private final TextView tvPronunciationUk;
        private final TextView tvPronunciationUs;
        private final TextView tvCefr;
        private final TextView tvMeaningEn;
        private final TextView tvMeaningVi;
        private final TextView tvExampleEn;
        private final TextView tvExampleVi;
        private final ImageView ivWordImage;
        private final ImageButton btnAudioUk;
        private final ImageButton btnAudioUs;

        public VocabularyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardVocabulary = itemView.findViewById(R.id.cardVocabulary);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvPronunciationUk = itemView.findViewById(R.id.tvPronunciationUk);
            tvPronunciationUs = itemView.findViewById(R.id.tvPronunciationUs);
            tvCefr = itemView.findViewById(R.id.tvCefr);
            tvMeaningEn = itemView.findViewById(R.id.tvMeaningEn);
            tvMeaningVi = itemView.findViewById(R.id.tvMeaningVi);
            tvExampleEn = itemView.findViewById(R.id.tvExampleEn);
            tvExampleVi = itemView.findViewById(R.id.tvExampleVi);
            ivWordImage = itemView.findViewById(R.id.ivWordImage);
            btnAudioUk = itemView.findViewById(R.id.btnAudioUk);
            btnAudioUs = itemView.findViewById(R.id.btnAudioUs);
        }

        public void bind(Vocabulary vocabulary) {
            // Set basic word info
            tvWord.setText(vocabulary.getWord());
            tvPronunciationUk.setText(vocabulary.getPronunciation_uk());
            tvPronunciationUs.setText(vocabulary.getPronunciation_us());
            tvCefr.setText(vocabulary.getCefr());

            // Set meaning and examples
            if (vocabulary.getMeaning() != null && !vocabulary.getMeaning().isEmpty()) {
                Vocabulary.Meaning firstMeaning = vocabulary.getMeaning().get(0);

                // Set English meaning
                if (firstMeaning.getEn() != null && !firstMeaning.getEn().isEmpty()) {
                    tvMeaningEn.setText(firstMeaning.getEn());
                    tvMeaningEn.setVisibility(View.VISIBLE);
                } else {
                    tvMeaningEn.setVisibility(View.GONE);
                }

                // Set Vietnamese meaning
                if (firstMeaning.getVi() != null && !firstMeaning.getVi().isEmpty()) {
                    tvMeaningVi.setText(firstMeaning.getVi());
                    tvMeaningVi.setVisibility(View.VISIBLE);
                } else {
                    tvMeaningVi.setVisibility(View.GONE);
                }

                // Set examples
                if (firstMeaning.getExample_en() != null && !firstMeaning.getExample_en().isEmpty()) {
                    tvExampleEn.setText(firstMeaning.getExample_en());
                    tvExampleEn.setVisibility(View.VISIBLE);
                } else {
                    tvExampleEn.setVisibility(View.GONE);
                }

                if (firstMeaning.getExample_vi() != null && !firstMeaning.getExample_vi().isEmpty()) {
                    tvExampleVi.setText(firstMeaning.getExample_vi());
                    tvExampleVi.setVisibility(View.VISIBLE);
                } else {
                    tvExampleVi.setVisibility(View.GONE);
                }
            } else {
                // Hide all meaning and example views if no meaning data
                tvMeaningEn.setVisibility(View.GONE);
                tvMeaningVi.setVisibility(View.GONE);
                tvExampleEn.setVisibility(View.GONE);
                tvExampleVi.setVisibility(View.GONE);
            }

            // Load image from assets
            String imagePath = vocabulary.getFullImagePath();
            if (imagePath != null) {
                try {
                    Drawable drawable = getDrawableFromAssets(context, imagePath);
                    ivWordImage.setImageDrawable(drawable);
                    ivWordImage.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    Log.e(TAG, "Error loading image from assets: " + imagePath, e);
                    ivWordImage.setVisibility(View.GONE);
                }
            } else {
                ivWordImage.setVisibility(View.GONE);
            }

            // Setup audio buttons
            setupAudioButton(btnAudioUk, vocabulary.getFullAudioUkPath(), "UK");
            setupAudioButton(btnAudioUs, vocabulary.getFullAudioUsPath(), "US");
        }

        private void setupAudioButton(ImageButton button, String audioPath, String accent) {
            if (audioPath != null && assetExists(context, audioPath)) {
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(v -> playAudio(audioPath, accent));
            } else {
                button.setVisibility(View.GONE);
            }
        }

        private void playAudio(String audioPath, String accent) {
            // Stop any currently playing audio
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            try {
                mediaPlayer = new MediaPlayer();
                AssetFileDescriptor afd = getAssetFileDescriptor(context, audioPath);
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mediaPlayer.prepare();
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    mediaPlayer = null;
                });
            } catch (IOException e) {
                Log.e(TAG, "Error playing audio: " + audioPath, e);
                Toast.makeText(context, "Không thể phát audio " + accent, Toast.LENGTH_SHORT).show();
            }
        }

        // Helper methods for assets
        private Drawable getDrawableFromAssets(Context context, String path) throws IOException {
            InputStream inputStream = context.getAssets().open(path);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            inputStream.close();
            return drawable;
        }

        private AssetFileDescriptor getAssetFileDescriptor(Context context, String path) throws IOException {
            return context.getAssets().openFd(path);
        }

        private boolean assetExists(Context context, String path) {
            try {
                InputStream inputStream = context.getAssets().open(path);
                inputStream.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        // Release media player when adapter is detached
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
