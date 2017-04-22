package com.taxiapp.group28.taxiapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tom on 17/04/2017.
 */

public class SharedPreferencesManager {
    public static  SharedPreferences sharedpreferences;
    public  static SharedPreferences getUserPreferences(Context context){
        sharedpreferences = context.getSharedPreferences(TaxiConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences;
    }
}
