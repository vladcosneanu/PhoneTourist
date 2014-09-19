package com.avallon.phonetourist.activities;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.avallon.phonetourist.R;
import com.avallon.phonetourist.items.Coordinate;
import com.avallon.phonetourist.items.RangeSeekBar;
import com.avallon.phonetourist.items.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.avallon.phonetourist.utils.PreferenceHelper;
import com.avallon.phonetourist.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class DistanceSettingsActivity extends FragmentActivity implements OnClickListener {

    private ViewGroup distanceSettingsContainer;
    private TextView fromTextView;
    private TextView upToTextView;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private CameraPosition.Builder currentPosition;
    private Button cancelButton;
    private Button selectButton;
    private int maxTravelDistance;
    private int minTravelDistance;
    private boolean distanceChanged = false;
    private Button fromMinusButton;
    private Button fromPlusButton;
    private Button upToMinusButton;
    private Button upToPlusButton;
    private RangeSeekBar<Integer> seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distance_settings);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = 0;
        params.height = LayoutParams.WRAP_CONTENT;
        params.width = LayoutParams.MATCH_PARENT;
        params.y = 0;

        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        fromTextView = (TextView) findViewById(R.id.from_text);
        upToTextView = (TextView) findViewById(R.id.up_to_text);

        distanceSettingsContainer = (ViewGroup) findViewById(R.id.distance_settings_container);

        seekBar = new RangeSeekBar<Integer>(0, 400, this);
        seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                updateValuesAndMap();
            }
        });

        String savedDistance = PreferenceHelper.loadValue(this, PreferenceHelper.DISTANCE);
        if (savedDistance.equals(PreferenceHelper.DISTANCE_CLOSE_BY)) {
            fromTextView.setText(getString(R.string.from, "0"));
            upToTextView.setText(getString(R.string.up_to, "150"));
            seekBar.setSelectedMinValue(0);
            seekBar.setSelectedMaxValue(3);
            minTravelDistance = 0;
            maxTravelDistance = 150;
        } else if (savedDistance.equals(PreferenceHelper.DISTANCE_FAR_AWAY)) {
            fromTextView.setText(getString(R.string.from, "150"));
            upToTextView.setText(getString(R.string.up_to, "2,000"));
            seekBar.setSelectedMinValue(3);
            seekBar.setSelectedMaxValue(40);
            minTravelDistance = 150;
            maxTravelDistance = 2000;
        } else if (savedDistance.equals(PreferenceHelper.DISTANCE_WHOLE_WORLD)) {
            fromTextView.setText(getString(R.string.from, "0"));
            upToTextView.setText(getString(R.string.up_to, "20,000"));
            seekBar.setSelectedMinValue(0);
            seekBar.setSelectedMaxValue(400);
            minTravelDistance = 0;
            maxTravelDistance = 20000;
        } else if (savedDistance.equals(PreferenceHelper.DISTANCE_CUSTOM)) {
            minTravelDistance = Integer.parseInt(PreferenceHelper.loadValue(this, PreferenceHelper.DISTANCE_CUSTOM_MIN));
            maxTravelDistance = Integer.parseInt(PreferenceHelper.loadValue(this, PreferenceHelper.DISTANCE_CUSTOM_MAX));

            fromTextView.setText(getString(R.string.from, Utils.formatInt(minTravelDistance)));
            upToTextView.setText(getString(R.string.up_to, Utils.formatInt(maxTravelDistance)));
            seekBar.setSelectedMinValue(minTravelDistance / 50);
            seekBar.setSelectedMaxValue(maxTravelDistance / 50);
        }

        distanceSettingsContainer.addView(seekBar);

        mapFragment = new SupportMapFragment() {
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                map = mapFragment.getMap();
                if (map != null) {
                    map.setMyLocationEnabled(true);

                    Coordinate coordinate = Utils.getLocation();

                    currentPosition = new CameraPosition.Builder();
                    currentPosition.bearing(0);
                    currentPosition.tilt(0);
                    currentPosition.target(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()));
                    currentPosition.zoom(16f);
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(currentPosition.build()));

                    setMapCircles();
                }
            }
        };
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.map, mapFragment).commit();

        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);
        selectButton = (Button) findViewById(R.id.select_button);
        selectButton.setOnClickListener(this);
        fromMinusButton = (Button) findViewById(R.id.from_minus_button);
        fromMinusButton.setOnClickListener(this);
        fromPlusButton = (Button) findViewById(R.id.from_plus_button);
        fromPlusButton.setOnClickListener(this);
        upToMinusButton = (Button) findViewById(R.id.up_to_minus_button);
        upToMinusButton.setOnClickListener(this);
        upToPlusButton = (Button) findViewById(R.id.up_to_plus_button);
        upToPlusButton.setOnClickListener(this);
    }

    private void setMapCircles() {
        if (map != null) {
            map.clear();
            Coordinate coordinate = Utils.getLocation();

            // min travel distance circle
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()));
            circleOptions.fillColor(getResources().getColor(R.color.map_accuracy_fill_red));
            circleOptions.strokeColor(getResources().getColor(R.color.map_accuracy_stroke_red));
            circleOptions.strokeWidth(2);
            circleOptions.radius(minTravelDistance * 1000);
            circleOptions.visible(true);
            map.addCircle(circleOptions);

            // max travel distance circle
            circleOptions = new CircleOptions();
            circleOptions.center(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()));
            circleOptions.fillColor(getResources().getColor(R.color.map_accuracy_fill_blue));
            circleOptions.strokeColor(getResources().getColor(R.color.map_accuracy_stroke_blue));
            circleOptions.strokeWidth(2);
            circleOptions.radius(maxTravelDistance * 1000);
            circleOptions.visible(true);
            map.addCircle(circleOptions);

            mapFragment.getView().post(new Runnable() {

                @Override
                public void run() {
                    int mapWidth = mapFragment.getView().getWidth();
                    int mapHeight = mapFragment.getView().getHeight();

                    LatLngBounds.Builder bc = new LatLngBounds.Builder();
                    List<LatLng> circleBounds = new ArrayList<LatLng>();
                    circleBounds.add(Utils.getLocationFromDistanceAndBearing(maxTravelDistance, 0));
                    circleBounds.add(Utils.getLocationFromDistanceAndBearing(maxTravelDistance, 90));
                    circleBounds.add(Utils.getLocationFromDistanceAndBearing(maxTravelDistance, 180));
                    circleBounds.add(Utils.getLocationFromDistanceAndBearing(maxTravelDistance, 270));
                    
                    double longDiff = Math.abs(Math.floor(circleBounds.get(0).longitude - circleBounds.get(2).longitude));
                    if (longDiff <= 1) {
                        // not over the Ecuator yet
                        for (int i = 0; i < circleBounds.size(); i++) {
                            bc.include(circleBounds.get(i));
                        }
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), mapWidth, mapHeight, 100));
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.cancel_button:
            finish();
            break;
        case R.id.select_button:
            if (distanceChanged) {
                distanceChanged = false;
                PreferenceHelper.saveValue(DistanceSettingsActivity.this, PreferenceHelper.DISTANCE, PreferenceHelper.DISTANCE_CUSTOM);
                PreferenceHelper.saveValue(DistanceSettingsActivity.this, PreferenceHelper.DISTANCE_CUSTOM_MIN,
                        String.valueOf(minTravelDistance));
                PreferenceHelper.saveValue(DistanceSettingsActivity.this, PreferenceHelper.DISTANCE_CUSTOM_MAX,
                        String.valueOf(maxTravelDistance));
            }

            finish();
            break;
        case R.id.from_minus_button:
            seekBar.setSelectedMinValue(seekBar.getSelectedMinValue() - 1);
            updateValuesAndMap();
            break;
        case R.id.from_plus_button:
            if (seekBar.getSelectedMaxValue() - seekBar.getSelectedMinValue() > 1){
                seekBar.setSelectedMinValue(seekBar.getSelectedMinValue() + 1);
                updateValuesAndMap();
            }
            break;
        case R.id.up_to_minus_button:
            if (seekBar.getSelectedMaxValue() - seekBar.getSelectedMinValue() > 1) {
                seekBar.setSelectedMaxValue(seekBar.getSelectedMaxValue() - 1);
                updateValuesAndMap();
            }
            break;
        case R.id.up_to_plus_button:
            seekBar.setSelectedMaxValue(seekBar.getSelectedMaxValue() + 1);
            updateValuesAndMap();
            break;
        default:
            break;
        }
    }

    private void updateValuesAndMap() {
        distanceChanged = true;
        minTravelDistance = seekBar.getSelectedMinValue() * 50;
        maxTravelDistance = seekBar.getSelectedMaxValue() * 50;

        fromTextView.setText(getString(R.string.from, Utils.formatInt(seekBar.getSelectedMinValue() * 50)));
        upToTextView.setText(getString(R.string.up_to, Utils.formatInt(seekBar.getSelectedMaxValue() * 50)));

        setMapCircles();
    }
}
