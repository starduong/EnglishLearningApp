package com.example.englishlearningapp.view.features_home.video;

import android.content.Context;
import android.util.Log;

import com.example.englishlearningapp.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VideoRepository {
    private static final String TAG = "VideoRepository";
    private static final String API_KEY = "KEY_YOUTUBE";
    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3/search";

    private Context context;

    // Thêm constructor nhận Context
    public VideoRepository(Context context) {
        this.context = context.getApplicationContext(); // Sử dụng Application Context
    }

    public interface VideoCallback {
        void onVideosLoaded(List<VideoItem> videos);

        void onError(String message);
    }

    public void searchVideos(String query, VideoCallback callback) {
        // Kiểm tra internet với context đã được truyền
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("Không có kết nối internet");
            return;
        }

        new Thread(() -> {
            try {
                String searchQuery = query.isEmpty() ? "english learning" : query;
                String urlString = BASE_URL + "?part=snippet&q=" +
                        searchQuery.replace(" ", "%20") +
                        "&maxResults=20&type=video&key=" + API_KEY;

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    List<VideoItem> videos = parseJsonResponse(response.toString());
                    callback.onVideosLoaded(videos);
                } else {
                    callback.onError("HTTP Error: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching videos", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private List<VideoItem> parseJsonResponse(String jsonResponse) {
        List<VideoItem> videos = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray items = jsonObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String videoId = item.getJSONObject("id").getString("videoId");

                JSONObject snippet = item.getJSONObject("snippet");
                String title = snippet.getString("title");
                String description = snippet.getString("description");
                String channelName = snippet.getString("channelTitle");
                String publishedAt = snippet.getString("publishedAt");

                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                String thumbnailUrl = thumbnails.getJSONObject("medium").getString("url");

                // Create video item (duration would require additional API call)
                videos.add(new VideoItem(
                        videoId, title, description, channelName,
                        "10:00", thumbnailUrl, videoId, publishedAt
                ));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
        return videos;
    }
}