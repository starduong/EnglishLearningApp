package com.example.englishlearningapp.view.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.util.SharedPrefManager;
import com.google.android.material.card.MaterialCardView;

public class SettingsActivity extends BaseActivity {

    private SharedPrefManager sharedPrefManager;
    private SwitchCompat switchDarkMode, switchNotification;
    private MaterialCardView cardLanguage, cardNotificationTime;
    private ImageButton btnBack;
    private TextView tvCurrentLanguage, tvNotificationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPrefManager = new SharedPrefManager(this);
        initViews();
        setupListeners();
        loadCurrentSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCurrentLanguageDisplay();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotification = findViewById(R.id.switchNotification);
        cardLanguage = findViewById(R.id.cardLanguage);
        cardNotificationTime = findViewById(R.id.cardNotificationTime);
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);
        tvNotificationTime = findViewById(R.id.tvNotificationTime);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPrefManager.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPrefManager.setNotificationEnabled(isChecked);
            String message = isChecked ? getString(R.string.notifications_enabled) : getString(R.string.notifications_disabled);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        cardLanguage.setOnClickListener(v -> showLanguageDialog());

        cardNotificationTime.setOnClickListener(v -> showTimePickerDialog());
    }

    private void loadCurrentSettings() {
        boolean isDarkMode = sharedPrefManager.isDarkMode();
        switchDarkMode.setChecked(isDarkMode);

        boolean isNotificationEnabled = sharedPrefManager.isNotificationEnabled();
        switchNotification.setChecked(isNotificationEnabled);

        String notificationTime = sharedPrefManager.getNotificationTime();
        tvNotificationTime.setText(notificationTime);

        updateCurrentLanguageDisplay();
    }

    private void updateCurrentLanguageDisplay() {
        String currentLanguageCode = sharedPrefManager.getAppLanguage();
        String displayName = getLanguageDisplayName(currentLanguageCode);
        tvCurrentLanguage.setText(displayName);
    }

    private String getLanguageDisplayName(String languageCode) {
        if (languageCode.equals("vi")) {
            return getString(R.string.vietnamese);
        } else {
            return getString(R.string.english);
        }
    }

    private void showLanguageDialog() {
        final String[] languages = {getString(R.string.english), getString(R.string.vietnamese)};
        final String[] languageCodes = {"en", "vi"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_language));

        int currentSelection = 0;
        String currentLanguage = sharedPrefManager.getAppLanguage();
        if (currentLanguage.equals("vi")) {
            currentSelection = 1;
        }

        builder.setSingleChoiceItems(languages, currentSelection, (dialog, which) -> {
            String selectedLanguageCode = languageCodes[which];
            String selectedLanguageName = languages[which];

            // Lưu ngôn ngữ mới
            sharedPrefManager.setAppLanguage(selectedLanguageCode);

            // Cập nhật hiển thị ngay
            tvCurrentLanguage.setText(selectedLanguageName);

            // Thông báo thay đổi ngôn ngữ
            String message = String.format(getString(R.string.language_changed_to), selectedLanguageName);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            // Đóng dialog
            dialog.dismiss();

            // **QUAN TRỌNG: Chuyển đổi ngay lập tức**
            applyLanguageImmediately(selectedLanguageCode);
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Phương thức áp dụng ngôn ngữ ngay lập tức
     */
    private void applyLanguageImmediately(String newLanguageCode) {
        // Cách 1: Restart activity hiện tại (SettingsActivity)
        // restartActivityForLanguageChange();

        // Hoặc cách 2: Restart toàn bộ app (áp dụng cho tất cả activities)
        restartAppWithNewLanguage();
    }

    private void showTimePickerDialog() {
        String[] times = {"08:00", "12:00", "18:00", "20:00", getString(R.string.custom)};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_notification_time));

        builder.setItems(times, (dialog, which) -> {
            if (which == 4) {
                showCustomTimePicker();
            } else {
                sharedPrefManager.setNotificationTime(times[which]);
                tvNotificationTime.setText(times[which]);
                String message = String.format(getString(R.string.notification_time_set), times[which]);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showCustomTimePicker() {
        android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String time = String.format("%02d:%02d", hourOfDay, minute);
            sharedPrefManager.setNotificationTime(time);
            tvNotificationTime.setText(time);
            String message = String.format(getString(R.string.notification_time_set), time);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }, 8, 0, true);

        timePickerDialog.setTitle(getString(R.string.select_custom_time));
        timePickerDialog.show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}