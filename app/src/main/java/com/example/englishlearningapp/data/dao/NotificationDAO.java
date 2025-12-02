package com.example.englishlearningapp.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.englishlearningapp.data.model.Notification;
import com.example.englishlearningapp.data.sqlite.DbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationDAO {
    private final DbHelper dbHelper;
    private SQLiteDatabase db;
    private static final String TAG = "NotificationDAO";

    public NotificationDAO(Context context) {
        dbHelper = new DbHelper(context.getApplicationContext());
        open();
    }

    public void open() {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening writable database, trying readable", e);
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
            db = null;
        }
    }

    // INSERT
    public long insertNotification(Notification notification) {
        dbOpen();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_NOTIFICATION_ID, notification.getId() != null ? notification.getId() : UUID.randomUUID().toString());
        values.put(DbHelper.COLUMN_NOTIFICATION_USER_ID, notification.getUserId());
        values.put(DbHelper.COLUMN_NOTIFICATION_TITLE, notification.getTitle());
        values.put(DbHelper.COLUMN_NOTIFICATION_MESSAGE, notification.getMessage());
        values.put(DbHelper.COLUMN_NOTIFICATION_TYPE, notification.getType());
        values.put(DbHelper.COLUMN_NOTIFICATION_IS_READ, notification.isRead() ? 1 : 0);
        values.put(DbHelper.COLUMN_NOTIFICATION_TIMESTAMP, notification.getTimestamp());
        values.put(DbHelper.COLUMN_NOTIFICATION_ACTION, notification.getAction());
        values.put(DbHelper.COLUMN_NOTIFICATION_ICON, notification.getIcon());

        long result = -1;
        try {
            result = db.insertOrThrow(DbHelper.TABLE_NOTIFICATION, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Insert notification failed", e);
        }
        return result;
    }

    // UPDATE - Đánh dấu đã đọc
    public int markAsRead(String notificationId) {
        dbOpen();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_NOTIFICATION_IS_READ, 1);

        int result = 0;
        try {
            result = db.update(DbHelper.TABLE_NOTIFICATION, values, DbHelper.COLUMN_NOTIFICATION_ID + "=?", new String[]{notificationId});
        } catch (Exception e) {
            Log.e(TAG, "Mark as read failed", e);
        }
        return result;
    }

    // UPDATE - Đánh dấu tất cả đã đọc cho user
    public int markAllAsRead(String userId) {
        dbOpen();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_NOTIFICATION_IS_READ, 1);

        int result = 0;
        try {
            result = db.update(DbHelper.TABLE_NOTIFICATION, values, DbHelper.COLUMN_NOTIFICATION_USER_ID + "=? AND " + DbHelper.COLUMN_NOTIFICATION_IS_READ + "=0", new String[]{userId});
        } catch (Exception e) {
            Log.e(TAG, "Mark all as read failed", e);
        }
        return result;
    }

    // DELETE
    public int deleteNotification(String notificationId) {
        dbOpen();
        int result = 0;
        try {
            result = db.delete(DbHelper.TABLE_NOTIFICATION, DbHelper.COLUMN_NOTIFICATION_ID + "=?", new String[]{notificationId});
        } catch (Exception e) {
            Log.e(TAG, "Delete notification failed", e);
        }
        return result;
    }

    // DELETE ALL cho user
    public int deleteAllNotifications(String userId) {
        dbOpen();
        int result = 0;
        try {
            result = db.delete(DbHelper.TABLE_NOTIFICATION, DbHelper.COLUMN_NOTIFICATION_USER_ID + "=?", new String[]{userId});
        } catch (Exception e) {
            Log.e(TAG, "Delete all notifications failed", e);
        }
        return result;
    }

    // GET BY ID
    public Notification getNotificationById(String notificationId) {
        dbOpen();
        Cursor cursor = null;
        try {
            cursor = db.query(DbHelper.TABLE_NOTIFICATION, null, DbHelper.COLUMN_NOTIFICATION_ID + " = ?", new String[]{notificationId}, null, null, null);
            if (cursor.moveToFirst()) {
                return cursorToNotification(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "getNotificationById failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    // GET ALL BY USER ID (mới nhất trước)
    public List<Notification> getAllNotificationsByUser(String userId) {
        dbOpen();
        List<Notification> notificationList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String orderBy = DbHelper.COLUMN_NOTIFICATION_TIMESTAMP + " DESC";
            cursor = db.query(DbHelper.TABLE_NOTIFICATION, null, DbHelper.COLUMN_NOTIFICATION_USER_ID + " = ?", new String[]{userId}, null, null, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Notification notification = cursorToNotification(cursor);
                    notificationList.add(notification);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllNotificationsByUser failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return notificationList;
    }

    // GET UNREAD COUNT
    public int getUnreadCount(String userId) {
        dbOpen();
        String sql = "SELECT COUNT(*) FROM " + DbHelper.TABLE_NOTIFICATION + " WHERE " + DbHelper.COLUMN_NOTIFICATION_USER_ID + " = ?" + " AND " + DbHelper.COLUMN_NOTIFICATION_IS_READ + " = 0";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{userId});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "getUnreadCount error", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0;
    }

    // GET RECENT NOTIFICATIONS (giới hạn số lượng)
    public List<Notification> getRecentNotifications(String userId, int limit) {
        dbOpen();
        List<Notification> notificationList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String orderBy = DbHelper.COLUMN_NOTIFICATION_TIMESTAMP + " DESC";
            String limitStr = String.valueOf(limit);
            cursor = db.query(DbHelper.TABLE_NOTIFICATION, null, DbHelper.COLUMN_NOTIFICATION_USER_ID + " = ?", new String[]{userId}, null, null, orderBy, limitStr);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Notification notification = cursorToNotification(cursor);
                    notificationList.add(notification);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getRecentNotifications failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return notificationList;
    }

    // Tạo dữ liệu mẫu
    public void createSampleNotifications(String userId) {
        List<Notification> sampleNotifications = new ArrayList<>();

        // Thông báo học tập
        sampleNotifications.add(new Notification(UUID.randomUUID().toString(), userId, "Nhắc nhở học tập", "Bạn có 3 bài học mới chờ hoàn thành. Đừng quên học mỗi ngày nhé!", 1, false, System.currentTimeMillis() - (2 * 60 * 60 * 1000), // 2 giờ trước
                "open_lesson_list", "ic_learning"));

        // Thông báo hệ thống
        sampleNotifications.add(new Notification(UUID.randomUUID().toString(), userId, "Cập nhật phiên bản mới", "Ứng dụng đã được cập nhật lên phiên bản 2.0 với nhiều tính năng mới", 2, false, System.currentTimeMillis() - (24 * 60 * 60 * 1000), // 1 ngày trước
                "open_settings", "ic_system"));

        // Thông báo khuyến mãi
        sampleNotifications.add(new Notification(UUID.randomUUID().toString(), userId, "Khuyến mãi đặc biệt", "Giảm 50% cho gói Premium trong tháng này. Nâng cấp ngay!", 3, true, System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000), // 3 ngày trước
                "open_premium", "ic_promotion"));

        // Thông báo thành tích
        sampleNotifications.add(new Notification(UUID.randomUUID().toString(), userId, "Chúc mừng!", "Bạn đã hoàn thành chuỗi học 7 ngày liên tiếp. Tiếp tục phát huy nhé!", 1, true, System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000), // 5 ngày trước
                "open_achievement", "ic_achievement"));

        // Insert tất cả thông báo mẫu
        for (Notification notification : sampleNotifications) {
            insertNotification(notification);
        }
    }

    // Helper methods
    private void dbOpen() {
        if (db == null || !db.isOpen()) {
            open();
        }
    }

    private Notification cursorToNotification(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NOTIFICATION_ID));
        String userId = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NOTIFICATION_USER_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NOTIFICATION_TITLE));
        String message = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NOTIFICATION_MESSAGE));
        int type = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NOTIFICATION_TYPE));
        boolean isRead = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NOTIFICATION_IS_READ)) == 1;
        long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NOTIFICATION_TIMESTAMP));
        String action = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NOTIFICATION_ACTION));
        String icon = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NOTIFICATION_ICON));

        return new Notification(id, userId, title, message, type, isRead, timestamp, action, icon);
    }
}