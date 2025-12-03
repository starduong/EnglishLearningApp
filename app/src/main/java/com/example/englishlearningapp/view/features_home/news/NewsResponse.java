package com.example.englishlearningapp.view.features_home.news;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsResponse {

    @SerializedName("response")
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class Response {

        @SerializedName("status")
        private String status;

        @SerializedName("total")
        private int total;

        @SerializedName("results")
        private List<Result> results;

        @SerializedName("content")
        private Content content;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }
    }

    public static class Result {

        @SerializedName("id")
        private String id;

        @SerializedName("type")
        private String type;

        @SerializedName("sectionId")
        private String sectionId;

        @SerializedName("sectionName")
        private String sectionName;

        @SerializedName("webPublicationDate")
        private String webPublicationDate;

        @SerializedName("webTitle")
        private String webTitle;

        @SerializedName("webUrl")
        private String webUrl;

        @SerializedName("apiUrl")
        private String apiUrl;

        @SerializedName("fields")
        private Fields fields;

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
            return webPublicationDate != null ? webPublicationDate : "";
        }

        public void setWebPublicationDate(String webPublicationDate) {
            this.webPublicationDate = webPublicationDate;
        }

        public String getWebTitle() {
            return webTitle != null ? webTitle : "";
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

        public Fields getFields() {
            return fields;
        }

        public void setFields(Fields fields) {
            this.fields = fields;
        }

        public NewsItem toNewsItem() {
            NewsItem item = new NewsItem();
            item.setId(getId());
            item.setType(getType());
            item.setSectionId(getSectionId());
            item.setSectionName(getSectionName());
            item.setWebPublicationDate(getWebPublicationDate());
            item.setWebTitle(getWebTitle());
            item.setWebUrl(getWebUrl());
            item.setApiUrl(getApiUrl());

            if (fields != null) {
                item.setBodyText(fields.getBodyText());
                item.setTrailText(fields.getTrailText());
            }

            return item;
        }
    }

    public static class Content {

        @SerializedName("id")
        private String id;

        @SerializedName("webTitle")
        private String webTitle;

        @SerializedName("webPublicationDate")
        private String webPublicationDate;

        @SerializedName("webUrl")
        private String webUrl;

        @SerializedName("fields")
        private Fields fields;

        public String getId() {
            return id != null ? id : "";
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getWebTitle() {
            return webTitle != null ? webTitle : "";
        }

        public void setWebTitle(String webTitle) {
            this.webTitle = webTitle;
        }

        public String getWebPublicationDate() {
            return webPublicationDate != null ? webPublicationDate : "";
        }

        public void setWebPublicationDate(String webPublicationDate) {
            this.webPublicationDate = webPublicationDate;
        }

        public String getWebUrl() {
            return webUrl != null ? webUrl : "";
        }

        public void setWebUrl(String webUrl) {
            this.webUrl = webUrl;
        }

        public Fields getFields() {
            return fields;
        }

        public void setFields(Fields fields) {
            this.fields = fields;
        }

        public NewsItem toNewsItem() {
            NewsItem item = new NewsItem();
            item.setId(getId());
            item.setWebTitle(getWebTitle());
            item.setWebPublicationDate(getWebPublicationDate());
            item.setWebUrl(getWebUrl());

            if (fields != null) {
                item.setBodyText(fields.getBodyText());
                item.setTrailText(fields.getTrailText());
            }

            return item;
        }
    }

    public static class Fields {

        @SerializedName("bodyText")
        private String bodyText;

        @SerializedName("headline")
        private String headline;

        @SerializedName("trailText")
        private String trailText;

        public String getBodyText() {
            return bodyText != null ? bodyText : "";
        }

        public void setBodyText(String bodyText) {
            this.bodyText = bodyText;
        }

        public String getHeadline() {
            return headline != null ? headline : "";
        }

        public void setHeadline(String headline) {
            this.headline = headline;
        }

        public String getTrailText() {
            return trailText != null ? trailText : "";
        }

        public void setTrailText(String trailText) {
            this.trailText = trailText;
        }
    }
}