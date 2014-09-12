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

public class RequestLandmarkDirections extends AsyncTask<String, Integer, JSONObject> {

	private boolean done = false;
	private MainActivity activity;
	private String currentLocationLatLng;
	private String landmarkLocationLatLng;

	public RequestLandmarkDirections(MainActivity activity, String currentLocationLatLng, String landmarkLocationLatLng) {
		this.activity = activity;
		this.currentLocationLatLng = currentLocationLatLng;
		this.landmarkLocationLatLng = landmarkLocationLatLng;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		byte[] result = null;
		JSONObject json = null;
		
		try {
		    String request = "https://maps.googleapis.com/maps/api/directions/json";
		    request += "?sensor=true";
		    request += "&mode=driving";
		    request += "&alternatives=false";
		    request += "&units=metric";
		    request += "&origin=" + currentLocationLatLng;
		    request += "&destination=" + landmarkLocationLatLng;
		    
			Log.e("request", request);
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(request);
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
			activity.onLandmarkDirectionsReceived(json);
		}
	}
}
