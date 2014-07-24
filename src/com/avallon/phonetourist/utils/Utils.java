package com.avallon.phonetourist.utils;

import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.avallon.phonetourist.R;
import com.avallon.phonetourist.items.Coordinate;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

public class Utils {

    private static Coordinate coordinate;
    private static Random rand = new Random();
    static final float ALPHA = 0.2f;

    public static Coordinate getLocation() {
        if (coordinate == null) {
            coordinate = new Coordinate();
            coordinate.setValid(false);
        }
        return coordinate;
    }

    public static void updateCoordinate(LocationInfo locationInfo) {
        coordinate = new Coordinate();
        Log.i("Location", "Location update at:" + LocationInfo.formatTimeAndDay(locationInfo.lastLocationUpdateTimestamp, true)
                + ", accuracy: " + locationInfo.lastAccuracy + ", lat: " + locationInfo.lastLat + ", lng: " + locationInfo.lastLong);
        coordinate.setLatitude(locationInfo.lastLat);
        coordinate.setLongitude(locationInfo.lastLong);
        coordinate.setValid(true);
        coordinate.setAccuracy(locationInfo.lastAccuracy);
    }

    public static Calendar getMaxWeatherDate(int destinationTimeDistance) {
        Calendar currentCal = Calendar.getInstance();

        currentCal.add(Calendar.HOUR, 240);
        currentCal.add(Calendar.SECOND, (-1 * destinationTimeDistance));
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);

        return currentCal;
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getRandInt(int min, int max) {
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    
    public static String getTravelPhrase(Context context) {
        String[] travelPhrases = context.getResources().getStringArray(R.array.travel_phrase_provider);
        int randomPhraseNr = getRandInt(0, (travelPhrases.length - 1));
        
        return travelPhrases[randomPhraseNr];
    }
    
    public static float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;     
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }
}
