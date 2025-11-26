package com.example.englishlearningapp.view.features_home.dictionary;

public class ApiDictionaryResponse<T> {
    private final T data;
    private final String error;
    private final boolean loading;

    public ApiDictionaryResponse(T data, String error, boolean loading) {
        this.data = data;
        this.error = error;
        this.loading = loading;
    }

    public static <T> ApiDictionaryResponse<T> success(T data) {
        return new ApiDictionaryResponse<>(data, null, false);
    }

    public static <T> ApiDictionaryResponse<T> error(String error) {
        return new ApiDictionaryResponse<>(null, error, false);
    }

    public static <T> ApiDictionaryResponse<T> loading() {
        return new ApiDictionaryResponse<>(null, null, true);
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isSuccess() {
        return data != null;
    }
}
