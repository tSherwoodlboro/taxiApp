package com.taxiapp.group28.taxiapp;

/**
 * Created by Tom on 04/04/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;


public class AddBookingPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    PickUpFragment pickUpTab = null;
    DestFragment destTab = null;
    ConfirmBookingFragment confirmTab = null;

    public AddBookingPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    public boolean isLocationsSet(){
        return (pickUpTab != null && destTab !=null && pickUpTab.isLocationSet() && destTab.isLocationSet());
    }
    public void setConfirmLocations(){
        destTab.setAddress();
        pickUpTab.setAddress();
        confirmTab.setDestAddress(destTab.getAddress());
        confirmTab.setPickUpAddress(pickUpTab.getAddress());
        confirmTab.setPickUpTime(pickUpTab.getPickUpTime());

    }
    public PickUpFragment getPickUpTab(){
        return pickUpTab;
    }
    public DestFragment getDestTab(){
        return destTab;
    }
    public ConfirmBookingFragment getConfirmTab(){
        return confirmTab;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("POSITION",new Integer(position).toString());
        switch (position) {
            case 0:
                pickUpTab = new PickUpFragment();
                return pickUpTab;
            case 1:
                destTab = new DestFragment();
                return destTab;
            case 2:
                confirmTab = new ConfirmBookingFragment();
                return confirmTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}