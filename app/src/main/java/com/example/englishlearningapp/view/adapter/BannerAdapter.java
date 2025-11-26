package com.example.englishlearningapp.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final Context context;
    private final List<String> bannerFiles;
    private OnItemClickListener onItemClickListener;

    public BannerAdapter(Context context, List<String> bannerFiles) {
        this.context = context;
        this.bannerFiles = bannerFiles;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        String bannerFile = bannerFiles.get(position);
        Bitmap bitmap = loadBitmapFromAssets(bannerFile);

        if (bitmap != null) {
            holder.bannerImage.setImageBitmap(bitmap);
        } else {
            // Hiển thị placeholder nếu load ảnh thất bại
            holder.bannerImage.setImageResource(R.drawable.ic_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerFiles.size();
    }

    private Bitmap loadBitmapFromAssets(String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}