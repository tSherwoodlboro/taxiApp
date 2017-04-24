package com.taxiapp.group28.taxiapp;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.location.Address;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by Tom on 04/04/2017.
 */

public class DestFragment extends Fragment {
    private String location; // location string format
    private Double latitude; // latitude of location
    private Double longitude; // longitude of location
    private boolean locationSet = false; // true if location is set i.e either searched for or using current locaiton
    private Address address; // address format of location
    private String houseNumberResult; // result house number
    private String postcodeResult; // result postcode
    private String streetResult; // result street
    private boolean updateBooking=false;

    private  View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view!=null){
            return view;
        }
        view = inflater.inflate(R.layout.destination_tab, container, false);
        // onclick listener for destination up button

        setLocationSet(false);

        final Button destButton = (Button) view.findViewById(R.id.add_booking_destination_button);
        destButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                // load map activity
                String searchText = getSearchText();
                if(!searchText.isEmpty()){
                    Intent mapLoadIntent = new Intent(getActivity(), MapActivity.class);
                    mapLoadIntent.putExtra("type", TaxiConstants.DEST);
                    mapLoadIntent.putExtra("destName", getSearchText());
                    startActivityForResult(mapLoadIntent, TaxiConstants.MAP_START_ACTIVITY_DEST);
                }

            }
        });
        isUpdatingBooking();
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState !=null){
            setLocationSet(savedInstanceState.getBoolean("locationSet"));
            if(isLocationSet()){
                setLocation(savedInstanceState.getDouble("latitude"),savedInstanceState.getDouble("longitude"),savedInstanceState.getString("locationName"));
                setSearchText(savedInstanceState.getString("searchText"));
            }
        }
        Log.d("FRAGMENT_STATE_DEST","Activity Created");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("FRAGMENT_STATE_DEST","Resume");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("FRAGMENT_STATE_DEST","Pause");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("FRAGMENT_STATE_DEST","Start");
    }
    @Override
    public void onStop(){
        super.onStop();
        Bundle savedInstanceState = new Bundle();
        savedInstanceState.putBoolean("locationSet",isLocationSet());
        savedInstanceState.putString("searchText",getSearchText());

        if(isLocationSet()){
            savedInstanceState.putString("locationName",location);
            savedInstanceState.putDouble("latitude",latitude);
            savedInstanceState.putDouble("longitude",longitude);
        }
        onSaveInstanceState(savedInstanceState);
        Log.d("FRAGMENT_STATE_DEST","Stop");

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("FRAGMENT_STATE_DEST","Destroy");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d("FRAGMENT_STATE_DEST","Saved");

    }
    @Override
    public void onViewStateRestored (Bundle savedInstanceState){
        super.onViewStateRestored(savedInstanceState);
        Log.d("FRAGMENT_STATE_DEST","Restored");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // called when returning from the activity
        if (requestCode == TaxiConstants.MAP_START_ACTIVITY_DEST && resultCode == TaxiConstants.MAP_DEST_POINT_DONE){
            setLocation(data.getDoubleExtra("destLocationLatitude",0),data.getDoubleExtra("destLocationLongitude",0),data.getStringExtra("destLocation"));
        }
    }
    public void setSearchText(String text){
        EditText destText = (EditText) view.findViewById(R.id.editDestLocation);
        destText.setText(text);
    }
    private void setStreetResult(String val){
        EditText editStreet = (EditText)view.findViewById(R.id.edit_dest_street);
        streetResult = val;
        editStreet.setText(streetResult);
    }
    private void setHouseNumberResult(String val){
        EditText editHouseNumber = (EditText)view.findViewById(R.id.edit_dest_house_number);
        houseNumberResult = val;
        editHouseNumber.setText(houseNumberResult);
    }
    private void setPostcodeResult(String val){
        EditText editPostcode = (EditText)view.findViewById(R.id.edit_dest_postcode);
        postcodeResult = val;
        editPostcode.setText(postcodeResult);
    }
    private void  setLocationSet(boolean val){
        locationSet = val;
        enableResultEdit(val);
    }
    private void setLocation(Double lat,Double longitude,String name){
        // sets dest location (coords and sets the result information)
        setCoords(lat,longitude);
        setResultText(name);
        location = name;
        setLocationSet(true);
    }
    private void setCoords(Double lat,Double longitude){
        // sets the lat and long of dest point
        latitude = lat;
        this.longitude = longitude;
    }
    private void setResultText(String locationInfo){
        // sets the house number, steet and postcode UI fields
        String[] resultArray = AddBookingFragment.getResultTextArray(locationInfo);
        setHouseNumberResult(resultArray[0]);
        setStreetResult(resultArray[1]);
        if(!resultArray[2].equals(resultArray[1].split(" ")[0])){
            //makes sure postcode is present
            setPostcodeResult(resultArray[2]);
        }
    }
    public void setAddress(){
        // set location and address
        if(isLocationSet() && !getStreetResult().isEmpty()) {
            String locationInfo = getHouseNumberResult()+" " + getStreetResult()+" "+getPostcodeResult();
            address = MapActivity.getAddress(locationInfo, getActivity());
            if(address != null) {
                setLocation(address.getLatitude(), address.getLongitude(), MapActivity.getLocationName(address));
            }else{
                Toast toast = Toast.makeText(getActivity(),"Destination Location Invalid", Toast.LENGTH_SHORT);
                toast.show();
                setLocationSet(false);
            }
        }else{
            setLocationSet(false);
        }
    }

    public String getStreetResult(){
        EditText editText = (EditText) view.findViewById(R.id.edit_dest_street);
        streetResult = editText.getText().toString();
        return streetResult;
    }
    public String getHouseNumberResult(){
        EditText editText = (EditText) view.findViewById(R.id.edit_dest_house_number);
        houseNumberResult = editText.getText().toString();
        return houseNumberResult;
    }
    public String getPostcodeResult(){
        EditText editText = (EditText) view.findViewById(R.id.edit_dest_postcode);
        postcodeResult = editText.getText().toString();
        return postcodeResult;
    }

    public boolean isLocationSet(){
        return locationSet;
    }
    public String getLocation(){
        return location;
    }

    private boolean isUpdatingBooking(){
        // check if it's for updating a booking
        if(updateBooking){
            setLocationSet(true);
            return true;
        }
        Bundle argBundle = this.getArguments();
        if(argBundle != null && argBundle.get(BookingPagerAdapter.UPDATE_BOOKING) != null){
            updateBooking=true;
            String locationName = (String)argBundle.get(BookingPagerAdapter.UPDATE_BOOKING_DEST_LOCATION_NAME);
            Double latitude = (Double)argBundle.get(BookingPagerAdapter.UPDATE_BOOKING_DEST_LATITUDE);
            Double longitude = (Double)argBundle.get(BookingPagerAdapter.UPDATE_BOOKING_DEST_LONGITUDE);
            setLocation(latitude,longitude,locationName);
            return true;
        }else{
            updateBooking=false;
            return false;
        }
    }

    public Address getAddress(){
        return address;
    }
    public String getSearchText() {
        EditText destNameText = (EditText) view.findViewById(R.id.editDestLocation);
        return destNameText.getText().toString();

    }
    private void enableResultEdit(boolean val){
        EditText editStreet = (EditText)view.findViewById(R.id.edit_dest_street);
        EditText editHouseNum = (EditText)view.findViewById(R.id.edit_dest_house_number);
        EditText editPostcode = (EditText)view.findViewById(R.id.edit_dest_postcode);
        editStreet.setEnabled(val);
        editHouseNum.setEnabled(val);
        editPostcode.setEnabled(val);
    }
    public Double getLatitude(){
        return latitude;
    }
    public Double getLongitude(){
        return longitude;
    }

}
