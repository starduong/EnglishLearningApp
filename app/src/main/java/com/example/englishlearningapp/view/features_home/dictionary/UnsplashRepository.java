package com.example.englishlearningapp.view.features_home.dictionary;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class UnsplashRepository {
    private static final String UNSPLASH_BASE_URL = "https://api.unsplash.com/";
    private static final String UNSPLASH_API_KEY = "UNSPLASH_API_KEY";

    private final UnsplashApiService apiService;

    public interface UnsplashCallback {
        void onSuccess(String imageUrl);

        void onError(String errorMessage);
    }

    public UnsplashRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UNSPLASH_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(UnsplashApiService.class);
    }

    public void getRandomImage(String query, UnsplashCallback callback) {
        Call<List<UnsplashResponse>> call = apiService.getRandomImage(
                "Client-ID " + UNSPLASH_API_KEY,
                query,
                "landscape",
                1
        );

        call.enqueue(new Callback<List<UnsplashResponse>>() {
            @Override
            public void onResponse(Call<List<UnsplashResponse>> call, Response<List<UnsplashResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UnsplashResponse unsplashResponse = response.body().get(0);
                    if (unsplashResponse.getUrls() != null) {
                        callback.onSuccess(unsplashResponse.getUrls().getRegular());
                    } else {
                        callback.onError("Không có URL ảnh");
                    }
                } else {
                    callback.onError("Không tìm thấy ảnh phù hợp");
                }
            }

            @Override
            public void onFailure(Call<List<UnsplashResponse>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}