package com.example.englishlearningapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.englishlearningapp.data.dao.NotificationDAO;
import com.example.englishlearningapp.data.model.Notification;

import java.util.List;
import java.util.UUID;

public class NotificationManager {
    private Context context;
    private NotificationDAO notificationDAO;
    private SharedPrefManager sharedPrefManager;
    private static final String PREF_NOTIFICATION_LAST_ID = "last_notification_id";
    private static final String PREF_SAMPLE_CREATED = "sample_notifications_created";

    public NotificationManager(Context context) {
        this.context = context;
        this.notificationDAO = new NotificationDAO(context);
        this.sharedPrefManager = new SharedPrefManager(context);
    }

    // Lấy ID user hiện tại (giả sử có user đăng nhập)
    private String getCurrentUserId() {
        // Trong thực tế, bạn nên lấy từ SharedPreferences hoặc session
        // Ở đây tôi giả sử user có ID là "user_001"
        return "user_001";
    }

    // Lấy danh sách thông báo
    public List<Notification> getNotifications() {
        String userId = getCurrentUserId();
        return notificationDAO.getAllNotificationsByUser(userId);
    }

    // Lấy số thông báo chưa đọc
    public int getUnreadCount() {
        String userId = getCurrentUserId();
        return notificationDAO.getUnreadCount(userId);
    }

    // Đánh dấu đã đọc
    public void markAsRead(String notificationId) {
        notificationDAO.markAsRead(notificationId);
    }

    // Đánh dấu tất cả đã đọc
    public void markAllAsRead() {
        String userId = getCurrentUserId();
        notificationDAO.markAllAsRead(userId);
        Toast.makeText(context, "Đã đánh dấu tất cả đã đọc", Toast.LENGTH_SHORT).show();
    }

    // Xóa thông báo
    public void deleteNotification(String notificationId) {
        notificationDAO.deleteNotification(notificationId);
        Toast.makeText(context, "Đã xóa thông báo", Toast.LENGTH_SHORT).show();
    }

    // Xóa tất cả thông báo
    public void deleteAllNotifications() {
        String userId = getCurrentUserId();
        notificationDAO.deleteAllNotifications(userId);
        Toast.makeText(context, "Đã xóa tất cả thông báo", Toast.LENGTH_SHORT).show();
    }

    // Tạo thông báo mẫu nếu chưa có
    public void createSampleNotificationsIfNeeded() {
        SharedPreferences prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE);
        boolean sampleCreated = prefs.getBoolean(PREF_SAMPLE_CREATED, false);

        if (!sampleCreated) {
            String userId = getCurrentUserId();
            notificationDAO.createSampleNotifications(userId);
            prefs.edit().putBoolean(PREF_SAMPLE_CREATED, true).apply();
        }
    }

    // Tạo thông báo học tập
    public void createLearningNotification(String lessonName, int progress) {
        String userId = getCurrentUserId();
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID().toString());
        notification.setUserId(userId);
        notification.setTitle("Tiến độ học tập");
        notification.setMessage("Bạn đã hoàn thành " + progress + "% bài học \"" + lessonName + "\"");
        notification.setType(1); // Loại học tập
        notification.setRead(false);
        notification.setAction("open_lesson:" + lessonName);
        notification.setIcon("ic_learning");

        notificationDAO.insertNotification(notification);
    }

    // Tạo thông báo nhắc nhở học
    public void createReminderNotification() {
        String userId = getCurrentUserId();
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID().toString());
        notification.setUserId(userId);
        notification.setTitle("Nhắc nhở học tập");
        notification.setMessage("Đã đến giờ học tiếng Anh của bạn hôm nay. Hãy duy trì thói quen!");
        notification.setType(1);
        notification.setRead(false);
        notification.setAction("open_home");
        notification.setIcon("ic_reminder");

        notificationDAO.insertNotification(notification);
    }

    // Tạo thông báo thành tích
    public void createAchievementNotification(String achievementName) {
        String userId = getCurrentUserId();
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID().toString());
        notification.setUserId(userId);
        notification.setTitle("Thành tích mới!");
        notification.setMessage("Chúc mừng bạn đã đạt được thành tích \"" + achievementName + "\"");
        notification.setType(1);
        notification.setRead(false);
        notification.setAction("open_achievements");
        notification.setIcon("ic_achievement");

        notificationDAO.insertNotification(notification);
    }

    // Đóng kết nối database
    public void close() {
        if (notificationDAO != null) {
            notificationDAO.close();
        }
    }
}