package com.taxiapp.group28.taxiapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Tom on 27/03/2017.
 */

public class DBContentProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int USER = 0;
    public static final int USER_WITH_ID = 1;
    public static final int BOOKING = 2;
    public static final int BOOKING_WITH_ID = 3;
    public static final int ROUTE = 4;
    public static final int ROUTE_WITH_ID = 5;
    public static final int DRIVER_INFORMATION = 6;
    public static final int DRIVER_INFORMATION_WITH_ID = 7;
    private DBHelper dbHelper;

    static {
        // add uri for user table multiple and single
        uriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.User_Table.PATH, USER);
        uriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.User_Table.PATH+"/#", USER_WITH_ID);
        // add uri for booking table multiple and single
        uriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.Booking_Table.PATH, BOOKING);
        uriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.Booking_Table.PATH+"/#", BOOKING_WITH_ID);
        // add uri for route table multiple and single
        uriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.Route_Table.PATH, ROUTE);
        uriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.Route_Table.PATH+"/#", ROUTE_WITH_ID);
        // add uri for driver_information table multiple and single
        uriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.Driver_Information_Table.PATH, DRIVER_INFORMATION);
        uriMatcher.addURI(DBContract.CONTENT_AUTHORITY, DBContract.Driver_Information_Table.PATH+"/#", DRIVER_INFORMATION);
    }
    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext(),DBHelper.DB_NAME,null,DBHelper.DB_VERSION);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        Log.d("URI DATA",uri.toString());
        switch(uriMatcher.match(uri)) {
            case USER:
                return DBContract.User_Table.CONTENT_TYPE_DIR;
            case USER_WITH_ID:
                return DBContract.User_Table.CONTENT_TYPE_ITEM;
            case BOOKING:
                return DBContract.Booking_Table.CONTENT_TYPE_DIR;
            case BOOKING_WITH_ID:
                return DBContract.Booking_Table.CONTENT_TYPE_ITEM;
            case ROUTE:
                return DBContract.Route_Table.CONTENT_TYPE_DIR;
            case ROUTE_WITH_ID:
                return DBContract.Route_Table.CONTENT_TYPE_ITEM;
            case DRIVER_INFORMATION:
                return DBContract.Driver_Information_Table.CONTENT_TYPE_DIR;
            case DRIVER_INFORMATION_WITH_ID:
                return DBContract.Driver_Information_Table.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("URI Not found.");
        }
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("URI DATA",uri.toString());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;
        Uri returnUri =null;
        boolean error=false;
        switch (uriMatcher.match(uri)) {
            case USER:
                id = db.insert(DBContract.User_Table.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = DBContract.User_Table.buildUserUriWithID(id);
                } else {
                    error = true;
                }
                break;
            case BOOKING:
                id = db.insert(DBContract.Booking_Table.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = DBContract.Booking_Table.buildBookingUriWithID(id);
                } else {
                    error = true;
                }
                break;
            case ROUTE:
                id = db.insert(DBContract.Route_Table.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = DBContract.Route_Table.buildRouteUriWithID(id);
                } else {
                    error = true;
                }
                break;
            case DRIVER_INFORMATION:
                id = db.insert(DBContract.Driver_Information_Table.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = DBContract.Driver_Information_Table.buildDriverInformationUriWithID(id);
                } else {
                    error = true;
                }
                break;
            default:
                throw new UnsupportedOperationException("URI Not found.");
        }
        if(error){
            throw new SQLException("Failed to insert record.");
        }
        return returnUri;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d("URI DATA",uri.toString());
        int returnVal;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case USER: returnVal = db.delete(DBContract.User_Table.TABLE_NAME, null, null);
                break;
            case USER_WITH_ID:returnVal =  db.delete(DBContract.User_Table.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKING:returnVal =  db.delete(DBContract.Booking_Table.TABLE_NAME, null, null);
                break;
            case BOOKING_WITH_ID:returnVal =  db.delete(DBContract.Booking_Table.TABLE_NAME, selection, selectionArgs);
                break;
            case ROUTE:returnVal =  db.delete(DBContract.Route_Table.TABLE_NAME, null, null);
                break;
            case ROUTE_WITH_ID:returnVal =  db.delete(DBContract.Route_Table.TABLE_NAME, selection, selectionArgs);
                break;
            case DRIVER_INFORMATION:returnVal =  db.delete(DBContract.Driver_Information_Table.TABLE_NAME, null, null);
                break;
            case DRIVER_INFORMATION_WITH_ID:returnVal =  db.delete(DBContract.Driver_Information_Table.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("URI Not found.");
        }
        return returnVal;

    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d("URI DATA",uri.toString());
        Cursor cursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case USER:
                cursor = db.query(DBContract.User_Table.TABLE_NAME, projection, selection, selectionArgs, null, null, null, null);
                break;
            case USER_WITH_ID:
                cursor = db.query(DBContract.User_Table.TABLE_NAME, projection, selection, selectionArgs, null, null, "1");
                break;
            case BOOKING:
                cursor = db.query(DBContract.Booking_Table.TABLE_NAME, projection, selection, selectionArgs, null, null, null, null);
                break;
            case BOOKING_WITH_ID:
                cursor = db.query(DBContract.Booking_Table.TABLE_NAME, projection, selection, selectionArgs, null, null, "1");
                break;
            case ROUTE:
                cursor = db.query(DBContract.Route_Table.TABLE_NAME, projection, selection, selectionArgs, null, null, null, null);
                break;
            case ROUTE_WITH_ID:
                cursor = db.query(DBContract.Route_Table.TABLE_NAME, projection, selection, selectionArgs, null, null, "1");
                break;
            case DRIVER_INFORMATION:
                cursor = db.query(DBContract.Driver_Information_Table.TABLE_NAME, projection, selection, selectionArgs, null, null, null, null);
                break;
            case DRIVER_INFORMATION_WITH_ID:
                cursor = db.query(DBContract.Driver_Information_Table.TABLE_NAME, projection, selection, selectionArgs, null, null, "1");
                break;
            default:
                throw new UnsupportedOperationException("URI Not found.");
        }
        return cursor;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d("URI DATA",uri.toString());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnVal;
        switch (uriMatcher.match(uri)) {
            case USER_WITH_ID:returnVal= db.update(DBContract.User_Table.TABLE_NAME, values, selection, selectionArgs);
                break;
            case BOOKING_WITH_ID:returnVal= db.update(DBContract.Booking_Table.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUTE_WITH_ID: returnVal= db.update(DBContract.Route_Table.TABLE_NAME, values, selection, selectionArgs);
                break;
            case DRIVER_INFORMATION_WITH_ID: returnVal= db.update(DBContract.Driver_Information_Table.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("URI Not found.");
        }
        return returnVal;
    }

}
