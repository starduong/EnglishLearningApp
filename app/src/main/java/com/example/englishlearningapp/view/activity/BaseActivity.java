package com.example.englishlearningapp.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.util.SharedPrefManager;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    private static final int RESTART_REQUEST_CODE = 1001;
    private static String pendingLanguageCode = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Áp dụng ngôn ngữ mỗi khi activity được tạo
        applyLanguage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Kiểm tra và áp dụng ngôn ngữ khi quay lại
        applyLanguage();

        // Kiểm tra nếu có ngôn ngữ đang chờ áp dụng
        if (pendingLanguageCode != null) {
            applyLanguage();
            pendingLanguageCode = null;
        }
    }

    protected void applyLanguage() {
        SharedPrefManager prefManager = new SharedPrefManager(this);
        String languageCode = prefManager.getAppLanguage();
        setLocale(languageCode);
    }

    protected void setLocale(String languageCode) {
        Locale locale;
        if (languageCode.equals("vi")) {
            locale = new Locale("vi");
        } else {
            locale = Locale.ENGLISH;
        }

        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    /**
     * Phương thức để restart activity với ngôn ngữ mới
     * Sẽ được gọi từ tất cả các activity khi đổi ngôn ngữ
     */
    protected void restartActivityForLanguageChange() {
        // Lưu trạng thái activity hiện tại nếu cần
        Bundle savedState = new Bundle();
        onSaveInstanceState(savedState);

        // Khởi động lại activity hiện tại
        Intent intent = getIntent();
        intent.putExtras(savedState);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        // Animation chuyển đổi
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Phương thức để thông báo cho tất cả các activity khác thay đổi ngôn ngữ
     */
    protected void broadcastLanguageChange(String newLanguageCode) {
        // Lưu ngôn ngữ đang chờ áp dụng
        pendingLanguageCode = newLanguageCode;

        // Có thể thêm BroadcastReceiver nếu muốn thông báo cho các activity khác
        Intent intent = new Intent("LANGUAGE_CHANGED");
        intent.putExtra("language_code", newLanguageCode);
        sendBroadcast(intent);
    }

    /**
     * Phương thức để áp dụng ngôn ngữ cho tất cả activities trong back stack
     */
    protected void restartAppWithNewLanguage() {
        // Phương thức này sẽ restart toàn bộ app
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity(); // Đóng tất cả activities
        }
    }
}