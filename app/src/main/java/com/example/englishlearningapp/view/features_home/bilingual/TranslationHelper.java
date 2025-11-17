package com.example.englishlearningapp.view.features_home.bilingual;

import android.content.Context;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class TranslationHelper {
    private Translator translator;
    private Context context;

    // Supported languages
    public static final String ENGLISH = "en";
    public static final String VIETNAMESE = "vi";

    public TranslationHelper(Context context) {
        this.context = context;
    }

    public void translateText(String text, String sourceLang, String targetLang, TranslationCallback callback) {
        // Validate input
        if (text == null || text.trim().isEmpty()) {
            callback.onError("Vui lòng nhập văn bản cần dịch");
            return;
        }
        // Kiểm tra không cho dịch cùng ngôn ngữ
        if (sourceLang.equals(targetLang)) {
            callback.onError("Không thể dịch từ " + sourceLang + " sang chính nó");
            return;
        }
        if (sourceLang.equals(targetLang)) {
            callback.onError("Hai ngôn ngữ không được giống nhau");
            return;
        }

        // Create translator
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLang)
                .setTargetLanguage(targetLang)
                .build();

        translator = Translation.getClient(options);

        // Download model if needed
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(v -> {
                    // Model ready, start translation
                    translator.translate(text)
                            .addOnSuccessListener(translatedText -> {
                                callback.onSuccess(translatedText);
                                closeTranslator();
                            })
                            .addOnFailureListener(e -> {
                                callback.onError("Lỗi dịch: " + e.getMessage());
                                closeTranslator();
                            });
                })
                .addOnFailureListener(e -> {
                    callback.onError("Lỗi tải model dịch: " + e.getMessage());
                    closeTranslator();
                });
    }

    public void downloadBothModels(DownloadCallback callback) {
        // Pre-download both translation models for better UX
        downloadModel(ENGLISH, VIETNAMESE, callback);
    }

    private void downloadModel(String sourceLang, String targetLang, DownloadCallback callback) {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLang)
                .setTargetLanguage(targetLang)
                .build();

        Translator tempTranslator = Translation.getClient(options);

        tempTranslator.downloadModelIfNeeded()
                .addOnSuccessListener(v -> {
                    tempTranslator.close();
                    callback.onProgress("Đã tải model " + sourceLang + "→" + targetLang);
                })
                .addOnFailureListener(e -> {
                    tempTranslator.close();
                    callback.onError("Lỗi tải model " + sourceLang + "→" + targetLang);
                });
    }

    private void closeTranslator() {
        if (translator != null) {
            translator.close();
        }
    }

    public interface TranslationCallback {
        void onSuccess(String translatedText);

        void onError(String errorMessage);
    }

    public interface DownloadCallback {
        void onProgress(String message);

        void onError(String errorMessage);
    }
}