package com.example.englishlearningapp.view.features_home.news;

import android.content.Context;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ApiClientNews {
    private static final String BASE_URL = "https://content.guardianapis.com/";
    private static final long CACHE_SIZE = 20 * 1024 * 1024; // 20 MB
    private static final int TIMEOUT = 30; // seconds

    private static Retrofit retrofit = null;
    private static NewsApiService apiService = null;

    public static NewsApiService getApiService(Context context) {
        if (apiService == null) {
            apiService = getRetrofit(context).create(NewsApiService.class);
        }
        return apiService;
    }

    private static Retrofit getRetrofit(Context context) {
        if (retrofit == null) {
            // Create cache
            File httpCacheDirectory = new File(context.getCacheDir(), "guardian_cache");
            Cache cache = new Cache(httpCacheDirectory, CACHE_SIZE);

            // Create logging interceptor với level cao hơn để debug
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Create OkHttp client
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        // Add headers
                        okhttp3.Request original = chain.request();
                        okhttp3.Request request = original.newBuilder()
                                .header("User-Agent", "EnglishLearningApp/1.0")
                                .header("Accept", "application/json")
                                .method(original.method(), original.body())
                                .build();

                        okhttp3.Response response = chain.proceed(request);
                        return response.newBuilder()
                                .header("Cache-Control", "public, max-age=" + 300)
                                .build();
                    })
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .build();

            // Create Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}