package com.avallon.phonetourist.items;

import org.json.JSONException;
import org.json.JSONObject;

public class LandmarkPhoto {
    
    private int height;
    private int width;
    private String photoReference;
    
    public LandmarkPhoto(JSONObject json) {
        try {
            height = json.getInt("height");
            width = json.getInt("width");
            photoReference = json.getString("photo_reference");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public String getPhotoReference() {
        return photoReference;
    }
    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }
    
}
