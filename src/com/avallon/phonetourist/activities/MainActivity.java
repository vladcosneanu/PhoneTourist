package com.avallon.phonetourist.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.avallon.phonetourist.R;
import com.avallon.phonetourist.fragments.ButtonFragment;
import com.avallon.phonetourist.fragments.LandmarkFragment;
import com.avallon.phonetourist.items.Coordinate;
import com.avallon.phonetourist.items.Landmark;
import com.avallon.phonetourist.items.LandmarkDetails;
import com.avallon.phonetourist.requests.RequestLandmark;
import com.avallon.phonetourist.requests.RequestLandmarkDetails;
import com.avallon.phonetourist.requests.RequestLandmarkDistance;
import com.avallon.phonetourist.utils.LocationBroadcastReceiver;
import com.avallon.phonetourist.utils.PreferenceHelper;
import com.avallon.phonetourist.utils.Utils;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

public class MainActivity extends FragmentActivity implements SensorEventListener {

	private static final String BUTTON_FRAGMENT = "BUTTON_FRAGMENT";
	private static final String LANDMARK_FRAGMENT = "LANDMARK_FRAGMENT";
	private SensorManager mSensorManager;
	// private Sensor mSensorMagnetic;
	// private Sensor mSensorAcceleration;
	private Sensor mSensorRotation;
	private float[] mGravity = new float[3];
	private float[] mGeomagnetic = new float[3];
	private LocationBroadcastReceiver lftBroadcastReceiver;
	private List<Landmark> landmarks;
	private boolean receivedAllItems = false;
	private ButtonFragment buttonFragment;
	private LandmarkFragment landmarkFragment;
	private LandmarkDetails landmarkDetails;
	private boolean isLoading = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// mSensorMagnetic =
		// mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		// mSensorAcceleration =
		// mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		buttonFragment = new ButtonFragment();
		ft.add(R.id.fragment_container, buttonFragment, BUTTON_FRAGMENT);
		ft.commit();

