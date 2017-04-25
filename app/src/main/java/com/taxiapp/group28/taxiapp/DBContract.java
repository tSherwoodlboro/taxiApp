package com.taxiapp.group28.taxiapp;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.widget.TabHost;

/**
 * Created by Tom on 11/04/2017.
 */

public class DBContract {
    //Uri for ContentProvider
    public static final String CONTENT_AUTHORITY = "com.taxiapp.group28.taxiapp";
    public static final String PATH_TAXI_APP= "taxiApp_DB";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY+"/"+PATH_TAXI_APP);


    public static class Booking_Table implements BaseColumns {
        public static final String TABLE_NAME = "booking";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+PATH_TAXI_APP+"/"+TABLE_NAME;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/"+PATH_TAXI_APP+"/"+TABLE_NAME;
        public static final String PATH = PATH_TAXI_APP+"/"+ TABLE_NAME;

        public static final String COLUMN_USER_ID= "user_id";
        public static final String COLUMN_ASSIGNED_DRIVER_ID= "assigned_driver_id";
        public static final String COLUMN_DATE= "date";
        public static final String COLUMN_PICK_UP_NAME= "pick_up_name";
        public static final String COLUMN_PICK_UP_LATITUDE= "pick_up_latitude";
        public static final String COLUMN_PICK_UP_LONGITUDE= "pick_up_longitude";
        public static final String COLUMN_DEST_NAME = "dest_name";
        public static final String COLUMN_DEST_LATITUDE = "dest_latitude";
        public static final String COLUMN_DEST_LONGITUDE = "dest_longitude";
        public static final String COLUMN_PRICE  = "price";
        public static final String COLUMN_EST_ARRIVAL_TIME= "est_arrival_time";
        public static final String COLUMN_EST_DEST_TIME= "est_dest_time";
        public static final String COLUMN_CONFIRMED_ARRIVAL_TIME = "confirmed_arrival_time";
        public static final String COLUMN_CONFIRMED_DEST_TIME = "confirmed_dest_time";
        public static final String COLUMN_BOOKING_COMPLETE= "booking_complete";
        public static final String COLUMN_NOTE= "note";

        public static Uri buildBookingUriWithID(long ID){
            return ContentUris.withAppendedId(CONTENT_URI,ID);
        }
    }
    public static class Route_Table implements BaseColumns {
        public static final String TABLE_NAME = "route";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+PATH_TAXI_APP+"/"+TABLE_NAME;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/"+PATH_TAXI_APP+"/"+TABLE_NAME;
        public static final String PATH = PATH_TAXI_APP+"/"+ TABLE_NAME;

        public static final String COLUMN_NAME= "name";
        public static final String COLUMN_USER_ID= "user_id";
        public static final String COLUMN_ASSIGNED_DRIVER_ID= "assigned_driver_id";
        public static final String COLUMN_DATE= "date";
        public static final String COLUMN_PICK_UP_NAME= "pick_up_name";
        public static final String COLUMN_PICK_UP_LATITUDE= "pick_up_latitude";
        public static final String COLUMN_PICK_UP_LONGITUDE= "pick_up_longitude";
        public static final String COLUMN_DEST_NAME = "dest_name";
        public static final String COLUMN_DEST_LATITUDE = "dest_latitude";
        public static final String COLUMN_DEST_LONGITUDE = "dest_longitude";
        public static final String COLUMN_TIMES_USED = "times_used";
        public static final String COLUMN_NOTE= "note";

        public static Uri buildRouteUriWithID(long ID){
            return ContentUris.withAppendedId(CONTENT_URI,ID);
        }
    }
    public static class User_Table implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+PATH_TAXI_APP+"/"+TABLE_NAME;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/"+PATH_TAXI_APP+"/"+TABLE_NAME;
        public static final String PATH = PATH_TAXI_APP+"/"+ TABLE_NAME;

        public static final String COLUMN_TEL_NO= "tel_no";
        public static final String COLUMN_USER_NAME= "user_name";
        public static final String COLUMN_PREFERRED_DRIVER_ID= "preferred_driver_id";
        // online only
        public static final String COLUMN_VERIFICATION_CODE = "verification_code";
        public static final String COLUMN_VERIFIED = "verified";


        public static Uri buildUserUriWithID(long ID){
            return ContentUris.withAppendedId(CONTENT_URI,ID);
        }
    }
    public static class Driver_Information_Table implements BaseColumns {
        public static final String TABLE_NAME = "driver_information";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+PATH_TAXI_APP+"/"+TABLE_NAME;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/"+PATH_TAXI_APP+"/"+TABLE_NAME;
        public static final String PATH = PATH_TAXI_APP+"/"+ TABLE_NAME;

        public static final String COLUMN_FIRST_NAME= "first_name";
        public static final String COLUMN_LAST_NAME= "last_name";
        public static final String COLUMN_CONTACT_NUMBER= "contact_number";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";

        public static Uri buildDriverInformationUriWithID(long ID){
            return ContentUris.withAppendedId(CONTENT_URI,ID);
        }
    }
}
