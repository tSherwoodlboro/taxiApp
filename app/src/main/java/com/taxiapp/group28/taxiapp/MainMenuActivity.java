package com.taxiapp.group28.taxiapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Tom on 22/04/2017.
 */

public class MainMenuActivity extends AppCompatActivity {
    private String[] fragmentTitles = {"Add Booking","Bookings","Routes","Guide","Settings"};
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    public static final int ADD_BOOKING_FRAGMENT_POSITION = 0;
    public static final int VIEW_BOOKINGS_FRAGMENT_POSITION = 1;
    public static final int UPDATE_BOOKINGS_FRAGMENT_POSITION = 3;
    public static final int ADD_ROUTES_FRAGMENT_POSITION = 4;
    public static final int ROUTES_FRAGMENT_POSITION = 5;
    public static final int GUIDE_FRAGMENT_POSITION = 6;
    public static final int SETTINGS_FRAGMENT_POSITION = 7;
    private Fragment currentFragment = null;
    private String key;
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main_menu);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerListView = (ListView)findViewById(R.id.left_drawer);

        drawerListView.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_list_item,fragmentTitles));
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());

        if(saveInstanceState == null) {
            key = Integer.toString(VIEW_BOOKINGS_FRAGMENT_POSITION);
            loadFragment(new ViewBookingsFragment(), VIEW_BOOKINGS_FRAGMENT_POSITION);
        }
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onStop(){
        super.onStop();
        onSaveInstanceState(new Bundle());
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(fragment!=null) {
            savedInstanceState.putString("fragmentTag", fragment.getTag());
        }
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState !=null && savedInstanceState.getString("fragmentTag") != null) {
            int position = Integer.valueOf(savedInstanceState.getString("fragmentTag"));
            loadFragment(getSupportFragmentManager().findFragmentByTag(savedInstanceState.getString("fragmentTag")), position);
        }
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("ITEM_SELECTED","Item selected");
            Fragment selectedFragment;

            switch(position){
                case ADD_BOOKING_FRAGMENT_POSITION:
                        selectedFragment = new AddBookingFragment();
                        key = Integer.toString(ADD_BOOKING_FRAGMENT_POSITION);
                    break;
                case VIEW_BOOKINGS_FRAGMENT_POSITION:
                        selectedFragment =  new ViewBookingsFragment();
                        key = Integer.toString(VIEW_BOOKINGS_FRAGMENT_POSITION);
                    break;
             /*   case ROUTES_FRAGMENT_POSITION:return;
                    break;
                case SETTINGS_FRAGMENT_POSITION:return;
                    break;
                case GUIDE_FRAGMENT_POSITION:return;
                    break;*/
                default : return;
            }
            loadFragment(selectedFragment,position);
        }
    }
    @Override
    public void  onBackPressed(){
        super.onBackPressed();
        if(currentFragment == null){
            return;
        }
        int position =0;
        try {
            position = Integer.valueOf(currentFragment.getTag());
        }catch(Exception e){
            position = VIEW_BOOKINGS_FRAGMENT_POSITION; // set default
        }finally {
            if(position == UPDATE_BOOKINGS_FRAGMENT_POSITION){
                position = VIEW_BOOKINGS_FRAGMENT_POSITION;
            }
            try {
                drawerListView.setItemChecked(position, true);
                setTitle(fragmentTitles[position]);
            }catch (Exception e){

            }
        }
    }
    public void loadFragment(Fragment fragment, int position){
        currentFragment = fragment;
        if(position == UPDATE_BOOKINGS_FRAGMENT_POSITION){
            position = VIEW_BOOKINGS_FRAGMENT_POSITION;
        }
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        if(fragmentManager.findFragmentByTag(key) != null){
            // load fragment on stack
            fragment = fragmentManager.findFragmentByTag(key);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment,key);
        if(fragmentManager.findFragmentByTag(key) == null){
            fragmentTransaction.addToBackStack(key);
        }
        fragmentTransaction.commit();
        // Highlight the selected item, update the title, and close the drawer
        drawerListView.setItemChecked(position, true);
        setTitle(fragmentTitles[position]);
        drawerLayout.closeDrawer(drawerListView);
    }
}

