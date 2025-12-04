package com.example.englishlearningapp.view.features_home.video;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class VideoViewModel extends ViewModel {
    private MutableLiveData<List<VideoItem>> videoList = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private VideoRepository repository;
    private List<VideoItem> allVideos = new ArrayList<>();

    // Constructor không tham số (ViewModel yêu cầu)
    public VideoViewModel() {
        // Khởi tạo repository sau khi có context
    }

    // Phương thức để set context sau khi ViewModel được tạo
    public void init(Context context) {
        if (repository == null) {
            repository = new VideoRepository(context);
            videoList.setValue(new ArrayList<>());
            isLoading.setValue(false);
        }
    }

    public LiveData<List<VideoItem>> getVideoList() {
        return videoList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void searchVideos(String query) {
        if (repository == null) return;

        isLoading.setValue(true);

        repository.searchVideos(query, new VideoRepository.VideoCallback() {
            @Override
            public void onVideosLoaded(List<VideoItem> videos) {
                allVideos.clear();
                allVideos.addAll(videos);
                videoList.postValue(videos);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void filterVideos(String searchText) {
        if (searchText.isEmpty()) {
            videoList.setValue(allVideos);
            return;
        }

        List<VideoItem> filtered = new ArrayList<>();
        for (VideoItem video : allVideos) {
            if (video.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                    video.getDescription().toLowerCase().contains(searchText.toLowerCase())) {
                filtered.add(video);
            }
        }
        videoList.setValue(filtered);
    }
}