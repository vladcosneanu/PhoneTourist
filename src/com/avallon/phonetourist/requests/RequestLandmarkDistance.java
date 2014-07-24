package com.avallon.phonetourist.requests;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

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
import com.avallon.phonetourist.items.Coordinate;

public class RequestLandmarkDistance extends AsyncTask<String, Integer, JSONObject> {

    public static final String GAS_STATION = "gas_station";
    public static final String CAR_REPAIR = "car_repair";
    public static final String CAR_WASH = "car_wash";
    public static final String PARKING = "parking";
    
	private boolean done = false;
	private MainActivity activity;
	private Coordinate currentLocation;
	private double destinationLat;
	private double destinationLng;

	public RequestLandmarkDistance(MainActivity activity, Coordinate currentLocation, double destinationLat, double destinationLng) {
		this.activity = activity;
		this.currentLocation = currentLocation;
		this.destinationLat = destinationLat;
		this.destinationLng = destinationLng;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		byte[] result = null;
		JSONObject json = null;
		
		String url = "https://maps.googleapis.com/maps/api/distancematrix/json?";
		url += "origins=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
		url += "&destinations=" + destinationLat + "," + destinationLng;
		url += "&sensor=true";
		url += "&mode=driving";
		url += "&units=metric";
		url += "&key=AIzaSyClU64iccv6LVTB0IkccBvL3OKPSrh9jPo";
			
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
			activity.onLandmarkDistanceReceived(json);
		}
	}
}
