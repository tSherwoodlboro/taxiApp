package com.taxiapp.group28.taxiapp;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class AddBookingActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        final TabLayout tabLayout = (TabLayout)findViewById(R.id.add_booking_tabLayout);
        final ViewPager viewPager = (ViewPager)findViewById(R.id.add_booking_pager);
        final AddBookingPagerAdapter adapter = new AddBookingPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() != 2 || adapter.isLocationsSet()){
                    if(tab.getPosition() == 2){
                        adapter.setConfirmLocations();
                    }
                    viewPager.setCurrentItem(tab.getPosition());
                }else{
                    if(tabLayout.getTabAt(1) != null) {
                        tabLayout.getTabAt(1).select();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }
    public static String[] getResultTextArray(String locationInfo){
        String[] locationArray = locationInfo.split(",");
        String[] houseStreetArray= locationArray[0].split(" ");

        String houseNumber;
        String street;
        String postcode = locationArray[3];
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

