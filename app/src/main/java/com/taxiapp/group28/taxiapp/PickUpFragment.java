package com.taxiapp.group28.taxiapp;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Tom on 04/04/2017.
 */

public class PickUpFragment extends Fragment {
    private boolean useCurrentLocation=false;
    private String location; // location string format
    private Double latitude; // latitude of location
    private Double longitude; // longitude of location
    private static final int  MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =123;
    private boolean locationAccess = false; // current location permission
    private boolean locationSet = false; // true if location is set i.e either searched for or using current locaiton
    private Address address; // address format of location
    private Calendar pickUpTime; // preferred pick up time
    private String houseNumberResult; // result house number
    private String postcodeResult; // result postcode
    private String streetResult; // result street
    private LocationManager locationManager=null; // helps get current location
    private boolean disableSeekBar = false;
    private Switch locationSwitch;
    private  View view=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view!=null){
            return view;
        }

        view = inflater.inflate(R.layout.pick_up_tab, container, false);
        locationSwitch = (Switch)view.findViewById(R.id.add_booking_cLocation_switch);
        // set default pickUpTime
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.MINUTE,calendar.get(calendar.MINUTE)+30);
        pickUpTime = calendar;
        setLocationSet(false);
        // onclick listener for pick up button
        final Button pickUpButton = (Button) view.findViewById(R.id.add_booking_pick_up_button);
        pickUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                // load map activity
                String searchText = getSearchText();
                if(!searchText.isEmpty()){
                    Intent mapLoadIntent = new Intent(getActivity(), MapActivity.class);
                    // put information in intent
                    mapLoadIntent.putExtra("type", TaxiConstants.PICK_UP);
                    mapLoadIntent.putExtra("pickUpName", searchText);
                    startActivityForResult(mapLoadIntent, TaxiConstants.MAP_START_ACTIVITY_PICK_UP);
                }
            }
        });
        // switch listener
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // check for permissions if not granted request permission otherwise get location if checked
                useCurrentLocation = isChecked;
                enableSearch(!useCurrentLocation);
                if (isChecked) {
                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }else{
                        setLocationAccess(true);
                        setLocationManager();
                    }
                }
            }
        });
        // final seek bar to set the preferred pick up time.
        final SeekBar pickUpTimeSeekBar = (SeekBar)view.findViewById(R.id.pick_up_time_seek_bar);
        pickUpTimeSeekBar.setMax(48); // allows 24 hours time difference in half a hour intervals
        pickUpTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Toast toast=null;
            String timeString="Pick Up Time: "; // set the header text
            private void setToast(){
                toast = Toast.makeText(getActivity(),timeString, Toast.LENGTH_SHORT);
            }
            private String getTime(int progress){
                progress = 30+progress*30; // times progress by 30 to represent 30 minutes add 30 for minimum pick up time of 30 minutes
                Calendar calendar = Calendar.getInstance();
                calendar.set(calendar.MINUTE,calendar.get(calendar.MINUTE)+progress);

                SimpleDateFormat dateFormat = new SimpleDateFormat("E HH:mm", Locale.UK);
                pickUpTime = calendar;
                return dateFormat.format(calendar.getTime());
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(toast==null){
                    return;
                }
                toast.setText(timeString+getTime(progress));
                toast.show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setToast();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                toast.cancel();
            }
        });

        isUpdatingBooking();
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("FRAGMENT_STATE","Resume");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("FRAGMENT_STATE","Pause");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("FRAGMENT_STATE","Start");
    }
    @Override
    public void onStop(){
        super.onStop();

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d("FRAGMENT_STATE","Saved");
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState ){
        super.onActivityCreated(savedInstanceState);
        Log.d("FRAGMENT_STATE","Created");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // called when returning from the Maps activity
        if(requestCode == TaxiConstants.MAP_START_ACTIVITY_PICK_UP && resultCode == TaxiConstants.MAP_PICKUP_SET_POINT_DONE) {
            // if codes above ok toast result
            setLocation(data.getDoubleExtra("pickUpLocationLatitude", 0), data.getDoubleExtra("pickUpLocationLongitude", 0), data.getStringExtra("pickUpLocation"));
            Toast toast = Toast.makeText(getActivity(), "Pick up location: " + getLocation(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        // getting permission for current device location
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocationAccess(true);
                } else {
                    setLocationAccess(false);
                }
            }
        }
    }

    private void setLocationAccess(boolean val){
        locationAccess = val;
        locationSwitch.setChecked(val);
    }
    private void setStreetResult(String val){
        // set the street edit text value
        EditText editStreet = (EditText)view.findViewById(R.id.edit_street);
        streetResult = val;
        editStreet.setText(streetResult);
    }
    private void setHouseNumberResult(String val){
        // set the house number text value
        EditText editHouseNumber = (EditText)view.findViewById(R.id.edit_house_number);
        houseNumberResult = val;
        editHouseNumber.setText(houseNumberResult);
    }
    private void setPostcodeResult(String val){
        // set the postcode text value
        EditText editPostcode = (EditText)view.findViewById(R.id.edit_postcode);
        postcodeResult = val;
        editPostcode.setText(postcodeResult);
    }

    // for getting the current location of device
    private void setLocationManager(){
        // location manager for devices current location
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            setLocationAccess(false);
            makeToast("Location Services disabled. Please enable them");
            return;
        }
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                getCurrentLocation(location);
                Log.d("CHECKED","checked current location");
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };

        try {
            if(locationManager != null){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener); // request latest location
                if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                    // get last known location if present
                    getCurrentLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                }
            }
        }catch(SecurityException e){
            Log.d("SECURITY_ERROR","Location permission denied");
        }

    }
    private void setLocation(Double lat,Double longitude,String name){
        // set location of pickUp point
        location = name;
        setCoords(lat,longitude);
        setResultText(name);
        setLocationSet(true);
    }
    private void setCoords(Double lat,Double longitude){
        // set lat and long for pick up point
        latitude = lat;
        this.longitude = longitude;
    }
    private void setResultText(String locationInfo){
        // set the result information
        String[] resultArray = BookingActivity.getResultTextArray(locationInfo);
        setHouseNumberResult(resultArray[0]);
        setStreetResult(resultArray[1]);
        if(!resultArray[2].equals(resultArray[1].split(" ")[0])){
            //makes sure postcode is present
            setPostcodeResult(resultArray[2]);
        }
    }
    private void setLocationSet(boolean val){
        locationSet = val;
        enableResultEdit(val);
    }
    public void setAddress(){
        // set location and address
        if(isLocationSet()  && !getStreetResult().isEmpty()) {
            String locationInfo = getHouseNumberResult()+" " + getStreetResult()+" "+getPostcodeResult();
            address = MapActivity.getAddress(locationInfo, getActivity());
            if(address != null) {
                setLocation(address.getLatitude(), address.getLongitude(), MapActivity.getLocationName(address));
            }else{
                makeToast("Pick Up Location Invalid");
                setLocationSet(false);
            }
        }else{
            setLocationSet(false);
        }
    }

    private void getCurrentLocation(Location location){
        // get the current location information such as street name, long and lat coords
        Geocoder geoCoder = new Geocoder(getActivity());
        Address currentAddress;
        List<Address> locationList;
        try{
            locationList = geoCoder.getFromLocation(location.getLatitude(),location.getLongitude(),1); // get list of locations based on geoCoder search
        }catch(IOException e){
            Log.d("ERROR",e.getMessage());
            return;
        }
        if(locationList.size()>0) {
            // get the first value from list if any
            currentAddress = locationList.get(0);
            setLocation(location.getLatitude(), location.getLongitude(), MapActivity.getLocationName(currentAddress));
        }else{
            // if no locations returned return invalid location
            makeToast("Current Location Invalid");
            enableSearch(true);
            setLocationAccess(true);
        }
    }
    public String getStreetResult(){
        EditText editText = (EditText) view.findViewById(R.id.edit_street);
        streetResult = editText.getText().toString();
        return streetResult;
    }
    public String getHouseNumberResult(){
        EditText editText = (EditText) view.findViewById(R.id.edit_house_number);
        houseNumberResult = editText.getText().toString();
        return houseNumberResult;
    }
    public String getPostcodeResult(){
        EditText editText = (EditText) view.findViewById(R.id.edit_postcode);
        postcodeResult = editText.getText().toString();
        return postcodeResult;
    }
    public Double getLatitude(){
        return latitude;
    }
    public Double getLongitude(){
        return longitude;
    }
    public String getLocation(){
        return location;
    }
    public Calendar getPickUpTime(){
        return pickUpTime;
    }
    public Address getAddress(){
        return address;
    }
    public String getNoteText(){
        if(view == null){
            return null;
        }
        EditText note = (EditText)view.findViewById(R.id.edit_note);
        return note.getText().toString();
    }
    public String getSearchText(){
        EditText pickUpNameText = (EditText) view.findViewById(R.id.editPickUpLocation);
        return pickUpNameText.getText().toString();
    }
    public boolean isLocationSet(){
        return locationSet;
    }
    public boolean isLocationAccess(){
        return locationAccess;
    }
    private boolean isUpdatingBooking(){
        // check if it's for updating a booking
        Bundle argBundle = this.getArguments();
        if(argBundle != null && argBundle.get(BookingPagerAdapter.UPDATE_BOOKING) != null){
            String locationName = (String)argBundle.get(BookingPagerAdapter.UPDATE_BOOKING_PICK_UP_LOCATION_NAME);
            Log.d("PICK_UP_NAME",(String)argBundle.get(BookingPagerAdapter.UPDATE_BOOKING_PICK_UP_LOCATION_NAME));

            Double latitude = (Double)argBundle.get(BookingPagerAdapter.UPDATE_BOOKING_PICK_UP_LATITUDE);
            Double longitude = (Double)argBundle.get(BookingPagerAdapter.UPDATE_BOOKING_PICK_UP_LONGITUDE);
            setLocation(latitude,longitude,locationName);
            return true;
        }else{
            return false;
        }
    }
    private  void makeToast(String message){
        // make a toast
        Toast toast = Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT);
        toast.show();
    }
    private void enableSearch(boolean  val){
        // disables and enables pick up location. set to false when current location is used
        EditText pickUpEdit = (EditText) view.findViewById(R.id.editPickUpLocation);
        pickUpEdit.setEnabled(val);
        Button pickUpButton = (Button) view.findViewById(R.id.add_booking_pick_up_button);
        pickUpButton.setEnabled(val);
    }
    private void enableResultEdit(boolean val){
        EditText editStreet = (EditText)view.findViewById(R.id.edit_street);
        EditText editHouseNum = (EditText)view.findViewById(R.id.edit_house_number);
        EditText editPostcode = (EditText)view.findViewById(R.id.edit_postcode);
        editStreet.setEnabled(val);
        editHouseNum.setEnabled(val);
        editPostcode.setEnabled(val);
    }
}
