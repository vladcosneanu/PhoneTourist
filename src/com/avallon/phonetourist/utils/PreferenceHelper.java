package com.avallon.phonetourist.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceHelper {

    public static final String PHONE_TOURIST = "PHONE_TOURIST";
    
    public static final String DISTANCE = "DISTANCE";
    public static final String DISTANCE_CLOSE_BY = "DISTANCE_CLOSE_BY";
    public static final String DISTANCE_FAR_AWAY = "DISTANCE_FAR_AWAY";
    public static final String DISTANCE_WHOLE_WORLD = "DISTANCE_WHOLE_WORLD";
    public static final String DISTANCE_CUSTOM = "DISTANCE_CUSTOM";
    public static final String DISTANCE_CUSTOM_MIN = "DISTANCE_CUSTOM_MIN";
    public static final String DISTANCE_CUSTOM_MAX = "DISTANCE_CUSTOM_MAX";

    public static void saveValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PHONE_TOURIST, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String loadValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PHONE_TOURIST, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static String loadDefauldValue(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }
    
    public static boolean loadBooleanDefauldValue(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, true);
    }
    
    public static void saveBooleanDefauldValue(Context context, String key, boolean value) {
    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void clearSavedData(Context context) {
    	SharedPreferences sharedPreferences = context.getSharedPreferences(PHONE_TOURIST, Context.MODE_PRIVATE);
    	Editor editor = sharedPreferences.edit();
    	editor.clear().commit();
    	
    	SharedPreferences sharedDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	Editor editorDefault = sharedDefaultPreferences.edit();
    	editorDefault.clear().commit();
    }
}
