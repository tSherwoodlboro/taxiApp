package com.taxiapp.group28.taxiapp;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


public class BookingActivity extends AppCompatActivity {
    private final static int PICK_UP_TAB = 0;
    private final static int DEST_TAB = 1;
    private final static int CONFIRM_TAB = 2;
    private Bundle argsBundle =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);
        // if booking is being updated

        if(getIntent() !=  null){
            if(getIntent().getExtras() != null){
                argsBundle= new Bundle(getIntent().getExtras());
            }
        }
        final TabLayout tabLayout = (TabLayout)findViewById(R.id.add_booking_tabLayout); // get tablayout
        final ViewPager viewPager = (ViewPager)findViewById(R.id.add_booking_pager); // get view pager (holds the fragments)
        final BookingPagerAdapter adapter = new BookingPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),argsBundle); // adapter for pager
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
                    Toast toast = Toast.makeText(BookingActivity.this,"Locations Not Set Or Invalid.",Toast.LENGTH_SHORT);
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

