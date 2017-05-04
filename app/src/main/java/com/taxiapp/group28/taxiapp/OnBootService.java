package com.taxiapp.group28.taxiapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Tom on 19/04/2017.
 *
 * Service sends notification on boot if the user has a current booking open
 */

public class OnBootService extends IntentService {
    private String pickUpTime=null;
    public OnBootService() {
        super("onBootService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        String[] args = {"-1"};
        Log.d("Handle_LOADED","handle loaded");
        Cursor cursor = getContentResolver().query(DBContract.Booking_Table.CONTENT_URI, null, DBContract.Booking_Table.COLUMN_BOOKING_COMPLETE + "= ?", args, null);
        while (cursor.moveToNext()) {
            cursor.getColumnIndex(DBContract.Booking_Table.COLUMN_EST_ARRIVAL_TIME);
            pickUpTime = cursor.getString(cursor.getColumnIndex(DBContract.Booking_Table.COLUMN_EST_ARRIVAL_TIME));
            createNotification();
            break;
        }
    }

    private void createNotification() {
        Intent bookingsIntent = new Intent(this,MainMenuActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,bookingsIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager mNotifyMgr = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle("Taxi App Booking")
                        .setContentText("You have a booking at "+ pickUpTime+".");
        mNotifyMgr.notify(0,mBuilder.build());
    }
}
