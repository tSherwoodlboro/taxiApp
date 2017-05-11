package com.taxiapp.group28.taxiapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by Tom on 24/04/2017.
 */

public class PickUpTimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // start onBoot service to remind user of booking
        Log.d("PickUpTimeReceiver","Pick up alarm received");
        if (intent.getAction().equals(TaxiConstants.CUSTOM_ALARM_ACTION)) {
            context.startService(new Intent(context,OnBootService.class));
        }
    }
}
