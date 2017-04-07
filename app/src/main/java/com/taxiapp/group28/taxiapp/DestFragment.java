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
    private  View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view!=null){
            return view;
        }
        view = inflater.inflate(R.layout.destination_tab, container, false);
        // onclick listener for destination up button
        final Button destButton = (Button) view.findViewById(R.id.add_booking_destination_button);
        destButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                // load map activity
                String searchText = getSearchText();
                if(!searchText.isEmpty()){
                    setLocationSet(false);
                    Intent mapLoadIntent = new Intent(getActivity(), MapActivity.class);
                    mapLoadIntent.putExtra("type", TaxiConstants.DEST);
                    mapLoadIntent.putExtra("destName", getSearchText());
                    startActivityForResult(mapLoadIntent, TaxiConstants.MAP_START_ACTIVITY_DEST);
                }

            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // called when returning from the activity
        if (requestCode == TaxiConstants.MAP_START_ACTIVITY_DEST && resultCode == TaxiConstants.MAP_DEST_POINT_DONE){
            setLocation(data.getDoubleExtra("destLocationLatitude",0),data.getDoubleExtra("destLocationLongitude",0),data.getStringExtra("destLocation").toString());
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
    public boolean isLocationSet(){
        return locationSet;
    }

    public Address getAddress(){
        return address;
    }
    public String getSearchText() {
        EditText destNameText = (EditText) view.findViewById(R.id.editDestLocation);
        return destNameText.getText().toString();

    }
    private void setStreetResult(String val){
        EditText editStreet = (EditText)view.findViewById(R.id.edit_street);
        streetResult = val;
        editStreet.setText(streetResult);
    }
    private void setHouseNumberResult(String val){
        EditText editHouseNumber = (EditText)view.findViewById(R.id.edit_house_number);
        houseNumberResult = val;
        editHouseNumber.setText(houseNumberResult);
    }
    private void setPostcodeResult(String val){
        EditText editPostcode = (EditText)view.findViewById(R.id.edit_postcode);
        postcodeResult = val;
        editPostcode.setText(postcodeResult);
    }
    private void  setLocationSet(boolean val){
        locationSet = val;
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
        String[] resultArray = AddBookingActivity.getResultTextArray(locationInfo);
        setHouseNumberResult(resultArray[0]);
        setStreetResult(resultArray[1]);
        setPostcodeResult(resultArray[2]);
    }
    public void setAddress(){
        // sets the address based on result UI information
        if(isLocationSet() && !getPostcodeResult().isEmpty() && !getStreetResult().isEmpty()) {
            String locationInfo = getHouseNumberResult()+" " + getStreetResult()+" "+getPostcodeResult();
            address = MapActivity.getAddress(locationInfo, getActivity());
            setLocation(address.getLatitude(), address.getLongitude(), MapActivity.getLocationName(address));
        }
    }
}
