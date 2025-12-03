package com.example.englishlearningapp.view.features_home.news;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;
    private Context context;

    public NewsAdapter(List<NewsItem> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);

        // Set title
        holder.tvTitle.setText(newsItem.getWebTitle());

        // Set description từ trailText
        holder.tvDescription.setText(newsItem.getShortDescription());

        // Set date
        holder.tvDate.setText(newsItem.getWebPublicationDate());

        // Set section với màu tương ứng
        holder.tvSection.setText(newsItem.getSectionName());
        holder.tvSection.setBackgroundColor(android.graphics.Color.parseColor(newsItem.getSectionColor()));

        // Set click listener
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.putExtra("news_item", newsItem);
            context.startActivity(intent);
        });

        // Long click để share/save (optional)
        holder.cardView.setOnLongClickListener(v -> {
            // Có thể thêm context menu ở đây
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    public void updateNewsList(List<NewsItem> newList) {
        newsList.clear();
        newsList.addAll(newList);
        notifyDataSetChanged();
    }

    public void addNewsList(List<NewsItem> moreList) {
        int startPosition = newsList.size();
        newsList.addAll(moreList);
        notifyItemRangeInserted(startPosition, moreList.size());
    }

    public void clear() {
        newsList.clear();
        notifyDataSetChanged();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvSection;
        TextView tvDate;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvReadMore;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvSection = itemView.findViewById(R.id.tvSection);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvReadMore = itemView.findViewById(R.id.tvReadMore);
        }
    }
}