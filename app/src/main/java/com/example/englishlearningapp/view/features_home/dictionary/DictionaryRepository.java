package com.example.englishlearningapp.view.features_home.dictionary;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Objects;

public class DictionaryRepository {

    public interface DictionaryCallback {
        void onSuccess(List<DictionaryResponse> response);

        void onError(String errorMessage);
    }

    public void searchWord(String word, DictionaryCallback callback) {
        // Kiểm tra từ rỗng
        if (word == null || word.trim().isEmpty()) {
            callback.onError("Vui lòng nhập từ cần tra");
            return;
        }

        DictionaryApiService apiService = ApiDictionaryClient.getApiService();

        Call<List<DictionaryResponse>> call = apiService.getWordDefinition(word);
        call.enqueue(new Callback<List<DictionaryResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<DictionaryResponse>> call, @NonNull Response<List<DictionaryResponse>> response) {
                if (response.isSuccessful()) {
                    List<DictionaryResponse> body = response.body();
                    if (body != null && !body.isEmpty()) {
                        // Kiểm tra dữ liệu có hợp lệ không
                        DictionaryResponse firstResult = body.get(0);
                        if (isValidDictionaryResponse(firstResult)) {
                            callback.onSuccess(body);
                        } else {
                            callback.onError("Dữ liệu từ điển không hợp lệ");
                        }
                    } else {
                        callback.onError("Không tìm thấy từ: " + word);
                    }
                } else {
                    handleErrorResponse(response.code(), word, callback);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DictionaryResponse>> call, @NonNull Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                if (Objects.requireNonNull(t.getMessage()).contains("Unable to resolve host")) {
                    errorMsg = "Không có kết nối internet";
                } else if (t instanceof SocketTimeoutException) {
                    errorMsg = "Kết nối timeout, vui lòng thử lại";
                }
                callback.onError(errorMsg);
            }
        });
    }

    private boolean isValidDictionaryResponse(DictionaryResponse response) {
        return response != null &&
                response.getWord() != null &&
                !response.getWord().isEmpty() &&
                response.getMeanings() != null &&
                !response.getMeanings().isEmpty();
    }

    private void handleErrorResponse(int code, String word, DictionaryCallback callback) {
        switch (code) {
            case 404:
                callback.onError("Không tìm thấy từ: " + word);
                break;
            case 429:
                callback.onError("Quá nhiều yêu cầu, vui lòng thử lại sau");
                break;
            case 500:
            case 502:
            case 503:
                callback.onError("Máy chủ đang bận, vui lòng thử lại sau");
                break;
            default:
                callback.onError("Lỗi máy chủ: " + code);
        }
    }
}