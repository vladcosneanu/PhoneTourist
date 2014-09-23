package com.avallon.phonetourist.items;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.avallon.phonetourist.requests.RequestReviewImage;
import com.avallon.phonetourist.utils.Utils;

public class LandmarkReview {

    private String authorName;
    private String authorUrl;
    private String authorId;
    private String language;
    private double rating;
    private String text;
    private long time;
    private String imageUrl;
    
    private boolean imageRequestFinished = true;

    public LandmarkReview(JSONObject json) {
        try {
            authorName = json.getString("author_name");
            if (json.has("author_url")) {
                imageRequestFinished = false;
                authorUrl = json.getString("author_url");
                String[] split = authorUrl.split("/");
                authorId = split[split.length - 1];
                
                RequestReviewImage requestReviewImage = new RequestReviewImage(this, authorId);
                requestReviewImage.execute(new String[] {});
            } else {
                imageRequestFinished = true;
                authorUrl = "";
                authorId = "";
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
    
    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public void onReviewImageUrlReceived(JSONObject json, String authorId) {
        try {
            JSONObject image = json.getJSONObject("image");
            imageUrl = image.getString("url");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        imageRequestFinished = true;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isImageRequestFinished() {
        return imageRequestFinished;
    }

    public void setImageRequestFinished(boolean imageRequestFinished) {
        this.imageRequestFinished = imageRequestFinished;
    }
}
