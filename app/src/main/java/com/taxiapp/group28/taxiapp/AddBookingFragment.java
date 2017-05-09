package com.taxiapp.group28.taxiapp;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class AddBookingFragment extends Fragment {
    private final static int PICK_UP_TAB = 0;
    private final static int DEST_TAB = 1;
    private final static int CONFIRM_TAB = 2;
    private Bundle argsBundle =null;
    private  TabLayout tabLayout=null;
    private ViewPager viewPager=null ;
    private BookingPagerAdapter adapter=null;
    private View view;
    private int previousTabPosition =0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view!=null){
            return view;
        }
        if(savedInstanceState != null){
            if(savedInstanceState.getInt("previousTabPosition") >0){
                previousTabPosition = savedInstanceState.getInt("previousTabPosition");
            }
        }
        view = inflater.inflate(R.layout.fragment_add_booking, container, false);
        // if booking is being updated
        if(this.getArguments() !=  null){
            argsBundle= new Bundle(getArguments());
        }
        tabLayout = (TabLayout)view.findViewById(R.id.add_booking_tabLayout); // get tablayout
        viewPager = (ViewPager)view.findViewById(R.id.add_booking_pager); // get view pager (holds the fragments)
        if(Build.VERSION.SDK_INT>=17) {
            adapter = new BookingPagerAdapter(this.getChildFragmentManager(), tabLayout.getTabCount(), argsBundle); // adapter for pager
        }else{
            // needs changing to support API level < 17 currently doesn't work correctly
            adapter = new BookingPagerAdapter(this.getFragmentManager(), tabLayout.getTabCount(), argsBundle); // adapter for pager
        }

        viewPager.setAdapter(adapter); // set the adapter
        viewPager.setOffscreenPageLimit(3); // increase memory for tabs to 3 tabs/pages
        // add listeners
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout)); // keeps the pager and tablayout in sync
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // if confirm tab selected when the locations (pick up and dest) are not set, switch to the previous tab(dest tab)
                if(tab.getPosition() != CONFIRM_TAB  || adapter.isLocationsSet()){
                    if(tab.getPosition() == CONFIRM_TAB){
                        adapter.setConfirmLocations(); // set locations
                        if(!adapter.isLocationsSet()){
                            // if locations set are not valid
                                selectDestTab();
                                return;
                        }
                    }
                    viewPager.setCurrentItem(tab.getPosition()); // go to selected tab
                    previousTabPosition = tab.getPosition();
                }else{
                    // locations not valid
                    if(previousTabPosition != CONFIRM_TAB) {
                        Toast toast = Toast.makeText(AddBookingFragment.this.getActivity(), "Locations Not Set Or Invalid.", Toast.LENGTH_SHORT);
                        toast.show();
                        selectDestTab(); // go to dest tab
                    }else{
                        viewPager.setCurrentItem(tab.getPosition()); // go to selected tab
                        previousTabPosition = tab.getPosition();
                    }
                }
            }
            private void selectDestTab(){
                // select the destination tab to screen
                if(tabLayout.getTabAt(DEST_TAB) != null) {
                    tabLayout.getTabAt(DEST_TAB).select();
                    previousTabPosition= DEST_TAB;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do nothing
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // do nothing
            }
        });
        Log.d("ADD_BOOKING_FRAGMENT","created");
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("ADD_BOOKING_FRAGMENT","START");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("ADD_BOOKING_FRAGMENT","STOP");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("ADD_BOOKING_FRAGMENT","RESUME");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("ADD_BOOKING_FRAGMENT","PAUSE");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("previousTabPosition",previousTabPosition);
        Log.d("ADD_BOOKING_FRAGMENT","SAVED");

    }
    @Override
    public void onViewStateRestored (Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("ADD_BOOKING_FRAGMENT","RESTORED");
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    public void removeChildInstances(){
        //remove child instances from child fragment manager
        if(Build.VERSION.SDK_INT>=17) {
            FragmentManager cFM= this.getChildFragmentManager();
            for(int i=0;i<cFM.getBackStackEntryCount();++i){
                cFM.popBackStack();
            }
             cFM.beginTransaction()
                    .remove(adapter.getPickUpTab())
                    .remove(adapter.getDestTab())
                    .remove(adapter.getConfirmTab())
                    .commit();
                Log.d("FRAGMENT_REMOVED", "true");
            }else{
                Log.d("FRAGMENT_REMOVED", "false");
            }
    }

    public static String[] getResultTextArray(String locationInfo){
        // sort house number, street and postcode information from string in format "house number street,postcode,UK"
        String[] locationArray = locationInfo.split(",");
        String[] houseStreetArray= locationArray[0].split(" "); // get the street number, street, city and post code individually

        String houseNumber;
        String street;
        String postcode = locationArray[locationArray.length-2]; // postcode it always the second last entry
        int i=0; // 0 if a house number is not present
        // check if the the house number is valid for a list of digits, plus "-"  0 or 1 times plus a list of integers 0 or 1 times
        // possible results 123,23,23-24,12-59 etc
        if(houseStreetArray.length >1 && houseStreetArray[0].toString().matches("\\d+(-?\\d+)?")){
            houseNumber = houseStreetArray[0];
            i=1; // house number present don't include as street part.
        }else{
            houseNumber = ""; // no house number

        }
        StringBuilder stringBuilder = new StringBuilder();
        // build new street value
        for(int z=i;z<houseStreetArray.length;++z){
            stringBuilder.append(houseStreetArray[z]+" ");
        }
        street = stringBuilder.toString();
        String[] resultArray = {houseNumber,street,postcode};
        return resultArray;
    }
}

