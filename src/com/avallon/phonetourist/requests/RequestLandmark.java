package com.avallon.phonetourist.requests;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.avallon.phonetourist.activities.MainActivity;

public class RequestLandmark extends AsyncTask<String, Integer, JSONObject> {

    public static final String GAS_STATION = "gas_station";
    public static final String CAR_REPAIR = "car_repair";
    public static final String CAR_WASH = "car_wash";
    public static final String PARKING = "parking";
    
	private boolean done = false;
	private MainActivity activity;
	private String nextPageToken;
	private double landmarkLat;
	private double landmarkLng;

	public RequestLandmark(MainActivity activity, double lat, double lng, String nextPageToken) {
		this.activity = activity;
		this.landmarkLat = lat;
		this.landmarkLng = lng;
		this.nextPageToken = nextPageToken;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		byte[] result = null;
		JSONObject json = null;
		
		String url = "";
		try {
    		if (nextPageToken == null) {
    		    url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    	        url += "location=" + landmarkLat + "," + landmarkLng;
    	        url += "&radius=50000";
                url += "&types=" + URLEncoder.encode("museum|amusement_park|aquarium|art_gallery|casino|church|hindu_temple|mosque|place_of_worship|synagogue|university", "UTF-8");
    	        url += "&language=en-US";
    	        url += "&sensor=true";
    	        url += "&key=AIzaSyClU64iccv6LVTB0IkccBvL3OKPSrh9jPo";
    		} else {
    	        url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
                url += "pagetoken=" + nextPageToken;
                url += "&sensor=true";
                url += "&key=AIzaSyClU64iccv6LVTB0IkccBvL3OKPSrh9jPo";
    		}
		} catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		try {
			Log.e("request", url);
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			Log.d("status", statusLine.toString());
			if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
				result = EntityUtils.toByteArray(response.getEntity());
				publishProgress(result.length);
				String str = new String(result, "UTF-8");
				json = new JSONObject(str);
			}

			if (json != null) {
				done = true;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}
		return json;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		Log.d("size", values[0].toString());
	}

	@Override
	protected void onPostExecute(JSONObject json) {
		super.onPostExecute(json);
		
		if (done) {
			activity.onLandmarkReceived(json);
		}
	}
}
