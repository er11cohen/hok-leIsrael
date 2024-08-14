package com.eran.hokleisrael;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context,"AlarmReceiver - BroadcastReceiver",Toast.LENGTH_LONG).show();

        // setAlarm for next time of notifications
        HokUtils.setAlarm(context);
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            return;
        }
        String notificationType = intent.getStringExtra("notificationType");
        HokUtils.showNotification(context, notificationType);
    }
}
