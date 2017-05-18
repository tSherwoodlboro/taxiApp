package com.taxiapp.group28.taxiapp;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tom on 27/04/2017.
 */

public class SyncDatabases {
    private Context context;
    private int userId;
    private HashMap<String,String> params=null;
    private SyncDatabases.onSyncCompleteListener onSyncComplete=null;
    public interface onSyncCompleteListener{
        void onSyncComplete();
    }
    public void setOnSyncCompleteListener(SyncDatabases.onSyncCompleteListener listener){
        onSyncComplete = listener;
    }
    public SyncDatabases(Context _context,int _userId){
        this.context = _context;
        this.userId = _userId;
        params = new HashMap<>();
        params.put("user_id",String.valueOf(userId));
    }
    public boolean syncDataBase(){
        syncBookings();
        return true;
    }
    private void syncBookings(){
        final TaxiAppOnlineDatabase conn = new TaxiAppOnlineDatabase(context);
        conn.getBookings(params);
        conn.setOnGetResultListener(new TaxiAppOnlineDatabase.onGetResultListener() {
            @Override
            public void onGetResult() {
                if(!conn.isResultError()){
                    ArrayList<ContentValues> cvList = conn.getContentValuesList();
                    if(cvList==null) return;
                    for(ContentValues values : cvList){
                        context.getContentResolver().insert(DBContract.Booking_Table.CONTENT_URI,values);
                        Log.d("REGISTER_SYNC","Data inserted");
                    }
                    syncRoutes();
                }else{
                    Log.d("SYNC_ERROR",conn.getResult().toString());
                    syncDrivers();
                }
            }
        });
    }
    private void syncRoutes(){
        Log.d("SYNC_ROUTES","syncing routes");
        final TaxiAppOnlineDatabase conn = new TaxiAppOnlineDatabase(context);
        conn.getRoutes(params);
        conn.setOnGetResultListener(new TaxiAppOnlineDatabase.onGetResultListener() {
            @Override
            public void onGetResult() {
                if(!conn.isResultError()){
                    ArrayList<ContentValues> cvList = conn.getContentValuesList();
                    if(cvList==null) return;
                    for(ContentValues values : cvList){
                        context.getContentResolver().insert(DBContract.Route_Table.CONTENT_URI,values);
                    }
                }
                syncDrivers();
            }
        });
    }
    private void syncDrivers(){
        Log.d("SYNC_DRIVERS","syncing drivers");
        final TaxiAppOnlineDatabase conn = new TaxiAppOnlineDatabase(context);
        conn.getDriversInformation(params);
        conn.setOnGetResultListener(new TaxiAppOnlineDatabase.onGetResultListener() {
            @Override
            public void onGetResult() {
                if(!conn.isResultError()) {
                    ArrayList<ContentValues> cvList = conn.getContentValuesList();
                    if (cvList == null) return;
                    for (ContentValues values : cvList) {
                        context.getContentResolver().insert(DBContract.Driver_Information_Table.CONTENT_URI, values);
                    }
                    if (onSyncComplete != null) {
                        onSyncComplete.onSyncComplete();
                    }
                }else{
                    Log.d("SYNC_ERROR","Driver sync"+conn.getResultMessage());

                }
            }
        });
    }
}
