package com.avallon.phonetourist.items;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Landmark implements Comparable<Landmark> {

    private double latitude;
    private double longitude;
    private String id;
    private String name;
    private List<LandmarkPhoto> landmarkPhotos;
    private double rating;
    private String reference;
    private List<String> types;
    private String vicinity;

    public Landmark(JSONObject json) {
        try {
            JSONObject geometry = json.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            latitude = location.getDouble("lat");
            longitude = location.getDouble("lng");

            id = json.getString("id");
            name = json.getString("name");

            landmarkPhotos = new ArrayList<LandmarkPhoto>();
            if (json.has("photos")) {
                JSONArray photos = json.getJSONArray("photos");
                for (int i = 0; i < photos.length(); i++) {
                    LandmarkPhoto landmarkPhoto = new LandmarkPhoto(photos.getJSONObject(i));
                    landmarkPhotos.add(landmarkPhoto);
                }
            }

            if (json.has("rating")) {
                rating = json.getDouble("rating");
            } else {
                rating = 0;
            }

            reference = json.getString("reference");

            types = new ArrayList<String>();
            JSONArray typesJson = json.getJSONArray("types");
            for (int i = 0; i < typesJson.length(); i++) {
                types.add(typesJson.getString(i));
            }

            vicinity = json.getString("vicinity");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LandmarkPhoto> getLandmarkPhotos() {
        return landmarkPhotos;
    }

    public void setLandmarkPhotos(List<LandmarkPhoto> landmarkPhotos) {
        this.landmarkPhotos = landmarkPhotos;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public int compareTo(Landmark compareLandmark) {
        double compareRating = compareLandmark.getRating();
        if (getRating() > compareRating) {
            return -1;
        } else if (getRating() < compareRating) {
            return 1;
        } else {
            return 0;
        }
    }
}
