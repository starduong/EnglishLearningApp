package com.example.englishlearningapp.view.features_home.video;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VideoItem {
    private String id;
    private String title;
    private String description;
    private String channelName;
    private String duration;
    private String thumbnailUrl;
    private String videoId;
    private String publishedAt;

    public VideoItem(String id, String title, String description, String channelName, String duration, String thumbnailUrl, String videoId, String publishedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.channelName = channelName;
        this.duration = duration;
        this.thumbnailUrl = thumbnailUrl;
        this.videoId = videoId;
        this.publishedAt = publishedAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getDuration() {
        return duration;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    // Format publishedAt thành thời gian dễ đọc
    public String getFormattedPublishedAt() {
        if (publishedAt == null || publishedAt.isEmpty()) {
            return "Vừa xong";
        }

        try {
            // Parse ISO 8601 format: "2023-10-15T14:30:00Z"
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            Date publishDate = isoFormat.parse(publishedAt);
            Date now = new Date();

            assert publishDate != null;
            long diffInMillis = now.getTime() - publishDate.getTime();

            // Tính số ngày
            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (days == 0) {
                // Hôm nay
                long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
                if (hours == 0) {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
                    if (minutes == 0) {
                        return "Vừa xong";
                    }
                    return minutes + " phút trước";
                }
                return hours + " giờ trước";
            } else if (days < 7) {
                return days + " ngày trước";
            } else if (days < 30) {
                long weeks = days / 7;
                return weeks + " tuần trước";
            } else if (days < 365) {
                long months = days / 30;
                return months + " tháng trước";
            } else {
                long years = days / 365;
                return years + " năm trước";
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return publishedAt;
        }
    }

    // Format thành ngày tháng đẹp: "15 Th10 2023"
    public String getFormattedDate() {
        if (publishedAt == null || publishedAt.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            Date date = isoFormat.parse(publishedAt);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            assert date != null;
            return outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return publishedAt;
        }
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}