package com.eran.hokleisrael;

import java.util.Calendar;
import java.util.Date;

import net.sourceforge.zmanim.ZmanimCalendar;

import com.eran.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class HokUtils extends Activity {
 
	public static void setAlarm(Context context)
	{
	  //Toast.makeText(context,"setAlarm",Toast.LENGTH_LONG).show();
	  int dayInWeek;
	  AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	  
	 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

	//Bundle extraService = new Bundle();  
	boolean timeCBNotificationDaily = prefs.getBoolean("notifications_CB_timeNotificationDaily", false);
	if (timeCBNotificationDaily) 
	{
		Intent alarmReciverIntent = new Intent(context, AlarmReceiver.class);
		alarmReciverIntent.putExtra("notificationType", "daily");  
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmReciverIntent, PendingIntent.FLAG_UPDATE_CURRENT); 
		
		String timeNotificationDaily = prefs.getString("timePref_timeNotificationDaily", "00:00");
		String[] time = timeNotificationDaily.split(":");
		Calendar alarmStartTime = Calendar.getInstance();
		alarmStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
		alarmStartTime.set(Calendar.MINUTE,Integer.parseInt(time[1]));
		alarmStartTime.set(Calendar.SECOND,0);
		alarmStartTime.set(Calendar.MILLISECOND,0);
		
		
		if (Calendar.getInstance().getTime().after(alarmStartTime.getTime())) {
			alarmStartTime.add(Calendar.DATE, 1);
		}
		
		///////for notification now/////////////////////////////////////////////////////
//		Calendar alarmStart = Calendar.getInstance();
//	    alarmStart.add(Calendar.SECOND, 1);
//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStart.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);
		///////
		
	    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);

	}
	else 
	{
		//cancel daily notification
		
		Intent alarmReciverDailyCancelIntent = new Intent(context, AlarmReceiver.class);
		PendingIntent pendingCancelIntent = PendingIntent.getBroadcast(context, 0, alarmReciverDailyCancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingCancelIntent);
	}

		
	boolean timeFridayCBNotificationDaily = prefs.getBoolean("notifications_CB_timeFridayNotification", false);
   if (timeFridayCBNotificationDaily) 
	{
		Intent alarmReciverIntent = new Intent(context, AlarmReceiver.class);
		alarmReciverIntent.putExtra("notificationType","friday");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2, alarmReciverIntent, PendingIntent.FLAG_UPDATE_CURRENT); 
		
		
		
		ZmanimCalendar zc = Utils.getZmanimCalendar(context,"HLPreferences");
		
		Calendar currentCal = Calendar.getInstance();
		
		dayInWeek = currentCal.get(Calendar.DAY_OF_WEEK);

		int dayToAdd = 0;
		
		if (dayInWeek >= 1 && dayInWeek <= 4) {
			dayToAdd = 5 - dayInWeek;
		}
		else if (dayInWeek == 7) {
			dayToAdd = 5;
		}
		
		Calendar hatzotCal = calcHatzotCalendar(dayToAdd,zc, prefs);
		
		
		if (dayInWeek == 5 && currentCal.after(hatzotCal)) 
		{
				hatzotCal = calcHatzotCalendar(7,zc, prefs);
		}
		
		if (dayInWeek == 6 && currentCal.after(hatzotCal)) 
		{
				hatzotCal = calcHatzotCalendar(6,zc, prefs);
		}
		
		
	 	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, hatzotCal.getTimeInMillis(), 604800000/*weekly*/ , pendingIntent);
	 	Log.i("hatzotCal",hatzotCal.getTime().toString());
	 	//Toast.makeText(context,"hatzotCal:  " + hatzotCal.getTime().toString(),Toast.LENGTH_LONG).show();
	}
   else 
   {
		//cancel friday notification
		
		Intent alarmReciverFridayCancelIntent = new Intent(context, AlarmReceiver.class);
		PendingIntent pendingFridayCancelIntent = PendingIntent.getBroadcast(context, 2, alarmReciverFridayCancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingFridayCancelIntent);
	}
   
}
	private static Calendar calcHatzotCalendar(int dayToAdd, ZmanimCalendar zc,
			SharedPreferences prefs) {
		Calendar hatzotCal = Calendar.getInstance();
		hatzotCal.add(Calendar.DAY_OF_MONTH, dayToAdd);
		zc.getCalendar().set(hatzotCal.get(Calendar.YEAR),
				hatzotCal.get(Calendar.MONTH), hatzotCal.get(Calendar.DATE));
		hatzotCal.setTime(zc.getChatzos()); // this is Chatzos of day
		int hourToAdd = 12;
		if (hatzotCal.get(Calendar.DAY_OF_WEEK) == 6) {
			hourToAdd = -12;
		}
		hatzotCal.add(Calendar.HOUR, hourToAdd);
		String timeFridayBeforeStr = prefs.getString(
				"numberPickerPref_timeFridayBeforeNotification", "0");
		int timeFridayBefore = Integer.parseInt(timeFridayBeforeStr);
		hatzotCal.add(Calendar.MINUTE, (0 - timeFridayBefore));

		return hatzotCal;
	}
	
	@SuppressLint("NewApi")
	public static void createChannelNotification(Context context)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) 
	    {
			return;
	    }
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		SharedPreferences HLPreferences = context.getSharedPreferences("HLPreferences", MODE_PRIVATE);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		String notificationId = HLPreferences.getString("notificationId", null);
		String lastSoundNotification = HLPreferences.getString("lastSoundNotification", "");
		boolean lastVibrateNotification = HLPreferences.getBoolean("lastVibrateNotification", false);
		
		String sound = prefs.getString("notifications_new_message_ringtone", null);
		boolean vibratePref = prefs.getBoolean("notifications_new_message_vibrate", false);
		
		if(!lastSoundNotification.equals(sound) || vibratePref != lastVibrateNotification)
		{
			if(notificationId != null)
			{
				notificationManager.deleteNotificationChannel(notificationId);
			}
			notificationId = Long.toString((new Date()).getTime());
			
			Uri mySound = null;
			if(sound != null)
			{
				mySound = Uri.parse(sound);
			}
			else{
				mySound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			}
			long[] vibrate = null;
			if (vibratePref) {
				vibrate = new long[] { 1000, 1000, 1000 };
			}
			
			NotificationChannel mChannel = notificationManager.getNotificationChannel(notificationId);
			CharSequence name = context.getString(R.string.app_name);// The user-visible name of the channel.
	    	int importance = NotificationManager.IMPORTANCE_HIGH;
    		mChannel = new NotificationChannel(notificationId, name, importance);
	    	mChannel.setSound(mySound, new AudioAttributes.Builder()
	    	.setUsage(AudioAttributes.USAGE_MEDIA)
	    	.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
	    	.build());
	    	mChannel.setVibrationPattern(vibrate);
	    	notificationManager.createNotificationChannel(mChannel);
	    	
	    	 SharedPreferences.Editor editor = HLPreferences.edit();
		     editor.putString("notificationId", notificationId);
		     editor.putString("lastSoundNotification", sound);
		     editor.putBoolean("lastVibrateNotification", vibratePref);
			 editor.commit();
		}
	}

	
	@SuppressLint("NewApi")
	public static void showNotification(Context context, String notificationKind) {
		//Toast.makeText(context,"showNotification",Toast.LENGTH_LONG).show();
		
		Calendar currentCal = Calendar.getInstance();
		int dayInWeek = currentCal.get(Calendar.DAY_OF_WEEK);
		ZmanimCalendar zc = Utils.getZmanimCalendar(context, "HLPreferences");

		if (dayInWeek == 7 || (dayInWeek == 6 && currentCal.getTime().after(
						zc.getCandleLighting()))) {
			return;
		}
		
		Intent intent = new Intent(context, MainActivity.class);
		String showNotification = "צדיק הגיע זמן הלימוד היומי";
		if (notificationKind.equals("friday")) {
			showNotification = "צדיק הגיע זמן לימוד ליל שישי";
			intent.putExtra("fromNotification", "friday"); 
		}
		
		
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    PendingIntent pendingIntent  = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    final int notifyID = 1; 
	    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 
	    {
	    	SharedPreferences HLPreferences = context.getSharedPreferences("HLPreferences", MODE_PRIVATE);
	    	String notificationId = HLPreferences.getString("notificationId", null);
	    	if(notificationId == null){
	    		return;
	    	}
			Notification notification = new Notification.Builder(context, notificationId) //Builder(context)
	    	            .setContentTitle(context.getString(R.string.app_name))
	    	            .setContentText(showNotification)
	    	            .setSmallIcon(R.drawable.ic_launcher)
	    	            .setContentIntent(pendingIntent)
	    	            .setAutoCancel(true)
	    	            .setShowWhen(true)
	    	            .build();
	    	notificationManager.notify(notifyID, notification);
	    }
	    else 
	    { 
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String sound = prefs.getString("notifications_new_message_ringtone", null);
			Uri mySound = null;
			if(sound != null)
			{
				mySound = Uri.parse(sound);
			}
			else{
				mySound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			}
			
			long[] vibrate = null;
			boolean vibratePref = prefs.getBoolean(
					"notifications_new_message_vibrate", false);
			if (vibratePref) {
				vibrate = new long[] { 1000, 1000, 1000 };
			}
			
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			
			builder.setContentIntent(pendingIntent)
			            .setSmallIcon(R.drawable.ic_launcher)
			            .setTicker(showNotification)//show for 1 second
			            .setAutoCancel(true)
			            .setContentTitle(context.getString(R.string.app_name))
			            .setContentText(showNotification)
			            .setSound(mySound)
	                   .setVibrate(vibrate);
			notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
			notificationManager.notify(notifyID, builder.build());
	    }
	   
	}

}
