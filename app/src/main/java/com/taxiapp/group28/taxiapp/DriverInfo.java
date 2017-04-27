package com.taxiapp.group28.taxiapp;

import android.content.ContentValues;
import android.content.Context;

/**
 * Created by Tom on 25/04/2017.
 */

public class DriverInfo{
    private int id =-1;
    private String firstName=null;
    private String lastName=null;
    private int contactNumber= -1;
    private String location = null;
    private Double latitude = null;
    private Double longitude = null;
    private ContentValues contentValues =null;
    private Context context;
    public DriverInfo(Context context, int id, String firstName, String lastName, int contactNumber, String location, Double latitude, Double longitude){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.context = context;
        setContentValues();
    }
    public void addToLocalDatabase(){
        if(contentValues != null){
            //context.getContentResolver().insert(DBContract.Driver_Information_Table.CONTENT_URI,contentValues);
        }
    }
    private void setContentValues(){
        contentValues = new ContentValues();
        contentValues.put(DBContract.Driver_Information_Table._ID,id);
        contentValues.put(DBContract.Driver_Information_Table.COLUMN_FIRST_NAME,firstName);
        contentValues.put(DBContract.Driver_Information_Table.COLUMN_LAST_NAME,lastName);
        contentValues.put(DBContract.Driver_Information_Table.COLUMN_CONTACT_NUMBER,contactNumber);
        contentValues.put(DBContract.Driver_Information_Table.COLUMN_LOCATION,location);
        contentValues.put(DBContract.Driver_Information_Table.COLUMN_LATITUDE,latitude);
        contentValues.put(DBContract.Driver_Information_Table.COLUMN_LONGITUDE,longitude);
    }
    public int getId(){ return id;}
    public String getFirstName() { return firstName;}
    public String getLastName(){ return lastName;}
    public int getContactNumber(){ return contactNumber;}
    public String getLocation(){return  location;}
    public Double getLatitude(){ return latitude;}
    public Double getLongitude(){ return longitude;}
    public ContentValues getContentValues(){return contentValues;}

}