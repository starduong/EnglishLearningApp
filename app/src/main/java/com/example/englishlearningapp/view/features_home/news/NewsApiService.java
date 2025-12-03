package com.example.englishlearningapp.view.features_home.news;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface NewsApiService {

    // API Key của bạn
    String API_KEY = "API_KEY";

    // Thêm phương thức mới với @Url annotation
    @GET
    Call<NewsResponse> getArticleByUrl(
            @Url String url,
            @Query("api-key") String apiKey,
            @Query("show-fields") String fields
    );

    // Tìm kiếm bài viết với các fields cụ thể
    @GET("search")
    Call<NewsResponse> searchNews(
            @Query("api-key") String apiKey,
            @Query("q") String query,
            @Query("section") String section,
            @Query("show-fields") String fields,
            @Query("page-size") int pageSize,
            @Query("order-by") String orderBy,
            @Query("page") int page
    );

    // Lấy tin tức mới nhất
    @GET("search")
    Call<NewsResponse> getLatestNews(
            @Query("api-key") String apiKey,
            @Query("show-fields") String fields,
            @Query("page-size") int pageSize,
            @Query("order-by") String orderBy
    );

    // Lấy bài viết theo category/education
    @GET("search")
    Call<NewsResponse> getEducationNews(
            @Query("api-key") String apiKey,
            @Query("q") String query,
            @Query("show-fields") String fields,
            @Query("page-size") int pageSize,
            @Query("order-by") String orderBy
    );

    // Lấy chi tiết bài viết cụ thể - FIX: Thêm base URL đầy đủ
    @GET("https://content.guardianapis.com/{id}")
    Call<NewsResponse> getArticleDetail(
            @Path("id") String articleId,
            @Query("api-key") String apiKey,
            @Query("show-fields") String fields
    );
}