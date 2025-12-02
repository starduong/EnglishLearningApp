package com.example.englishlearningapp.data.model;

import com.example.englishlearningapp.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notification implements Serializable {
    private String id;
    private String userId;           // Khóa ngoại: user_id của người nhận
    private String title;
    private String message;
    private int type;               // Loại thông báo: 1=Học tập, 2=Hệ thống, 3=Khuyến mãi
    private boolean isRead;         // Đã đọc chưa
    private long timestamp;         // Thời gian tạo
    private String action;          // Hành động khi click (ví dụ: "open_lesson:123")
    private String icon;           // Icon hiển thị

    // Constructor
    public Notification() {
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    public Notification(String id, String userId, String title, String message,
                        int type, boolean isRead, long timestamp, String action, String icon) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.timestamp = timestamp;
        this.action = action;
        this.icon = icon;
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    // Helper methods
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getTypeText() {
        switch (type) {
            case 1:
                return "Học tập";
            case 2:
                return "Hệ thống";
            case 3:
                return "Khuyến mãi";
            default:
                return "Thông báo";
        }
    }

    public int getTypeColor() {
        switch (type) {
            case 1:
                return 0xFF2196F3; // Blue
            case 2:
                return 0xFFFF9800; // Orange
            case 3:
                return 0xFF4CAF50; // Green
            default:
                return 0xFF9E9E9E; // Gray
        }
    }

    public int getTypeIcon() {
        switch (type) {
            case 1:
                return R.drawable.ic_notification_learning_24;
            case 2:
                return R.drawable.ic_notification_system_24;
            case 3:
                return R.drawable.ic_notification_promotion_24;
            default:
                return R.drawable.ic_notifications_black_24dp;
        }
    }
}