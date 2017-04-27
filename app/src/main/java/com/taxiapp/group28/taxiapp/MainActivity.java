package com.taxiapp.group28.taxiapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by user on 3/27/2017.
 */

public class MainActivity extends AppCompatActivity {
    @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
        if (id == R.id.menu_add_booking) {
            Intent addBookingIntent = new Intent(this,AddBookingFragment.class);
            this.startActivity(addBookingIntent);
            return true;
        }
        if (id == R.id.menu_booking) {
            Intent BookingIntent = new Intent(this,ViewBookingsFragment.class);
            this.startActivity(BookingIntent);
            return true;
        }
        if (id == R.id.menu_edit_booking) {
            //Intent editBookingIntent = new Intent(this,EditBookingActivity.class);
            //this.startActivity(editBookingIntent);
            return true;
        }
        if (id == R.id.menu_routes) {
           // Intent routesIntent = new Intent(this,RoutesActivity.class);
            //this.startActivity(routesIntent);
            return true;
        }
        if (id == R.id.menu_settings) {
            //Intent settingsIntent = new Intent(this,SettingsActivity.class);
            //this.startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.menu_guide) {
            //Intent guideIntent = new Intent(this,GuideActivity.class);
           // this.startActivity(guideIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
