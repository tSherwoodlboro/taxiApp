package com.taxiapp.group28.taxiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by Tom on 26/04/2017.
 */

public class SettingsPreferenceFragment extends PreferenceFragment {
    private View view=null;
    public  static final int LIGHT_THEME =0;
    public static final int DEFAULT_THEME =1;
    public SettingsPreferenceFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getDriverPref();
        ListPreference themeListPreference = (ListPreference)this.findPreference("list_preference_1");
        themeListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                switch(Integer.valueOf((String)newValue)){
                    case LIGHT_THEME:setTheme(SettingsPreferenceFragment.this.getActivity().getApplicationInfo().theme);
                        break;
                    case DEFAULT_THEME:setTheme(android.R.style.Theme_DeviceDefault);
                        break;
                    default:setTheme(SettingsPreferenceFragment.this.getActivity().getApplicationInfo().theme);
                        break;
                }
                SharedPreferences sharedpreferences;
                sharedpreferences = SettingsPreferenceFragment.this.getActivity().getSharedPreferences(TaxiConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(getString(R.string.user_preferred_theme_pref_key),(String)newValue);
                editor.apply();
                Toast toast = Toast.makeText(SettingsPreferenceFragment.this.getActivity(),"Restart App For Best Results!",Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
        });
    }
    private void setTheme(int themeId){
        this.getActivity().setTheme(themeId);
    }
    private void getDriverPref(){
        // get driver info from the database
        final DriversInfo driversInfo = new DriversInfo(this.getActivity());

        driversInfo.setOnGetResultListener(new DriversInfo.onGetResultListener() {
                @Override
                public void onGetResult() {
                    setAssignedDriverPref(driversInfo.getDriverList());
                }
        });
        driversInfo.setDriverInfo();
    }
    private void setAssignedDriverPref(ArrayList<DriverInfo> driverList ){
        // set entries and entry values
        CharSequence[] entries = new CharSequence[driverList.size()];
        CharSequence[] entryValues= new CharSequence[driverList.size()];
        int i=0;
        for(DriverInfo driverInfo : driverList){
            entries[i]= driverInfo.getFirstName()+" "+driverInfo.getLastName();
            entryValues[i] =String.valueOf(driverInfo.getId());
            ++i;
        }
        // get listPreference object and set entries and values
        ListPreference driverListPreference = (ListPreference)SettingsPreferenceFragment.this.findPreference("list_preference_2");
        driverListPreference.setEntries(entries);
        driverListPreference.setEntryValues(entryValues);
        // set onChange listener
        driverListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // update the preferred driver id preference and return true
                SharedPreferences sharedpreferences;
                sharedpreferences = SettingsPreferenceFragment.this.getActivity().getSharedPreferences(TaxiConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(getString(R.string.user_preferred_driver_id_pref_key),(String)newValue);
                editor.apply();
                return true;
            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("SETTINGS","START");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("SETTINGS","STOP");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("SETTINGS","RESUME");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("SETTINGS","PAUSE");
    }

}