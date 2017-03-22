package com.taxiapp.group28.taxiapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Address;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private String locationName=null;
    private Address currentAddress = null;
    private String returnIntentKey = null;
    private int returnIntentResultCode;
    private static String NO_LOCATION_MESSAGE = "Warning no location!";
    private String markerText;
    @Override
    public void onMapReady(final GoogleMap map) {
        addInitialMarker(map);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               addNewMarker(map,latLng);
            }
        });


    }
    private void addNewMarker(GoogleMap map,LatLng location) {
        try {
            map.clear();
            map.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Pick Up Point"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20));
            Geocoder geoCoder = new Geocoder(this);
            List<Address> locationList;
            locationList = geoCoder.getFromLocation(location.latitude,location.longitude,1);
            currentAddress= locationList.get(0);
            locationName = currentAddress.getAddressLine(0);
            setLocationNameText();
        } catch (Exception e) {
            return;
        }
    }
    private void addInitialMarker(GoogleMap map) {
        Geocoder geoCoder = new Geocoder(this);
        try {
            if (!locationName.isEmpty()) {
                List<Address> locationList;
                locationList = geoCoder.getFromLocationName(locationName+" UK", 10);
                currentAddress= locationList.get(0);
                locationName = currentAddress.getAddressLine(0);
                setLocationNameText();
                LatLng currentLocation = new LatLng(currentAddress.getLatitude(), currentAddress.getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .title("Pick Up Point"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20));
            }
        } catch (Exception e) {
            return;
        }
    }
    private void setLocationNameText(){
        TextView currentLocationView = (TextView)this.findViewById(R.id.map_current_location);
        if(!locationName.isEmpty()) {
            currentLocationView.setText(locationName);
        }else{
            currentLocationView.setText(NO_LOCATION_MESSAGE);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        // get intent data
        int type = this.getIntent().getIntExtra("type",3);
        if(type == TaxiConstants.PICK_UP){
            locationName = this.getIntent().getStringExtra("pickUpName").toString();
            returnIntentResultCode= TaxiConstants.MAP_PICKUP_SET_POINT_DONE;
            returnIntentKey = "pickUpLocation";
            markerText = "Pick Up Point";
        }else if(type == TaxiConstants.DEST){
            locationName = this.getIntent().getStringExtra("destName").toString();
            returnIntentResultCode= TaxiConstants.MAP_DEST_POINT_DONE;
            returnIntentKey="destLocation";
            markerText = "Destination Point";
        }


        // create map fragment manually
        /*
        MapFragment mapFragment = new MapFragment();
        FragmentTransaction mapFragmentTransaction = getFragmentManager().beginTransaction();
        mapFragmentTransaction.add(R.id.map_activity,mapFragment);
        mapFragmentTransaction.commit();
        mapFragment.getMapAsync(MapActivity.this);
        */

        //start map fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.current_map);
        mapFragment.getMapAsync(this);
        setLocationNameText();

        //add button event listener
        Button confirmButton = (Button)this.findViewById(R.id.map_confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addBooking = new Intent(MapActivity.this, AddBookingActivity.class);
                String locationDetails ="";
                int i=0;
                if(currentAddress != null) {
                    do {
                        locationDetails += currentAddress.getAddressLine(i);
                        ++i;
                        if (i <= currentAddress.getMaxAddressLineIndex()) {
                            locationDetails += ", ";
                        }
                    } while (i <= currentAddress.getMaxAddressLineIndex());
                    addBooking.putExtra(returnIntentKey+"Latitude",currentAddress.getLatitude());
                    addBooking.putExtra(returnIntentKey+"Longitude",currentAddress.getLongitude());
                    addBooking.putExtra(returnIntentKey, locationDetails);
                    setResult(returnIntentResultCode, addBooking);
                    finish();
                }
            }

        });
    }
}
