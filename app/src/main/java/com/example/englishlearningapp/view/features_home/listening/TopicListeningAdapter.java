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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.englishlearningapp.R;

import java.util.List;

public class TopicListeningAdapter extends BaseAdapter {

    private final Context context;
    private final List<TopicListening> topics;
    private static final int targetWidth = 600;
    private static final int targetHeight = 400;

    public TopicListeningAdapter(Context context, List<TopicListening> topics) {
        this.context = context;
        this.topics = topics;
    }

    @Override
    public int getCount() {
        return topics.size();
    }

    @Override
    public Object getItem(int position) {
        return topics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gv_topic_listening, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TopicListening topic = topics.get(position);
        holder.bind(topic);

        // BẮT CLICK TRỰC TIẾP TẠI ĐÂY (trên toàn bộ item)
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TopicListeningActivity.class);
            intent.putExtra("topic", topic);

            context.startActivity(intent);

            // Animation chuyển trang
            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                );
            }
        });

        return convertView;
    }

    // ViewHolder pattern
    private static class ViewHolder {
        ImageView ivBg;
        TextView tvTitle;
        TextView tvCount;

        ViewHolder(View itemView) {
            ivBg = itemView.findViewById(R.id.iv_bg_topic_listen);
            tvTitle = itemView.findViewById(R.id.tv_topic_title);
            tvCount = itemView.findViewById(R.id.tv_article_count);
        }

        void bind(TopicListening topic) {
            tvTitle.setText(topic.topic);
            tvCount.setText(String.valueOf(topic.number_of_articles));

            String imagePath = topic.getFullImagePath();

            Glide.with(ivBg.getContext())
                    .load("file:///android_asset/" + imagePath)
                    .apply(new RequestOptions()
                            .override(targetWidth, targetHeight)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true))
                    .into(ivBg);
        }
    }
}