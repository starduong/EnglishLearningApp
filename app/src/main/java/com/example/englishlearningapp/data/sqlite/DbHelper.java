package com.example.englishlearningapp.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "english_learning.db";
    private static final int DATABASE_VERSION = 7;

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
    // WORD TABLE
    // =======================
    public static final String TABLE_WORD = "word";

    // Word columns
    public static final String COLUMN_WORD_ID = "word_id";
    public static final String COLUMN_USER_ID = "user_id"; // Khóa ngoại
    public static final String COLUMN_ENGLISH_WORD = "english_word";
    public static final String COLUMN_PRONUNCIATION = "pronunciation";
    public static final String COLUMN_VIETNAMESE_MEANING = "vietnamese_meaning";
    public static final String COLUMN_ENGLISH_DEFINITION = "english_definition";
    public static final String COLUMN_EXAMPLE_SENTENCE = "example_sentence";
    public static final String COLUMN_EXAMPLE_TRANSLATION = "example_translation";
    public static final String COLUMN_PART_OF_SPEECH = "part_of_speech";
    public static final String COLUMN_SYNONYMS = "synonyms";
    public static final String COLUMN_ANTONYMS = "antonyms";
    public static final String COLUMN_TAGS = "tags";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_AUDIO_URL = "audio_url";
    public static final String COLUMN_DIFFICULTY_LEVEL = "difficulty_level";
    public static final String COLUMN_MASTERY_LEVEL = "mastery_level";
    public static final String COLUMN_ADDED_DATE = "added_date";
    public static final String COLUMN_LAST_REVIEWED = "last_reviewed";
    public static final String COLUMN_NEXT_REVIEW_DATE = "next_review_date";
    public static final String COLUMN_REVIEW_COUNT = "review_count";
    public static final String COLUMN_CORRECT_COUNT = "correct_count";
    public static final String COLUMN_WRONG_COUNT = "wrong_count";
    public static final String COLUMN_IS_FAVORITE = "is_favorite";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_SOURCE = "source";

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

    // Tạo bảng Word
    private static final String CREATE_TABLE_WORD =
            "CREATE TABLE " + TABLE_WORD + " (" +
                    COLUMN_WORD_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_USER_ID + " TEXT, " +
                    COLUMN_ENGLISH_WORD + " TEXT, " +
                    COLUMN_PRONUNCIATION + " TEXT, " +
                    COLUMN_VIETNAMESE_MEANING + " TEXT, " +
                    COLUMN_ENGLISH_DEFINITION + " TEXT, " +
                    COLUMN_EXAMPLE_SENTENCE + " TEXT, " +
                    COLUMN_EXAMPLE_TRANSLATION + " TEXT, " +
                    COLUMN_PART_OF_SPEECH + " TEXT, " +
                    COLUMN_SYNONYMS + " TEXT, " +
                    COLUMN_ANTONYMS + " TEXT, " +
                    COLUMN_TAGS + " TEXT, " +
                    COLUMN_IMAGE_URL + " TEXT, " +
                    COLUMN_AUDIO_URL + " TEXT, " +
                    COLUMN_DIFFICULTY_LEVEL + " INTEGER DEFAULT 2, " +
                    COLUMN_MASTERY_LEVEL + " INTEGER DEFAULT 0, " +
                    COLUMN_ADDED_DATE + " INTEGER, " +
                    COLUMN_LAST_REVIEWED + " INTEGER, " +
                    COLUMN_NEXT_REVIEW_DATE + " INTEGER, " +
                    COLUMN_REVIEW_COUNT + " INTEGER DEFAULT 0, " +
                    COLUMN_CORRECT_COUNT + " INTEGER DEFAULT 0, " +
                    COLUMN_WRONG_COUNT + " INTEGER DEFAULT 0, " +
                    COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0, " +
                    COLUMN_NOTES + " TEXT, " +
                    COLUMN_PRIORITY + " INTEGER DEFAULT 3, " +
                    COLUMN_SOURCE + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " +
                    TABLE_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ")";

    // Tạo index cho user_id và next_review_date
    private static final String CREATE_INDEX_WORD_USER_ID =
            "CREATE INDEX idx_word_user_id ON " + TABLE_WORD +
                    "(" + COLUMN_USER_ID + ")";

    private static final String CREATE_INDEX_WORD_NEXT_REVIEW =
            "CREATE INDEX idx_word_next_review ON " + TABLE_WORD +
                    "(" + COLUMN_NEXT_REVIEW_DATE + ")";

    private static final String CREATE_INDEX_WORD_MASTERY =
            "CREATE INDEX idx_word_mastery ON " + TABLE_WORD +
                    "(" + COLUMN_MASTERY_LEVEL + ")";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_MESSAGE_CHAT);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
        db.execSQL(CREATE_INDEX_NOTIFICATION_USER_ID);
        db.execSQL(CREATE_TABLE_WORD);
        db.execSQL(CREATE_INDEX_WORD_USER_ID);
        db.execSQL(CREATE_INDEX_WORD_NEXT_REVIEW);
        db.execSQL(CREATE_INDEX_WORD_MASTERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // --- VERSION 5: Thêm bảng Notification ---
        if (oldVersion < 5) {
            db.execSQL(CREATE_TABLE_NOTIFICATION);
            db.execSQL(CREATE_INDEX_NOTIFICATION_USER_ID);
            Log.d("DbHelper", "Upgraded to version 5: Created Notification table");
        }

        // --- VERSION 6: Thêm cột account_type & pro_expiry_date ---
        if (oldVersion < 6) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN "
                        + COLUMN_ACCOUNT_TYPE + " TEXT DEFAULT 'FREE'");

                db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN "
                        + COLUMN_PRO_EXPIRY_DATE + " INTEGER DEFAULT 0");

                Log.d("DbHelper", "Upgraded to version 6: Added account_type, pro_expiry_date");
            } catch (Exception e) {
                Log.e("DbHelper", "Error upgrading to v6: " + e.getMessage());
                recreateUserTable(db);
            }
        }

        // --- VERSION 7: Thêm bảng Word ---
        if (oldVersion < 7) {
            db.execSQL(CREATE_TABLE_WORD);
            db.execSQL(CREATE_INDEX_WORD_USER_ID);
            db.execSQL(CREATE_INDEX_WORD_NEXT_REVIEW);
            db.execSQL(CREATE_INDEX_WORD_MASTERY);
            Log.d("DbHelper", "Upgraded to version 7: Added Word table & indexes");
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