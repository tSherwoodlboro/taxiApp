package com.taxiapp.group28.taxiapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Tom on 24/04/2017.
 */

public class PickUpTimeReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        // start onBoot service to remind user of booking
        context.startService(new Intent(context,OnBootService.class));
    }
}
