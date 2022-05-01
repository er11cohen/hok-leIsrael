package com.eran.hokleisrael;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context,"AlarmReceiver - BroadcastReceiver",Toast.LENGTH_LONG).show();
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            HokUtils.setAlarm(context);
            return;
        }
        String notificationType = intent.getStringExtra("notificationType");
        HokUtils.showNotification(context, notificationType);
    }
}
