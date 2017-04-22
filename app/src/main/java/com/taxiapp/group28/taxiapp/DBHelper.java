package com.taxiapp.group28.taxiapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Tom on 11/04/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "taxiApp_DB.db";

    public SQLiteDatabase sqLiteDatabase;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        sqLiteDatabase = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        sqLiteDatabase = db;
        String userQuery = "CREATE TABLE IF NOT EXISTS " + DBContract.User_Table.TABLE_NAME+" (" +
                    DBContract.User_Table._ID+" INTEGER PRIMARY KEY ON CONFLICT REPLACE," +
                    DBContract.User_Table.COLUMN_TEL_NO+" TEXT," +
                    DBContract.User_Table.COLUMN_USER_NAME+" TEXT," +
                    DBContract.User_Table.COLUMN_PREFERRED_DRIVER_ID+" INTEGER DEFAULT NULL" +
                ")";
        String bookingQuery ="CREATE TABLE IF NOT EXISTS " + DBContract.Booking_Table.TABLE_NAME+" (" +
                    DBContract.Booking_Table._ID+" INTEGER PRIMARY KEY ON CONFLICT REPLACE," +
                    DBContract.Booking_Table.COLUMN_USER_ID+" INTEGER," +
                    DBContract.Booking_Table.COLUMN_ASSIGNED_DRIVER_ID+" INTEGER," +
                    DBContract.Booking_Table.COLUMN_DATE+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    DBContract.Booking_Table.COLUMN_PICK_UP_NAME+" TEXT," +
                    DBContract.Booking_Table.COLUMN_PICK_UP_LATITUDE+" DOUBLE," +
                    DBContract.Booking_Table.COLUMN_PICK_UP_LONGITUDE+" DOUBLE," +
                    DBContract.Booking_Table.COLUMN_DEST_NAME+" TEXT," +
                    DBContract.Booking_Table.COLUMN_DEST_LATITUDE+" DOUBLE," +
                    DBContract.Booking_Table.COLUMN_DEST_LONGITUDE+" DOUBLE," +
                    DBContract.Booking_Table.COLUMN_PRICE+" DOUBLE," +
                    DBContract.Booking_Table.COLUMN_EST_ARRIVAL_TIME+" TIMESTAMP," +
                    DBContract.Booking_Table.COLUMN_EST_DEST_TIME+" TIMESTAMP," +
                    DBContract.Booking_Table.COLUMN_CONFIRMED_ARRIVAL_TIME+" TIMESTAMP DEFAULT '0000-00-00 00:00:00'," +
                    DBContract.Booking_Table.COLUMN_CONFIRMED_DEST_TIME+" TIMESTAMP DEFAULT '0000-00-00 00:00:00'," +
                    DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE+" INTEGER," +
                    DBContract.Booking_Table.COLUMN_NOTE+" TEXT DEFAULT NULL," +
                    "FOREIGN KEY("+DBContract.Booking_Table.COLUMN_USER_ID+") REFERENCES "+DBContract.User_Table.TABLE_NAME+"("+DBContract.User_Table._ID+")," +
                    "FOREIGN KEY("+DBContract.Booking_Table.COLUMN_ASSIGNED_DRIVER_ID+") REFERENCES "+DBContract.Driver_Information_Table.TABLE_NAME+"("+DBContract.Driver_Information_Table._ID+")" +
                ")";
        String routeQuery ="CREATE TABLE IF NOT EXISTS " + DBContract.Route_Table.TABLE_NAME+"(" +
                    DBContract.Route_Table._ID+" INTEGER PRIMARY KEY ON CONFLICT REPLACE," +
                    DBContract.Route_Table.COLUMN_NAME+" TEXT," +
                    DBContract.Route_Table.COLUMN_PICK_UP_NAME+" TEXT," +
                    DBContract.Route_Table.COLUMN_PICK_UP_LATITUDE+" DOUBLE," +
                    DBContract.Route_Table.COLUMN_PICK_UP_LONGITUDE+" DOUBLE," +
                    DBContract.Route_Table.COLUMN_DEST_NAME+" TEXT," +
                    DBContract.Route_Table.COLUMN_DEST_LATITUDE+" DOUBLE," +
                    DBContract.Route_Table.COLUMN_DEST_LONGITUDE+" DOUBLE," +
                    DBContract.Route_Table.COLUMN_TIMES_USED+" INTEGER," +
                    DBContract.Route_Table.COLUMN_USER_ID+" INTEGER," +
                    DBContract.Route_Table.COLUMN_NOTE+" TEXT DEFAULT NULL," +
                    "FOREIGN KEY("+DBContract.Route_Table.COLUMN_USER_ID+") REFERENCES "+DBContract.User_Table.TABLE_NAME+"("+DBContract.User_Table._ID+")" +
                ")";
        String driverQuery="CREATE TABLE IF NOT EXISTS " + DBContract.Driver_Information_Table.TABLE_NAME+" (" +
                    DBContract.Driver_Information_Table._ID+" INTEGER PRIMARY KEY ON CONFLICT REPLACE," +
                    DBContract.Driver_Information_Table.COLUMN_FIRST_NAME+" TEXT," +
                    DBContract.Driver_Information_Table.COLUMN_LAST_NAME+" TEXT," +
                    DBContract.Driver_Information_Table.COLUMN_CONTACT_NUMBER+" INTEGER," +
                    DBContract.Driver_Information_Table.COLUMN_LOCATION+" TEXT" +
                ");";
        db.execSQL(bookingQuery);
        db.execSQL(userQuery);
        db.execSQL(driverQuery);
        db.execSQL(routeQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + DBContract.User_Table.TABLE_NAME+
                        "DROP TABLE IF EXISTS " + DBContract.Booking_Table.TABLE_NAME+
                        "DROP TABLE IF EXISTS " + DBContract.Route_Table.TABLE_NAME+
                        "DROP TABLE IF EXISTS " + DBContract.Driver_Information_Table.TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    public void clearBookings(){
        String sql = "DELETE FROM " + DBContract.Booking_Table.TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
    }
    public void clearTables(){
        String sql = "DELETE FROM " + DBContract.User_Table.TABLE_NAME+
                        "DELETE FROM " + DBContract.Booking_Table.TABLE_NAME+
                        "DELETE FROM " + DBContract.Route_Table.TABLE_NAME+
                        "DELETE FROM " + DBContract.Driver_Information_Table.TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
    }
}
