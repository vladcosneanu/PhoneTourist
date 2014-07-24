package com.avallon.phonetourist.items;

import org.json.JSONException;
import org.json.JSONObject;

public class LandmarkReview {

    private String authorName;
    private String authorUrl;
    private String language;
    private double rating;
    private String text;
    private long time;

    public LandmarkReview(JSONObject json) {
        try {
            authorName = json.getString("author_name");
            if (json.has("author_url")) {
                authorUrl = json.getString("author_url");
            } else {
                authorUrl = "";
            }
            if (json.has("language")) {
                language = json.getString("language");
            } else {
                language = "";
            }
            rating = json.getDouble("rating");
            text = json.getString("text");
            time = json.getLong("time");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
