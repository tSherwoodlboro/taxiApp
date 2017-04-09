package com.taxiapp.group28.taxiapp;

/**
 * Created by Tom on 04/04/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;


public class AddBookingPagerAdapter extends FragmentPagerAdapter {
    private static final int PICK_UP_TAB =0;
    private static final int DEST_TAB = 1;
    private static final int CONFIRM_TAB =2;

    int mNumOfTabs;
    PickUpFragment pickUpTab = null;
    DestFragment destTab = null;
    ConfirmBookingFragment confirmTab = null;

    public AddBookingPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    public boolean isLocationsSet(){
        // checks if the location is set for both pickup point and dest point
        return (pickUpTab != null && destTab !=null && pickUpTab.isLocationSet() && destTab.isLocationSet());
    }
    public void setConfirmLocations(){
        // sends information to the confirm tab once the pick up and dest tabs have been completed
        destTab.setAddress();
        pickUpTab.setAddress();
        if(destTab.getAddress() != null && pickUpTab.getAddress() != null) {
            confirmTab.setDestAddress(destTab.getAddress());
            confirmTab.setPickUpAddress(pickUpTab.getAddress());
            confirmTab.setPickUpTime(pickUpTab.getPickUpTime());
            confirmTab.setPickUpName(pickUpTab.getLocation());
            confirmTab.setDestName(destTab.getLocation());
            confirmTab.setPickUpNote(pickUpTab.getNoteText());
        }

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
            case PICK_UP_TAB:
                pickUpTab = new PickUpFragment();
                return pickUpTab;
            case DEST_TAB:
                destTab = new DestFragment();
                return destTab;
            case CONFIRM_TAB:
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