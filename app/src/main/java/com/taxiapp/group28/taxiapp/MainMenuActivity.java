package com.taxiapp.group28.taxiapp;

import android.content.Context;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 22/04/2017.
 */

public class MainMenuActivity extends AppCompatActivity {
    private String[] fragmentTitles = {"Bookings","Add Booking","Routes","Guide","Settings","Call Help","Log Out","Quit"};
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;
    public static final int VIEW_BOOKINGS_FRAGMENT_POSITION = 0;
    public static final int ADD_BOOKING_FRAGMENT_POSITION = 1;
    public static final int VIEW_ROUTES_FRAGMENT_POSITION = 2;
    public static final int GUIDE_FRAGMENT_POSITION = 3;
    public static final int SETTINGS_FRAGMENT_POSITION = 4;
    public static final int CALL_HELP_POSITION = 5;
    public static final int LOG_OUT_POSITION = 6;
    public static final int QUIT_POSITION = 7;

    private Fragment currentFragment = null;
    private FragmentManager fragmentManager=null;
    private String key;
    private int previousPosition=1;
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        int themeValue = Integer.valueOf(SharedPreferencesManager.getUserPreferences(this).getString(getString(R.string.user_preferred_theme_pref_key),"0")); // get theme value from preferences
        setTheme(getAppTheme(themeValue));
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerListView = (ListView)findViewById(R.id.left_drawer);
        drawerListView.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_list_item,fragmentTitles));
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
        // set toggle for action bar
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer,R.string.close_drawer){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if(fragmentManager == null){
            fragmentManager = this.getFragmentManager();
            fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {

                }
            });
        }
        if(saveInstanceState == null) {
            key = Integer.toString(VIEW_BOOKINGS_FRAGMENT_POSITION);
            loadFragment(new ViewBookingsFragment(), VIEW_BOOKINGS_FRAGMENT_POSITION,false);
            Toast.makeText(this,"Welcome "+PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.user_name_pref_key),"User"),Toast.LENGTH_SHORT).show();
        }
        Log.d("MAIN_MENU_ACTIVITY","CREATED");
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
                case GUIDE_FRAGMENT_POSITION:
                    selectedFragment = new GuideFragment();
                    key = Integer.toString(GUIDE_FRAGMENT_POSITION);
                    break;
                case CALL_HELP_POSITION:callHelp();
                    return;
                case LOG_OUT_POSITION:
                    logout();
                    return;
                case QUIT_POSITION:
                    finish();
                    return;
                default : return;
            }
            loadFragment(selectedFragment,position,false);
            previousPosition = position;
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
        if(position == ADD_BOOKING_FRAGMENT_POSITION && !TaxiAppOnlineDatabase.isNetworkEnabled(this,0)){
            setDrawerPosition(previousPosition);
            return;
        }
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
    public void removeAddBookingInstance(){
        // clear the stack and clear the childFragment (Instance states) stack.
        AddBookingFragment addBookingFragment = (AddBookingFragment)(fragmentManager.findFragmentByTag(String.valueOf(ADD_BOOKING_FRAGMENT_POSITION)));
        if(addBookingFragment !=null){
            addBookingFragment.removeChildInstances();
            fragmentManager.beginTransaction().remove(addBookingFragment);
        }
        for(int i=0;i<fragmentManager.getBackStackEntryCount();++i){
            fragmentManager.popBackStack();
        }
    }
    public static ArrayAdapter<String> getNoResultAdapter(Context context){
        // add a no results view to listView
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.items_empty_text));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,R.layout.items_empty_row,R.id.item_empty_text_view,list);
        return adapter;
    }
    private int getAppTheme(int val){
        // set app theme on load
        switch(val){
            case SettingsPreferenceFragment.LIGHT_THEME:return this.getApplicationInfo().theme;
            case SettingsPreferenceFragment.DEFAULT_THEME:return android.R.style.Theme_DeviceDefault;
            default: return this.getApplicationInfo().theme;
        }
    }
    private void callHelp(){
        String helpNum = "07426992220";
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", helpNum, null));
        startActivity(intent);
    }

    private void logout(){
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(TaxiConstants.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(getString(R.string.user_preferred_user_id_pref_key),"null");
        editor.apply();
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {

            loadFragment(new SettingsPreferenceFragment(),SETTINGS_FRAGMENT_POSITION,true);
            return true;
        }
        if (id == R.id.menu_guide) {
            loadFragment(new GuideFragment(),GUIDE_FRAGMENT_POSITION,true);
            return true;
        }
        if (id == R.id.menu_quit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

