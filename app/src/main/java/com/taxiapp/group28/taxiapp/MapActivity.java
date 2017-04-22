package com.taxiapp.group28.taxiapp;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Address;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private String locationName=null;
    private Address currentAddress = null;
    private String returnIntentKey = null; // for either pickUp or destination fragments
    private int returnIntentResultCode;// for either pickUp or destination fragments
    private static String NO_LOCATION_MESSAGE = "Warning no location!";
    private String markerText; // overhead text for marker/pointer
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // get intent data
        int type = this.getIntent().getIntExtra("type",3);
        if(type == TaxiConstants.PICK_UP){
            setLocationNameText(this.getIntent().getStringExtra("pickUpName").toString());
            returnIntentResultCode= TaxiConstants.MAP_PICKUP_SET_POINT_DONE;
            returnIntentKey = "pickUpLocation";
            markerText = "Pick Up Point";
        }else if(type == TaxiConstants.DEST){
            locationName = this.getIntent().getStringExtra("destName").toString();
            returnIntentResultCode= TaxiConstants.MAP_DEST_POINT_DONE;
            returnIntentKey="destLocation";
            markerText = "Destination Point";
        }


        //start map fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.current_map);
        mapFragment.getMapAsync(this);

        //add button event listener
        Button confirmButton = (Button)this.findViewById(R.id.map_confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addBooking = new Intent(MapActivity.this, BookingActivity.class);
                String locationDetails =getLocationName(currentAddress);
                int i=0;
                if(currentAddress != null) {
                    // add data to bundle/intent
                    addBooking.putExtra(returnIntentKey+"Latitude",currentAddress.getLatitude());
                    addBooking.putExtra(returnIntentKey+"Longitude",currentAddress.getLongitude());
                    addBooking.putExtra(returnIntentKey, locationDetails);
                    setResult(returnIntentResultCode, addBooking);
                    finish();
                }
            }

        });
    }
    @Override
    public void onMapReady(final GoogleMap map) {
        // when map is ready add marker and onclick listener
        addInitialMarker(map);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               addNewMarker(map,latLng);
            }
        });
    }
    private void addInitialMarker(GoogleMap map) {
        // adds the initial marker when the activity first loads
        try {
            // make sure location name is not empty
            if (!locationName.isEmpty()) {

                currentAddress= getAddress(locationName,this); // get first list address
                setLocationNameText(currentAddress.getAddressLine(0)); // set locationName
                LatLng currentLocation = new LatLng(currentAddress.getLatitude(), currentAddress.getLongitude()); // get coords for address
                map.addMarker(new MarkerOptions() // place new maker
                        .position(currentLocation)
                        .title(markerText));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 20)); // move the camera to point
            }
        } catch (Exception e) {
            return;
        }
    }
    private void addNewMarker(GoogleMap map,LatLng location) {
        // add a marker to the map
        try {
            map.clear();
            map.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Pick Up Point"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20));
            Geocoder geoCoder = new Geocoder(this);
            // get address information for new marker
            List<Address> locationList;
            locationList = geoCoder.getFromLocation(location.latitude,location.longitude,1);
            currentAddress= locationList.get(0);
            setLocationNameText(currentAddress.getAddressLine(0)); // set the location name
        } catch (Exception e) {
            return;
        }
    }
    public static Address getAddress(String name, Context context) {
        // static class returns address based on search text
        Geocoder geoCoder = new Geocoder(context);
        try {
            List<Address> locationList; //create address list
            locationList = geoCoder.getFromLocationName(name + " UK", 10); // get 10 locations from uk
            if(locationList.size() <=0){
                return null;
            }
            return locationList.get(0);
        }catch(Exception e){
            return null;
        }
    }
    public static String getLocationName(Address address){
        // get location information based of address
        String locationDetails ="";
        int i=0;
        if(address != null) {
            do {

                locationDetails += address.getAddressLine(i); // get the entire address details and add to string
                ++i;
                if (i <= address.getMaxAddressLineIndex()) {
                    locationDetails += ", ";
                }
            } while (i <= address.getMaxAddressLineIndex());
        }
        return locationDetails;
    }
    private void setLocationNameText(String text){
        // set the header location name text for the map(this) activity
        TextView currentLocationView = (TextView)this.findViewById(R.id.map_current_location);
        if(!text.isEmpty()) {
            currentLocationView.setText(text);
            locationName = text;
        }else{
            currentLocationView.setText(NO_LOCATION_MESSAGE); // no location set
            locationName = null;
        }
    }

}
