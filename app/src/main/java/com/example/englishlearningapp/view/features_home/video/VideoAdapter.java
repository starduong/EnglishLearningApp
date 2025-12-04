package com.example.englishlearningapp.view.features_home.video;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishlearningapp.R;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final List<VideoItem> videos;
    private final OnVideoClickListener listener;

    public interface OnVideoClickListener {
        void onVideoClick(VideoItem video);
    }

    public VideoAdapter(List<VideoItem> videos, OnVideoClickListener listener) {
        this.videos = videos;
        this.listener = listener;
    }

    public void updateVideos(List<VideoItem> newVideos) {
        videos.clear();
        videos.addAll(newVideos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoItem item = videos.get(position);
        holder.bind(item);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVideoClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvTitle;
        TextView tvChannel;
        TextView tvPublishedAt;

        ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvChannel = itemView.findViewById(R.id.tv_channel);
            tvPublishedAt = itemView.findViewById(R.id.tv_published_at);
        }

        void bind(VideoItem item) {
            tvTitle.setText(item.getTitle());
            tvChannel.setText(item.getChannelName());

            // Hiển thị published at (đã format)
            tvPublishedAt.setText(item.getFormattedPublishedAt());

            // Load thumbnail với Glide
            Glide.with(itemView.getContext())
                    .load(item.getThumbnailUrl())
                    .placeholder(R.drawable.ic_video_placeholder)
                    .error(R.drawable.ic_error)
                    .into(imgThumbnail);
        }
    }
}