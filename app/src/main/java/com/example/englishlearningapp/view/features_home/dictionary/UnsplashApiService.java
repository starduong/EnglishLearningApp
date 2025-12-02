package com.example.englishlearningapp.view.features_home.dictionary;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import java.util.List;

public interface UnsplashApiService {
    @GET("photos/random")
    Call<List<UnsplashResponse>> getRandomImage(
            @Header("Authorization") String authorization,
            @Query("query") String query,
            @Query("orientation") String orientation,
            @Query("count") int count
    );
}