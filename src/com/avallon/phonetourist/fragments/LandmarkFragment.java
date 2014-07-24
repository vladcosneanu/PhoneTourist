package com.avallon.phonetourist.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.avallon.phonetourist.R;
import com.avallon.phonetourist.activities.MainActivity;
import com.avallon.phonetourist.items.LandmarkDetails;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LandmarkFragment extends Fragment {

    private View mView;
    private TextView landmarkName;
    private TextView landmarkAddress;
    private LandmarkDetails landmarkDetails;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private RatingBar ratingBar;
    private TextView landmarkRatingText;
    private TextView landmarkTypes;
    private TextView landmarkDistance;
    private TextView landmarkDuration;
    private TextView landmarkPhone;
    private View landmarkAddressButton;
    private CameraPosition.Builder currentPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.landmark_fragment, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mapFragment = new SupportMapFragment() {
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                map = mapFragment.getMap();
                if (map != null) {
                    map.setMyLocationEnabled(true);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(landmarkDetails.getLatitude(), landmarkDetails.getLongitude()));
                    markerOptions.title(landmarkDetails.getName());
                    markerOptions.draggable(false);
                    markerOptions.snippet(landmarkDetails.getVicinity());
                    Marker marker = map.addMarker(markerOptions);

                    marker.showInfoWindow();

                    currentPosition = new CameraPosition.Builder();
                    currentPosition.bearing(0);
                    currentPosition.tilt(0);
                    currentPosition.target(new LatLng(landmarkDetails.getLatitude(), landmarkDetails.getLongitude()));
                    currentPosition.zoom(16f);
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(currentPosition.build()));
                }
            }
        };
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.map, mapFragment).commit();

        landmarkDetails = ((MainActivity) getActivity()).getLandmarkDetails();
        landmarkName = (TextView) mView.findViewById(R.id.landmark_name);
        landmarkName.setText(landmarkDetails.getName());

        landmarkAddress = (TextView) mView.findViewById(R.id.landmark_address);
        landmarkAddress.setText(landmarkDetails.getFormattedAddress());

        ratingBar = (RatingBar) mView.findViewById(R.id.landmark_rating_value);
        ratingBar.setRating((float) landmarkDetails.getRating());

        landmarkRatingText = (TextView) mView.findViewById(R.id.landmark_rating_text);
        landmarkRatingText.setText(String.format(getString(R.string.rating), String.valueOf(landmarkDetails.getRating())));
        
        landmarkTypes = (TextView) mView.findViewById(R.id.landmark_types);
        String typesValue = "(";
        for (int i = 0; i < landmarkDetails.getTypes().size(); i++) {
            if (i == 0) {
                typesValue += landmarkDetails.getTypes().get(i);
            } else {
                typesValue += ", " + landmarkDetails.getTypes().get(i);
            }
        }
        typesValue += ")";
        landmarkTypes.setText(typesValue);
        
        landmarkDistance = (TextView) mView.findViewById(R.id.landmark_distance);
        landmarkDistance.setText(landmarkDetails.getDistanceText());
        
        landmarkDuration = (TextView) mView.findViewById(R.id.landmark_duration);
        landmarkDuration.setText(landmarkDetails.getDurationText());
        
        landmarkPhone = (TextView) mView.findViewById(R.id.landmark_phone);
        landmarkPhone.setText(landmarkDetails.getInternationalPhoneNumber());
        
        landmarkAddressButton = mView.findViewById(R.id.landmark_address_button);
        landmarkAddressButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                map.animateCamera(CameraUpdateFactory.newCameraPosition(currentPosition.build()));
            }
        });
    }
}
