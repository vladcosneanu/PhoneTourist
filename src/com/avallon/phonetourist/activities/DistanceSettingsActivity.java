package com.avallon.phonetourist.activities;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

import com.avallon.phonetourist.R;
import com.avallon.phonetourist.items.RangeSeekBar;
import com.avallon.phonetourist.items.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.avallon.phonetourist.utils.PreferenceHelper;

public class DistanceSettingsActivity extends Activity {

    private ViewGroup distanceSettingsContainer;
    private TextView fromTextView;
    private TextView upToTextView;

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

        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(1, 399, this);
        seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                fromTextView.setText(getString(R.string.from, (minValue - 1) * 50));
                upToTextView.setText(getString(R.string.up_to, (maxValue + 1) * 50));

                PreferenceHelper.saveValue(DistanceSettingsActivity.this, PreferenceHelper.DISTANCE, PreferenceHelper.DISTANCE_CUSTOM);
                PreferenceHelper.saveValue(DistanceSettingsActivity.this, PreferenceHelper.DISTANCE_CUSTOM_MIN,
                        String.valueOf(minValue * 50));
                PreferenceHelper.saveValue(DistanceSettingsActivity.this, PreferenceHelper.DISTANCE_CUSTOM_MAX,
                        String.valueOf(maxValue * 50));
            }
        });

        String savedDistance = PreferenceHelper.loadValue(this, PreferenceHelper.DISTANCE);
        if (savedDistance.equals(PreferenceHelper.DISTANCE_CLOSE_BY)) {
            fromTextView.setText(getString(R.string.from, "0"));
            upToTextView.setText(getString(R.string.up_to, "150"));
            seekBar.setSelectedMinValue(1);
            seekBar.setSelectedMaxValue(2);
        } else if (savedDistance.equals(PreferenceHelper.DISTANCE_FAR_AWAY)) {
            fromTextView.setText(getString(R.string.from, "150"));
            upToTextView.setText(getString(R.string.up_to, "2,000"));
            seekBar.setSelectedMinValue(2);
            seekBar.setSelectedMaxValue(39);
        } else if (savedDistance.equals(PreferenceHelper.DISTANCE_WHOLE_WORLD)) {
            fromTextView.setText(getString(R.string.from, "0"));
            upToTextView.setText(getString(R.string.up_to, "20,000"));
            seekBar.setSelectedMinValue(1);
            seekBar.setSelectedMaxValue(399);
        } else if (savedDistance.equals(PreferenceHelper.DISTANCE_CUSTOM)) {
            int minDIstance = Integer.parseInt(PreferenceHelper.loadValue(this, PreferenceHelper.DISTANCE_CUSTOM_MIN)) - 50;
            int maxDIstance = Integer.parseInt(PreferenceHelper.loadValue(this, PreferenceHelper.DISTANCE_CUSTOM_MAX)) + 50;
            
            fromTextView.setText(getString(R.string.from, minDIstance));
            upToTextView.setText(getString(R.string.up_to, maxDIstance));
            seekBar.setSelectedMinValue(minDIstance / 50);
            seekBar.setSelectedMaxValue(maxDIstance / 50);
        }

        distanceSettingsContainer.addView(seekBar);
    }
}
