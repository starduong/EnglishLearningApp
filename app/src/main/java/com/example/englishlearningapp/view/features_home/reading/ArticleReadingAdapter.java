package com.example.englishlearningapp.view.features_home.reading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.englishlearningapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ArticleReadingAdapter extends ArrayAdapter<ArticleReading> {

    private final Context context;
    private final List<ArticleReading> articles;
    private OnArticleClickListener onArticleClickListener;

    public ArticleReadingAdapter(Context context, List<ArticleReading> articles) {
        super(context, 0, articles);
        this.context = context;
        this.articles = articles;
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.onArticleClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_article_reading, parent, false);
            holder = new ViewHolder();
            holder.ivArticleImage = convertView.findViewById(R.id.ivArticleImage);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            holder.tvLevel = convertView.findViewById(R.id.tvLevel);
            holder.btnStartReading = convertView.findViewById(R.id.btnStartReading);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ArticleReading article = articles.get(position);

        // Set data to views
        holder.tvTitle.setText(article.getTitle());
        holder.tvDate.setText(article.getCreatedDate());
        holder.tvLevel.setText(article.getLevel());

        // Set image from assets/reading/image/
        setArticleImageFromAssets(holder.ivArticleImage, article.getImage());

        // Set click listener for button - THÊM LOG ĐỂ DEBUG
        holder.btnStartReading.setOnClickListener(v -> {
            Log.d("ArticleAdapter", "Button clicked for article: " + article.getTitle());
            if (onArticleClickListener != null) {
                onArticleClickListener.onArticleClick(article);
            } else {
                Log.e("ArticleAdapter", "onArticleClickListener is null!");
            }
        });

        // Set click listener for entire item - THÊM LOG ĐỂ DEBUG
        convertView.setOnClickListener(v -> {
            Log.d("ArticleAdapter", "Item clicked for article: " + article.getTitle());
            if (onArticleClickListener != null) {
                onArticleClickListener.onArticleClick(article);
            } else {
                Log.e("ArticleAdapter", "onArticleClickListener is null!");
            }
        });

        return convertView;
    }

    private void setArticleImageFromAssets(ImageView imageView, String imageName) {
        try {
            String imagePath = "reading/image/" + imageName;
            InputStream inputStream = context.getAssets().open(imagePath);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.bg_placeholder_topic_listening);
        }
    }

    private static class ViewHolder {
        ImageView ivArticleImage;
        TextView tvTitle;
        TextView tvDate;
        TextView tvLevel;
        com.google.android.material.button.MaterialButton btnStartReading;
    }

    public interface OnArticleClickListener {
        void onArticleClick(ArticleReading article);
    }
}