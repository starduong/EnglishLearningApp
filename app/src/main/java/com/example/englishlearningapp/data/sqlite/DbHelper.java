package com.example.englishlearningapp.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "english_learning.db";
    private static final int DATABASE_VERSION = 6; // Tăng version lên 6

    // =======================
    // TABLE NAMES
    // =======================
    public static final String TABLE_USER = "user";
    public static final String TABLE_MESSAGE_CHAT = "message_chat";
    public static final String TABLE_NOTIFICATION = "notification";

    // =======================
    // USER COLUMNS (CẬP NHẬT)
    // =======================
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_FULLNAME = "fullname";
    public static final String COLUMN_BIRTH = "birth";
    public static final String COLUMN_AVATAR = "avatar";
    public static final String COLUMN_ACCOUNT_TYPE = "account_type"; // Thêm cột mới
    public static final String COLUMN_PRO_EXPIRY_DATE = "pro_expiry_date"; // Thêm cột mới

    // =======================
    // MESSAGE_CHAT COLUMNS
    // =======================
    public static final String COLUMN_MESSAGE_ID = "id";
    public static final String COLUMN_MESSAGE_CONTENT = "content";
    public static final String COLUMN_MESSAGE_IS_USER = "isUser";
    public static final String COLUMN_MESSAGE_TIMESTAMP = "timestamp";

    // =======================
    // NOTIFICATION COLUMNS
    // =======================
    public static final String COLUMN_NOTIFICATION_ID = "id";
    public static final String COLUMN_NOTIFICATION_USER_ID = "user_id"; // Khóa ngoại
    public static final String COLUMN_NOTIFICATION_TITLE = "title";
    public static final String COLUMN_NOTIFICATION_MESSAGE = "message";
    public static final String COLUMN_NOTIFICATION_TYPE = "type";
    public static final String COLUMN_NOTIFICATION_IS_READ = "is_read";
    public static final String COLUMN_NOTIFICATION_TIMESTAMP = "timestamp";
    public static final String COLUMN_NOTIFICATION_ACTION = "action";
    public static final String COLUMN_NOTIFICATION_ICON = "icon";

    // =======================
    // CREATE TABLE QUERIES
    // =======================

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_FULLNAME + " TEXT, " +
                    COLUMN_BIRTH + " TEXT, " +
                    COLUMN_AVATAR + " TEXT, " +
                    COLUMN_ACCOUNT_TYPE + " TEXT DEFAULT 'FREE', " + // Thêm cột account_type
                    COLUMN_PRO_EXPIRY_DATE + " INTEGER DEFAULT 0" + // Thêm cột pro_expiry_date
                    ")";

    private static final String CREATE_TABLE_MESSAGE_CHAT =
            "CREATE TABLE " + TABLE_MESSAGE_CHAT + " (" +
                    COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MESSAGE_CONTENT + " TEXT, " +
                    COLUMN_MESSAGE_IS_USER + " INTEGER, " +
                    COLUMN_MESSAGE_TIMESTAMP + " INTEGER" +
                    ")";

    private static final String CREATE_TABLE_NOTIFICATION =
            "CREATE TABLE " + TABLE_NOTIFICATION + " (" +
                    COLUMN_NOTIFICATION_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_NOTIFICATION_USER_ID + " TEXT, " +
                    COLUMN_NOTIFICATION_TITLE + " TEXT, " +
                    COLUMN_NOTIFICATION_MESSAGE + " TEXT, " +
                    COLUMN_NOTIFICATION_TYPE + " INTEGER DEFAULT 1, " +
                    COLUMN_NOTIFICATION_IS_READ + " INTEGER DEFAULT 0, " +
                    COLUMN_NOTIFICATION_TIMESTAMP + " INTEGER, " +
                    "[" + COLUMN_NOTIFICATION_ACTION + "]" + " TEXT, " +
                    COLUMN_NOTIFICATION_ICON + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_NOTIFICATION_USER_ID + ") REFERENCES " +
                    TABLE_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ")";

    private static final String CREATE_INDEX_NOTIFICATION_USER_ID =
            "CREATE INDEX idx_notification_user_id ON " + TABLE_NOTIFICATION +
                    "(" + COLUMN_NOTIFICATION_USER_ID + ")";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_MESSAGE_CHAT);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
        db.execSQL(CREATE_INDEX_NOTIFICATION_USER_ID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            // Nâng cấp từ version cũ lên version 6
            if (oldVersion < 5) {
                // Thêm bảng notification nếu chưa có
                db.execSQL(CREATE_TABLE_NOTIFICATION);
                db.execSQL(CREATE_INDEX_NOTIFICATION_USER_ID);
            }

            // Thêm cột account_type và pro_expiry_date vào bảng user
            try {
                db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " +
                        COLUMN_ACCOUNT_TYPE + " TEXT DEFAULT 'FREE'");
                db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " +
                        COLUMN_PRO_EXPIRY_DATE + " INTEGER DEFAULT 0");

                Log.d("DbHelper", "Database upgraded to version " + newVersion +
                        " - Added account_type and pro_expiry_date columns");
            } catch (Exception e) {
                Log.e("DbHelper", "Error upgrading database: " + e.getMessage());
                // Nếu lỗi, tạo lại bảng
                recreateUserTable(db);
            }
        }
    }

    private void recreateUserTable(SQLiteDatabase db) {
        // Backup dữ liệu cũ
        db.execSQL("CREATE TABLE user_backup AS SELECT * FROM " + TABLE_USER);

        // Xóa bảng cũ
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Tạo bảng mới với cấu trúc mới
        db.execSQL(CREATE_TABLE_USER);

        // Copy dữ liệu từ backup
        db.execSQL("INSERT INTO " + TABLE_USER + " (" +
                COLUMN_ID + ", " +
                COLUMN_USERNAME + ", " +
                COLUMN_PASSWORD + ", " +
                COLUMN_EMAIL + ", " +
                COLUMN_PHONE + ", " +
                COLUMN_FULLNAME + ", " +
                COLUMN_BIRTH + ", " +
                COLUMN_AVATAR + ", " +
                COLUMN_ACCOUNT_TYPE + ", " +
                COLUMN_PRO_EXPIRY_DATE + ") " +
                "SELECT " +
                COLUMN_ID + ", " +
                COLUMN_USERNAME + ", " +
                COLUMN_PASSWORD + ", " +
                COLUMN_EMAIL + ", " +
                COLUMN_PHONE + ", " +
                COLUMN_FULLNAME + ", " +
                COLUMN_BIRTH + ", " +
                COLUMN_AVATAR + ", " +
                "'FREE', " + // Giá trị mặc định cho account_type
                "0 " + // Giá trị mặc định cho pro_expiry_date
                "FROM user_backup");

        // Xóa bảng backup
        db.execSQL("DROP TABLE IF EXISTS user_backup");
    }

    // Thêm method để xóa notification cũ (30 ngày trước)
    public void deleteOldNotifications(SQLiteDatabase db) {
        long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);
        db.delete(TABLE_NOTIFICATION,
                COLUMN_NOTIFICATION_TIMESTAMP + " < ?",
                new String[]{String.valueOf(thirtyDaysAgo)});
    }
}