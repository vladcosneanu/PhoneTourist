package com.avallon.phonetourist.fragments;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.avallon.phonetourist.R;
import com.avallon.phonetourist.activities.MainActivity;
import com.avallon.phonetourist.interfaces.CustomButtonAnimationListener;
import com.avallon.phonetourist.items.Coordinate;
import com.avallon.phonetourist.items.CustomButton;
import com.avallon.phonetourist.items.CustomCompassArrows;
import com.avallon.phonetourist.requests.RequestLandmark;
import com.avallon.phonetourist.utils.PreferenceHelper;
import com.avallon.phonetourist.utils.Utils;

public class ButtonFragment extends Fragment implements OnClickListener, CustomButtonAnimationListener {

    private View mView;
    private CustomButton customButton;
    private CustomCompassArrows customCompassArrows;
    private float bearing;
    private float earthRadius = 6371f;
    private double landmarkLat;
    private double landmarkLng;
    private Button closeByButton;
    private Button farAwayButton;
    private Button wholeWorldButton;
    private View distanceButtonsContainer;
    private RelativeLayout wordCloudAbove;
    private RelativeLayout wordCloudBelow;
    private Timer timer;
    private Timer timer2;
    private int wordCloudAboveWidth = 0;
    private int wordCloudAboveHeight = 0;
    private int wordCloudBelowWidth = 0;
    private int wordCloudBelowHeight = 0;
    private boolean displayWords = false;
    private View instructionsContainer;
    private TextView instructionsTitle;
    private TextView instruction1Text;
    private TextView instruction2Text;
    private TextView instruction3Text;
    private Typeface customFont;
    
    private static int customCompassArrowsWidth;
    private static int customCompassArrowsHeight;
    
