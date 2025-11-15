package com.example.englishlearningapp.view.features_home.listening;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishlearningapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class ArticleListeningAdapter extends BaseAdapter {
    private final Context context;
    private final List<ArticleListening> articles;
    private final String topicTitle;

    public ArticleListeningAdapter(Context context, List<ArticleListening> articles, String topicTitle) {
        this.context = context;
        this.articles = articles;
        this.topicTitle = topicTitle;
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_article_listening, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ArticleListening article = articles.get(position);
        holder.bind(article);

        // BẮT CLICK TRỰC TIẾP TRÊN ITEM
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleDetailListeningActivity.class);
            intent.putExtra("article", article);
            intent.putExtra("topic_title", topicTitle);
            context.startActivity(intent);

            // Animation mượt
            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                );
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle;
        TextView tvLevel;
        TextView tvDuration;

        ViewHolder(View itemView) {
            ivThumbnail = itemView.findViewById(R.id.iv_article_thumbnail);
            tvTitle = itemView.findViewById(R.id.tv_article_title);
            tvLevel = itemView.findViewById(R.id.tv_article_level);
            tvDuration = itemView.findViewById(R.id.tv_article_duration);
        }

        void bind(ArticleListening article) {
            tvTitle.setText(article.title);
            tvLevel.setText(article.level);

            int minutes = article.duration / 60;
            int seconds = article.duration % 60;
            tvDuration.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));

            String imagePath = article.getFullThumbnailPath();
            try {
                InputStream inputStream = ivThumbnail.getContext().getAssets().open(imagePath);
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                inputStream.close();
                ivThumbnail.setImageDrawable(drawable);
                ivThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (IOException e) {
                ivThumbnail.setImageResource(R.drawable.bg_placeholder_topic_listening);
            }
        }
    }
}