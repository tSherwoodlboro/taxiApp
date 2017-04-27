package com.taxiapp.group28.taxiapp;
import android.app.Fragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view!=null){
            return view;
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
                if(tab.getPosition() != CONFIRM_TAB || adapter.isLocationsSet()){
                    if(tab.getPosition() == CONFIRM_TAB){
                        adapter.setConfirmLocations(); // set locations
                        if(!adapter.isLocationsSet()){
                            // if locations set are not valid
                            selectDestTab();
                            return;
                        }
                    }
                    viewPager.setCurrentItem(tab.getPosition()); // go to selected tab
                }else{
                    // locations not valid
                    Toast toast = Toast.makeText(AddBookingFragment.this.getActivity(),"Locations Not Set Or Invalid.",Toast.LENGTH_SHORT);
                    toast.show();
                    selectDestTab(); // go to dest tab
                }
            }
            private void selectDestTab(){
                // select the destination tab to screen
                if(tabLayout.getTabAt(DEST_TAB) != null) {
                    tabLayout.getTabAt(DEST_TAB).select();
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
        Log.d("ADD_BOOKING","START");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("ADD_BOOKING","STOP");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("ADD_BOOKING","RESUME");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("ADD_BOOKING","PAUSE");
    }
    public static String[] getResultTextArray(String locationInfo){
        // sort house number, street and postcode information from string in format "housenumber street,postcode,UK"
        String[] locationArray = locationInfo.split(",");
        String[] houseStreetArray= locationArray[0].split(" ");

        String houseNumber;
        String street;
        String postcode = locationArray[locationArray.length-2];
        int i=0;
        if(houseStreetArray.length >1 && houseStreetArray[0].toString().matches("\\d+(-?\\d+)?")){
            houseNumber = houseStreetArray[0];
            i=1;
        }else{
            houseNumber = "";

        }
        StringBuilder stringBuilder = new StringBuilder();
        for(int z=i;z<houseStreetArray.length;++z){
            stringBuilder.append(houseStreetArray[z]+" ");
        }
        street = stringBuilder.toString();
        String[] resultArray = {houseNumber,street,postcode};
        return resultArray;
    }
}

