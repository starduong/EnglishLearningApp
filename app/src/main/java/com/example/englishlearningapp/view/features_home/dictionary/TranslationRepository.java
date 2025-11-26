package com.example.englishlearningapp.view.features_home.dictionary;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TranslationRepository {

    private static final String TRANSLATE_API_URL = "https://api.mymemory.translated.net/get";
    private final OkHttpClient client;
    private final Gson gson;

    public interface TranslationCallback {
        void onSuccess(String translatedText);

        void onError(String errorMessage);
    }

    public TranslationRepository() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public void translateText(String text, String targetLang, TranslationCallback callback) {
        // Không dịch nếu text rỗng hoặc quá ngắn
        if (text == null || text.trim().isEmpty() || text.length() < 2) {
            callback.onSuccess(text);
            return;
        }

        String url = TRANSLATE_API_URL + "?q=" + text + "&langpair=en|" + targetLang;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError("Lỗi dịch thuật: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        TranslationResponse translationResponse = gson.fromJson(responseBody, TranslationResponse.class);
                        if (translationResponse != null &&
                                translationResponse.responseData != null &&
                                translationResponse.responseData.translatedText != null) {

                            String translatedText = translationResponse.responseData.translatedText;
                            callback.onSuccess(cleanTranslatedText(translatedText));
                        } else {
                            callback.onError("Không thể dịch văn bản");
                        }
                    } catch (Exception e) {
                        callback.onError("Lỗi xử lý dữ liệu dịch");
                    }
                } else {
                    callback.onError("Lỗi kết nối dịch vụ dịch thuật");
                }
            }
        });
    }

    private String cleanTranslatedText(String text) {
        // Loại bỏ các ký tự đặc biệt không cần thiết
        return text.replace("&#39;", "'")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .trim();
    }

    // Inner classes for JSON parsing
    private static class TranslationResponse {
        @SerializedName("responseData")
        ResponseData responseData;

        @SerializedName("responseStatus")
        int responseStatus;
    }

    private static class ResponseData {
        @SerializedName("translatedText")
        String translatedText;

        @SerializedName("match")
        double match;
    }
}