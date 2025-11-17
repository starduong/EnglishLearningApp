package com.example.englishlearningapp.view.features_home.chat;

public class MessageChat {
    public int id;
    public String content;
    public boolean isUser;
    public long timestamp;

    public MessageChat() {
    }

    public MessageChat(int id, String content, boolean isUser, long timestamp) {
        this.id = id;
        this.content = content;
        this.isUser = isUser;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}