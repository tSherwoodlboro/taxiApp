package com.taxiapp.group28.taxiapp;

/**
 * Created by Tom on 04/04/2017.
 */
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Parcelable;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;


public class BookingPagerAdapter extends FragmentPagerAdapter {
    public static final int PICK_UP_TAB =0;
    public static final int DEST_TAB = 1;
    public static final int CONFIRM_TAB =2;
    public static final String USING_ROUTE = "usingRoute";
    public static final String UPDATE_BOOKING = "updateBooking";
    public static final String UPDATE_BOOKING_PICK_UP_LOCATION_NAME = "pickUpLocationName";
    public static final String UPDATE_BOOKING_PICK_UP_LATITUDE = "pickUpLatitude";
    public static final String UPDATE_BOOKING_PICK_UP_LONGITUDE = "pickUpLongitude";
    public static final String UPDATE_BOOKING_DEST_LOCATION_NAME = "destLocationName";
    public static final String UPDATE_BOOKING_DEST_LATITUDE = "destLatitude";
    public static final String UPDATE_BOOKING_DEST_LONGITUDE = "destLongitude";
    public static final String UPDATE_BOOKING_NOTE = "pickUpNote";
    public static final String UPDATE_BOOKING_ID = "bookingId";
    private int mNumOfTabs;
    private PickUpFragment pickUpTab = null;
    private DestFragment destTab = null;
    private ConfirmBookingFragment confirmTab = null;
    private Bundle argsBundle=null;
    private FragmentManager fragmentManager= null;
    public BookingPagerAdapter(FragmentManager fm, int NumOfTabs,Bundle argsBundle) {
        super(fm);
        this.fragmentManager = fm;
        this.mNumOfTabs = NumOfTabs;
        this.argsBundle = argsBundle; // bundle for updating otherwise null
    }
    public boolean isLocationsSet(){
        // checks if the location is set for both pickup point and dest point
        if (pickUpTab != null && destTab !=null && pickUpTab.isLocationSet() && destTab.isLocationSet()){
            if(pickUpTab.getLatitude() != destTab.getLatitude() && pickUpTab.getLongitude() != destTab.getLongitude()){
                return true;
            }
        }
        return false;
    }
    public void setConfirmLocations(){
        // sends information to the confirm tab once the pick up and dest tabs have been completed
        destTab.setAddress();
        pickUpTab.setAddress();
        if(destTab.getAddress() != null && pickUpTab.getAddress() != null) {
            // create a new booking and pass to the confirmTab
            Booking booking = new Booking(confirmTab.getActivity(),pickUpTab.getLocation(),destTab.getLocation(),pickUpTab.getAddress(),destTab.getAddress(),pickUpTab.getNoteText(),pickUpTab.getPickUpTime());
            confirmTab.setBooking(booking);
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
    public Parcelable saveState(){
        return super.saveState();
    }
    @Override
    public void restoreState(Parcelable state,ClassLoader loader){
        super.restoreState(state,loader);
    }
    @Override
    public Fragment getItem(int position) {
        // called to initialise child fragment instances
        Log.d("POSITION",Integer.valueOf(position).toString());
        switch (position) {
            case PICK_UP_TAB:
                pickUpTab = new PickUpFragment();
                pickUpTab.setArguments(argsBundle);
                return pickUpTab;
            case DEST_TAB:
                destTab = new DestFragment();
                destTab.setArguments(argsBundle);
                return destTab;
            case CONFIRM_TAB:
                confirmTab = new ConfirmBookingFragment();
                confirmTab.setArguments(argsBundle);
                return confirmTab;
            default:
                return null;
        }
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        // restore fragment instances
        switch (position) {
            case PICK_UP_TAB:pickUpTab = (PickUpFragment)createdFragment;
                break;
            case DEST_TAB:destTab = (DestFragment)createdFragment;
                break;
            case CONFIRM_TAB:confirmTab = (ConfirmBookingFragment) createdFragment;
                break;
            default:
                return null;
        }
        if(fragmentManager != null && fragmentManager.findFragmentByTag(String.valueOf(position)) == null){
            // add the fragment to the stack if it's a new fragment
            fragmentManager.beginTransaction().addToBackStack(String.valueOf(position)).commit();
            Log.d("BOOKING_PAGER","Add to stack: "+position);
        }
        return createdFragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}