		String savedDistance = PreferenceHelper.loadValue(this, PreferenceHelper.DISTANCE);
		if (savedDistance.equals("")) {
			PreferenceHelper.saveValue(this, PreferenceHelper.DISTANCE, PreferenceHelper.DISTANCE_CLOSE_BY);
		}
	}

	public LandmarkDetails getLandmarkDetails() {
		return landmarkDetails;
	}

	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
	}

	@Override
	public final void onSensorChanged(SensorEvent event) {
//		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//			mGravity[0] = event.values[0] * accelFilteringFactor + mGravity[0] * (1.0f - accelFilteringFactor);
//			mGravity[1] = event.values[1] * accelFilteringFactor + mGravity[1] * (1.0f - accelFilteringFactor);
//			mGravity[2] = event.values[2] * accelFilteringFactor + mGravity[2] * (1.0f - accelFilteringFactor);
//		}
//
//		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//			mGeomagnetic[0] = event.values[0] * magFilteringFactor + mGeomagnetic[0] * (1.0f - magFilteringFactor);
//			mGeomagnetic[1] = event.values[1] * magFilteringFactor + mGeomagnetic[1] * (1.0f - magFilteringFactor);
//			mGeomagnetic[2] = event.values[2] * magFilteringFactor + mGeomagnetic[2] * (1.0f - magFilteringFactor);
//		}
//
//		if (mGravity != null && mGeomagnetic != null) {
//			float R[] = new float[9];
//			float I[] = new float[9];
//			boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
//			if (success) {
//				float orientation[] = new float[3];
//				SensorManager.getOrientation(R, orientation);
//
//				float azimuthInRadians = orientation[0];
//				float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;
//				if (!buttonFragment.isCompassAnimationRunning()) {
//					buttonFragment.onSensorChanged(azimuthInDegress);
//				}
//			}
//		}

		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			buttonFragment.onSensorChanged(event);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// force a location update
		LocationLibrary.forceLocationUpdate(this);

		LocationInfo locationInfo = new LocationInfo(this);
		locationInfo.refresh(this);
		Utils.updateCoordinate(new LocationInfo(this));

		final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
		lftBroadcastReceiver = new LocationBroadcastReceiver();
		registerReceiver(lftBroadcastReceiver, lftIntentFilter);

		// mSensorManager.registerListener(this, mSensorMagnetic,
		// SensorManager.SENSOR_DELAY_FASTEST);
		// mSensorManager.registerListener(this, mSensorAcceleration,
		// SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(this, mSensorRotation, SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (lftBroadcastReceiver != null) {
			unregisterReceiver(lftBroadcastReceiver);
		}

		mSensorManager.unregisterListener(this);
	}

	public void onLandmarkReceived(JSONObject json) {
		receivedAllItems = false;
		if (landmarks == null) {
			landmarks = new ArrayList<Landmark>();
		}

		try {
			if (json.getString("status").equals("OK")) {
				JSONArray rows = json.getJSONArray("results");
				for (int i = 0; i < rows.length(); i++) {
//					if (rows.getJSONObject(i).has("photos") && rows.getJSONObject(i).has("rating")) {
				    if (rows.getJSONObject(i).has("photos")) {
						Landmark landmark = new Landmark(rows.getJSONObject(i));
						landmarks.add(landmark);
					}
				}
			}

			if (json.has("next_page_token") && isLoading) {
				receivedAllItems = false;
				final String nextPageToken = json.getString("next_page_token");
				Timer mTimer = new Timer();
				mTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						RequestLandmark requestLandmark = new RequestLandmark(MainActivity.this, 0, 0, nextPageToken);
						requestLandmark.execute(new String[] {});
					}
				}, 2000);
			} else {
				receivedAllItems = true;
			}

			if (receivedAllItems) {
				Log.d("Vlad", "landmarks found: " + landmarks.size());

				if (landmarks.size() > 0 && isLoading) {
					Collections.sort(landmarks);
					for (int i = 0; i < landmarks.size(); i++) {
						Log.d("Vlad", landmarks.get(i).getName() + " - " + landmarks.get(i).getRating());
					}

					int itemNr = Utils.getRandInt(0, (landmarks.size() - 1));
					Landmark selectedLandmark = landmarks.get(itemNr);
					Log.d("Vlad", "selected landmark: " + selectedLandmark.getName());

					RequestLandmarkDetails requestLandmarkDetails = new RequestLandmarkDetails(this, selectedLandmark.getReference());
					requestLandmarkDetails.execute(new String[] {});
				} else {
					// no landmarks found
				    
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onLandmarkDetailsReceived(JSONObject json) {
		setIsLoading(false);
		try {
			if (json.getString("status").equals("OK")) {
				landmarkDetails = new LandmarkDetails(json.getJSONObject("result"));

				Utils.updateCoordinate(new LocationInfo(this));
				Coordinate location = Utils.getLocation();

				RequestLandmarkDistance requestLandmarkDistance = new RequestLandmarkDistance(this, location,
						landmarkDetails.getLatitude(), landmarkDetails.getLongitude());
				requestLandmarkDistance.execute(new String[] {});
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onLandmarkDistanceReceived(JSONObject json) {
		setIsLoading(false);
		try {
			if (json.getString("status").equals("OK")) {
				landmarkDetails.addDistanceDuration(json);

				landmarkFragment = new LandmarkFragment();

				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);
				ft.replace(R.id.fragment_container, landmarkFragment, LANDMARK_FRAGMENT);
				ft.commit();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		LandmarkFragment landmarkFragment = (LandmarkFragment) getSupportFragmentManager().findFragmentByTag(LANDMARK_FRAGMENT);
		if (landmarkFragment != null || isLoading) {
			setIsLoading(false);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			buttonFragment = new ButtonFragment();
			ft.replace(R.id.fragment_container, buttonFragment, BUTTON_FRAGMENT);
			ft.commit();
		} else {
			super.onBackPressed();
		}
	}

	public void setIsLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public void clearLandmarks() {
		landmarks = new ArrayList<Landmark>();
	}
}