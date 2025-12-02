package com.example.englishlearningapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "EnglishAppPrefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
    private static final String KEY_NOTIFICATION_TIME = "notification_time";
    private static final String KEY_APP_LANGUAGE = "app_language";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Khởi tạo giá trị mặc định nếu chưa có
        initializeDefaultValues();
    }

    private void initializeDefaultValues() {
        // Nếu chưa có ngôn ngữ được lưu, set mặc định là English
        if (!sharedPreferences.contains(KEY_APP_LANGUAGE)) {
            setAppLanguage("en");
        }

        // Nếu chưa có thời gian thông báo, set mặc định là 08:00
        if (!sharedPreferences.contains(KEY_NOTIFICATION_TIME)) {
            setNotificationTime("08:00");
        }

        // Nếu chưa có setting notification, set mặc định là true
        if (!sharedPreferences.contains(KEY_NOTIFICATION_ENABLED)) {
            setNotificationEnabled(true);
        }

        // Nếu chưa có setting dark mode, set mặc định là false
        if (!sharedPreferences.contains(KEY_DARK_MODE)) {
            setDarkMode(false);
        }
    }

    public void setDarkMode(boolean isDarkMode) {
        editor.putBoolean(KEY_DARK_MODE, isDarkMode);
        editor.apply();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void setNotificationEnabled(boolean enabled) {
        editor.putBoolean(KEY_NOTIFICATION_ENABLED, enabled);
        editor.apply();
    }

    public boolean isNotificationEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true);
    }

    public void setNotificationTime(String time) {
        editor.putString(KEY_NOTIFICATION_TIME, time);
        editor.apply();
    }

    public String getNotificationTime() {
        return sharedPreferences.getString(KEY_NOTIFICATION_TIME, "08:00");
    }

    public void setAppLanguage(String languageCode) {
        editor.putString(KEY_APP_LANGUAGE, languageCode);
        editor.apply();
    }

    public String getAppLanguage() {
        // Luôn trả về giá trị mặc định nếu có lỗi
        return sharedPreferences.getString(KEY_APP_LANGUAGE, "en");
    }
}