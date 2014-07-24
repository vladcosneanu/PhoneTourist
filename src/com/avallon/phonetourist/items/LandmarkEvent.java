package com.avallon.phonetourist.items;

import org.json.JSONException;
import org.json.JSONObject;

public class LandmarkEvent {

    private long startTime;
    private String summary;
    private String url;
    
    public LandmarkEvent(JSONObject json) {
        try {
            startTime = json.getLong("start_time");
            summary = json.getString("summary");
            
            if (json.has("url")) {
                url = json.getString("url");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
