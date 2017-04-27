package com.taxiapp.group28.taxiapp;

import android.content.Context;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
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
    public static final int VIEW_ROUTES_FRAGMENT_POSITION = 2;
    public static final int GUIDE_FRAGMENT_POSITION = 3;
    public static final int SETTINGS_FRAGMENT_POSITION = 4;
    public static final int ADD_ROUTES_FRAGMENT_KEY = 5;
    public static final int UPDATE_BOOKINGS_FRAGMENT_KEY = 6;
    public static boolean liveBooking= false;
    private Fragment currentFragment = null;
    private FragmentManager fragmentManager=null;
    private String key;
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main_menu);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerListView = (ListView)findViewById(R.id.left_drawer);

        drawerListView.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_list_item,fragmentTitles));
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
        if(fragmentManager == null){
            fragmentManager = this.getFragmentManager();
        }
        if(saveInstanceState == null) {
            key = Integer.toString(VIEW_BOOKINGS_FRAGMENT_POSITION);
            loadFragment(new ViewBookingsFragment(), VIEW_BOOKINGS_FRAGMENT_POSITION,false);
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
        Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);
        if(fragment!=null) {
            savedInstanceState.putString("fragmentTag", fragment.getTag());
        }
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState !=null && savedInstanceState.getString("fragmentTag") != null) {
            int position = Integer.valueOf(savedInstanceState.getString("fragmentTag"));
            loadFragment(fragmentManager.findFragmentByTag(savedInstanceState.getString("fragmentTag")), position,false);
        }
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("ITEM_SELECTED","Item selected");
            Fragment selectedFragment=null;

            switch(position){
                case ADD_BOOKING_FRAGMENT_POSITION:
                        selectedFragment = new AddBookingFragment();
                        key = Integer.toString(ADD_BOOKING_FRAGMENT_POSITION);
                    break;
                case VIEW_BOOKINGS_FRAGMENT_POSITION:
                        selectedFragment =  new ViewBookingsFragment();
                        key = Integer.toString(VIEW_BOOKINGS_FRAGMENT_POSITION);
                    break;
               case VIEW_ROUTES_FRAGMENT_POSITION:
                        selectedFragment =  new ViewRoutesFragment();
                        key = Integer.toString(VIEW_ROUTES_FRAGMENT_POSITION);
                    break;
                case SETTINGS_FRAGMENT_POSITION:
                    selectedFragment = new SettingsPreferenceFragment();
                    key = Integer.toString(SETTINGS_FRAGMENT_POSITION);
                    break;
                /*case GUIDE_FRAGMENT_POSITION:return;
                    break;*/
                default : return;
            }
            loadFragment(selectedFragment,position,false);

        }
    }
    @Override
    public void  onBackPressed(){
        if(fragmentManager.getBackStackEntryCount() <=1){
            // if the stack is equal to 1 close program (Nothing to go back to)
            this.finish();
        }else {
            printFragmentStack("0Before");
            fragmentManager.popBackStackImmediate(); // pop the current element
            printFragmentStack("0After");
            Fragment fragment = fragmentManager.findFragmentByTag(fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName()); // get the latest added entry
            if(fragment !=null) {
                loadFragment(fragment, Integer.valueOf(fragment.getTag()), false); // load the previous fragment
            }else{
                Log.d("FRAGMENT_STACK", "FRAGMENT state: null");
            }
        }
    }
    private void printFragmentStack(String message){
        String result ="";
        for(int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++){
            result+=  fragmentManager.findFragmentByTag(fragmentManager.getBackStackEntryAt(entry).getName()).getTag()+ " ";
        }
        Log.i("FRAGMENT_STACK", message+": " + result);
    }
    public void setDrawerPosition(int pos){
        drawerListView.setItemChecked(pos, true);
        setTitle(fragmentTitles[pos]);
        drawerLayout.closeDrawer(drawerListView);
    }
    public void loadFragment(Fragment fragment, int position, boolean loadNewFragment){
        currentFragment = fragment; // set current fragment
        key = String.valueOf(position); // set the key/tag
        Log.d("FRAGMENT_STACK","Key: "+key);
        printFragmentStack("1Before");
        if(!loadNewFragment && fragmentManager.findFragmentByTag(key) != null){
            // if the fragment isn't new  or doesn't need to be reloaded. fetch fragment
            currentFragment = fragmentManager.findFragmentByTag(key);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // begin transaction
        if(fragmentManager.getBackStackEntryCount() == 0 || fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName() !=key){
            fragmentTransaction.addToBackStack(key); // add to stack if stack empty or not previously in the stack
            Log.d("FRAGMENT_STACK","Added to stack");
        }
        // replace the container in the activity with the current fragment
        fragmentTransaction.replace(R.id.content_frame, currentFragment,key);
        fragmentTransaction.commit(); // commit the transaction
        printFragmentStack("1After:");
        // Highlight the selected item, update the title, and close the drawer
        drawerListView.setItemChecked(position, true);
        setTitle(fragmentTitles[position]);
        drawerLayout.closeDrawer(drawerListView);
    }
    public static ArrayAdapter<String> getNoResultAdapter(Context context){
        // add a no results view to listView
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.items_empty_text));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,R.layout.items_empty_row,R.id.item_empty_text_view,list);
        return adapter;
    }
}

