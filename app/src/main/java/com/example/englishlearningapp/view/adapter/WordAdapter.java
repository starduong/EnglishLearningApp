package com.example.englishlearningapp.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.dao.WordDAO;
import com.example.englishlearningapp.data.model.Word;
import com.example.englishlearningapp.util.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {
    private List<Word> wordList;
    private OnWordItemClickListener listener;
    private WordDAO wordDAO;
    private Context context;

    public interface OnWordItemClickListener {
        void onWordClick(Word word);

        void onFavoriteClick(Word word, boolean isFavorite);

        void onPracticeClick(Word word);

        void onEditClick(Word word);

        void onDeleteClick(Word word);

        void onAudioPlayClick(Word word);
    }

    public WordAdapter(List<Word> wordList, OnWordItemClickListener listener, Context context) {
        this.wordList = wordList;
        this.listener = listener;
        this.context = context;
        this.wordDAO = new WordDAO(context);
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = wordList.get(position);
        holder.bind(word, listener);
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public void removeWord(Word word) {
        int position = wordList.indexOf(word);
        if (position != -1) {
            wordList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateWord(Word word) {
        int position = wordList.indexOf(word);
        if (position != -1) {
            notifyItemChanged(position);
        }
    }

    class WordViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWord, tvPronunciation, tvPartOfSpeech, tvVietnameseMeaning;
        private TextView tvMasteryLevel, tvReviewCount, tvSuccessRate, tvNextReview;
        private ImageButton btnFavorite, btnEdit, btnDelete;
        private MaterialButton btnPractice;
        private View layoutNextReview;
//        private ImageView ivWordImage;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvPronunciation = itemView.findViewById(R.id.tvPronunciation);
            tvPartOfSpeech = itemView.findViewById(R.id.tvPartOfSpeech);
            tvVietnameseMeaning = itemView.findViewById(R.id.tvVietnameseMeaning);
            tvMasteryLevel = itemView.findViewById(R.id.tvMasteryLevel);
            tvReviewCount = itemView.findViewById(R.id.tvReviewCount);
            tvSuccessRate = itemView.findViewById(R.id.tvSuccessRate);
            tvNextReview = itemView.findViewById(R.id.tvNextReview);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnPractice = itemView.findViewById(R.id.btnPractice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            layoutNextReview = itemView.findViewById(R.id.layoutNextReview);
//            ivWordImage = itemView.findViewById(R.id.ivWordImage);
        }

        public void bind(Word word, OnWordItemClickListener listener) {
            // Basic word info
            tvWord.setText(word.getEnglishWord());

            // Pronunciation với icon play
            if (word.getPronunciation() != null && !word.getPronunciation().isEmpty()) {
                tvPronunciation.setText("/" + word.getPronunciation() + "/");
                // Thêm click listener cho pronunciation để play audio
                tvPronunciation.setOnClickListener(v -> {
                    if (listener != null && word.getAudioUrl() != null) {
                        listener.onAudioPlayClick(word);
                    }
                });
            } else {
                tvPronunciation.setText("");
                tvPronunciation.setOnClickListener(null);
            }

            tvPartOfSpeech.setText(word.getPartOfSpeech());
            tvVietnameseMeaning.setText(word.getVietnameseMeaning());

            // Hiển thị ảnh nếu có
//            if (word.getImageUrl() != null && !word.getImageUrl().isEmpty()) {
//                ivWordImage.setVisibility(View.VISIBLE);
//                Glide.with(itemView.getContext())
//                        .load(word.getImageUrl())
//                        .transform(new CenterCrop(), new RoundedCorners(8))
//                        .placeholder(R.drawable.ic_image_placeholder)
//                        .error(R.drawable.ic_image_error)
//                        .into(ivWordImage);
//
//                // Click ảnh để xem toàn màn hình
//                ivWordImage.setOnClickListener(v -> {
//                    if (listener != null) {
//                        listener.onWordClick(word); // Hoặc có thể tạo listener riêng cho ảnh
//                    }
//                });
//            } else {
//                ivWordImage.setVisibility(View.GONE);
//            }

            // Stats
            tvMasteryLevel.setText(word.getMasteryLevelText());
            tvReviewCount.setText(String.valueOf(word.getReviewCount()));

            float successRate = (float) word.getSuccessRate();
            tvSuccessRate.setText(String.format(Locale.getDefault(), "%.0f%%", successRate));
            // Đổi màu theo tỉ lệ thành công
            if (successRate >= 80) {
                tvSuccessRate.setTextColor(itemView.getContext().getColor(R.color.success_green));
            } else if (successRate >= 50) {
                tvSuccessRate.setTextColor(itemView.getContext().getColor(R.color.warning_orange));
            } else {
                tvSuccessRate.setTextColor(itemView.getContext().getColor(R.color.error_red));
            }

            // Next review date
            if (word.needsReview()) {
                tvNextReview.setText("Hôm nay");
                tvNextReview.setTextColor(itemView.getContext().getColor(R.color.urgent_red));
                layoutNextReview.setVisibility(View.VISIBLE);
            } else {
                long nextReview = word.getNextReviewDate();
                if (nextReview > 0) {
                    String dateStr = DateUtils.getRelativeDate(nextReview);
                    tvNextReview.setText(dateStr);
                    tvNextReview.setTextColor(itemView.getContext().getColor(R.color.primary_blue));
                    layoutNextReview.setVisibility(View.VISIBLE);
                } else {
                    layoutNextReview.setVisibility(View.GONE);
                }
            }

            // Favorite button
            updateFavoriteButton(word.isFavorite());
            btnFavorite.setOnClickListener(v -> {
                boolean newFavoriteState = !word.isFavorite();
                word.setFavorite(newFavoriteState);
                updateFavoriteButton(newFavoriteState);
                if (listener != null) {
                    listener.onFavoriteClick(word, newFavoriteState);
                }
            });

            // Practice button
            btnPractice.setOnClickListener(v -> {
                if (listener != null) {
                    // Hiển thị dialog lựa chọn trước
                    showPracticeOptionsDialog(word, listener);
                }
            });

            // Edit button
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(word);
                }
            });

            // Delete button với confirm dialog
            btnDelete.setOnClickListener(v -> {
                showDeleteConfirmationDialog(word, listener);
            });

            // Item click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWordClick(word);
                }
            });
        }

        private void updateFavoriteButton(boolean isFavorite) {
            if (isFavorite) {
                btnFavorite.setImageResource(R.drawable.ic_favorite_24);
                btnFavorite.setColorFilter(itemView.getContext().getColor(R.color.favorite_red));
            } else {
                btnFavorite.setImageResource(R.drawable.ic_favorite_border_24);
                btnFavorite.setColorFilter(itemView.getContext().getColor(R.color.gray));
            }
        }

        private void showDeleteConfirmationDialog(Word word, OnWordItemClickListener listener) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Xóa từ vựng")
                    .setMessage("Bạn có chắc chắn muốn xóa từ \"" + word.getEnglishWord() + "\"?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        if (listener != null) {
                            listener.onDeleteClick(word);
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }

        private void showPracticeOptionsDialog(Word word, OnWordItemClickListener listener) {
            String[] practiceOptions = {
                    "Đưa vào danh sách cần ôn",
                    "Luyện tập ngay",
                    "Hủy"
            };

            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Luyện tập từ: " + word.getEnglishWord())
                    .setItems(practiceOptions, (dialog, which) -> {
                        switch (which) {
                            case 0: // Ôn tập ngay
                                // Đặt lại ngày ôn tập về hôm nay
                                word.setNextReviewDate(System.currentTimeMillis());
                                word.setLastReviewed(System.currentTimeMillis());
                                word.setReviewCount(word.getReviewCount() + 1);

                                // Cập nhật vào database
                                wordDAO.updateWord(word);

                                // Thông báo
                                Toast.makeText(itemView.getContext(),
                                        "Đã đưa từ \"" + word.getEnglishWord() + "\" vào danh sách cần ôn",
                                        Toast.LENGTH_SHORT).show();


                                break;

                            case 1:
                                // Gọi callback để cập nhật UI
                                if (listener != null) {
                                    listener.onPracticeClick(word);
                                }
                                break;
                            case 2: // Hủy
                                // Không làm gì
                                break;
                        }
                    })
                    .setNegativeButton("Đóng", null)
                    .show();
        }
    }
}