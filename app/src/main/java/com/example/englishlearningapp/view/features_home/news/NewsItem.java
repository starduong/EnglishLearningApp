package com.example.englishlearningapp.view.features_home.news;

import java.io.Serializable;

public class NewsItem implements Serializable {
    private String id;
    private String type;
    private String sectionId;
    private String sectionName;
    private String webPublicationDate;
    private String webTitle;
    private String webUrl;
    private String apiUrl;
    private String bodyText;
    private String trailText;

    // Constructor
    public NewsItem() {
    }

    // Getters and Setters
    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type != null ? type : "";
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSectionId() {
        return sectionId != null ? sectionId : "";
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName != null ? sectionName : "";
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getWebPublicationDate() {
        if (webPublicationDate == null) return "";
        // Format: "2025-12-03T14:52:07Z" -> "Dec 03, 2025"
        try {
            String[] parts = webPublicationDate.split("T")[0].split("-");
            if (parts.length >= 3) {
                String year = parts[0];
                String month = getMonthName(parts[1]);
                String day = parts[2];
                return month + " " + day + ", " + year;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return webPublicationDate;
    }

    public void setWebPublicationDate(String webPublicationDate) {
        this.webPublicationDate = webPublicationDate;
    }

    public String getWebTitle() {
        return webTitle != null ? webTitle.trim() : "No Title";
    }

    public void setWebTitle(String webTitle) {
        this.webTitle = webTitle;
    }

    public String getWebUrl() {
        return webUrl != null ? webUrl : "";
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getApiUrl() {
        return apiUrl != null ? apiUrl : "";
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getBodyText() {
        return bodyText != null ? bodyText : "";
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getTrailText() {
        return trailText != null ? trailText : "";
    }

    public void setTrailText(String trailText) {
        this.trailText = trailText;
    }

    public String getShortDescription() {
        // Ưu tiên dùng trailText từ API
        String desc = getTrailText();
        if (desc == null || desc.isEmpty()) {
            desc = getBodyText();
        }

        if (desc.length() > 150) {
            return desc.substring(0, 150) + "...";
        }
        return desc;
    }

    public String getSectionColor() {
        String section = getSectionName().toLowerCase();

        if (section.contains("sport")) return "#4CAF50"; // Green
        else if (section.contains("business") || section.contains("money"))
            return "#FF9800"; // Orange
        else if (section.contains("world")) return "#F44336"; // Red
        else if (section.contains("politics")) return "#3F51B5"; // Indigo
        else if (section.contains("entertainment") || section.contains("culture"))
            return "#9C27B0"; // Purple
        else if (section.contains("technology") || section.contains("science"))
            return "#00BCD4"; // Cyan
        else if (section.contains("education") || section.contains("learning"))
            return "#795548"; // Brown
        else if (section.contains("lifestyle") || section.contains("life"))
            return "#E91E63"; // Pink
        else return "#607D8B"; // Blue Grey
    }

    private String getMonthName(String monthNumber) {
        switch (monthNumber) {
            case "01":
                return "Jan";
            case "02":
                return "Feb";
            case "03":
                return "Mar";
            case "04":
                return "Apr";
            case "05":
                return "May";
            case "06":
                return "Jun";
            case "07":
                return "Jul";
            case "08":
                return "Aug";
            case "09":
                return "Sep";
            case "10":
                return "Oct";
            case "11":
                return "Nov";
            case "12":
                return "Dec";
            default:
                return monthNumber;
        }
    }
}