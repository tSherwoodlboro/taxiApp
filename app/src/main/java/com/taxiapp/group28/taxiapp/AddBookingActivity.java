package com.taxiapp.group28.taxiapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddBookingActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        // onclick listener for pick up button
        final Button pickUpButton = (Button) findViewById(R.id.add_booking_pick_up_button);
        pickUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent mapLoadIntent = new Intent(AddBookingActivity.this,MapActivity.class);
                EditText pickUpNameText = (EditText)AddBookingActivity.this.findViewById(R.id.editPickUpLocation);
                Log.d("Pickup Text",pickUpNameText.getText().toString());
                mapLoadIntent.putExtra("pickUpName",pickUpNameText.getText().toString());
                startActivity(mapLoadIntent);
            }
        });

        // onclick listener for destination up button
        final Button destButton = (Button) findViewById(R.id.add_booking_destination_button);
        destButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });

        // onclick listener for calculate
        final Button calculateButton = (Button) findViewById(R.id.add_booking_calculate_button);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });



    }



}
