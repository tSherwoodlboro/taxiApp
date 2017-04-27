package com.taxiapp.group28.taxiapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tom on 25/04/2017.
 */

public class DriversInfo{
    private TaxiAppOnlineDatabase conn =null;
    private JSONArray driverData = null;
    private ArrayList<DriverInfo> driverList =null;
    private Context context;
    private DriversInfo.onGetResultListener getResultListener;

    public interface onGetResultListener{
        void onGetResult();
    }
    public void setOnGetResultListener(DriversInfo.onGetResultListener listener){
        getResultListener = listener;

    }
    public  DriversInfo(Context context){
        this.context = context;
    }
    public void setDriverInfo(){

        conn = new TaxiAppOnlineDatabase();
        HashMap<String,String> hashMap = new HashMap<>();
        conn.getDriversInformation(hashMap);
        conn.setOnGetResultListener(new TaxiAppOnlineDatabase.onGetResultListener() {
            @Override
            public void onGetResult() {
                driverList = new ArrayList<>();
                driverData = conn.getResult();
                for(int i=0;i<driverData.length();++i){
                    try{
                        JSONObject driverObject = driverData.getJSONObject(i);
                        int driverId = driverObject.getInt("id");
                        String firstName = driverObject.getString(DBContract.Driver_Information_Table.COLUMN_FIRST_NAME);
                        String lastName = driverObject.getString(DBContract.Driver_Information_Table.COLUMN_LAST_NAME);
                        int contactNumber = driverObject.getInt(DBContract.Driver_Information_Table.COLUMN_CONTACT_NUMBER);
                        String location = driverObject.getString(DBContract.Driver_Information_Table.COLUMN_LOCATION);
                        Double latitude = driverObject.getDouble(DBContract.Driver_Information_Table.COLUMN_LATITUDE);
                        Double longitude = driverObject.getDouble(DBContract.Driver_Information_Table.COLUMN_LONGITUDE);
                        DriverInfo driver = new DriverInfo(context,driverId,firstName,lastName,contactNumber,location,latitude,longitude);
                        driverList.add(driver);
                        Log.d("",driver.getFirstName());
                    }catch(JSONException e){

                    }catch(Exception e1){
                    }
                }
                for(int z=0;z<driverList.size();++z){
                    driverList.get(z).addToLocalDatabase();
                }
                if(getResultListener != null) {
                    getResultListener.onGetResult();
                }
            }
        });
    }
    public ArrayList<DriverInfo> getDriverList(){
        return driverList;
    }
    public JSONArray getDriverData(){
        return  driverData;
    }
}
