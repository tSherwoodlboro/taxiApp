package com.taxiapp.group28.taxiapp;

import android.app.FragmentTransaction;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Address;
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
    private static String NO_LOCATION_MESSAGE = "Warning no location!";
    @Override
    public void onMapReady(GoogleMap map) {
        Geocoder geoCoder = new Geocoder(this);
        try {
            if(!locationName.isEmpty()) {
                List<Address> locationList;
                locationList = geoCoder.getFromLocationName(locationName, 1);
                Address mapAddress = locationList.get(0);
                LatLng currentLocation = new LatLng(mapAddress.getLatitude(), mapAddress.getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .title("Pick Up Point"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,20));
            }
        }catch(Exception e){
            return;
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        // get intent data
        locationName = this.getIntent().getStringExtra("pickUpName").toString();
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
        TextView currentLocationView = (TextView)this.findViewById(R.id.map_current_location);
        if(!locationName.isEmpty()) {
            currentLocationView.setText(locationName);
        }else{
            currentLocationView.setText(NO_LOCATION_MESSAGE);
        }

    }
}
