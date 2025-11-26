package com.example.englishlearningapp.view.features_home.dictionary;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface DictionaryApiService {
    @GET("api/v2/entries/en/{word}")
    Call<List<DictionaryResponse>> getWordDefinition(@Path("word") String word);
}