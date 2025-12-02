package com.example.englishlearningapp.view.fragment;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishlearningapp.util.SharedPrefManager;

import java.util.Locale;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // Áp dụng ngôn ngữ cho fragment
        super.onCreate(savedInstanceState);
        applyLanguage();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Kiểm tra và áp dụng ngôn ngữ khi fragment hiển thị
        applyLanguage();
    }

    protected void applyLanguage() {
        if (getContext() != null) {
            SharedPrefManager prefManager = new SharedPrefManager(getContext());
            String languageCode = prefManager.getAppLanguage();
            setLocale(languageCode);
        }
    }

    protected void setLocale(String languageCode) {
        if (getContext() == null) return;

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
}