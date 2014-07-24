package com.avallon.phonetourist.items;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LandmarkDetails {

    private double latitude;
    private double longitude;
    private String formattedAddress;
    private String internationalPhoneNumber;
    private String id;
    private String name;
    private List<LandmarkPhoto> landmarkPhotos;
    private List<LandmarkReview> landmarkReviews;
    private List<LandmarkEvent> landmarkEvents;
    private double rating;
    private String url;
    private String vicinity;
    private String website;
    private List<String> types;
    private String distanceText;
    private String durationText;

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public LandmarkDetails(JSONObject json) {
        try {
            JSONObject geometry = json.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            latitude = location.getDouble("lat");
            longitude = location.getDouble("lng");

            formattedAddress = json.getString("formatted_address");
            if (json.has("international_phone_number")) {
                internationalPhoneNumber = json.getString("international_phone_number");
            } else {
                internationalPhoneNumber = "";
            }

            id = json.getString("id");

            name = json.getString("name").substring(0, 1).toUpperCase() + json.getString("name").substring(1);

            landmarkPhotos = new ArrayList<LandmarkPhoto>();
            if (json.has("photos")) {
                JSONArray photos = json.getJSONArray("photos");
                for (int i = 0; i < photos.length(); i++) {
                    LandmarkPhoto landmarkPhoto = new LandmarkPhoto(photos.getJSONObject(i));
                    landmarkPhotos.add(landmarkPhoto);
                }
            }

            landmarkReviews = new ArrayList<LandmarkReview>();
            if (json.has("photos")) {
                JSONArray reviews = json.getJSONArray("reviews");
                for (int i = 0; i < reviews.length(); i++) {
                    LandmarkReview landmarkReview = new LandmarkReview(reviews.getJSONObject(i));
                    landmarkReviews.add(landmarkReview);
                }
            }

            landmarkEvents = new ArrayList<LandmarkEvent>();
            if (json.has("events")) {
                JSONArray events = json.getJSONArray("events");
                for (int i = 0; i < events.length(); i++) {
                    LandmarkEvent landmarkEvent = new LandmarkEvent(events.getJSONObject(i));
                    landmarkEvents.add(landmarkEvent);
                }
            }

            if (json.has("rating")) {
                rating = json.getDouble("rating");
            } else {
                rating = 0;
            }

            url = json.getString("url");
            vicinity = json.getString("vicinity");
            if (json.has("website")) {
                website = json.getString("website");
            } else {
                website = "";
            }

            types = new ArrayList<String>();
            JSONArray typesJson = json.getJSONArray("types");
            for (int i = 0; i < typesJson.length(); i++) {
                if (!typesJson.getString(i).equals("establishment")) {
                    String formattedValue = typesJson.getString(i).substring(0, 1).toUpperCase() + typesJson.getString(i).substring(1);
                    types.add(formattedValue.replace("_", " "));
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addDistanceDuration(JSONObject json) {
        try {
            JSONArray rows = json.getJSONArray("rows");
            JSONObject row = rows.getJSONObject(0);
            JSONArray elements = row.getJSONArray("elements");
            JSONObject element = elements.getJSONObject(0);

            JSONObject distance = element.getJSONObject("distance");
            setDistanceText(distance.getString("text"));

            JSONObject duration = element.getJSONObject("duration");
            setDurationText(duration.getString("text"));
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

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
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

    public List<LandmarkReview> getLandmarkReviews() {
        return landmarkReviews;
    }

    public void setLandmarkReviews(List<LandmarkReview> landmarkReviews) {
        this.landmarkReviews = landmarkReviews;
    }

    public List<LandmarkEvent> getLandmarkEvents() {
        return landmarkEvents;
    }

    public void setLandmarkEvents(List<LandmarkEvent> landmarkEvents) {
        this.landmarkEvents = landmarkEvents;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public String getDurationText() {
        return durationText;
    }
}
