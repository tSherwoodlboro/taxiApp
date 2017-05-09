package com.taxiapp.group28.taxiapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Jeffrey on 3/27/2017.
 */

public class RightMenuActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            Intent settingsIntent = new Intent(RightMenuActivity.this,SettingsPreferenceFragment.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.menu_guide) {
            Intent guideIntent = new Intent(RightMenuActivity.this,GuideFragment.class);
            startActivity(guideIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
