package com.taxiapp.group28.taxiapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddBookingActivity  extends AppCompatActivity {
    private boolean useCurrentLocation=false;
    private String destLocation;
    private String pickUpLocation;
    private Double pickUpLatitude;
    private Double pickUpLongitude;
    private Double destLatitude;
    private Double destLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        // onclick listener for pick up button
        final Button pickUpButton = (Button) findViewById(R.id.add_booking_pick_up_button);
        pickUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                // load map activity
                Intent mapLoadIntent = new Intent(AddBookingActivity.this, MapActivity.class);
                EditText pickUpNameText = (EditText) AddBookingActivity.this.findViewById(R.id.editPickUpLocation);
                mapLoadIntent.putExtra("type",TaxiConstants.PICK_UP);
                mapLoadIntent.putExtra("pickUpName", pickUpNameText.getText().toString());
                startActivityForResult(mapLoadIntent,TaxiConstants.MAP_START_ACTIVITY_PICK_UP);
            }
        });

        // onclick listener for destination up button
        final Button destButton = (Button) findViewById(R.id.add_booking_destination_button);
        destButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                // load map activity
                Intent mapLoadIntent = new Intent(AddBookingActivity.this, MapActivity.class);
                EditText destNameText = (EditText) AddBookingActivity.this.findViewById(R.id.editDestLocation);
                mapLoadIntent.putExtra("type",TaxiConstants.DEST);
                mapLoadIntent.putExtra("destName", destNameText.getText().toString());
                startActivityForResult(mapLoadIntent,TaxiConstants.MAP_START_ACTIVITY_DEST);
            }
        });

        // onclick listener for calculate
        final Button calculateButton = (Button) findViewById(R.id.add_booking_calculate_button);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Toast toast = Toast.makeText(AddBookingActivity.this,"Calculate: Pickup "+pickUpLocation+ " Coords: "+pickUpLatitude+","+pickUpLongitude+ " Destination "+destLocation+ " Coords: "+destLatitude+","+destLongitude, Toast.LENGTH_LONG);
                toast.show();
                // do calculations on route
                // call google api over http/https output driving distance estimate price e.g. 2.5 per mile.
            }
        });

        // switch listener
        final Switch currentLocationSwitch = (Switch)findViewById(R.id.add_booking_cLocation_switch);
        currentLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                useCurrentLocation = isChecked;
                disablePickupLocation(!useCurrentLocation);
            }
        });

    }
    private void disablePickupLocation(boolean  val){
        EditText pickUpEdit = (EditText) AddBookingActivity.this.findViewById(R.id.editPickUpLocation);
        pickUpEdit.setEnabled(val);
        Button pickUpButton = (Button) findViewById(R.id.add_booking_pick_up_button);
        pickUpButton.setEnabled(val);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == TaxiConstants.MAP_START_ACTIVITY_PICK_UP && resultCode == TaxiConstants.MAP_PICKUP_SET_POINT_DONE){
            pickUpLocation = data.getStringExtra("pickUpLocation");
            pickUpLatitude = data.getDoubleExtra("pickUpLocationLatitude",0);
            pickUpLongitude = data.getDoubleExtra("pickUpLocationLongitude",0);

            Toast toast = Toast.makeText(this,"Pick up location: "+pickUpLocation, Toast.LENGTH_SHORT);
            toast.show();
            TextView resultText = (TextView)this.findViewById(R.id.pickUpResultAddress);
            resultText.setText(pickUpLocation);
        }else if (requestCode == TaxiConstants.MAP_START_ACTIVITY_DEST && resultCode == TaxiConstants.MAP_DEST_POINT_DONE){
            destLocation = data.getStringExtra("destLocation").toString();
            destLatitude = data.getDoubleExtra("destLocationLatitude",0);
            destLongitude = data.getDoubleExtra("destLocationLongitude",0);
            Toast toast = Toast.makeText(this,"Destination location: "+destLocation, Toast.LENGTH_SHORT);
            toast.show();
            TextView resultText = (TextView)this.findViewById(R.id.destResultAddress);
            resultText.setText(destLocation);
        }

    }
}