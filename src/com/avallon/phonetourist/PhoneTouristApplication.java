package com.avallon.phonetourist;

import android.app.Application;
import android.util.Log;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

public class PhoneTouristApplication extends Application {

    private static PhoneTouristApplication singleton;

    public static PhoneTouristApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;

        try {
            LocationLibrary.initialiseLibrary(getBaseContext(), "com.avallon.phonetourist");
            LocationLibrary.useFineAccuracyForRequests(true);
        } catch (Exception e) {
            Log.e("PhoneTouristApplication", "Could not setup the location library: " + e.getMessage());
        }
    }

}
