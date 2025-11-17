package com.example.englishlearningapp.view.features_home.chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiChatService {
    // Nếu không có 2.5 pro thì điền models/gemini-2.5-flash nha
    private static final String API_KEY = "YOUR_KEY";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 30000;
    private static final int MAX_RETRIES = 2;
    private static final long RETRY_DELAY_MS = 1000;

    public static String getGeminiResponse(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return createErrorJson("Please enter a message");
        }

        HttpURLConnection connection = null;
        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            try {
                URL url = new URL(GEMINI_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);

                // Build request
                JSONObject requestBody = new JSONObject();

                JSONArray contents = new JSONArray();
                JSONObject content = new JSONObject();
                JSONArray parts = new JSONArray();
                JSONObject part = new JSONObject();
                part.put("text", prompt);
                parts.put(part);
                content.put("parts", parts);
                contents.put(content);

                requestBody.put("contents", contents);

                // Generation config
                JSONObject generationConfig = new JSONObject();
                generationConfig.put("temperature", 0.5);
                generationConfig.put("maxOutputTokens", 20000);

                requestBody.put("generationConfig", generationConfig);

                // Send request
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int statusCode = connection.getResponseCode();

                if (statusCode == 429) {
                    retryCount++;
                    if (retryCount < MAX_RETRIES) {
                        Thread.sleep(RETRY_DELAY_MS);
                        continue;
                    }
                    return createErrorJson("Too many requests. Please wait.");
                }

                if (statusCode != HttpURLConnection.HTTP_OK) {
                    String errorDetail = readErrorStream(connection);
                    return createErrorJson("API error: " + statusCode);
                }

                String response = readInputStream(connection.getInputStream());
                return parseResponse(response);

            } catch (IOException e) {
                if (retryCount < MAX_RETRIES - 1) {
                    retryCount++;
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                        continue;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return createErrorJson("Request interrupted");
                    }
                }
                return createErrorJson("Network error: Please check your connection");
            } catch (Exception e) {
                return createErrorJson("Error: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        return createErrorJson("Failed after retries");
    }

    /**
     * Parser đơn giản cho cấu trúc response đã xác nhận
     */
    private static String parseResponse(String jsonResponse) {
        try {
            JSONObject response = new JSONObject(jsonResponse);

            // Check for API errors
            if (response.has("error")) {
                JSONObject error = response.getJSONObject("error");
                String message = error.optString("message", "API error");
                return createErrorJson(message);
            }

            // Parse successful response
            if (response.has("candidates")) {
                JSONArray candidates = response.getJSONArray("candidates");
                if (candidates.length() > 0) {
                    JSONObject candidate = candidates.getJSONObject(0);

                    // Get content text
                    if (candidate.has("content")) {
                        JSONObject content = candidate.getJSONObject("content");
                        if (content.has("parts")) {
                            JSONArray parts = content.getJSONArray("parts");
                            if (parts.length() > 0) {
                                JSONObject part = parts.getJSONObject(0);
                                if (part.has("text")) {
                                    String text = part.getString("text").trim();
                                    JSONObject result = new JSONObject();
                                    result.put("text", text);
                                    return result.toString();
                                }
                            }
                        }
                    }
                }
            }

            return createErrorJson("No response content received");

        } catch (JSONException e) {
            return createErrorJson("Failed to parse response");
        }
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    private static String readErrorStream(HttpURLConnection connection) {
        try {
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                }
                return errorResponse.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No error details";
    }

    private static String createErrorJson(String message) {
        try {
            JSONObject error = new JSONObject();
            error.put("error", message);
            return error.toString();
        } catch (JSONException e) {
            return "{\"error\":\"Error\"}";
        }
    }
}