package com.avallon.phonetourist.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.avallon.phonetourist.R;
import com.avallon.phonetourist.activities.MainActivity;
import com.avallon.phonetourist.adapters.ReviewsListAdapter;
import com.avallon.phonetourist.items.LandmarkDetails;
import com.avallon.phonetourist.items.LandmarkReview;
import com.avallon.phonetourist.requests.RequestLandmarkDirections;
import com.avallon.phonetourist.utils.MapHelper;
import com.avallon.phonetourist.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
    private View landmarkRatingContainer;
    private View landmarkNoRatingContainer;
    private ImageButton landmarkDirectionsButton;
    private List<LatLng> allLocations;
    private Marker marker;
    private ListView reviewsList;
    private ReviewsListAdapter reviewsListAdapter;
    private List<LandmarkReview> landmarkReviews;
    private View landmarkReviewsContainer;

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
                    map.getUiSettings().setAllGesturesEnabled(false);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(landmarkDetails.getLatitude(), landmarkDetails.getLongitude()));
                    markerOptions.title(landmarkDetails.getName());
                    markerOptions.draggable(false);
                    markerOptions.snippet(landmarkDetails.getVicinity());
                    marker = map.addMarker(markerOptions);

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

        landmarkRatingContainer = mView.findViewById(R.id.landmark_rating_container);
        landmarkNoRatingContainer = mView.findViewById(R.id.landmark_no_rating_container);

        if (landmarkDetails.getRating() != 0) {
            landmarkRatingContainer.setVisibility(View.VISIBLE);
            landmarkNoRatingContainer.setVisibility(View.GONE);
            ratingBar = (RatingBar) mView.findViewById(R.id.landmark_rating_value);
            ratingBar.setRating((float) landmarkDetails.getRating());

            landmarkRatingText = (TextView) mView.findViewById(R.id.landmark_rating_text);
            landmarkRatingText.setText(String.format(getString(R.string.rating), String.valueOf(landmarkDetails.getRating())));
        } else {
            landmarkRatingContainer.setVisibility(View.GONE);
            landmarkNoRatingContainer.setVisibility(View.VISIBLE);
        }

        landmarkTypes = (TextView) mView.findViewById(R.id.landmark_types);
        if (landmarkDetails.getTypes() != null && landmarkDetails.getTypes().size() > 0) {
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
        } else {
            landmarkTypes.setVisibility(View.GONE);
        }

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

                if (marker != null) {
                    marker.showInfoWindow();
                }
            }
        });

        landmarkDirectionsButton = (ImageButton) mView.findViewById(R.id.landmark_directions_button);
        landmarkDirectionsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allLocations != null && allLocations.size() > 0) {
                    addLandmarkDirections();
                } else {
                    String currentLocationLatLng = Utils.getLocation().getLatitude() + "," + Utils.getLocation().getLongitude();
                    String landmarkLocationLatLng = landmarkDetails.getLatitude() + "," + landmarkDetails.getLongitude();

                    RequestLandmarkDirections requestLandmarkDirections = new RequestLandmarkDirections((MainActivity) getActivity(),
                            currentLocationLatLng, landmarkLocationLatLng);
                    requestLandmarkDirections.execute(new String[] {});
                }
            }
        });

        landmarkReviewsContainer = mView.findViewById(R.id.landmark_reviews_container);
        reviewsList = (ListView) mView.findViewById(R.id.reviews_list);
        landmarkReviews = landmarkDetails.getLandmarkReviews();
        if (landmarkReviews.size() > 0) {
            landmarkReviewsContainer.setVisibility(View.VISIBLE);
            reviewsListAdapter = new ReviewsListAdapter(getActivity(), R.layout.review_item, landmarkReviews);
            reviewsList.setAdapter(reviewsListAdapter);

            setListViewHeightBasedOnChildren(reviewsList);
        } else {
            landmarkReviewsContainer.setVisibility(View.GONE);
        }
    }

    public void onLandmarkDirectionsReceived(JSONObject json) {
        try {
            allLocations = new ArrayList<LatLng>();

            JSONArray routes = json.getJSONArray("routes");
            JSONObject route = routes.getJSONObject(0);
            JSONArray legs = route.getJSONArray("legs");

            for (int i = 0; i < legs.length(); i++) {
                JSONObject leg = new JSONObject(legs.get(i).toString());

                JSONArray steps = leg.getJSONArray("steps");
                for (int j = 0; j < steps.length(); j++) {
                    JSONObject step = new JSONObject(steps.get(j).toString());

                    JSONObject polylineObj = step.getJSONObject("polyline");
                    String polyline = polylineObj.getString("points");
                    List<LatLng> polyList = MapHelper.decodePoly(polyline);
                    allLocations.addAll(polyList);
                }
            }

            map.addPolyline(new PolylineOptions().addAll(allLocations).width(5).color(getResources().getColor(R.color.holo_blue_light)));

            addLandmarkDirections();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addLandmarkDirections() {
        mapFragment.getView().post(new Runnable() {
            @Override
            public void run() {
                if (allLocations.size() > 0) {
                    final LatLngBounds.Builder bc = new LatLngBounds.Builder();

                    for (int k = 0; k < allLocations.size(); k++) {
                        bc.include(allLocations.get(k));
                    }

                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 100));
                }
            }
        });
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