    private boolean compassAnimationRunning = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.button_fragment, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        customButton = (CustomButton) mView.findViewById(R.id.custom_button);
        customButton.setCustomButtonAnimationListener(this);
        customButton.startAnimation((AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in)));
        
        customCompassArrows = (CustomCompassArrows) mView.findViewById(R.id.custom_compass_arrows);
        customCompassArrows.startAnimation((AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in)));
        
        setCompassArrowsMeasurements();

        distanceButtonsContainer = mView.findViewById(R.id.distance_buttons_container);
        distanceButtonsContainer.startAnimation((AnimationUtils.loadAnimation(getActivity(), R.anim.enter_up)));

        closeByButton = (Button) mView.findViewById(R.id.close_by_button);
        closeByButton.setOnClickListener(this);
        farAwayButton = (Button) mView.findViewById(R.id.far_away_button);
        farAwayButton.setOnClickListener(this);
        wholeWorldButton = (Button) mView.findViewById(R.id.whole_world_button);
        wholeWorldButton.setOnClickListener(this);
        wordCloudAbove = (RelativeLayout) mView.findViewById(R.id.word_cloud_above);
        wordCloudBelow = (RelativeLayout) mView.findViewById(R.id.word_cloud_below);

        String savedDistance = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.DISTANCE);
        if (savedDistance.equals(PreferenceHelper.DISTANCE_CLOSE_BY)) {
            selectCloseBy();
        } else if (savedDistance.equals(PreferenceHelper.DISTANCE_FAR_AWAY)) {
            selectFarAway();
        } else if (savedDistance.equals(PreferenceHelper.DISTANCE_WHOLE_WORLD)) {
            selectWholeWorld();
        }
        
        instructionsContainer = mView.findViewById(R.id.instructions_container);
        instructionsContainer.startAnimation((AnimationUtils.loadAnimation(getActivity(), R.anim.enter_down)));
        
        customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/a_little_sunshine.ttf");
        instructionsTitle = (TextView) mView.findViewById(R.id.instructions_title);
        instructionsTitle.setTypeface(customFont);
        instruction1Text = (TextView) mView.findViewById(R.id.instruction1_text);
        instruction1Text.setTypeface(customFont);
        instruction2Text = (TextView) mView.findViewById(R.id.instruction2_text);
        instruction2Text.setTypeface(customFont);
        instruction3Text = (TextView) mView.findViewById(R.id.instruction3_text);
        instruction3Text.setTypeface(customFont);
    }

    private void getLocationInfo() {
        Coordinate location = Utils.getLocation();

        float currentLat = (float) Math.toRadians(location.getLatitude());
        float currentLng = (float) Math.toRadians(location.getLongitude());

        double bearingRad = Math.toRadians(bearing);
        Log.d("Vlad", "bearing: " + bearing);
        float distance = 0;
        String savedDistance = PreferenceHelper.loadValue(getActivity(), PreferenceHelper.DISTANCE);
        if (savedDistance.equals(PreferenceHelper.DISTANCE_CLOSE_BY)) {
            distance = Utils.getRandInt(50, 100);
        } else if (savedDistance.equals(PreferenceHelper.DISTANCE_FAR_AWAY)) {
            distance = Utils.getRandInt(100, 2000);
        } else if (savedDistance.equals(PreferenceHelper.DISTANCE_WHOLE_WORLD)) {
            distance = Utils.getRandInt(50, 20000);
        }
        Log.d("Vlad", "distance: " + distance);

        float destinationLat = (float) Math.asin(Math.sin(currentLat) * Math.cos(distance / earthRadius) + Math.cos(currentLat)
                * Math.sin(distance / earthRadius) * Math.cos(bearingRad));
        float destinationLng = (float) (currentLng + Math.atan2(
                Math.sin(bearingRad) * Math.sin(distance / earthRadius) * Math.cos(currentLat),
                Math.cos(distance / earthRadius) - Math.sin(currentLat) * Math.sin(destinationLat)));

        landmarkLat = Math.toDegrees(destinationLat);
        landmarkLng = Math.toDegrees(destinationLng);
        Log.d("Vlad", "destination: " + landmarkLat + "," + landmarkLng);
    }

    private void measureWordCloudContainers() {
        wordCloudAbove.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                wordCloudAboveWidth = wordCloudAbove.getMeasuredWidth();
                wordCloudAboveHeight = wordCloudAbove.getMeasuredHeight();
                onHeightReceived();

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    wordCloudAbove.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    wordCloudAbove.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        wordCloudBelow.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                wordCloudBelowWidth = wordCloudBelow.getMeasuredWidth();
                wordCloudBelowHeight = wordCloudBelow.getMeasuredHeight();
                onHeightReceived();

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    wordCloudBelow.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    wordCloudBelow.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void onHeightReceived() {
        if (isInvalidHeights()) {
            return;
        }

        if (displayWords) {
            addWordsUp();
            addWordsBelow();
        }
    }

    private boolean isInvalidHeights() {
        if (wordCloudAboveWidth == 0 || wordCloudAboveHeight == 0 || wordCloudBelowWidth == 0 || wordCloudBelowHeight == 0) {
            return true;
        }

        return false;
    }

    @Override
    public void onCustomButtonAnimationEnd() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    customCompassArrows.startAnimation((AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out)));
                    
                    ((MainActivity) getActivity()).setIsLoading(true);
                    ((MainActivity) getActivity()).clearLandmarks();
                    
                    getLocationInfo();

                    Animation exitUp = AnimationUtils.loadAnimation(getActivity(), R.anim.exit_up);
                    exitUp.setAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            distanceButtonsContainer.setVisibility(View.GONE);
                            displayWords = true;
                            measureWordCloudContainers();
                        }
                    });
                    distanceButtonsContainer.startAnimation(exitUp);

                    Animation exitdown = AnimationUtils.loadAnimation(getActivity(), R.anim.exit_down);
                    exitdown.setAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            instructionsContainer.setVisibility(View.GONE);
                        }
                    });
                    instructionsContainer.startAnimation(exitdown);
                    
                    RequestLandmark requestLandmark = new RequestLandmark((MainActivity) getActivity(), landmarkLat, landmarkLng, null);
                    requestLandmark.execute(new String[] {});
                }
            });
        }
    }

    private void addWordsUp() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    timer.cancel();
                    timer2.cancel();
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() == null) {
                            return;
                        }
                        
                        final TextView textView = new TextView(getActivity());
                        textView.setText(Utils.getTravelPhrase(getActivity()));
                        textView.setTextSize(20);
                        textView.setTypeface(customFont);
                        textView.setTextColor(getResources().getColor(R.color.text_color_grey));
                        textView.measure(0, 0);
                        int width = textView.getMeasuredWidth();
                        int height = textView.getMeasuredHeight();
                        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        params.setMargins(Utils.getRandInt(0, wordCloudAboveWidth - width),
                                Utils.getRandInt(0, wordCloudAboveHeight - height), 0, 0);
                        textView.setLayoutParams(params);

                        wordCloudAbove.addView(textView);
                        
                        Animation wordAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.word_fade);
                        wordAnimation.setAnimationListener(new AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                wordCloudAbove.removeView(textView);
                            }
                        });
                        textView.startAnimation(wordAnimation);
                    }
                });
            }
        }, 0, 2000);
    }

    private void addWordsBelow() {
        if (timer2 != null) {
            timer2.cancel();
        }
        timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    timer.cancel();
                    timer2.cancel();
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() == null) {
                            return;
                        }
                        
                        final TextView textView = new TextView(getActivity());
                        textView.setText(Utils.getTravelPhrase(getActivity()));
                        textView.setTextSize(20);
                        textView.setTypeface(customFont);
                        textView.setTextColor(getResources().getColor(R.color.text_color_grey));
                        textView.measure(0, 0);
                        int width = textView.getMeasuredWidth();
                        int height = textView.getMeasuredHeight();
                        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        params.setMargins(Utils.getRandInt(0, wordCloudBelowWidth - width),
                                Utils.getRandInt(0, wordCloudBelowHeight - height), 0, 0);
                        textView.setLayoutParams(params);

                        wordCloudBelow.addView(textView);
                        
                        Animation wordAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.word_fade);
                        wordAnimation.setAnimationListener(new AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                wordCloudBelow.removeView(textView);
                            }
                        });
                        textView.startAnimation(wordAnimation);
                    }
                });
            }
        }, 1000, 2000);
    }

    public boolean isCompassAnimationRunning() {
        return compassAnimationRunning;
    }
    
    public void onSensorChanged(float bearing) {
        float difference = bearing - this.bearing;
        if (difference > 3 || difference < -3) {
            if (customCompassArrows != null && !compassAnimationRunning) {
                compassAnimationRunning = true;
                Animation animation = new RotateAnimation(-this.bearing, -bearing, customCompassArrowsWidth/2, customCompassArrowsHeight/2);
                animation.setDuration(33);
                animation.setFillEnabled(true);
                animation.setFillAfter(true);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.setRepeatCount(0);
                animation.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        compassAnimationRunning = true;
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        compassAnimationRunning = false;
                    }
                });
                customCompassArrows.startAnimation(animation);
            }
            this.bearing = bearing;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.close_by_button:
            selectCloseBy();
            PreferenceHelper.saveValue(getActivity(), PreferenceHelper.DISTANCE, PreferenceHelper.DISTANCE_CLOSE_BY);
            break;
        case R.id.far_away_button:
            selectFarAway();
            PreferenceHelper.saveValue(getActivity(), PreferenceHelper.DISTANCE, PreferenceHelper.DISTANCE_FAR_AWAY);
            break;
        case R.id.whole_world_button:
            selectWholeWorld();
            PreferenceHelper.saveValue(getActivity(), PreferenceHelper.DISTANCE, PreferenceHelper.DISTANCE_WHOLE_WORLD);
            break;
        default:
            break;
        }
    }

    private void selectCloseBy() {
        closeByButton.setBackgroundResource(R.drawable.button_left_selected);
        closeByButton.setTextColor(getResources().getColor(android.R.color.white));
        farAwayButton.setBackgroundResource(R.drawable.button_center_unselected);
        farAwayButton.setTextColor(getResources().getColor(R.color.text_color_grey));
        wholeWorldButton.setBackgroundResource(R.drawable.button_right_unselected);
        wholeWorldButton.setTextColor(getResources().getColor(R.color.text_color_grey));
    }

    private void selectFarAway() {
        closeByButton.setBackgroundResource(R.drawable.button_left_unselected);
        closeByButton.setTextColor(getResources().getColor(R.color.text_color_grey));
        farAwayButton.setBackgroundResource(R.drawable.button_center_selected);
        farAwayButton.setTextColor(getResources().getColor(android.R.color.white));
        wholeWorldButton.setBackgroundResource(R.drawable.button_right_unselected);
        wholeWorldButton.setTextColor(getResources().getColor(R.color.text_color_grey));
    }

    private void selectWholeWorld() {
        closeByButton.setBackgroundResource(R.drawable.button_left_unselected);
        closeByButton.setTextColor(getResources().getColor(R.color.text_color_grey));
        farAwayButton.setBackgroundResource(R.drawable.button_center_unselected);
        farAwayButton.setTextColor(getResources().getColor(R.color.text_color_grey));
        wholeWorldButton.setBackgroundResource(R.drawable.button_right_selected);
        wholeWorldButton.setTextColor(getResources().getColor(android.R.color.white));
    }
    
    private void setCompassArrowsMeasurements() {
        if (customCompassArrows != null) {
            customCompassArrows.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressLint("NewApi")
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    customCompassArrowsWidth = customCompassArrows.getWidth();
                    customCompassArrowsHeight = customCompassArrows.getHeight();
                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        customCompassArrows.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        customCompassArrows.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
    }
}
