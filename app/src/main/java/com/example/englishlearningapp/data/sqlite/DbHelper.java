package com.example.englishlearningapp.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "english_learning.db";
    private static final int DATABASE_VERSION = 4;

    // =======================
    // TABLE NAMES
    // =======================
    public static final String TABLE_USER = "user";
    public static final String TABLE_MESSAGE_CHAT = "message_chat";

    // =======================
    // USER COLUMNS
    // =======================
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_FULLNAME = "fullname";
    public static final String COLUMN_BIRTH = "birth";
    public static final String COLUMN_AVATAR = "avatar";

    // =======================
    // MESSAGE_CHAT COLUMNS
    // =======================
    public static final String COLUMN_MESSAGE_ID = "id";
    public static final String COLUMN_MESSAGE_CONTENT = "content";
    public static final String COLUMN_MESSAGE_IS_USER = "isUser";
    public static final String COLUMN_MESSAGE_TIMESTAMP = "timestamp";

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
                    COLUMN_AVATAR + " TEXT" +
                    ")";

    // ★ NEW: Create Chat Table
    private static final String CREATE_TABLE_MESSAGE_CHAT =
            "CREATE TABLE " + TABLE_MESSAGE_CHAT + " (" +
                    COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MESSAGE_CONTENT + " TEXT, " +
                    COLUMN_MESSAGE_IS_USER + " INTEGER, " +
                    COLUMN_MESSAGE_TIMESTAMP + " INTEGER" +
                    ")";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_MESSAGE_CHAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_CHAT);
        onCreate(db);
    }
}