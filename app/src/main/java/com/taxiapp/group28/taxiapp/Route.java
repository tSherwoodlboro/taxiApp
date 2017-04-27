package com.taxiapp.group28.taxiapp;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by Tom on 25/04/2017.
 */

public class Route {
    private int id =-1;
    private String name = null;
    private String pickUpName= null;
    private Double pickUpLatitude = null;
    private Double pickUpLongitude = null;
    private String destName = null;
    private Double destLatitude = null;
    private Double destLongitude  = null;
    private int timesUsed = -1;
    private String note = null;
    private String userId = null;
    private Context context;
    private ContentValues contentValues = null;
    private HashMap<String,String> params = null;
    public Route(Context context){
        this.context = context;
        this.userId = SharedPreferencesManager.getUserPreferences(this.context).getString(context.getString(R.string.user_preferred_user_id_pref_key),"null");
    }
    public Route(Context context,String name,String pickUpName, Double pickUpLatitude, Double pickUpLongitude, String destName, Double destLatitude,Double destLongitude, int timesUsed,String note){
        // called for creating route
        this(context);
        this.name = name;
        this.pickUpName = pickUpName;
        this.pickUpLatitude = pickUpLatitude;
        this.pickUpLongitude = pickUpLongitude;
        this.destName = destName;
        this.destLatitude = destLatitude;
        this.destLongitude = destLongitude;
        this.timesUsed = timesUsed;
        this.note = note;
    }
    public Route(Context context,int id,String name,String pickUpName, Double pickUpLatitude, Double pickUpLongitude, String destName, Double destLatitude,Double destLongitude, int timesUsed,String note){
        // includes id in constructor called for updating if id known initially
        this(context);
        this.id = id;
        this.name = name;
        this.pickUpName = pickUpName;
        this.pickUpLatitude = pickUpLatitude;
        this.pickUpLongitude = pickUpLongitude;
        this.destName = destName;
        this.destLatitude = destLatitude;
        this.destLongitude = destLongitude;
        this.timesUsed = timesUsed;
        this.note = note;
    }
    public boolean setParams(){
        params = new HashMap<>();
        params.put("id",Integer.valueOf(id).toString());
        params.put(DBContract.Route_Table.COLUMN_USER_ID,userId);
        params.put(DBContract.Route_Table.COLUMN_NAME,name);
        params.put(DBContract.Route_Table.COLUMN_PICK_UP_NAME,pickUpName);
        params.put(DBContract.Route_Table.COLUMN_PICK_UP_LATITUDE,Double.valueOf(pickUpLatitude).toString());
        params.put(DBContract.Route_Table.COLUMN_PICK_UP_LONGITUDE,Double.valueOf(pickUpLongitude).toString());
        params.put(DBContract.Route_Table.COLUMN_DEST_NAME,destName);
        params.put(DBContract.Route_Table.COLUMN_DEST_LATITUDE,Double.valueOf(destLatitude).toString());
        params.put(DBContract.Route_Table.COLUMN_DEST_LONGITUDE,Double.valueOf(destLongitude).toString());
        params.put(DBContract.Route_Table.COLUMN_TIMES_USED,Integer.valueOf(timesUsed).toString());
        params.put(DBContract.Route_Table.COLUMN_NOTE,note);
        return true;
    }
    public boolean setContentValues(){
        if(id==-1){
            return false;
        }
        contentValues = new ContentValues();
        contentValues.put(DBContract.Route_Table._ID,id);
        contentValues.put(DBContract.Route_Table.COLUMN_USER_ID,userId);
        contentValues.put(DBContract.Route_Table.COLUMN_NAME,name);
        contentValues.put(DBContract.Route_Table.COLUMN_PICK_UP_NAME,pickUpName);
        contentValues.put(DBContract.Route_Table.COLUMN_PICK_UP_LATITUDE,pickUpLatitude);
        contentValues.put(DBContract.Route_Table.COLUMN_PICK_UP_LONGITUDE,pickUpLongitude);
        contentValues.put(DBContract.Route_Table.COLUMN_DEST_NAME,destName);
        contentValues.put(DBContract.Route_Table.COLUMN_DEST_LATITUDE,destLatitude);
        contentValues.put(DBContract.Route_Table.COLUMN_DEST_LONGITUDE,destLongitude);
        contentValues.put(DBContract.Route_Table.COLUMN_TIMES_USED,timesUsed);
        contentValues.put(DBContract.Route_Table.COLUMN_NOTE,note);
        return true;
    }
    public void setId(int val){
        id = val;
    }
    public void setNote(String val){
        note = val;
    }
    public void setName(String val){
        name = val;
    }
    public Bundle getRouteBundle(){
        Bundle bundle = new Bundle();
        bundle.putString(DBContract.Route_Table._ID,String.valueOf(id));
        bundle.putString(DBContract.Route_Table.COLUMN_USER_ID,userId);
        bundle.putString(DBContract.Route_Table.COLUMN_NAME,name);
        bundle.putString(DBContract.Route_Table.COLUMN_PICK_UP_NAME,pickUpName);
        bundle.putString(DBContract.Route_Table.COLUMN_PICK_UP_LATITUDE,String.valueOf(pickUpLatitude));
        bundle.putString(DBContract.Route_Table.COLUMN_PICK_UP_LONGITUDE,String.valueOf(pickUpLongitude));
        bundle.putString(DBContract.Route_Table.COLUMN_DEST_NAME,destName);
        bundle.putString(DBContract.Route_Table.COLUMN_DEST_LATITUDE,String.valueOf(destLatitude));
        bundle.putString(DBContract.Route_Table.COLUMN_DEST_LONGITUDE,String.valueOf(destLongitude));
        bundle.putString(DBContract.Route_Table.COLUMN_TIMES_USED,String.valueOf(timesUsed));
        bundle.putString(DBContract.Route_Table.COLUMN_NOTE,note);
        bundle.putString(BookingPagerAdapter.USING_ROUTE,BookingPagerAdapter.USING_ROUTE);
        return bundle;
    }
    public int getId(){return id;}
    public String getName(){return name;}
    public String getPickUpName(){return pickUpName;}
    public Double getPickUpLatitude(){return pickUpLatitude;}
    public Double getPickUpLongitude(){return  pickUpLongitude;}
    public String getDestName(){return destName;}
    public Double getDestLatitude(){return destLatitude;}
    public Double getDestLongitude(){return destLongitude;}
    public int getTimesUsed(){return timesUsed;}
    public String getUserId(){return  userId;}
    public String getNote(){return note;}
    public HashMap<String,String> getParams(){return params;}
    public ContentValues getContentValues(){return contentValues;}
}
