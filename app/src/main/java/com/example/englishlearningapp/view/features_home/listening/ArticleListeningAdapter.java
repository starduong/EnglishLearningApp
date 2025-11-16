package com.example.englishlearningapp.view.features_home.listening;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.englishlearningapp.R;

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
        holder.bind(article, context);

        // BẮT CLICK TRỰC TIẾP TRÊN ITEM
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleDetailListeningActivity.class);
            intent.putExtra("article", article);
            intent.putExtra("topic_title", topicTitle);
            context.startActivity(intent);

            // Animation mượt
            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

        void bind(ArticleListening article, Context context) {
            tvTitle.setText(article.title);
            tvLevel.setText(article.level);

            int minutes = article.duration / 60;
            int seconds = article.duration % 60;
            tvDuration.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));

            String imagePath = "file:///android_asset/" + article.getFullThumbnailPath();
            Glide.with(context).load(imagePath).placeholder(R.drawable.bg_placeholder_topic_listening).error(R.drawable.bg_placeholder_topic_listening).into(ivThumbnail);
        }
    }
}
